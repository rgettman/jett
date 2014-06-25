package net.sf.jett.transform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import net.sf.jett.event.SheetEvent;
import net.sf.jett.event.SheetListener;
import net.sf.jett.expression.Expression;
import net.sf.jett.formula.Formula;
import net.sf.jett.model.Block;
import net.sf.jett.model.WorkbookContext;
import net.sf.jett.parser.FormulaParser;
import net.sf.jett.tag.TagContext;
import net.sf.jett.util.FormulaUtil;
import net.sf.jett.util.SheetUtil;

/**
 * A <code>SheetTransformer</code> knows how to transform one
 * <code>Sheet</code> in an Excel spreadsheet.  For cell processing, it creates
 * a <code>Block</code> representing the entire <code>Sheet</code>, then it
 * delegates processing to a <code>BlockTransformer</code>.  It is also
 * responsible for gathering all <code>Formulas</code> at the beginning, and
 * replacing all <code>Formulas</code> with Excel Formulas at the end.  It also
 * exposes the "sheet" object in the "beans" <code>Map</code>.
 *
 * @author Randy Gettman
 */
public class SheetTransformer
{
   private static final boolean DEBUG = false;

   /**
    * Specifies a callback interface that is called after all off-sheet
    * properties are set.  This is only necesary so the
    * <code>ExcelTransformer</code> can safely apply these off-sheet properties
    * that XSSF doesn't retain after the sheet name is changed.
    * @since 0.7.0
    */
   public static interface AfterOffSheetProperties
   {
      /**
       * Apply settings to the given <code>Sheet</code> after all off-sheet
       * properties have been transformed.
       * @param sheet The given <code>Sheet</code>.
       */
      public void applySettings(Sheet sheet);
   }

   /**
    * Transforms the given <code>Sheet</code>, using the given <code>Map</code>
    * of bean names to bean objects.
    * @param sheet The <code>Sheet</code> to transform.
    * @param context The <code>WorkbookContext</code>.
    * @param beans The beans map.
    */
   public void transform(Sheet sheet, WorkbookContext context, Map<String, Object> beans)
   {
      transform(sheet, context, beans, null);
   }

   /**
    * Transforms the given <code>Sheet</code>, using the given <code>Map</code>
    * of bean names to bean objects.
    * @param sheet The <code>Sheet</code> to transform.
    * @param context The <code>WorkbookContext</code>.
    * @param beans The beans map.
    * @param callback An optional <code>AfterOffSheetProperties</code>.  This
    *    is only present so the <code>ExcelTransformer</code>, as the caller of
    *    this method, can safely apply certain off-sheet properties that XSSF
    *    doesn't retain after the sheet name is changed.
    * @since 0.7.0
    */
   public void transform(Sheet sheet, WorkbookContext context, Map<String, Object> beans, AfterOffSheetProperties callback)
   {
      exposeSheet(beans, sheet);

      boolean shouldProceed = fireBeforeSheetProcessedEvent(context, sheet, beans);

      if (shouldProceed)
         transformOffSheetProperties(sheet, beans);

      // This will happen regardless.
      if (callback != null)
         callback.applySettings(sheet);
      
      if (!shouldProceed)
         return;

      // Create a Block to encompass the entire sheet of Cells.
      // Create a Block as if there was a start tag at the beginning of the
      // text in the first column of the first row and an end tag in the last
      // populated column of the last row of the sheet.
      Block block = new Block(null, 0, SheetUtil.getLastPopulatedColIndex(sheet), 0, sheet.getLastRowNum());
      block.setDirection(Block.Direction.NONE);
      if (DEBUG)
      {
         System.err.println("Transforming sheet " + sheet.getSheetName());

         Set<String> keys = beans.keySet();
         for (String key : keys)
         {
            System.err.println("  Key: " + key);
            System.err.println("    Value: " + beans.get(key).toString());
         }
      }

      TagContext tagContext = new TagContext();
      tagContext.setSheet(sheet);
      tagContext.setBlock(block);
      tagContext.setBeans(beans);
      tagContext.setProcessedCellsMap(new HashMap<String, Cell>());
      BlockTransformer transformer = new BlockTransformer();
      transformer.transform(tagContext, context);

      fireSheetProcessedEvent(context, sheet, beans);
   }

   /**
    * Transform any expressions in "off-sheet" properties, including header/
    * footer and the sheet name itself.
    * @param sheet The <code>Sheet</code> to transform.
    * @param beans The beans map.
    * @since 0.7.0
    */
   private void transformOffSheetProperties(Sheet sheet, Map<String, Object> beans)
   {
      String text;
      Object result;
      // Header/footer.
      Header header = sheet.getHeader();
      text = header.getLeft();
      result = Expression.evaluateString(text, beans);
      header.setLeft(result.toString());
      text = header.getCenter();
      result = Expression.evaluateString(text, beans);
      header.setCenter(result.toString());
      text = header.getRight();
      result = Expression.evaluateString(text, beans);
      header.setRight(result.toString());

      Footer footer = sheet.getFooter();
      text = footer.getLeft();
      result = Expression.evaluateString(text, beans);
      footer.setLeft(result.toString());
      text = footer.getCenter();
      result = Expression.evaluateString(text, beans);
      footer.setCenter(result.toString());
      text = footer.getRight();
      result = Expression.evaluateString(text, beans);
      footer.setRight(result.toString());

      // Sheet name
      text = sheet.getSheetName();
      result = Expression.evaluateString(text, beans);
      Workbook workbook = sheet.getWorkbook();
      workbook.setSheetName(workbook.getSheetIndex(sheet), result.toString());
   }

   /**
    * Searches for all <code>Formulas</code> contained on the given
    * <code>Sheet</code>.  Adds them to the given formula map.
    * @param sheet The <code>Sheet</code> on which to search for
    *    <code>Formulas</code>.
    * @param formulaMap A <code>Map</code> of strings to <code>Formulas</code>,
    *    with the keys of the format "sheetName!formulaText".
    */
   public void gatherFormulas(Sheet sheet, Map<String, Formula> formulaMap)
   {
      int top = sheet.getFirstRowNum();
      int bottom = sheet.getLastRowNum();
      int left, right;
      String sheetName = sheet.getSheetName();
      FormulaParser parser = new FormulaParser();

      for (int rowNum = top; rowNum <= bottom; rowNum++)
      {
         Row row = sheet.getRow(rowNum);
         if (row != null)
         {
            left = row.getFirstCellNum();
            // For some reason, "getLastCellNum()" returns the last cell num "PLUS ONE".
            right = row.getLastCellNum() - 1;
            for (int cellNum = left; cellNum <= right; cellNum++)
            {
               Cell cell = row.getCell(cellNum);
               if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING)
               {
                  String cellText = cell.getStringCellValue();
                  if (cellText != null)
                  {
                     int startIdx = cellText.indexOf(Formula.BEGIN_FORMULA);
                     if (startIdx != -1)
                     {
                        int endIdx = cellText.indexOf(Formula.END_FORMULA, startIdx);
                        if (endIdx != -1)  // End token after Begin token
                        {
                           // Grab the formula, begin and end tokens and all, e.g. $[SUM(C3)]
                           cellText = cellText.substring(startIdx, endIdx + Formula.END_FORMULA.length());
                           // Formula text is cell text without the begin and end tokens.
                           String formulaText = cellText.substring(Formula.BEGIN_FORMULA.length(), endIdx - startIdx);
                           parser.setFormulaText(formulaText);
                           parser.setCell(cell);
                           parser.parse();
                           Formula formula = new Formula(cellText, parser.getCellReferences());
                           String key = sheetName + "!" + cellText;
                           if (DEBUG)
                              System.err.println("ST.gF: Formula found: " + key + " => " + formula);
                           formulaMap.put(key, formula);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   /**
    * Replace all <code>Formulas</code> found in the given <code>Sheet</code>
    * with Excel formulas.
    * @param sheet The <code>Sheet</code>.
    * @param context The <code>WorkbookContext</code>.
    */
   public void replaceFormulas(Sheet sheet, WorkbookContext context)
   {
      int top = sheet.getFirstRowNum();
      int bottom = sheet.getLastRowNum();
      int left, right;
      String sheetName = sheet.getSheetName();
      Map<String, Formula> formulaMap = context.getFormulaMap();
      if (DEBUG)
         System.err.println("ST.rF: Rows from " + top + " to " + bottom);

      for (int rowNum = top; rowNum <= bottom; rowNum++)
      {
         Row row = sheet.getRow(rowNum);
         if (row != null)
         {
            left = row.getFirstCellNum();
            // For some reason, "getLastCellNum()" returns the last cell num "PLUS ONE".
            right = row.getLastCellNum() - 1;
            for (int cellNum = left; cellNum <= right; cellNum++)
            {
               Cell cell = row.getCell(cellNum);
               if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING)
               {
                  String cellText = cell.getStringCellValue();
                  if (cellText != null && cellText.startsWith(Formula.BEGIN_FORMULA) &&
                      cellText.endsWith(Formula.END_FORMULA))
                  {
                     // Don't consider any suffixes (e.g. "[0,0]") when looking
                     // up the Formula.
                     int idx = cellText.indexOf("]");
                     String cellTextNoSfx = cellText.substring(0, idx + 1);
                     String key = sheetName + "!" + cellTextNoSfx;
                     Formula formula = formulaMap.get(key);
                     if (formula != null)
                     {
                        // Replace all original cell references with translated cell references.
                        String excelFormula = FormulaUtil.createExcelFormulaString(cellText, formula, sheetName, context);
                        if (DEBUG)
                        {
                           System.err.println("  At " + sheetName + ", row " + rowNum + ", cell " +
                              cellNum + ", replacing formula text \"" + cellText + "\" with excel formula \"" +
                              excelFormula + "\".");
                        }
                        cell.setCellFormula(excelFormula);
                     }
                  }
               }
            }  // End cell for loop.
         }
      }  // End row for loop.
   }

   /**
    * Make the <code>Sheet</code> object available as bean in the given
    * <code>Map</code> of beans.
    * @param beans The <code>Map</code> of beans.
    * @param sheet The <code>Sheet</code> to expose.
    */
   private void exposeSheet(Map<String, Object> beans, Sheet sheet)
   {
      beans.put("sheet", sheet);
   }

   /**
    * Calls all <code>SheetListeners'</code> <code>beforeSheetProcessed</code>
    * method, sending a <code>SheetEvent</code>.
    * @param context The <code>WorkbookContext</code> object.
    * @param sheet The <code>Sheet</code> to be processed.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @return Whether processing of the <code>Sheet</code> should occur.  If
    *    any <code>SheetListener's</code> <code>beforeSheetProcessed</code>
    *    method returns <code>false</code>, then this method returns
    *    <code>false</code>.
    * @since 0.8.0
    */
   private boolean fireBeforeSheetProcessedEvent(WorkbookContext context, Sheet sheet, Map<String, Object> beans)
   {
      boolean shouldProceed = true;
      List<SheetListener> listeners = context.getSheetListeners();
      SheetEvent event = new SheetEvent(sheet, beans);
      for (SheetListener listener : listeners)
      {
         shouldProceed &= listener.beforeSheetProcessed(event);
      }
      return shouldProceed;
   }

   /**
    * Calls all <code>SheetListeners'</code> <code>sheetProcessed</code>
    * method, sending a <code>SheetEvent</code>.
    * @param context The <code>WorkbookContext</code> object.
    * @param sheet The <code>Sheet</code> to be processed.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @since 0.8.0
    */
   private void fireSheetProcessedEvent(WorkbookContext context, Sheet sheet, Map<String, Object> beans)
   {
      List<SheetListener> listeners = context.getSheetListeners();
      SheetEvent event = new SheetEvent(sheet, beans);
      for (SheetListener listener : listeners)
      {
         listener.sheetProcessed(event);
      }
   }
}

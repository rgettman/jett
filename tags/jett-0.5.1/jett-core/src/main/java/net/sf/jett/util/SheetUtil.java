package net.sf.jett.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import net.sf.jett.formula.Formula;
import net.sf.jett.model.Block;
import net.sf.jett.model.ExcelColor;
import net.sf.jett.model.PastEndAction;
import net.sf.jett.model.WorkbookContext;

/**
 * The <code>SheetUtil</code> utility class provides methods for
 * <code>Sheet</code>, <code>Row</code>, and <code>Cell</code> manipulation.
 *
 * @author Randy Gettman
 */
public class SheetUtil
{
   private static final boolean DEBUG = false;

   /**
    * Copy only the column widths in the given range of column indexes left by
    * the given number of columns.
    * @param sheet The <code>Sheet</code> on which to copy column widths.
    * @param colStart The 0-based column index on which to start.
    * @param colEnd The 0-based column index on which to end.
    * @param numCols The number of columns to copy column widths left.
    */
   private static void copyColumnWidthsLeft(Sheet sheet, int colStart, int colEnd, int numCols)
   {
      if (DEBUG)
         System.err.println("    cCWL: colStart = " + colStart + ", colEnd = " + colEnd +
            ", numCols = " + numCols);
      int newColNum;
      for (int colNum = colStart; colNum <= colEnd; colNum++)
      {
         newColNum = colNum - numCols;
         if (DEBUG)
            System.err.println("    Setting column width on col " + newColNum + " to col " +
               colNum + "'s width: " + sheet.getColumnWidth(colNum) + ".");
         sheet.setColumnWidth(newColNum, sheet.getColumnWidth(colNum));
      }
   }

   /**
    * Copy only the column widths in the given range of column indexes right by
    * the given number of columns.
    * @param sheet The <code>Sheet</code> on which to copy column widths.
    * @param colStart The 0-based column index on which to start.
    * @param colEnd The 0-based column index on which to end.
    * @param numCols The number of columns to copy column widths left.
    */
   private static void copyColumnWidthsRight(Sheet sheet, int colStart, int colEnd, int numCols)
   {
      if (DEBUG)
         System.err.println("    cCWR: colStart = " + colStart + ", colEnd = " + colEnd +
            ", numCols = " + numCols);
      int newColNum;
      for (int colNum = colEnd; colNum >= colStart; colNum--)
      {
         newColNum = colNum + numCols;
         if (DEBUG)
            System.err.println("    Setting column width on col " + newColNum + " to col " +
               colNum + "'s width: " + sheet.getColumnWidth(colNum) + ".");
         sheet.setColumnWidth(newColNum, sheet.getColumnWidth(colNum));
      }
   }

   /**
    * Determine the last populated column and return its 0-based index.
    * @param sheet The <code>Sheet</code> on which to determine the last
    *    populated column.
    * @return The 0-based index of the last populated column (-1 if the
    *    <code>Sheet</code> is empty).
    */
   public static int getLastPopulatedColIndex(Sheet sheet)
   {
      int maxCol = -1;
      int lastCol;
      for (Row row : sheet)
      {
         // For some reason, "getLastCellNum()" returns the last cell index "PLUS ONE".
         lastCol = row.getLastCellNum() - 1;
         if (lastCol > maxCol)
            maxCol = lastCol;
      }
      return maxCol;
   }

   /**
    * Copy only the row heights in the given range of row indexes up by
    * the given number of columns.
    * @param sheet The <code>Sheet</code> on which to copy row heights.
    * @param rowStart The 0-based row index on which to start.
    * @param rowEnd The 0-based row index on which to end.
    * @param numRows The number of row to copy row heights up.
    */
   private static void copyRowHeightsUp(Sheet sheet, int rowStart, int rowEnd, int numRows)
   {
      if (DEBUG)
         System.err.println("    cRHU: rowStart = " + rowStart + ", rowEnd = " + rowEnd +
            ", numRows = " + numRows);
      int newRowNum;
      Row row, newRow;
      for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++)
      {
         newRowNum = rowNum - numRows;
         row = sheet.getRow(rowNum);
         newRow = sheet.getRow(newRowNum);
         if (row == null && newRow != null)
         {
            newRow.setHeight(sheet.getDefaultRowHeight());  // "standard" height
            if (DEBUG)
               System.err.println("      Setting row height on row " + newRowNum +
                  " to \"standard\" row height of " + newRow.getHeight() +
                  ". (Row " + rowNum + " does not exist.)");
         }
         else if (row != null)
         {
            if (newRow == null)
               newRow = sheet.createRow(newRowNum);
            if (DEBUG)
               System.err.println("      Setting row height on row " + newRowNum + " to row " +
                  rowNum + "'s height: " + row.getHeight());
            newRow.setHeight(row.getHeight());
         }
      }
   }

   /**
    * Copy only the row heights in the given range of row indexes down by
    * the given number of columns.
    * @param sheet The <code>Sheet</code> on which to copy row heights.
    * @param rowStart The 0-based row index on which to start.
    * @param rowEnd The 0-based row index on which to end.
    * @param numRows The number of row to copy row heights down.
    */
   private static void copyRowHeightsDown(Sheet sheet, int rowStart, int rowEnd, int numRows)
   {
      if (DEBUG)
         System.err.println("    cRHD: rowStart = " + rowStart + ", rowEnd = " + rowEnd +
            ", numRows = " + numRows);
      int newRowNum;
      Row row, newRow;
      for (int rowNum = rowEnd; rowNum >= rowStart; rowNum--)
      {
         newRowNum = rowNum + numRows;
         row = sheet.getRow(rowNum);
         newRow = sheet.getRow(newRowNum);
         if (row == null && newRow != null)
         {
            newRow.setHeight(sheet.getDefaultRowHeight());  // "standard" height
            if (DEBUG)
               System.err.println("      Setting row height on row " + newRowNum +
                  " to \"standard\" row height of " + newRow.getHeight() +
                  ". (Row " + rowNum + " does not exist.)");
         }
         else if (row != null)
         {
            if (newRow == null)
               newRow = sheet.createRow(newRowNum);
            if (DEBUG)
               System.err.println("      Setting row height on row " + newRowNum + " to row " +
                  rowNum + "'s height: " + row.getHeight());
            newRow.setHeight(row.getHeight());
         }
      }
   }

   /**
    * Shift all <code>Cells</code> in the given range of row and column indexes
    * left by the given number of columns.  This will replace any
    * <code>Cells</code> that are "in the way".  Shifts merged regions also.
    *
    * @param sheet The <code>Sheet</code> on which to move <code>Cells</code>.
    * @param colStart The 0-based column index on which to start moving cells.
    * @param colEnd The 0-based column index on which to end moving cells.
    * @param rowStart The 0-based row index on which to start moving cells.
    * @param rowEnd The 0-based row index on which to end moving cells.
    * @param numCols The number of columns to move <code>Cells</code> left.
    */
   private static void shiftCellsLeft(Sheet sheet, int colStart, int colEnd,
      int rowStart, int rowEnd, int numCols)
   {
      if (DEBUG)
         System.err.println("    Shifting cells left in rows " + rowStart +
            " to " + rowEnd + ", cells " + colStart +
            " to " + colEnd + " by " + numCols + " columns.");
      Row row;
      Cell cell, newCell;
      int newColIndex;
      for (int rowIndex = rowStart; rowIndex <= rowEnd; rowIndex++)
      {
         row = sheet.getRow(rowIndex);
         if (row != null)
         {
            for (int colIndex = colStart; colIndex <= colEnd; colIndex++)
            {
               cell = row.getCell(colIndex);
               newColIndex = colIndex - numCols;
               newCell = row.getCell(newColIndex);
               if (cell == null && newCell != null)
                  removeCell(row, newCell);
               else if (cell != null)
               {
                  if (newCell == null)
                     newCell = row.createCell(newColIndex);
                  copyCell(cell, newCell);

                  // Remove the just copied Cell if we detect that it won't be
                  // overwritten by future loops.
                  if (colIndex > colEnd - numCols && colIndex <= colEnd)
                     removeCell(row, cell);
               }
            }
         }
      }

      shiftMergedRegionsInRange(sheet, colStart, colEnd, rowStart, rowEnd, -numCols, 0, true, true);
   }

   /**
    * Shift all <code>Cells</code> in the given range of row and column indexes
    * right by the given number of columns.  This will leave empty
    * <code>Cells</code> behind.  Shifts merged regions also.
    *
    * @param sheet The <code>Sheet</code> on which to move <code>Cells</code>.
    * @param colStart The 0-based column index on which to start moving cells.
    * @param colEnd The 0-based column index on which to end moving cells.
    * @param rowStart The 0-based row index on which to start moving cells.
    * @param rowEnd The 0-based row index on which to end moving cells.
    * @param numCols The number of columns to move <code>Cells</code> right.
    */
   private static void shiftCellsRight(Sheet sheet, int colStart, int colEnd,
      int rowStart, int rowEnd, int numCols)
   {
      if (DEBUG)
         System.err.println("    Shifting cells right in rows " + rowStart +
            " to " + rowEnd + ", cells " + colStart +
            " to " + colEnd + " by " + numCols + " columns.");
      Row row;
      Cell cell, newCell;
      int newColIndex;
      for (int rowIndex = rowStart; rowIndex <= rowEnd; rowIndex++)
      {
         row = sheet.getRow(rowIndex);
         if (row != null)
         {
            for (int colIndex = colEnd; colIndex >= colStart; colIndex--)
            {
               cell = row.getCell(colIndex);
               newColIndex = colIndex + numCols;
               newCell = row.getCell(newColIndex);
               if (cell == null && newCell != null)
                  removeCell(row, newCell);
               else if (cell != null)
               {
                  if (newCell == null)
                     newCell = row.createCell(newColIndex);
                  copyCell(cell, newCell);

                  // Remove the just copied Cell if we detect that it won't be
                  // overwritten by future loops.
                  if (colIndex < colStart + numCols && colIndex <= colEnd)
                     removeCell(row, cell);
               }
            }
         }
      }

      shiftMergedRegionsInRange(sheet, colStart, colEnd, rowStart, rowEnd, numCols, 0, true, true);
   }

   /**
    * Shift all <code>Cells</code> in the given range of row and column indexes
    * up by the given number of rows.  This will leave empty
    * <code>Cells</code> behind.  Shifts merged regions also.
    *
    * @param sheet The <code>Sheet</code> on which to move <code>Cells</code>.
    * @param colStart The 0-based column index on which to start moving cells.
    * @param colEnd The 0-based column index on which to end moving cells.
    * @param rowStart The 0-based row index on which to start moving cells.
    * @param rowEnd The 0-based row index on which to end moving cells.
    * @param numRows The number of columns to move <code>Cells</code> up.
    */
   private static void shiftCellsUp(Sheet sheet, int colStart, int colEnd,
      int rowStart, int rowEnd, int numRows)
   {
      if (DEBUG)
         System.err.println("    Shifting cells up in rows " + rowStart +
            " to " + rowEnd + ", cells " + colStart +
            " to " + colEnd + " by " + numRows + " rows.");
      int newRowIndex;
      Row oldRow, newRow;
      Cell cell, newCell;
      for (int colIndex = colStart; colIndex <= colEnd; colIndex++)
      {
         for (int rowIndex = rowStart; rowIndex <= rowEnd; rowIndex++)
         {
            newRowIndex = rowIndex - numRows;
            oldRow = sheet.getRow(rowIndex);
            newRow = sheet.getRow(newRowIndex);
            cell = null;
            if (oldRow != null)
               cell = oldRow.getCell(colIndex);
            newCell = null;
            if (newRow != null)
               newCell = newRow.getCell(colIndex);

            if (cell == null && newRow != null && newCell != null)
               removeCell(newRow, newCell);
            else if (cell != null)
            {
               if (newRow == null)
                  newRow = sheet.createRow(newRowIndex);
               if (newCell == null)
                  newCell = newRow.createCell(colIndex);
               copyCell(cell, newCell);

               // Remove the just copied Cell if we detect that it won't be
               // overwritten by future loops.
               if (rowIndex > rowEnd - numRows && rowIndex <= rowEnd)
                  removeCell(oldRow, cell);
            }
         }
      }

      shiftMergedRegionsInRange(sheet, colStart, colEnd, rowStart, rowEnd, 0, -numRows, true, true);
   }

   /**
    * Shift all <code>Cells</code> in the given range of row and column indexes
    * down by the given number of rows.  This will leave empty
    * <code>Cells</code> behind.
    *
    * @param sheet The <code>Sheet</code> on which to move <code>Cells</code>.
    * @param colStart The 0-based column index on which to start moving cells.
    * @param colEnd The 0-based column index on which to end moving cells.
    * @param rowStart The 0-based row index on which to start moving cells.
    * @param rowEnd The 0-based row index on which to end moving cells.
    * @param numRows The number of columns to move <code>Cells</code> down.
    */
   private static void shiftCellsDown(Sheet sheet, int colStart, int colEnd,
      int rowStart, int rowEnd, int numRows)
   {
      if (DEBUG)
         System.err.println("    Shifting cells down in rows " + rowStart +
            " to " + rowEnd + ", cells " + colStart +
            " to " + colEnd + " by " + numRows + " rows.");
      int newRowIndex;
      Row oldRow, newRow;
      Cell cell, newCell;
      for (int rowIndex = rowEnd; rowIndex >= rowStart; rowIndex--)
      {
         newRowIndex = rowIndex + numRows;
         oldRow = sheet.getRow(rowIndex);
         if (oldRow == null)
            oldRow = sheet.createRow(rowIndex);
         newRow = sheet.getRow(newRowIndex);
         if (newRow == null)
            newRow = sheet.createRow(newRowIndex);
         for (int colIndex = colStart; colIndex <= colEnd; colIndex++)
         {
            cell = oldRow.getCell(colIndex);
            if (cell == null)
               cell = oldRow.createCell(colIndex);
            newCell = newRow.getCell(colIndex);
            if (newCell == null)
               newCell = newRow.createCell(colIndex);
            copyCell(cell, newCell);

            // Remove the just copied Cell if we detect that it won't be
            // overwritten by future loops.
            if (rowIndex < rowStart + numRows && rowIndex <= rowEnd)
               removeCell(oldRow, cell);
         }
      }

      shiftMergedRegionsInRange(sheet, colStart, colEnd, rowStart, rowEnd, 0, numRows, true, true);
   }

   /**
    * Removes the given <code>Cell</code> from the given <code>Row</code>.
    * Also removes any <code>Comment</code>.
    * @param row The <code>Row</code> on which to remove a <code>Cell</code>.
    * @param cell The <code>Cell</code> to remove.
    */
   private static void removeCell(Row row, Cell cell)
   {
      cell.removeCellComment();
      row.removeCell(cell);
   }

   /**
    * Copy the contents of the old <code>Cell</code> to the new
    * <code>Cell</code>, including borders, cell styles, etc.
    * @param oldCell The source <code>Cell</code>.
    * @param newCell The destination <code>Cell</code>.
    */
   private static void copyCell(Cell oldCell, Cell newCell)
   {
      newCell.setCellStyle(oldCell.getCellStyle());

      switch(oldCell.getCellType())
      {
      case Cell.CELL_TYPE_STRING:
         newCell.setCellValue(oldCell.getRichStringCellValue());
         break;
      case Cell.CELL_TYPE_NUMERIC:
         newCell.setCellValue(oldCell.getNumericCellValue());
         break;
      case Cell.CELL_TYPE_BLANK:
         newCell.setCellType(Cell.CELL_TYPE_BLANK);
         break;
      case Cell.CELL_TYPE_FORMULA:
         newCell.setCellFormula(oldCell.getCellFormula());
         break;
      case Cell.CELL_TYPE_BOOLEAN:
         newCell.setCellValue(oldCell.getBooleanCellValue());
         break;
      case Cell.CELL_TYPE_ERROR:
         newCell.setCellErrorValue(oldCell.getErrorCellValue());
         break;
      default:
         break;
      }
      // Copy the Comment (if any).
//      Comment comment = oldCell.getCellComment();
//      if (comment != null)
//      {
//         Sheet sheet = newCell.getSheet();
//         Drawing drawing;
//         if (sheet instanceof HSSFSheet)
//         {
//            // The POI documentation warns of corrupting other "drawings" such
//            // as charts and "complex" drawings!!!
//            drawing = ((HSSFSheet) sheet).getDrawingPatriarch();
//            if (drawing == null)
//               drawing = sheet.createDrawingPatriarch();
//         }
//         else if (sheet instanceof XSSFSheet)
//         {
//            drawing = sheet.createDrawingPatriarch();
//         }
//         else
//            throw new IllegalArgumentException("Don't know how to copy a Cell Comment on a " +
//               sheet.getClass().getName());
//         CreationHelper helper = sheet.getWorkbook().getCreationHelper();
//         ClientAnchor newAnchor = helper.createClientAnchor();
//
//         Comment newComment = drawing.createCellComment(newAnchor);
//         newComment.setString(comment.getString());
//         newComment.setAuthor(comment.getAuthor());
//         newCell.setCellComment(newComment);
//      }
   }

   /**
    * Sets the cell value on the given <code>Cell</code> to the given
    * <code>value</code>, regardless of data type.
    * @param cell The <code>Cell</code> on which to set the value.
    * @param value The value.
    * @return The actual value set in the <code>Cell</code>.
    */
   public static Object setCellValue(Cell cell, Object value)
   {
      return setCellValue(cell, value, null);
   }

   /**
    * Sets the cell value on the given <code>Cell</code> to the given
    * <code>value</code>, regardless of data type.
    * @param cell The <code>Cell</code> on which to set the value.
    * @param value The value.
    * @param origRichString The original <code>RichTextString</code>, to be
    *    used to set the <code>CellStyle</code> if the value isn't some kind of
    *    string (<code>String</code> or <code>RichTextString</code>).
    * @return The actual value set in the <code>Cell</code>.
    */
   public static Object setCellValue(Cell cell, Object value, RichTextString origRichString)
   {
      CreationHelper helper = cell.getSheet().getWorkbook().getCreationHelper();
      Object newValue = value;
      boolean applyStyle = true;
      if (value == null)
      {
         newValue = helper.createRichTextString("");
         cell.setCellValue((RichTextString) newValue);
         cell.setCellType(Cell.CELL_TYPE_BLANK);
      }
      else if (value instanceof String)
      {
         newValue = helper.createRichTextString(value.toString());
         cell.setCellValue((RichTextString) newValue);
         applyStyle = false;
      }
      else if (value instanceof RichTextString)
      {
         cell.setCellValue((RichTextString) value);
         applyStyle = false;
      }
      else if (value instanceof Double)
         cell.setCellValue((Double) value);
      else if (value instanceof Integer)
         cell.setCellValue((Integer) value);
      else if (value instanceof Float)
         cell.setCellValue((Float) value);
      else if (value instanceof Long)
         cell.setCellValue((Long) value);
      else if (value instanceof Date)
         cell.setCellValue((Date) value);
      else if (value instanceof Calendar)
         cell.setCellValue((Calendar) value);
      else if (value instanceof Short)
         cell.setCellValue((Short) value);
      else if (value instanceof Byte)
         cell.setCellValue((Byte) value);
      else if (value instanceof Boolean)
         cell.setCellValue((Boolean) value);
      else
      {
         newValue = helper.createRichTextString(value.toString());
         cell.setCellValue((RichTextString) newValue);
         applyStyle = false;
      }
      if (applyStyle)
      {
         RichTextStringUtil.applyFont(origRichString, cell);
      }
      return newValue;
   }

   /**
    * Determines whether the <code>Cell</code> on the given <code>Sheet</code>
    * at the given row and column indexes is blank: either it doesn't exist, or
    * it exists and the cell type is blank.  That is, whether the cell doesn't
    * exist, is blank, or is empty.
    * @param sheet The <code>Sheet</code>.
    * @param rowNum The 0-based row index.
    * @param colNum The 0-based column index.
    * @return Whether the <code>Cell</code> is blank.
    */
   public static boolean isCellBlank(Sheet sheet, int rowNum, int colNum)
   {
      Row r = sheet.getRow(rowNum);
      if (r == null)
         return true;
      Cell c = r.getCell(colNum);
      return (c == null ||
              c.getCellType() == Cell.CELL_TYPE_BLANK ||
              (c.getCellType() == Cell.CELL_TYPE_STRING && "".equals(c.getStringCellValue())));
   }

   /**
    * Determines whether the <code>Cell</code> in the given <code>Row</code>
    * at the given index is blank: either it doesn't exist, or
    * it exists and the cell type is blank.  That is, whether the cell doesn't
    * exist, is blank, or is empty.
    * @param r The <code>Row</code> in which to look for the <code>Cell</code>.
    * @param colNum The 0-based column index.
    * @return Whether the <code>Cell</code> is blank.
    */
   public static boolean isCellBlank(Row r, int colNum)
   {
      Cell c = r.getCell(colNum);
      return (c == null ||
              c.getCellType() == Cell.CELL_TYPE_BLANK ||
              (c.getCellType() == Cell.CELL_TYPE_STRING && "".equals(c.getStringCellValue())));
   }

   /**
    * Returns a <code>String</code> that can reference the given
    * <code>Cell</code>.
    * @param cell The <code>Cell</code>.
    * @return A string in the format "sheet!A1".
    */
   public static String getCellKey(Cell cell)
   {
      StringBuilder buf = new StringBuilder();
      buf.append(cell.getSheet().getSheetName());
      buf.append("!");
      buf.append(CellReference.convertNumToColString(cell.getColumnIndex()));
      buf.append(cell.getRowIndex() + 1);
      return buf.toString();
   }

   /**
    * Shifts all merged regions found in the given range by the given number
    * of rows and columns (usually one of those two will be zero).
    * @param sheet The <code>Sheet</code> on which to shift merged regions.
    * @param left The 0-based index of the column on which to start shifting
    *    merged regions.
    * @param right The 0-based index of the column on which to end shifting
    *    merged regions.
    * @param top The 0-based index of the row on which to start shifting
    *    merged regions.
    * @param bottom The 0-based index of the row on which to end shifting
    *    merged regions.
    * @param numCols The number of columns to shift the merged region (can be
    *    negative).
    * @param numRows The number of rows to shift the merged region (can be
    *    negative).
    * @param remove Determines whether to remove the old merged region,
    *    resulting in a shift, or not to remove the old merged region,
    *    resulting in a copy.
    * @param add Determines whether to add the new merged region, resulting in
    *    a copy, or not to add the new merged region, resulting in a shift.
    */
   private static void shiftMergedRegionsInRange(Sheet sheet,
      int left, int right, int top, int bottom, int numCols, int numRows,
      boolean remove, boolean add)
   {
      if (DEBUG)
         System.err.println("    sMRIR: left " + left + ", right " + right +
            ", top " + top + ", bottom " + bottom + ", numCols " + numCols +
            ", numRows " + numRows + ", remove " + remove + ", add " + add);
      if (numCols == 0 && numRows == 0 && remove && add)
         return;
      ArrayList<CellRangeAddress> regions = new ArrayList<CellRangeAddress>();
      for (int i = 0; i < sheet.getNumMergedRegions(); i++)
      {
         CellRangeAddress region = sheet.getMergedRegion(i);
         if (isCellAddressWhollyContained(region, left, right, top, bottom))
         {
            regions.add(region);
            // Remove this range, if desired.
            if (remove)
            {
               if (DEBUG)
                  System.err.println("      Removing merged region: " + region);
               sheet.removeMergedRegion(i);
               // Must try this index again!
               i--;
            }
         }
      }
      // If desired, add a new region with the shifted version.
      if (add)
      {
         for (CellRangeAddress region : regions)
         {
            CellRangeAddress newRegion = new CellRangeAddress(
               region.getFirstRow() + numRows,
               region.getLastRow() + numRows,
               region.getFirstColumn() + numCols,
               region.getLastColumn() + numCols);
            if (DEBUG)
               System.err.println("      Adding adjusted merged region: " + newRegion + ".");
            sheet.addMergedRegion(newRegion);
         }
      }
   }

   /**
    * Determines whether the given <code>CellRangeAddress</code>, representing
    * a merged region, is wholly contained in the given area of
    * <code>Cells</code>.  If <code>left</code> &gt;= <code>right</code>, then
    * this will search the entire row(s).
    * @param mergedRegion The <code>CellRangeAddress</code> merged region.
    * @param left The 0-based column index on which to start searching for
    *    merged regions.
    * @param right The 0-based column index on which to stop searching for
    *    merged regions.
    * @param top The 0-based row index on which to start searching for
    *    merged regions.
    * @param bottom The 0-based row index on which to stop searching for
    *    merged regions.
    * @return <code>true</code> if wholly contained, <code>false</code>
    *    otherwise.
    */
   private static boolean isCellAddressWhollyContained(CellRangeAddress mergedRegion,
      int left, int right, int top, int bottom)
   {
      return (mergedRegion.getFirstRow() >= top && mergedRegion.getLastRow() <= bottom &&
          mergedRegion.getFirstColumn() >= left && mergedRegion.getLastColumn() <= right);
   }

   /**
    * Removes all <code>Cells</code> found inside the given <code>Block</code>
    * on the given <code>Sheet</code>.
    * @param sheet The <code>Sheet</code> on which to delete a
    *    <code>Block</code>
    * @param block The <code>Block</code> of <code>Cells</code> to delete.
    * @param context The <code>WorkbookContext</code>.
    */
   public static void deleteBlock(Sheet sheet, Block block, WorkbookContext context)
   {
      if (DEBUG)
         System.err.println("  deleteBlock: " + sheet.getSheetName() + ": " + block + ".");
      int left = block.getLeftColNum();
      int right = block.getRightColNum();
      int top = block.getTopRowNum();
      int bottom = block.getBottomRowNum();

      // Blank out the Cells.
      for (int rowNum = top; rowNum <= bottom; rowNum++)
      {
         Row r = sheet.getRow(rowNum);
         if (r != null)
         {
            for (int cellNum = left; cellNum <= right; cellNum++)
            {
               Cell c = r.getCell(cellNum);
               if (c != null)
                  removeCell(r, c);
            }
         }
      }
      // Remove any merged regions in this Block.
      shiftMergedRegionsInRange(sheet, left, right, top, bottom, 0, 0, true, false);
      // Lose the current cell references.
      FormulaUtil.shiftCellReferencesInRange(sheet.getSheetName(), context.getCellRefMap(),
         left, right, top, bottom,
         0, 0, true, false);
   }

   /**
    * Blanks out all <code>Cells</code> found inside the given
    * <code>Block</code> on the given <code>Sheet</code>.
    * @param sheet The <code>Sheet</code> on which to clear a
    *    <code>Block</code>
    * @param block The <code>Block</code> of <code>Cells</code> to clear.
    * @param context The <code>WorkbookContext</code>.
    */
   public static void clearBlock(Sheet sheet, Block block, WorkbookContext context)
   {
      if (DEBUG)
         System.err.println("  clearBlock: " + sheet.getSheetName() + ": " + block + ".");
      int left = block.getLeftColNum();
      int right = block.getRightColNum();
      int top = block.getTopRowNum();
      int bottom = block.getBottomRowNum();

      // Blank out the Cells.
      for (int rowNum = top; rowNum <= bottom; rowNum++)
      {
         Row r = sheet.getRow(rowNum);
         if (r != null)
         {
            for (int cellNum = left; cellNum <= right; cellNum++)
            {
               Cell c = r.getCell(cellNum);
               if (c != null)
                  c.setCellType(Cell.CELL_TYPE_BLANK);
            }
         }
      }
      // Lose the current cell references.
      FormulaUtil.shiftCellReferencesInRange(sheet.getSheetName(), context.getCellRefMap(),
         left, right, top, bottom,
         0, 0, true, false);
   }

   /**
    * Takes the given <code>PastEndAction</code> on all <code>Cells</code>
    * found inside the given <code>Block</code> on the given <code>Sheet</code>
    * that contain any of the given expressions.
    * @param sheet The <code>Sheet</code> on which to delete a
    *    <code>Block</code>
    * @param block The <code>Block</code> of <code>Cells</code> to delete.
    * @param pastEndRefs A <code>List</code> of strings identifying which
    *    expressions represent collection access beyond the end of the
    *    collection.
    * @param pastEndAction An enumerated value representing the action to take
    *    on such a cell/expression that references collection access beyond the
    *    end of the collection.
    * @see PastEndAction
    */
   public static void takePastEndAction(Sheet sheet, Block block, List<String> pastEndRefs,
      PastEndAction pastEndAction)
   {
      int left = block.getLeftColNum();
      int right = block.getRightColNum();
      int top = block.getTopRowNum();
      int bottom = block.getBottomRowNum();
      if (DEBUG)
      {
         System.err.println("takePastEndAction: " + block + ", action " + pastEndAction + ".");
         for (String pastEndRef : pastEndRefs)
            System.err.println("  PastEndRef: \"" + pastEndRef + "\".");
      }

      for (int rowNum = top; rowNum <= bottom; rowNum++)
      {
         Row r = sheet.getRow(rowNum);
         if (r != null)
         {
            for (int cellNum = left; cellNum <= right; cellNum++)
            {
               Cell c = r.getCell(cellNum);
               if (c != null)
                  takePastEndActionOnCell(c, pastEndRefs, pastEndAction);
            }
         }
      }
   }

   /**
    * Take the given <code>PastEndAction</code> on the given <code>Cell</code>,
    * if its contents contains any of the given references.
    * @param cell The <code>Cell</code>.
    * @param pastEndRefs A <code>List</code> of strings identifying which
    *    expressions represent collection access beyond the end of the
    *    collection.
    * @param pastEndAction The <code>PastEndAction</code> to take.
    */
   private static void takePastEndActionOnCell(Cell cell, List<String> pastEndRefs,
      PastEndAction pastEndAction)
   {
      String strValue;
      boolean takeAction = false;
      if (cell.getCellType() == Cell.CELL_TYPE_STRING)
      {
         for (String pastEndRef : pastEndRefs)
         {
            strValue = cell.getStringCellValue();
            if (strValue != null && strValue.indexOf(pastEndRef) >= 0)
            {
               takeAction = true;
               break;
            }
         }
      }
      if (takeAction)
      {
         switch(pastEndAction)
         {
         case CLEAR_CELL:
            cell.setCellType(Cell.CELL_TYPE_BLANK);
            break;
         case REMOVE_CELL:
            removeCell(cell.getRow(), cell);
            break;
         default:
            throw new IllegalStateException("Unknown PastEndAction: " + pastEndAction);
         }
      }
   }

   /**
    * Removes the given <code>Block</code> of <code>Cells</code> from the given
    * <code>Sheet</code>.
    * @param sheet The <code>Sheet</code> on which to remove the block.
    * @param block The <code>Block</code> to remove.
    * @param context The <code>WorkbookContext</code>.
    */
   public static void removeBlock(Sheet sheet, Block block, WorkbookContext context)
   {
      int left = block.getLeftColNum();
      int right = block.getRightColNum();
      int top = block.getTopRowNum();
      int bottom = block.getBottomRowNum();
      Block ancestor;
      if (DEBUG)
         System.err.println("removeBlock: " + sheet.getSheetName() + ": " + block + ".");

      int numToShiftUp = bottom - top + 1;
      int numToShiftLeft = right - left + 1;
      int startCellNum, endCellNum, startRowNum, endRowNum;

      switch (block.getDirection())
      {
      case VERTICAL:
         // Cells will be shifted up.
         if (DEBUG)
            System.err.println("  Case: Vertical");
         // Shift up all Cells from leftmost to rightmost in Block, from just
         // below the Block all the way down to the bottom of the first Shift
         // Ending Ancestor.
         ancestor = getShiftEndingAncestor(block, -numToShiftUp, 0);
         startRowNum = bottom + 1;
         endRowNum = ancestor.getBottomRowNum();

         // Remove the contents of the Block.
         deleteBlock(sheet, block, context);

         // If we reached the root parent, and our block is as wide as it, then
         // shrink it too.
         if (ancestor.getParent() == null &&
             left == ancestor.getLeftColNum() && right == ancestor.getRightColNum())
         {
            if (DEBUG)
               System.err.println("  Shrinking ancestor block (" + ancestor + " ) by " + numToShiftUp + " rows!");
            ancestor.expand(0, -numToShiftUp);
            copyRowHeightsUp(sheet, startRowNum, endRowNum, numToShiftUp);
         }
         shiftCellsUp(sheet, left, right, startRowNum, endRowNum, numToShiftUp);
         FormulaUtil.shiftCellReferencesInRange(sheet.getSheetName(), context.getCellRefMap(),
            left, right, startRowNum, endRowNum,
            0, -numToShiftUp, true, true);
         break;
      case HORIZONTAL:
         // Cells will be shifted left.
         if (DEBUG)
            System.err.println("  Case: Horizontal");
         // Shift left all Cells from the top to the bottom in Block, from just
         // to the right of the Block all the way to the far right of the first
         // Shift Ending Ancestor.
         ancestor = getShiftEndingAncestor(block, 0, -numToShiftLeft);
         startCellNum = right + 1;
         endCellNum = ancestor.getRightColNum();

         // Remove the contents of the Block.
         deleteBlock(sheet, block, context);

         // If we reached the root parent, and our block is as tall as it, then
         // shrink it too.
         if (ancestor.getParent() == null &&
             top == ancestor.getTopRowNum() && bottom == ancestor.getBottomRowNum())
         {
            if (DEBUG)
               System.err.println("  Shrinking ancestor block (" + ancestor + " ) by " + numToShiftLeft + " columns!");
            ancestor.expand(-numToShiftLeft, 0);
            copyColumnWidthsLeft(sheet, startCellNum, endCellNum, numToShiftLeft);
         }

         shiftCellsLeft(sheet, startCellNum, endCellNum, top, bottom, numToShiftLeft);
         FormulaUtil.shiftCellReferencesInRange(sheet.getSheetName(), context.getCellRefMap(),
            startCellNum, endCellNum, top, bottom,
            -numToShiftLeft, 0, true, true);
         break;
      case NONE:
         if (DEBUG)
            System.err.println("  Case: None");
         // Remove the Block content.
         deleteBlock(sheet, block, context);
         break;
      }
   }

   /**
    * Walk up the <code>Block</code> tree until a "shift ending" ancestor is
    * found, or until the tree has been exhausted.  The "shift ending" ancestor
    * is defined as an ancestor <code>Block</code> that is either a different
    * direction than the original <code>Block</code> or is larger than the
    * original <code>Block</code> along the other direction (that is, larger in
    * height for Horizontal blocks, or larger in width for Vertical blocks).
    * @param block The <code>Block</code> to search for ancestors.
    * @return The closest "shift ending" ancestor <code>Block</code>.
    */
   public static Block getShiftEndingAncestor(Block block)
   {
      return getShiftEndingAncestor(block, 0, 0);
   }

   /**
    * Walk up the <code>Block</code> tree until a "shift ending" ancestor is
    * found, or until the tree has been exhausted.  Optionally, grow/shrink
    * parent blocks encountered until the "shift ending" ancestor is found.
    * (The "shift ending" ancestor is not grown/shrunk).  The "shift ending"
    * ancestor is defined as an ancestor <code>Block</code> that is either a
    * different direction than the original <code>Block</code> or is larger
    * than the original <code>Block</code> along the other direction (that is,
    * larger in height for Horizontal blocks, or larger in width for Vertical
    * blocks).
    * @param block The <code>Block</code> to search for ancestors.
    * @param numVertCells The number of cells to grow each parent vertically
    *    until the "shift ending" ancestor is found, or shrink if
    *    <code>numCells</code> is negative.
    * @param numHorizCells The number of cells to grow each parent horizontally
    *    until the "shift ending" ancestor is found, or shrink if
    *    <code>numCells</code> is negative.
    * @return The closest "shift ending" ancestor <code>Block</code>.
    */
   public static Block getShiftEndingAncestor(Block block, int numVertCells, int numHorizCells)
   {
      int left = block.getLeftColNum();
      int right = block.getRightColNum();
      int top = block.getTopRowNum();
      int bottom = block.getBottomRowNum();
      Block ancestor = block.getParent();
      Block.Direction dir = block.getDirection();

      switch(dir)
      {
      case VERTICAL:
         while (ancestor != null)
         {
            if (ancestor.getDirection() != dir || left != ancestor.getLeftColNum() ||
                right != ancestor.getRightColNum())
               break;

            // Ancestors grow until the Shift Ending Ancestor is found.
            if (numVertCells != 0)
            {
               if (DEBUG)
                  System.err.println("    Growing ancestor block (" + ancestor + " ) by " + numVertCells + " rows!");
               ancestor.expand(0, numVertCells);
            }
            if (numHorizCells != 0)
            {
               if (DEBUG)
                  System.err.println("    Growing ancestor block (" + ancestor + " ) by " + numHorizCells + " columns!");
               ancestor.expand(numHorizCells, 0);
            }

            // Prepare for next loop.
            ancestor = ancestor.getParent();
         }
         break;
      case HORIZONTAL:
         while (ancestor != null)
         {
            if (ancestor.getDirection() != dir || top != ancestor.getTopRowNum() ||
                bottom != ancestor.getBottomRowNum())
               break;

            // Ancestors grow until the Shift Ending Ancestor is found.
            if (numVertCells != 0)
            {
               if (DEBUG)
                  System.err.println("    Growing ancestor block (" + ancestor + " ) by " + numVertCells + " rows!");
               ancestor.expand(0, numVertCells);
            }
            if (numHorizCells != 0)
            {
               if (DEBUG)
                  System.err.println("    Growing ancestor block (" + ancestor + " ) by " + numHorizCells + " columns!");
               ancestor.expand(numHorizCells, 0);
            }

            // Prepare for next loop.
            ancestor = ancestor.getParent();
         }
         break;
      }
      if (DEBUG)
         System.err.println("    gSEA: Ancestor of " + block + " is " + ancestor);
      return ancestor;
   }

   /**
    * Shifts <code>Cells</code> out of the way.
    * @param sheet The <code>Sheet</code> on which to shift.
    * @param block The <code>Block</code> whose copies will occupy the
    *    <code>Cells</code> that will move to make way for the copies.
    * @param context The <code>WorkbookContext</code>.
    * @param numBlocksAway The number of blocks (widths or lengths, depending
    *    on the case of <code>block</code> that defines the area of
    *    <code>Cells</code> to shift.
    */
   public static void shiftForBlock(Sheet sheet, Block block, WorkbookContext context, int numBlocksAway)
   {
      int left = block.getLeftColNum();
      int right = block.getRightColNum();
      int top = block.getTopRowNum();
      int bottom = block.getBottomRowNum();
      Block ancestor, prevAncestor;

      if (DEBUG)
         System.err.println("shiftForBlock: " + sheet.getSheetName() + ": " + block + ", numBlocksAway=" + numBlocksAway + ".");

      // Below this point!

      // If moving down...
      int height = bottom - top + 1;
      int translateDown = (numBlocksAway - 1) * height;  // Make room for n - 1 more Blocks.
      // If moving right...
      int width = right - left + 1;
      int translateRight = (numBlocksAway - 1) * width;  // Make room for n - 1 more Blocks.

      int startCellNum, endCellNum, startRowNum, endRowNum;
      Stack<Block> blocksToShift = new Stack<Block>();
      Stack<Integer> shiftAmounts = new Stack<Integer>();

      switch (block.getDirection())
      {
      case VERTICAL:
         // Cells will be shifted down.
         if (DEBUG)
            System.err.println("  Case Vertical");
         // The number of shift operations could be as many as the number of
         // Shift Ending Ancestors + 1 (for the root parent of the Sheet).
         // Keep finding Shift Ending Ancestors (or the root) and push a new
         // shift operation for each one.
         prevAncestor = block;
         ancestor = getShiftEndingAncestor(block, translateDown, 0);
         // Gather temporary Blocks to shift until a Shift Ending Ancestor has
         // enough room already, or we've reached the root parent Block.
         while (translateDown > 0)
         {
            // Define the Block of Cells that will get shifted.
            startRowNum = prevAncestor.getBottomRowNum() + 1;
            startCellNum = prevAncestor.getLeftColNum();
            endCellNum = prevAncestor.getRightColNum();
            endRowNum = ancestor.getBottomRowNum();
            if (prevAncestor.getDirection() == Block.Direction.HORIZONTAL)
            {
               // Below a Horizontal Ancestor, the range of columns in the
               // block to shift downwards is bigger.  (This content has not
               // been transformed yet.)
               startCellNum = ancestor.getLeftColNum();
               endCellNum = ancestor.getRightColNum();
            }

            // If the previous ancestor was already expanded, then the top edge
            // of this block hasn't been shifted yet.
            if (!shiftAmounts.isEmpty())
               startRowNum -= shiftAmounts.peek();

            // Empty rows at the bottom mean less rows to shift and future
            // shifts will be smaller.  Only do this in the first loop.
            int emptyRowsAtBottom = 0;
            if (prevAncestor == block)
            {
               emptyRowsAtBottom = getEmptyRowsAtBottom(sheet, startCellNum, endCellNum, startRowNum, endRowNum);
               if (emptyRowsAtBottom > 0)
                  endRowNum -= emptyRowsAtBottom;
               if (DEBUG)
                  System.err.println("    emptyRowsAtBottom: " + emptyRowsAtBottom);
            }
            if (translateDown > 0)
            {
               Block toShift = new Block(null, startCellNum, endCellNum, startRowNum, endRowNum);
               if (DEBUG)
                  System.err.println("    Block to shift: " + toShift + " by " + translateDown + " rows.");
               blocksToShift.push(toShift);
               shiftAmounts.push(translateDown);
               // The shifting will fill the bottom of the block.  Reduce the
               // ancestor's expansion amount.
               if (emptyRowsAtBottom > 0)
                  translateDown -= emptyRowsAtBottom;
               // Manually expand the Shift Ending Ancestor.
               if (translateDown > 0)
               {
                  if (DEBUG)
                     System.err.println("    Growing ancestor block (" + ancestor + " ) by " + translateDown + " rows!");
                  ancestor.expand(0, translateDown);
               }
            }
            // Prepare for next loop.
            prevAncestor = ancestor;
            if (ancestor.getParent() != null)
               ancestor = getShiftEndingAncestor(ancestor, translateDown, 0);
            else  // Already reached root.
               break;
         }

         // Perform the shifts in reverse order of found (LIFO).
         while (!blocksToShift.isEmpty())
         {
            Block toShift = blocksToShift.pop();
            translateDown = shiftAmounts.pop();

            copyRowHeightsDown(sheet, toShift.getTopRowNum(), toShift.getBottomRowNum(), translateDown);
            shiftCellsDown(sheet, toShift.getLeftColNum(), toShift.getRightColNum(),
               toShift.getTopRowNum(), toShift.getBottomRowNum(), translateDown);
            FormulaUtil.shiftCellReferencesInRange(sheet.getSheetName(), context.getCellRefMap(),
               toShift.getLeftColNum(), toShift.getRightColNum(), toShift.getTopRowNum(), toShift.getBottomRowNum(),
               0, translateDown, true, true);
         }
         break;
      case HORIZONTAL:
         // Cells will be shifted right.
         if (DEBUG)
            System.err.println("  Case Horizontal");
         // The number of shift operations could be as many as the number of
         // Shift Ending Ancestors + 1 (for the root parent of the Sheet).
         // Keep finding Shift Ending Ancestors (or the root) and push a new
         // shift operation for each one.
         prevAncestor = block;
         ancestor = getShiftEndingAncestor(block, 0, translateRight);
         // Gather temporary Blocks to shift until a Shift Ending Ancestor has
         // enough room already, or we've reached the root parent Block.
         while (translateRight > 0)
         {
            // Define the Block of Cells that will get shifted.
            startCellNum = prevAncestor.getRightColNum() + 1;
            startRowNum = prevAncestor.getTopRowNum();
            endRowNum = prevAncestor.getBottomRowNum();
            endCellNum = ancestor.getRightColNum();
            // To the right of a Vertical Ancestor, do not expand the row
            // range.  Content above and to the right has already been
            // transformed.  Content below and to the right will be on its own.

            // If the previous ancestor was already expanded, then the top edge
            // of this block hasn't been shifted yet.
            if (!shiftAmounts.isEmpty())
               startCellNum -= shiftAmounts.peek();

            // Empty cols at the right mean less cols to shift and future
            // shifts will be smaller.   Only do this in the first loop.
            int emptyColsAtRight = 0;
            if (prevAncestor == block)
            {
               emptyColsAtRight = getEmptyColumnsAtRight(sheet, startCellNum, endCellNum, startRowNum, endRowNum);
               if (emptyColsAtRight > 0)
                  endCellNum -= emptyColsAtRight;
               if (DEBUG)
                  System.err.println("    emptyColsAtRight: " + emptyColsAtRight);
            }
            if (translateRight > 0)
            {
               Block toShift = new Block(null, startCellNum, endCellNum, startRowNum, endRowNum);
               if (DEBUG)
                  System.err.println("    Block to shift: " + toShift + " by " + translateRight + " columns.");
               blocksToShift.push(toShift);
               shiftAmounts.push(translateRight);
               // The shifting will fill the far right of the block.  Reduce
               // the ancestor's expansion amount.
               if (emptyColsAtRight > 0)
                  translateRight -= emptyColsAtRight;
               if (translateRight > 0)
               {
                  // Manually expand the Block Area ancestor (or the root!).
                  if (DEBUG)
                     System.err.println("    Growing ancestor block (" + ancestor + " ) by " + translateRight + " columns!");
                  ancestor.expand(translateRight, 0);
               }
            }
            // Prepare for next loop.
            prevAncestor = ancestor;
            if (ancestor.getParent() != null)
               ancestor = getShiftEndingAncestor(ancestor, 0, translateRight);
            else  // Already reached root.
               break;
         }

         // Perform the shifts in reverse order of found (LIFO).
         while (!blocksToShift.isEmpty())
         {
            Block toShift = blocksToShift.pop();
            translateRight = shiftAmounts.pop();

            copyColumnWidthsRight(sheet, toShift.getLeftColNum(), toShift.getRightColNum(), translateRight);
            shiftCellsRight(sheet, toShift.getLeftColNum(), toShift.getRightColNum(),
               toShift.getTopRowNum(), toShift.getBottomRowNum(), translateRight);
            FormulaUtil.shiftCellReferencesInRange(sheet.getSheetName(), context.getCellRefMap(),
               toShift.getLeftColNum(), toShift.getRightColNum(), toShift.getTopRowNum(), toShift.getBottomRowNum(),
               translateRight, 0, true, true);
         }
         break;
      }
   }

   /**
    * Determine how many "empty" rows are at the bottom of the given
    * block of cells, between the left and right positions (inclusive).
    * @param sheet The <code>Sheet</code> on which the <code>Block</code> is
    *    located.
    * @param left The 0-based column position to start looking for empty cells.
    * @param right The 0-based column position to stop looking for empty cells.
    * @param top The 0-based row index to stop looking for empty cells.
    * @param bottom The 0-based row index to start looking for empty cells.
    * @return The number of empty rows at the bottom of the <code>Block</code>.
    */
   private static int getEmptyRowsAtBottom(Sheet sheet, int left, int right, int top, int bottom)
   {
      int emptyRows = 0;
      for (int r = bottom; r >= top; r--)
      {
         boolean rowEmpty = true;
//         Row row = sheet.getRow(r);
//         if (row != null)
//         {
            for (int c = left; c <= right; c++)
            {
               if (!isCellBlank(sheet, r, c))
               {
                  if (DEBUG)
                     System.err.println("      gERAB: Row " + r + " is not empty because of cell " + c);
                  rowEmpty = false;
                  break;
               }
            }
         //}
         if (rowEmpty)
            emptyRows++;
         else
            break;
      }
      return emptyRows;
   }

   /**
    * Determine how many "empty" columns are at the right of the given
    * block of cells, between the top and bottom positions (inclusive).
    * @param sheet The <code>Sheet</code> on which the <code>Block</code> is
    *    located.
    * @param left The 0-based column position to stop looking for empty cells.
    * @param right The 0-based column position to start looking for empty cells.
    * @param top The 0-based row index to start looking for empty cells.
    * @param bottom The 0-based row index to stop looking for empty cells.
    * @return The number of empty columns at the right of the <code>Block</code>.
    */
   private static int getEmptyColumnsAtRight(Sheet sheet, int left, int right, int top, int bottom)
   {
      int emptyColumns = 0;
      for (int c = right; c >= left; c--)
      {
         boolean colEmpty = true;
         for (int r = top; r <= bottom; r++)
         {
            Row row = sheet.getRow(r);
            if (row != null)
            {
               if (!isCellBlank(sheet, r, c))
               {
                  if (DEBUG)
                     System.err.println("      gECAR: Column " + c + " is not empty because of row " + r);
                  colEmpty = false;
                  break;
               }
            }
         }
         if (colEmpty)
            emptyColumns++;
         else
            break;
      }
      return emptyColumns;
   }

   /**
    * Copies an entire <code>Block</code> the given number of blocks away on
    * the given <code>Sheet</code>.
    * @param sheet The <code>Sheet</code> on which to copy.
    * @param block The <code>Block</code> to copy.
    * @param context The <code>WorkbookContext</code>.
    * @param numBlocksAway The number of blocks (widths or lengths, depending
    *    on the direction of <code>block</code>), away to copy.
    * @return The newly copied <code>Block</code>.
    */
   public static Block copyBlock(Sheet sheet, Block block, WorkbookContext context, int numBlocksAway)
   {
      int left = block.getLeftColNum();
      int right = block.getRightColNum();
      int top = block.getTopRowNum();
      int bottom = block.getBottomRowNum();
      Block parent = block.getParent();
      Block newBlock = null;
      String sheetName = sheet.getSheetName();
      int seqNbr = context.getSequenceNbr();
      String currSuffix = null;
      String newSuffix = "[" + seqNbr + "," + numBlocksAway + "]";
      if (DEBUG)
         System.err.println("copyBlock: " + sheet.getSheetName() + ": " + block + ", numBlocksAway=" + numBlocksAway);

      // If copying down...
      int height = block.getBottomRowNum() - block.getTopRowNum() + 1;
      int translateDown = numBlocksAway * height;
      int newTop = top + translateDown;
      int newBottom = bottom + translateDown;
      // If copying right...
      int width = block.getRightColNum() - block.getLeftColNum() + 1;
      int translateRight = numBlocksAway * width;
      int newLeft = left + translateRight;
      int newRight = right + translateRight;

      switch(block.getDirection())
      {
      case VERTICAL:
         if (DEBUG)
            System.err.println("  Case Vertical");
         // Copy Cells.
         if (DEBUG)
            System.err.println("    Copying cells " + left + " to " + right +
               ", row " + top + " to " + bottom + " by " + translateDown + " rows.");
         for (int r = top; r <= bottom; r++)
         {
            Row oldRow = sheet.getRow(r);
            if (oldRow == null)
               oldRow = sheet.createRow(r);
            Row newRow = sheet.getRow(r + translateDown);
            if (newRow == null)
               newRow = sheet.createRow(r + translateDown);
            for (int c = left; c <= right; c++)
            {
               Cell oldCell = oldRow.getCell(c);
               if (oldCell == null)
                  oldCell = oldRow.createCell(c);
               Cell newCell = newRow.getCell(c);
               if (newCell == null)
                  newCell = newRow.createCell(c);
               if (numBlocksAway > 0)
                 copyCell(oldCell, newCell);
               // Append "[loop,iter]" on formulas.
               if (newCell.getCellType() == Cell.CELL_TYPE_STRING)
               {
                  String cellText = newCell.getStringCellValue();
                  int startIdx = cellText.indexOf(Formula.BEGIN_FORMULA);
                  int endIdx = cellText.lastIndexOf(Formula.END_FORMULA);
                  if (startIdx != -1 && endIdx != -1 && startIdx < endIdx)
                  {
                     // If this is NOT the first iteration, then the copied
                     // text already has the previous iteration's suffix
                     // appended to it!  Remove it first.
                     if (numBlocksAway > 0)
                     {
                        int idx = cellText.lastIndexOf("[");
                        if (idx > -1)
                           cellText = cellText.substring(0, idx);  // Lose the last suffix.
                     }
                     // All formulas found should have the same current suffix.
                     // Find it once.
                     if (currSuffix == null)
                     {
                        int idx = cellText.indexOf("[", Formula.BEGIN_FORMULA.length());  // Skip the initial $[.
                        if (idx > -1)
                           currSuffix = cellText.substring(idx);
                        else
                           currSuffix = "";
                     }
                     String newFormula = cellText + newSuffix;
                     setCellValue(newCell, newFormula);
                  }
               }
            }
         }

         if (numBlocksAway > 0)
         {
            // Copy merged regions down.
            shiftMergedRegionsInRange(sheet, left, right,
               top, bottom, 0, translateDown, false, true);
            copyRowHeightsDown(sheet, top, bottom, translateDown);
            newBlock = new Block(parent, left, right, newTop, newBottom);
            newBlock.setDirection(block.getDirection());
         }
         else
            newBlock = block;

         if (currSuffix == null)
            currSuffix = "";
         FormulaUtil.copyCellReferencesInRange(sheetName, context.getCellRefMap(),
            left, right, top, bottom, 0, translateDown, currSuffix, newSuffix);
         break;
      case HORIZONTAL:
         if (DEBUG)
            System.err.println("  Case Horizontal");

         // Copy Cells.
         if (DEBUG)
            System.err.println("    Copying cells " + left + " to " + right +
               ", row " + top + " to " + bottom + " by " + translateRight + " columns.");
         for (int r = top; r <= bottom; r++)
         {
            Row row = sheet.getRow(r);
            if (row == null)
               row = sheet.createRow(r);
            for (int col = left; col <= right; col++)
            {
               Cell oldCell = row.getCell(col);
               if (oldCell == null)
                  oldCell = row.createCell(col);
               Cell newCell = row.getCell(col + translateRight);
               if (newCell == null)
                  newCell = row.createCell(col + translateRight);
               if (numBlocksAway > 0)
                  copyCell(oldCell, newCell);
               // Append proper "[loop,iter]" on formulas.
               if (newCell.getCellType() == Cell.CELL_TYPE_STRING)
               {
                  String cellText = newCell.getStringCellValue();
                  int startIdx = cellText.indexOf(Formula.BEGIN_FORMULA);
                  int endIdx = cellText.lastIndexOf(Formula.END_FORMULA);
                  if (startIdx != -1 && endIdx != -1 && startIdx < endIdx)
                  {
                     // If this is NOT the first iteration, then the copied
                     // text already has the previous iteration's suffix
                     // appended to it!  Remove it first.
                     if (numBlocksAway > 0)
                     {
                        int idx = cellText.lastIndexOf("[");
                        if (idx > -1)
                           cellText = cellText.substring(0, idx);  // Lose the last suffix.
                     }
                     // All formulas found should have the same current suffix.
                     // Find it once.
                     if (currSuffix == null)
                     {
                        int idx = cellText.indexOf("[", Formula.BEGIN_FORMULA.length());  // Skip the initial $[.
                        if (idx > -1)
                           currSuffix = cellText.substring(idx);
                        else
                           currSuffix = "";
                     }
                     String newFormula = cellText + newSuffix;
                     setCellValue(newCell, newFormula);
                  }
               }
            }
         }

         if (numBlocksAway > 0)
         {
            // Copy merged regions right.
            shiftMergedRegionsInRange(sheet, left, right, top, bottom, translateRight, 0, false, true);
            copyColumnWidthsRight(sheet, left, right, translateRight);
            newBlock = new Block(parent, newLeft, newRight, top, bottom);
            newBlock.setDirection(block.getDirection());
         }
         else
            newBlock = block;

         if (currSuffix == null)
            currSuffix = "";
         FormulaUtil.copyCellReferencesInRange(sheetName, context.getCellRefMap(),
            left, right, top, bottom, 0, translateDown, currSuffix, newSuffix);
         break;
      }
      return newBlock;
   }

   /**
    * Replace all occurrences of the given collection expression name with the
    * given item name, in preparation for implicit collections processing
    * loops.
    * @param sheet The <code>Sheet</code> on which the <code>Block</code> lies.
    * @param block The <code>Block</code> in which to perform the replacement.
    * @param collExpr The collection expression string to replace.
    * @param itemName The item name that replaces the collection expression.
    */
   public static void setUpBlockForImplicitCollectionAccess(Sheet sheet, Block block,
      String collExpr, String itemName)
   {
      int left = block.getLeftColNum();
      int right = block.getRightColNum();
      int top = block.getTopRowNum();
      int bottom = block.getBottomRowNum();
      CreationHelper helper = sheet.getWorkbook().getCreationHelper();
      // Look at the given range of Cells in the given range of rows.
      for (int rowNum = top; rowNum <= bottom; rowNum++)
      {
         Row row = sheet.getRow(rowNum);
         if (row != null)
         {
            for (int cellNum = left; cellNum <= right; cellNum++)
            {
               Cell cell = row.getCell(cellNum);
               if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING)
               {
                  RichTextString value = cell.getRichStringCellValue();
                  cell.setCellValue(RichTextStringUtil.replaceAll(
                     value, helper, collExpr, itemName));
               }
            }
         }
      }
   }

   /**
    * Group all rows on the sheet between the "begin" and "end" indices,
    * inclusive.  Optionally collapse the rows.
    * @param sheet The <code>Sheet</code> on which to group the rows.
    * @param begin The 0-based index of the start row of the group.
    * @param end The 0-based index of the end row of the group.
    * @param collapse Whether to collapse the group.
    * @since 0.2.0
    */
   public static void groupRows(Sheet sheet, int begin, int end, boolean collapse)
   {
      if (DEBUG)
         System.err.println("groupRows: " + sheet.getSheetName() + ", (" + begin +
            " - " + end + "), collapse: " + collapse);
      sheet.groupRow(begin, end);
      if (collapse)
      {
         if (sheet instanceof XSSFSheet)
         {
            // XSSFSheet - Must manually collapse the rows.
            for (int r = begin; r <= end; r++)
            {
               Row row = sheet.getRow(r);
               if (row == null)
                  row = sheet.createRow(r);
               row.setZeroHeight(true);
            }
         }
         else
         {
            // HSSFSheet - setRowGroupCollapsed works.
            sheet.setRowGroupCollapsed(begin, true);
         }
      }
   }

   /**
    * Group all columns on the sheet between the "begin" and "end" indices,
    * inclusive.  Optionally collapse the columns.
    * @param sheet The <code>Sheet</code> on which to group the columns.
    * @param begin The 0-based index of the start column of the group.
    * @param end The 0-based index of the end column of the group.
    * @param collapse Whether to collapse the group.
    * @since 0.2.0
    */
   public static void groupColumns(Sheet sheet, int begin, int end, boolean collapse)
   {
      if (DEBUG)
         System.err.println("groupColumns: " + sheet.getSheetName() + ", (" + begin +
            " - " + end + "), collapse: " + collapse);
      // XSSFSheets will collapse the columns on "groupColumn".
      // Store the column widths to restore them after "groupColumn".
      Map<Integer, Integer> colWidths = new HashMap<Integer, Integer>();
      if (sheet instanceof XSSFSheet)
      {
         if (DEBUG)
            System.err.println("Def. col width = " + sheet.getDefaultColumnWidth());
         for (int c = begin; c <= end; c++)
         {
            int w = sheet.getColumnWidth(c);
            if (DEBUG)
               System.err.println("Col " + c + ", w " + w);
            colWidths.put(c, w);
         }
      }
      if (sheet instanceof XSSFSheet)
      {
         // When nested, XSSFSheet's groupColumn doesn't do the whole range.
         for (int c = begin; c <= end; c++)
         {
            sheet.groupColumn(c, c);
            int w = colWidths.get(c);
            if (DEBUG)
               System.err.println("Setting Col " + c + ", to width " + w);
            sheet.setColumnWidth(c, w);
         }
      }
      else
      {
         // HSSFSheet works as expected.
         sheet.groupColumn(begin, end);
      }
      if (collapse)
      {
         if (sheet instanceof XSSFSheet)
         {
            // XSSFSheet - Must manually collapse the columns.
            for (int c = begin; c <= end; c++)
            {
               if (DEBUG)
                  System.err.println("Setting Col " + c + " hidden");
               sheet.setColumnHidden(c, true);
            }
         }
         else
         {
            // HSSFSheet - setColumnGroupCollapsed works.
            sheet.setColumnGroupCollapsed(begin, true);
         }
      }
   }

   /**
    * Get the hex string that represents the <code>Color</code>.
    * @param color A POI <code>Color</code>.
    * @return The hex string that represents the <code>Color</code>.
    * @since 0.5.0
    */
   public static String getColorHexString(Color color)
   {
      if (color instanceof HSSFColor)
      {
         HSSFColor hssfColor = (HSSFColor) color;
         return getHSSFColorHexString(hssfColor);
      }
      else if (color instanceof XSSFColor)
      {
         XSSFColor xssfColor = (XSSFColor) color;
         return getXSSFColorHexString(xssfColor);
      }
      else if (color == null)
      {
         return "null";
      }
      else
      {
         throw new IllegalArgumentException("Unexpected type of Color: " + color.getClass().getName());
      }
   }

   /**
    * Get the hex string for a <code>HSSFColor</code>.  Moved from test code.
    * @param hssfColor A <code>HSSFColor</code>.
    * @return The hex string.
    * @since 0.5.0
    */
   private static String getHSSFColorHexString(HSSFColor hssfColor)
   {
      short[] shorts = hssfColor.getTriplet();
      StringBuilder hexString = new StringBuilder();
      for (short s : shorts)
      {
         String twoHex = Integer.toHexString(0x000000FF & s);
         if (twoHex.length() == 1)
            hexString.append('0');
         hexString.append(twoHex);
      }
      return hexString.toString();
   }

   /**
    * Get the hex string for a <code>XSSFColor</code>.  Moved from test code.
    * @param xssfColor A <code>XSSFColor</code>.
    * @return The hex string.
    * @since 0.5.0
    */
   private static String getXSSFColorHexString(XSSFColor xssfColor)
   {
      if (xssfColor == null)
         return "000000";
      byte[] bytes;
      // As of Apache POI 3.8, there are Bugs 51236 and 52079 about font
      // color where somehow black and white get switched.  It appears to
      // have to do with the fact that black and white "theme" colors get
      // flipped.  Be careful, because XSSFColor(byte[]) does NOT call
      // "correctRGB", but XSSFColor.setRgb(byte[]) DOES call it, and so
      // does XSSFColor.getRgb(byte[]).
      // The private method "correctRGB" flips black and white, but no
      // other colors.  However, correctRGB is its own inverse operation,
      // i.e. correctRGB(correctRGB(rgb)) yields the same bytes as rgb.
      // XSSFFont.setColor(XSSFColor) calls "getRGB", but
      // XSSFCellStyle.set[Xx]BorderColor and
      // XSSFCellStyle.setFill[Xx]Color do NOT.
      // Solution: Correct the font color on the way out for themed colors
      // only.  For unthemed colors, bypass the "correction".
      if (xssfColor.getCTColor().isSetTheme())
         bytes = xssfColor.getRgb();
      else
         bytes = xssfColor.getCTColor().getRgb();
      // End of workaround for Bugs 51236 and 52079.
      if (bytes == null)
      {
         // Indexed Color - like HSSF
         HSSFColor hColor = ExcelColor.getHssfColorByIndex(xssfColor.getIndexed());
         if (hColor != null)
            return getHSSFColorHexString(ExcelColor.getHssfColorByIndex(xssfColor.getIndexed()));
         else
            return "000000";
      }
      if (bytes.length == 4)
      {
         // Lose the alpha.
         bytes = new byte[] {bytes[1], bytes[2], bytes[3]};
      }
      StringBuilder hexString = new StringBuilder();
      for (byte b : bytes)
      {
         String twoHex = Integer.toHexString(0x000000FF & b);
         if (twoHex.length() == 1)
            hexString.append('0');
         hexString.append(twoHex);
      }
      return hexString.toString();
   }

   /**
    * Creates a new <code>CellStyle</code> for the given <code>Workbook</code>,
    * with the given attributes.  Moved from <code>StyleTag</code> here for
    * 0.5.0.
    * @param workbook A <code>Workbook</code>.
    * @param alignment A <code>short</code> alignment constant.
    * @param borderBottom A <code>short</code> border type constant.
    * @param borderLeft A <code>short</code> border type constant.
    * @param borderRight A <code>short</code> border type constant.
    * @param borderTop A <code>short</code> border type constant.
    * @param dataFormat A data format string.
    * @param wrapText Whether text is wrapped.
    * @param fillBackgroundColor A background <code>Color</code>.
    * @param fillForegroundColor A foreground <code>Color</code>.
    * @param fillPattern A <code>short</code> pattern constant.
    * @param verticalAlignment A <code>short</code> vertical alignment constant.
    * @param indention A <code>short</code> number of indent characters.
    * @param rotationDegrees A <code>short</code> degrees rotation of text.
    * @param bottomBorderColor A border <code>Color</code> object.
    * @param leftBorderColor A border <code>Color</code> object.
    * @param rightBorderColor A border <code>Color</code> object.
    * @param topBorderColor A border <code>Color</code> object.
    * @param locked Whether the cell is locked.
    * @param hidden Whether the cell is hidden.
    * @return A new <code>CellStyle</code>.
    */
   public static CellStyle createCellStyle(Workbook workbook, short alignment, short borderBottom, short borderLeft,
      short borderRight, short borderTop, String dataFormat, boolean wrapText, Color fillBackgroundColor,
      Color fillForegroundColor, short fillPattern, short verticalAlignment, short indention,
      short rotationDegrees, Color bottomBorderColor, Color leftBorderColor,
      Color rightBorderColor, Color topBorderColor, boolean locked, boolean hidden)
   {
      CellStyle cs = workbook.createCellStyle();
      cs.setAlignment(alignment);
      cs.setBorderBottom(borderBottom);
      cs.setBorderLeft(borderLeft);
      cs.setBorderRight(borderRight);
      cs.setBorderTop(borderTop);
      cs.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(dataFormat));
      cs.setHidden(hidden);
      cs.setIndention(indention);
      cs.setLocked(locked);
      cs.setRotation(rotationDegrees);
      cs.setVerticalAlignment(verticalAlignment);
      cs.setWrapText(wrapText);
      // Certain properties need a type of workbook check.
      if (workbook instanceof HSSFWorkbook)
      {
         if (bottomBorderColor != null)
            cs.setBottomBorderColor(((HSSFColor) bottomBorderColor).getIndex());
         if (leftBorderColor != null)
            cs.setLeftBorderColor(((HSSFColor) leftBorderColor).getIndex());
         if (rightBorderColor != null)
            cs.setRightBorderColor(((HSSFColor) rightBorderColor).getIndex());
         if (topBorderColor != null)
            cs.setTopBorderColor(((HSSFColor) topBorderColor).getIndex());
         // Per POI Javadocs, set foreground color first!
         cs.setFillForegroundColor(((HSSFColor) fillForegroundColor).getIndex());
         cs.setFillBackgroundColor(((HSSFColor) fillBackgroundColor).getIndex());
      }
      else
      {
         // XSSFWorkbook
         XSSFCellStyle xcs = (XSSFCellStyle) cs;
         if (bottomBorderColor != null)
            xcs.setBottomBorderColor((XSSFColor) bottomBorderColor);
         if (leftBorderColor != null)
            xcs.setLeftBorderColor((XSSFColor) leftBorderColor);
         if (rightBorderColor != null)
            xcs.setRightBorderColor((XSSFColor) rightBorderColor);
         if (topBorderColor != null)
            xcs.setTopBorderColor((XSSFColor) topBorderColor);
         // Per POI Javadocs, set foreground color first!
         if (fillForegroundColor != null)
            xcs.setFillForegroundColor((XSSFColor) fillForegroundColor);
         if (fillBackgroundColor != null)
            xcs.setFillBackgroundColor((XSSFColor) fillBackgroundColor);
      }
      cs.setFillPattern(fillPattern);
      return cs;
   }

   /**
    * Creates a new <code>Font</code> for the given <code>Workbook</code>,
    * with the given attributes.  Moved from <code>StyleTag</code> here for
    * 0.5.0.
    * @param workbook A <code>Workbook</code>.
    * @param fontBoldweight A <code>short</code> boldweight constant.
    * @param fontItalic Whether the text is italic.
    * @param fontColor A color <code>Color</code> opbject.
    * @param fontName A font name.
    * @param fontHeightInPoints A <code>short</code> font height in points.
    * @param fontUnderline A <code>byte</code> underline constant.
    * @param fontStrikeout Whether the font is strikeout.
    * @param fontCharset An <code>int</code> charset constant.
    * @param fontTypeOffset A <code>short</code> type offset constant.
    * @return A new <code>Font</code>.
    */
   public static Font createFont(Workbook workbook, short fontBoldweight, boolean fontItalic, Color fontColor, String fontName, short fontHeightInPoints, byte fontUnderline,
      boolean fontStrikeout, int fontCharset, short fontTypeOffset)
   {
      if (DEBUG)
      {
         System.err.println("createFont: " + fontBoldweight + "," + fontItalic + "," +
            ((fontColor == null) ? "null" :
               (fontColor instanceof HSSFColor) ? fontColor.toString() :
               ((XSSFColor) fontColor).getCTColor().toString()
            ) + "," + fontName + "," +
            fontHeightInPoints + "," + fontUnderline + "," + fontStrikeout + "," + fontCharset + "," + fontTypeOffset);
      }
      Font f = workbook.createFont();
      f.setBoldweight(fontBoldweight);
      f.setItalic(fontItalic);
      f.setFontName(fontName);
      f.setFontHeightInPoints(fontHeightInPoints);
      f.setUnderline(fontUnderline);
      f.setStrikeout(fontStrikeout);
      f.setCharSet(fontCharset);
      f.setTypeOffset(fontTypeOffset);
      // Color type check.
      if (fontColor instanceof HSSFColor)
      {
         f.setColor(((HSSFColor) fontColor).getIndex());
      }
      else
      {
         // XSSFWorkbook
         XSSFFont xf = (XSSFFont) f;
         XSSFColor xssfFontColor = (XSSFColor) fontColor;
         if (xssfFontColor != null)
         {
            // As of Apache POI 3.8, there are Bugs 51236 and 52079 about font
            // color where somehow black and white get switched.  It appears to
            // have to do with the fact that black and white "theme" colors get
            // flipped.  Be careful, because XSSFColor(byte[]) does NOT call
            // "correctRGB", but XSSFColor.setRgb(byte[]) DOES call it, and so
            // does XSSFColor.getRgb(byte[]).
            // The private method "correctRGB" flips black and white, but no
            // other colors.  However, correctRGB is its own inverse operation,
            // i.e. correctRGB(correctRGB(rgb)) yields the same bytes as rgb.
            // XSSFFont.setColor(XSSFColor) calls "getRGB", but
            // XSSFCellStyle.set[Xx]BorderColor and
            // XSSFCellStyle.setFill[Xx]Color do NOT.
            // Solution: Let setColor correct a theme color on the way in.
            // Un-correct other colors, so that setColor will correct it.
            if (xssfFontColor.getCTColor().isSetTheme())
               xf.setColor(xssfFontColor);
            else
               xf.setColor(new XSSFColor(xssfFontColor.getRgb()));
            // End of workaround for Bugs 51236 and 52079.
         }
      }

      return f;
   }

   /**
    * Determines the proper POI <code>Color</code>, given a string value that
    * could be a color name, e.g. "aqua", or a hex string, e.g. "#FFCCCC".
    *
    * @param workbook A <code>Workbook</code>, used only to determine whether
    *    to create an <code>HSSFColor</code> or an <code>XSSFColor</code>.
    * @param value The color value, which could be one of the 48 pre-defined
    *    color names, or a hex value of the format "#RRGGBB".
    * @return A <code>Color</code>, or <code>null</code> if an invalid color
    *    name was given.
    */
   public static Color getColor(Workbook workbook, String value)
   {
      if (DEBUG)
         System.err.println("getColor: " + value);
      Color color = null;
      if (workbook instanceof HSSFWorkbook)
      {
         // Create an HSSFColor.
         if (value.startsWith("#"))
         {
            ExcelColor best = ExcelColor.AUTOMATIC;
            int minDist = 255 * 3;
            String strRed = value.substring(1, 3);
            String strGreen = value.substring(3, 5);
            String strBlue = value.substring(5, 7);
            int red   = Integer.parseInt(strRed, 16);
            int green = Integer.parseInt(strGreen, 16);
            int blue  = Integer.parseInt(strBlue, 16);
            // Hex value.  Find the closest defined color.
            for (ExcelColor excelColor : ExcelColor.values())
            {
               int dist = excelColor.distance(red, green, blue);
               if (dist < minDist)
               {
                  best = excelColor;
                  minDist = dist;
               }
            }
            color = best.getHssfColor();
            if (DEBUG)
               System.err.println("  Best HSSFColor found: " + color);
         }
         else
         {
            // Treat it as a color name.
            try
            {
               ExcelColor excelColor = ExcelColor.valueOf(value);
               if (excelColor != null)
                  color = excelColor.getHssfColor();
               if (DEBUG)
                  System.err.println("  HSSFColor name matched: " + value);
            }
            catch (IllegalArgumentException e)
            {
               if (DEBUG)
                  System.err.println("  HSSFColor name not matched: " + e.toString());
            }
         }
      }
      else // XSSFWorkbook
      {
         // Create an XSSFColor.
         if (value.startsWith("#") && value.length() == 7)
         {
            // Create the corresponding XSSFColor.
            color = new XSSFColor(new byte[] {
               Integer.valueOf(value.substring(1, 3), 16).byteValue(),
               Integer.valueOf(value.substring(3, 5), 16).byteValue(),
               Integer.valueOf(value.substring(5, 7), 16).byteValue()
            });
            if (DEBUG)
               System.err.println("  XSSFColor created: " + color);
         }
         else
         {
            // Create an XSSFColor from the RGB values of the desired color.
            try
            {
               ExcelColor excelColor = ExcelColor.valueOf(value);
               if (excelColor != null)
               {
                  color = new XSSFColor(new byte[]
                     {(byte) excelColor.getRed(), (byte) excelColor.getGreen(), (byte) excelColor.getBlue()}
                  );
               }
               if (DEBUG)
                  System.err.println("  XSSFColor name matched: " + value);
            }
            catch (IllegalArgumentException e)
            {
               if (DEBUG)
                  System.err.println("  XSSFColor name not matched: " + e.toString());
            }
         }
      }
      return color;
   }
}


package net.sf.jett.test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static org.junit.Assert.assertNotNull;

import net.sf.jett.transform.ExcelTransformer;
import net.sf.jett.util.SheetUtil;

/**
 * A <code>TestCase</code> is the superclass for all JETT JUnit test classes.
 */
public abstract class TestCase
{
   /**
    * Standard extremely small delta value to satisfy
    * <code>Assert.assertEquals(double, double, double)</code>.
    */
   public static final double DELTA = 0.00000000001;

   private static final String TEMPLATES_DIR = "templates/";
   private static final String OUTPUT_DIR = "output/";
   private static final String TEMPLATE_SUFFIX = "Template";
   private static final String OUTPUT_SUFFIX = "Result";
   private static final String XLS_EXT = ".xls";
   private static final String XLSX_EXT = ".xlsx";

   private boolean amISetup = false;
   private Map<String, Object> myBeansMap;
   private List<Map<String, Object>> myListOfBeansMaps;
   private List<String> myTemplateSheetNames;
   private List<String> myResultSheetNames;

   /**
    * Tests the .xls template spreadsheet.  This is meant to have the
    * <code>@Test</code> annotation in all concrete subclasses.  Also, each
    * concrete subclass should simply call <code>super.testXls</code>.
    * @throws IOException If an I/O error occurs.
    * @throws InvalidFormatException If the input spreadsheet is invalid.
    */
   public void testXls() throws IOException, InvalidFormatException
   {
      File fOutputDir = new File(OUTPUT_DIR);
      if (!fOutputDir.exists() && !fOutputDir.mkdirs())
      {
         throw new RuntimeException("Couldn't create output directory: " + OUTPUT_DIR);
      }
      String excelNameBase = getExcelNameBase();
      genericTest(OUTPUT_DIR + excelNameBase + OUTPUT_SUFFIX + XLS_EXT,
                  TEMPLATES_DIR + excelNameBase + TEMPLATE_SUFFIX + XLS_EXT);
   }

   /**
    * Tests the .xlsx template spreadsheet.  This is meant to have the
    * <code>@Test</code> annotation in all concrete subclasses.  Also, each
    * concrete subclass should simply call <code>super.testXlsx</code>.
    * @throws IOException If an I/O error occurs.
    * @throws InvalidFormatException If the input spreadsheet is invalid.
    */
   public void testXlsx() throws IOException, InvalidFormatException
   {
      File fOutputDir = new File(OUTPUT_DIR);
      if (!fOutputDir.exists() && !fOutputDir.mkdirs())
      {
         throw new RuntimeException("Couldn't create output directory: " + OUTPUT_DIR);
      }
      String excelNameBase = getExcelNameBase();
      genericTest(OUTPUT_DIR + excelNameBase + OUTPUT_SUFFIX + XLSX_EXT,
                  TEMPLATES_DIR + excelNameBase + TEMPLATE_SUFFIX + XLSX_EXT);
   }

   /**
    * Gets the string value from a particular <code>Cell</code> on the given
    * <code>Sheet</code>.  As a helper method, it is meant to be called from
    * within the <code>check</code> method.
    * @param sheet The <code>Sheet</code>.
    * @param row The 0-based row index.
    * @param col The 0-based column index.
    * @return The string value, as a <code>String</code>.
    * @see #check
    */
   protected String getStringCellValue(Sheet sheet, int row, int col)
   {
      Row r = sheet.getRow(row);
      if (r != null)
      {
         Cell c = r.getCell(col);
         if (c != null)
            return c.getStringCellValue();
      }
      return null;
   }

   /**
    * Gets the <code>RichTextString</code> value from a particular
    * <code>Cell</code> on the given <code>Sheet</code>.  As a helper method,
    * it is meant to be called from within the <code>check</code> method.
    * @param sheet The <code>Sheet</code>.
    * @param row The 0-based row index.
    * @param col The 0-based column index.
    * @return The <code>RichTextStringValue</code>.
    * @see #check
    */
   protected RichTextString getRichTextStringCellValue(Sheet sheet, int row, int col)
   {
      Row r = sheet.getRow(row);
      if (r != null)
      {
         Cell c = r.getCell(col);
         if (c != null)
            return c.getRichStringCellValue();
      }
      return null;
   }

   /**
    * Gets the numeric value from a particular <code>Cell</code> on the given
    * <code>Sheet</code>.  As a helper method, it is meant to be called from
    * within the <code>check</code> method.
    * @param sheet The <code>Sheet</code>.
    * @param row The 0-based row index.
    * @param col The 0-based column index.
    * @return The numeric value, as a <code>double</code>.
    * @see #check
    */
   protected double getNumericCellValue(Sheet sheet, int row, int col)
   {
      Row r = sheet.getRow(row);
      if (r != null)
      {
         Cell c = r.getCell(col);
         if (c != null)
            return c.getNumericCellValue();
      }
      return Double.NaN;
   }

   /**
    * Gets the string formula value from a particular <code>Cell</code> on the
    * given <code>Sheet</code>.  As a helper method, it is meant to be called
    * from within the <code>check</code> method.
    * @param sheet The <code>Sheet</code>.
    * @param row The 0-based row index.
    * @param col The 0-based column index.
    * @return The string formula value.
    * @see #check
    */
   protected String getFormulaCellValue(Sheet sheet, int row, int col)
   {
      Row r = sheet.getRow(row);
      if (r != null)
      {
         Cell c = r.getCell(col);
         if (c != null)
            return c.getCellFormula();
      }
      return null;
   }

   /**
    * Determines whether the <code>Cell</code> on the given <code>Sheet</code>
    * at the given row and column indexes is blank: either it doesn't exist, or
    * it exists and the cell type is blank.  As a helper method, it is meant to
    * be called from within the <code>check</code> method.
    * @param sheet The <code>Sheet</code>.
    * @param row The 0-based row index.
    * @param col The 0-based column index.
    * @return Whether the <code>Cell</code> is blank.
    * @see #check
    */
   protected boolean isCellBlank(Sheet sheet, int row, int col)
   {
      return SheetUtil.isCellBlank(sheet, row, col);
   }

   /**
    * Determines whether the <code>CellRangeAddress</code>, representing a
    * "merged region", exists in the given <code>Sheet</code>.  As a helper
    * method, it is meant to be called from within the <code>check</code>
    * method.
    * @param sheet The <code>Sheet</code>.
    * @param region A <code>CellRangeAddress</code>.
    * @return <code>true</code> if the given region exists in the given sheet,
    *    <code>false</code> otherwise.
    * @see #check
    */
   protected boolean isMergedRegionPresent(Sheet sheet, CellRangeAddress region)
   {
      for (int i = 0; i < sheet.getNumMergedRegions(); i++)
      {
         CellRangeAddress candidate = sheet.getMergedRegion(i);
         if (candidate.getFirstRow() == region.getFirstRow() &&
             candidate.getLastRow() == region.getLastRow() &&
             candidate.getFirstColumn() == region.getFirstColumn() &&
            candidate.getLastColumn() == region.getLastColumn())
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Returns the <code>Cell</code> (if any), on the given <code>Sheet</code>,
    * at the given row and column indexes.
    * @param sheet The <code>Sheet</code>.
    * @param row The 0-based row index.
    * @param col The 0-based column index.
    * @return The <code>Cell</code> or <code>null</code> if it doesn't exist.
    */
   protected Cell getCell(Sheet sheet, int row, int col)
   {
      Row r = sheet.getRow(row);
      if (r != null)
         return r.getCell(col);
      return null;
   }

   /**
    * Runs the actual test on an Excel template spreadsheet.
    * @param inFilename The input filename.
    * @param outFilename The output filename.
    * @throws IOException If an I/O error occurs.
    * @throws InvalidFormatException If the input spreadsheet is invalid.
    */
   protected void genericTest(String inFilename, String outFilename)
      throws IOException, InvalidFormatException
   {
      FileOutputStream fileOut = null;
      InputStream fileIn = null;
      Workbook workbook;
      try
      {
         fileOut = new FileOutputStream(inFilename);
         fileIn = new BufferedInputStream(new FileInputStream(outFilename));

         ExcelTransformer transformer = new ExcelTransformer();
         setupTransformer(transformer);
         if (isMultipleBeans())
         {
            if (!amISetup)
            {
               myTemplateSheetNames = getListOfTemplateSheetNames();
               myResultSheetNames = getListOfResultSheetNames();
               myListOfBeansMaps = getListOfBeansMaps();
               amISetup = true;
            }
            assertNotNull(myTemplateSheetNames);
            assertNotNull(myResultSheetNames);
            assertNotNull(myListOfBeansMaps);
            workbook = transformer.transform(
               fileIn, myTemplateSheetNames, myResultSheetNames, myListOfBeansMaps);
         }
         else
         {
            if (!amISetup)
            {
               myBeansMap = getBeansMap();
               amISetup = true;
            }
            assertNotNull(myBeansMap);
            workbook = transformer.transform(fileIn, myBeansMap);
         }

         // Becomes invalid after write().
         if (workbook instanceof XSSFWorkbook)
            check(workbook);

         workbook.write(fileOut);
         fileOut.close();

         // Check HSSF after writing to see errors.
         if (workbook instanceof HSSFWorkbook)
            check(workbook);
      }
      finally
      {
         try
         {
            if (fileIn != null)
               fileIn.close();
         }
         catch (IOException ignored) {}
         try
         {
            if (fileOut != null)
               fileOut.close();
         }
         catch (IOException ignored) {}
      }
   }

   /**
    * Returns the Excel name base for the template and resultant spreadsheets
    * for this test.
    * @return The Excel name base for this test.
    */
   protected abstract String getExcelNameBase();

   /**
    * Call certain setup-related methods on the <code>ExcelTransformer</code>
    * before template sheet transformation.
    * @param transformer The <code>ExcelTransformer</code> that will transform
    *    the template worksheet(s).
    */
   protected void setupTransformer(ExcelTransformer transformer) {}

   /**
    * Validate the newly created resultant <code>Workbook</code> with JUnit
    * assertions.  Helper methods are available to extract values or determine
    * facts from <code>Cells</code>.
    * @param workbook A <code>Workbook</code>.
    * @see #getCell
    * @see #getFormulaCellValue
    * @see #getNumericCellValue
    * @see #getStringCellValue
    * @see #isCellBlank
    * @see #isMergedRegionPresent
    */
   protected abstract void check(Workbook workbook);

   /**
    * Determines whether this test uses a single map of beans, or if it uses
    * multiple maps of beans along with template sheet names and resultant
    * sheet names.
    * @return <code>true</code> if this test uses multiple bean maps, or
    *    <code>false</code> if this test uses a single map of beans.
    */
   protected abstract boolean isMultipleBeans();

   /**
    * For multiple beans map tests, return the <code>List</code> of template
    * sheet names.
    * @return A <code>List</code> of template sheet names.
    */
   protected List<String> getListOfTemplateSheetNames()
   {
      return null;
   }

   /**
    * For multiple beans map tests, return the <code>List</code> of result
    * sheet names.
    * @return A <code>List</code> of result sheet names.
    */
   protected List<String> getListOfResultSheetNames()
   {
      return null;
   }

   /**
    * For multiple beans map tests, return the <code>List</code> of beans maps,
    * which map bean names to bean values for each corresponsing sheet.
    * @return A <code>List</code> of <code>Maps</code> of bean names to bean
    *    values.
    */
   protected List<Map<String, Object>> getListOfBeansMaps()
   {
      return null;
   }

   /**
    * For single beans map tests, return the <code>Map</code> of bean names to
    * bean values.
    * @return A <code>Map</code> of bean names to bean values.
    */
   protected Map<String, Object> getBeansMap()
   {
      return null;
   }
}
package net.sf.jett.test;

import java.io.IOException;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jett.transform.ExcelTransformer;

/**
 * This JUnit Test class tests the implicit collections processing feature of
 * JETT.
 */
public class ImplCollProcessingTest extends TestCase
{
   /**
    * Tests the .xls template spreadsheet.
    * @throws java.io.IOException If an I/O error occurs.
    * @throws org.apache.poi.openxml4j.exceptions.InvalidFormatException If the input spreadsheet is invalid.
    */
   @Test
   public void testXls() throws IOException, InvalidFormatException
   {
      super.testXls();
   }

   /**
    * Tests the .xlsx template spreadsheet.
    * @throws IOException If an I/O error occurs.
    * @throws InvalidFormatException If the input spreadsheet is invalid.
    */
   @Test
   public void testXlsx() throws IOException, InvalidFormatException
   {
      super.testXlsx();
   }

   /**
    * Returns the Excel name base for the template and resultant spreadsheets
    * for this test.
    * @return The Excel name base for this test.
    */
   protected String getExcelNameBase()
   {
      return "ImplCollProcessing";
   }

   /**
    * Call certain setup-related methods on the <code>ExcelTransformer</code>
    * before template sheet transformation.
    * @param transformer The <code>ExcelTransformer</code> that will transform
    *    the template worksheet(s).
    */
   protected void setupTransformer(ExcelTransformer transformer)
   {
      transformer.turnOffImplicitCollectionProcessing("counties");
   }

   /**
    * Validate the newly created resultant <code>Workbook</code> with JUnit
    * assertions.
    * @param workbook A <code>Workbook</code>.
    */
   protected void check(Workbook workbook)
   {
      Sheet implicit = workbook.getSheetAt(0);
      assertEquals("Division: Atlantic", TestUtility.getStringCellValue(implicit, 0, 0));
      assertTrue(TestUtility.isMergedRegionPresent(implicit, new CellRangeAddress(0, 0, 0, 4)));
      assertEquals("City", TestUtility.getStringCellValue(implicit, 1, 0));
      assertEquals("Celtics", TestUtility.getStringCellValue(implicit, 2, 1));
      assertEquals(37, TestUtility.getNumericCellValue(implicit, 3, 2), DELTA);
      assertEquals(38, TestUtility.getNumericCellValue(implicit, 4, 3), DELTA);
      assertEquals((double) 23 / (23 + 49), TestUtility.getNumericCellValue(implicit, 5, 4), DELTA);
      assertEquals("Toronto", TestUtility.getStringCellValue(implicit, 6, 0));
      assertEquals("Division: Central", TestUtility.getStringCellValue(implicit, 7, 0));
      assertEquals("Lakers", TestUtility.getStringCellValue(implicit, 30, 1));
      assertEquals("Division: Empty",TestUtility. getStringCellValue(implicit, 42, 0));
      assertEquals("Division: Of Their Own", TestUtility.getStringCellValue(implicit, 44, 0));
      assertEquals("Globetrotters", TestUtility.getStringCellValue(implicit, 46, 1));
      assertEquals("After", TestUtility.getStringCellValue(implicit, 47, 0));
      assertEquals(8, implicit.getNumMergedRegions());

      Sheet leftRight = workbook.getSheetAt(1);
      assertEquals("Don't", TestUtility.getStringCellValue(leftRight, 0, 0));
      assertEquals("Division: Atlantic", TestUtility.getStringCellValue(leftRight, 0, 1));
      assertEquals("Do", TestUtility.getStringCellValue(leftRight, 0, 6));
      assertEquals("Copy", TestUtility.getStringCellValue(leftRight, 1, 0));
      assertEquals("Wins", TestUtility.getStringCellValue(leftRight, 1, 3));
      assertEquals("not", TestUtility.getStringCellValue(leftRight, 1, 6));
      assertEquals("Me", TestUtility.getStringCellValue(leftRight, 2, 0));
      assertEquals(51, TestUtility.getNumericCellValue(leftRight, 2, 3), DELTA);
      assertEquals("copy", TestUtility.getStringCellValue(leftRight, 2, 6));
      assertEquals("Down!", TestUtility.getStringCellValue(leftRight, 3, 0));
      assertEquals(37, TestUtility.getNumericCellValue(leftRight, 3, 3), DELTA);
      assertEquals("downward!", TestUtility.getStringCellValue(leftRight, 3, 6));
      assertTrue(TestUtility.isCellBlank(leftRight, 4, 0));
      assertEquals(35, TestUtility.getNumericCellValue(leftRight, 4, 3), DELTA);
      assertTrue(TestUtility.isCellBlank(leftRight, 4, 6));
      assertEquals(23, TestUtility.getNumericCellValue(leftRight, 5, 3), DELTA);
      assertEquals(20, TestUtility.getNumericCellValue(leftRight, 6, 3), DELTA);
      assertEquals("Division: Central", TestUtility.getStringCellValue(leftRight, 7, 1));
      assertEquals("Division: Empty", TestUtility.getStringCellValue(leftRight, 42, 1));
      assertTrue(TestUtility.isMergedRegionPresent(leftRight, new CellRangeAddress(42, 42, 1, 5)));
      assertEquals("Division: Of Their Own", TestUtility.getStringCellValue(leftRight, 44, 1));
      assertTrue(TestUtility.isMergedRegionPresent(leftRight, new CellRangeAddress(44, 44, 1, 5)));
      assertEquals(21227, TestUtility.getNumericCellValue(leftRight, 46, 3), DELTA);
      assertEquals("After", TestUtility.getStringCellValue(leftRight, 47, 1));
      assertEquals(8, leftRight.getNumMergedRegions());

      Sheet fixedHoriz = workbook.getSheetAt(2);
      assertEquals("Division: Atlantic", TestUtility.getStringCellValue(fixedHoriz, 0, 0));
      assertTrue(TestUtility.isMergedRegionPresent(fixedHoriz, new CellRangeAddress(0, 4, 0, 0)));
      assertEquals("Boston", TestUtility.getStringCellValue(fixedHoriz, 0, 2));
      assertEquals("Right", TestUtility.getStringCellValue(fixedHoriz, 0, 7));
      assertEquals("76ers", TestUtility.getStringCellValue(fixedHoriz, 1, 3));
      assertTrue(TestUtility.isCellBlank(fixedHoriz, 1, 7));
      assertEquals(35, TestUtility.getNumericCellValue(fixedHoriz, 2, 4), DELTA);
      assertEquals("Division: Pacific", TestUtility.getStringCellValue(fixedHoriz, 20, 0));
      assertTrue(TestUtility.isMergedRegionPresent(fixedHoriz, new CellRangeAddress(20, 24, 0, 0)));
      assertEquals("Sacramento", TestUtility.getStringCellValue(fixedHoriz, 20, 6));
      assertEquals("Right", TestUtility.getStringCellValue(fixedHoriz, 20, 7));
      assertEquals("Lakers", TestUtility.getStringCellValue(fixedHoriz, 21, 2));
      assertEquals(42, TestUtility.getNumericCellValue(fixedHoriz, 23, 4), DELTA);
      assertEquals("Division: Empty", TestUtility.getStringCellValue(fixedHoriz, 30, 0));
      assertTrue(TestUtility.isMergedRegionPresent(fixedHoriz, new CellRangeAddress(30, 34, 0, 0)));
      assertTrue(TestUtility.isCellBlank(fixedHoriz, 30, 2));
      assertEquals("Right", TestUtility.getStringCellValue(fixedHoriz, 30, 7));
      assertTrue(TestUtility.isCellBlank(fixedHoriz, 34, 2));
      assertEquals("Right", TestUtility.getStringCellValue(fixedHoriz, 35, 7));
      assertEquals(21227, TestUtility.getNumericCellValue(fixedHoriz, 37, 2), DELTA);
      assertTrue(TestUtility.isCellBlank(fixedHoriz, 38, 3));
      assertEquals("Below", TestUtility.getStringCellValue(fixedHoriz, 40, 0));
      assertEquals(8, fixedHoriz.getNumMergedRegions());

      Sheet turnOff = workbook.getSheetAt(3);
      assertEquals("California", TestUtility.getStringCellValue(turnOff, 0, 1));
      assertEquals(58, TestUtility.getNumericCellValue(turnOff, 0, 3), DELTA);
      assertTrue(TestUtility.isCellBlank(turnOff, 0, 4));
      assertEquals("Nevada", TestUtility.getStringCellValue(turnOff, 1, 1));
      assertEquals(17, TestUtility.getNumericCellValue(turnOff, 1, 3), DELTA);
      assertTrue(TestUtility.isCellBlank(turnOff, 1, 4));
      assertTrue(TestUtility.isCellBlank(turnOff, 2, 0));
      assertTrue(TestUtility.isCellBlank(turnOff, 2, 1));
      assertTrue(TestUtility.isCellBlank(turnOff, 2, 3));

      Sheet noPae = workbook.getSheetAt(4);
      assertEquals("Harlem", TestUtility.getStringCellValue(noPae, 2, 0));
      assertEquals("Lakers", TestUtility.getStringCellValue(noPae, 2, 6));
      assertTrue(TestUtility.isCellBlank(noPae, 3, 0));
      assertEquals("Kings", TestUtility.getStringCellValue(noPae, 6, 6));
      assertTrue(TestUtility.isCellBlank(noPae, 7, 6));
      Cell cNoPae = TestUtility.getCell(noPae, 6, 2);
      assertNotNull(cNoPae);
      CellStyle csNoPae = cNoPae.getCellStyle();
      assertEquals(CellStyle.BORDER_THIN, csNoPae.getBorderBottom());
      assertEquals(CellStyle.BORDER_THIN, csNoPae.getBorderTop());
      assertEquals(CellStyle.BORDER_THIN, csNoPae.getBorderLeft());
      assertEquals(CellStyle.BORDER_THIN, csNoPae.getBorderRight());
      assertEquals("c0c0c0", TestUtility.getCellForegroundColorString(noPae, 6, 2));

      Sheet paeClear = workbook.getSheetAt(5);
      assertEquals("Harlem", TestUtility.getStringCellValue(paeClear, 2, 0));
      assertEquals("Lakers", TestUtility.getStringCellValue(paeClear, 2, 6));
      assertTrue(TestUtility.isCellBlank(paeClear, 3, 0));
      assertEquals("Kings", TestUtility.getStringCellValue(paeClear, 6, 6));
      assertTrue(TestUtility.isCellBlank(paeClear, 7, 6));
      Cell cPaeClear = TestUtility.getCell(paeClear, 6, 2);
      assertNotNull(cPaeClear);
      CellStyle csPaeClear = cPaeClear.getCellStyle();
      assertEquals(CellStyle.BORDER_THIN, csPaeClear.getBorderBottom());
      assertEquals(CellStyle.BORDER_THIN, csPaeClear.getBorderTop());
      assertEquals(CellStyle.BORDER_THIN, csPaeClear.getBorderLeft());
      assertEquals(CellStyle.BORDER_THIN, csPaeClear.getBorderRight());
      assertEquals("c0c0c0", TestUtility.getCellForegroundColorString(paeClear, 6, 2));

      Sheet paeRemove = workbook.getSheetAt(6);
      assertEquals("Harlem", TestUtility.getStringCellValue(paeRemove, 2, 0));
      assertEquals("Lakers", TestUtility.getStringCellValue(paeRemove, 2, 6));
      assertTrue(TestUtility.isCellBlank(paeRemove, 3, 0));
      assertEquals("Kings",TestUtility. getStringCellValue(paeRemove, 6, 6));
      assertTrue(TestUtility.isCellBlank(paeRemove, 7, 6));
      Cell cPaeRemove = TestUtility.getCell(paeRemove, 6, 2);
      assertNull(cPaeRemove);

      // Cannot test for grouping but can test for the collapse side effect.
      Sheet groupDirNone = workbook.getSheetAt(7);
      for (int r = 0; r < 48; r++)
      {
         assertFalse(groupDirNone.getRow(r) != null && groupDirNone.getRow(r).getZeroHeight());
      }
      for (int c = 0; c < 6; c++)
      {
         assertFalse(groupDirNone.isColumnHidden(c));
      }

      Sheet groupDirRows = workbook.getSheetAt(8);
      for (int r = 0; r < 48; r++)
      {
         // These rows are collapsed.
         if (r >= 16 && r <= 20)
         {
            assertTrue(groupDirRows.getRow(r).getZeroHeight());
         }
         else
         {
            assertFalse(groupDirRows.getRow(r) != null && groupDirRows.getRow(r).getZeroHeight());
         }
      }
      for (int c = 0; c < 6; c++)
      {
         assertFalse(groupDirRows.isColumnHidden(c));
      }

      Sheet groupDirCols = workbook.getSheetAt(9);
      for (int r = 0; r < 8; r++)
      {
         assertFalse(groupDirCols.getRow(r) != null && groupDirCols.getRow(r).getZeroHeight());
      }
      for (int c = 0; c < 48; c++)
      {
         // These columns are collapsed.
         if (c >= 13 && c <= 17)
         {
            assertTrue(groupDirCols.isColumnHidden(c));
         }
         else
         {
            assertFalse(groupDirCols.isColumnHidden(c));
         }
      }
   }

   /**
    * This test is a single map test.
    * @return <code>false</code>.
    */
   protected boolean isMultipleBeans()
   {
      return false;
   }

   /**
    * For single beans map tests, return the <code>Map</code> of bean names to
    * bean values.
    * @return A <code>Map</code> of bean names to bean values.
    */
   protected Map<String, Object> getBeansMap()
   {
      Map<String, Object> beans = TestUtility.getDivisionData();
      beans.putAll(TestUtility.getStateData());
      beans.putAll(TestUtility.getSpecificDivisionData(4, "pacific"));
      beans.putAll(TestUtility.getSpecificDivisionData(7, "ofTheirOwn"));
      return beans;
   }
}

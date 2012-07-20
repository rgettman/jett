package net.sf.jett.test;

import java.io.IOException;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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
      assertEquals("Division: Atlantic", getStringCellValue(implicit, 0, 0));
      assertTrue(isMergedRegionPresent(implicit, new CellRangeAddress(0, 0, 0, 4)));
      assertEquals("City", getStringCellValue(implicit, 1, 0));
      assertEquals("Celtics", getStringCellValue(implicit, 2, 1));
      assertEquals(37, getNumericCellValue(implicit, 3, 2), DELTA);
      assertEquals(38, getNumericCellValue(implicit, 4, 3), DELTA);
      assertEquals((double) 23 / (23 + 49), getNumericCellValue(implicit, 5, 4), DELTA);
      assertEquals("Toronto", getStringCellValue(implicit, 6, 0));
      assertEquals("Division: Central", getStringCellValue(implicit, 7, 0));
      assertEquals("Lakers", getStringCellValue(implicit, 30, 1));
      assertEquals("Division: Empty", getStringCellValue(implicit, 42, 0));
      assertEquals("Division: Of Their Own", getStringCellValue(implicit, 44, 0));
      assertEquals("Globetrotters", getStringCellValue(implicit, 46, 1));
      assertEquals("After", getStringCellValue(implicit, 47, 0));
      assertEquals(8, implicit.getNumMergedRegions());

      Sheet leftRight = workbook.getSheetAt(1);
      assertEquals("Don't", getStringCellValue(leftRight, 0, 0));
      assertEquals("Division: Atlantic", getStringCellValue(leftRight, 0, 1));
      assertEquals("Do", getStringCellValue(leftRight, 0, 6));
      assertEquals("Copy", getStringCellValue(leftRight, 1, 0));
      assertEquals("Wins", getStringCellValue(leftRight, 1, 3));
      assertEquals("not", getStringCellValue(leftRight, 1, 6));
      assertEquals("Me", getStringCellValue(leftRight, 2, 0));
      assertEquals(51, getNumericCellValue(leftRight, 2, 3), DELTA);
      assertEquals("copy", getStringCellValue(leftRight, 2, 6));
      assertEquals("Down!", getStringCellValue(leftRight, 3, 0));
      assertEquals(37, getNumericCellValue(leftRight, 3, 3), DELTA);
      assertEquals("downward!", getStringCellValue(leftRight, 3, 6));
      assertTrue(isCellBlank(leftRight, 4, 0));
      assertEquals(35, getNumericCellValue(leftRight, 4, 3), DELTA);
      assertTrue(isCellBlank(leftRight, 4, 6));
      assertEquals(23, getNumericCellValue(leftRight, 5, 3), DELTA);
      assertEquals(20, getNumericCellValue(leftRight, 6, 3), DELTA);
      assertEquals("Division: Central", getStringCellValue(leftRight, 7, 1));
      assertEquals("Division: Empty", getStringCellValue(leftRight, 42, 1));
      assertTrue(isMergedRegionPresent(leftRight, new CellRangeAddress(42, 42, 1, 5)));
      assertEquals("Division: Of Their Own", getStringCellValue(leftRight, 44, 1));
      assertTrue(isMergedRegionPresent(leftRight, new CellRangeAddress(44, 44, 1, 5)));
      assertEquals(21227, getNumericCellValue(leftRight, 46, 3), DELTA);
      assertEquals("After", getStringCellValue(leftRight, 47, 1));
      assertEquals(8, leftRight.getNumMergedRegions());

      Sheet fixedHoriz = workbook.getSheetAt(2);
      assertEquals("Division: Atlantic", getStringCellValue(fixedHoriz, 0, 0));
      assertTrue(isMergedRegionPresent(fixedHoriz, new CellRangeAddress(0, 4, 0, 0)));
      assertEquals("Boston", getStringCellValue(fixedHoriz, 0, 2));
      assertEquals("Right", getStringCellValue(fixedHoriz, 0, 7));
      assertEquals("76ers", getStringCellValue(fixedHoriz, 1, 3));
      assertTrue(isCellBlank(fixedHoriz, 1, 7));
      assertEquals(35, getNumericCellValue(fixedHoriz, 2, 4), DELTA);
      assertEquals("Division: Pacific", getStringCellValue(fixedHoriz, 20, 0));
      assertTrue(isMergedRegionPresent(fixedHoriz, new CellRangeAddress(20, 24, 0, 0)));
      assertEquals("Sacramento", getStringCellValue(fixedHoriz, 20, 6));
      assertEquals("Right", getStringCellValue(fixedHoriz, 20, 7));
      assertEquals("Lakers", getStringCellValue(fixedHoriz, 21, 2));
      assertEquals(42, getNumericCellValue(fixedHoriz, 23, 4), DELTA);
      assertEquals("Division: Empty", getStringCellValue(fixedHoriz, 30, 0));
      assertTrue(isMergedRegionPresent(fixedHoriz, new CellRangeAddress(30, 34, 0, 0)));
      assertTrue(isCellBlank(fixedHoriz, 30, 2));
      assertEquals("Right", getStringCellValue(fixedHoriz, 30, 7));
      assertTrue(isCellBlank(fixedHoriz, 34, 2));
      assertEquals("Right", getStringCellValue(fixedHoriz, 35, 7));
      assertEquals(21227, getNumericCellValue(fixedHoriz, 37, 2), DELTA);
      assertTrue(isCellBlank(fixedHoriz, 38, 3));
      assertEquals("Below", getStringCellValue(fixedHoriz, 40, 0));
      assertEquals(8, fixedHoriz.getNumMergedRegions());

      Sheet turnOff = workbook.getSheetAt(3);
      assertEquals("California", getStringCellValue(turnOff, 0, 1));
      assertEquals(58, getNumericCellValue(turnOff, 0, 3), DELTA);
      assertTrue(isCellBlank(turnOff, 0, 4));
      assertEquals("Nevada", getStringCellValue(turnOff, 1, 1));
      assertEquals(17, getNumericCellValue(turnOff, 1, 3), DELTA);
      assertTrue(isCellBlank(turnOff, 1, 4));
      assertTrue(isCellBlank(turnOff, 2, 0));
      assertTrue(isCellBlank(turnOff, 2, 1));
      assertTrue(isCellBlank(turnOff, 2, 3));
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
      return beans;
   }
}

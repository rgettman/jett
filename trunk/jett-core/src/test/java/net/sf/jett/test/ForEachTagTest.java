package net.sf.jett.test;

import java.io.IOException;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This JUnit Test class tests the evaluation of the "forEach" tag in entire
 * rows, block area, and bodiless modes.
 */
public class ForEachTagTest extends TestCase
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
      return "ForEachTag";
   }

   /**
    * Validate the newly created resultant <code>Workbook</code> with JUnit
    * assertions.
    * @param workbook A <code>Workbook</code>.
    */
   protected void check(Workbook workbook)
   {
      Sheet vertVert = workbook.getSheetAt(0);
      assertEquals("Division: Atlantic", getStringCellValue(vertVert, 0, 0));
      assertTrue(isMergedRegionPresent(vertVert, new CellRangeAddress(0, 0, 0, 4)));
      assertEquals("Boston", getStringCellValue(vertVert, 2, 0));
      assertEquals("Raptors", getStringCellValue(vertVert, 6, 1));
      assertEquals("Division: Pacific", getStringCellValue(vertVert, 28, 0));
      // getFillForegroundColor returns zero for XSSFCellStyles!!!
      //assertEquals(IndexedColors.PALE_BLUE.getIndex(), getCell(vertVert, 29, 4).getCellStyle().getFillForegroundColor());
      assertEquals(53, getNumericCellValue(vertVert, 30, 2), DELTA);
      assertEquals(36, getNumericCellValue(vertVert, 31, 3), DELTA);
      assertEquals((double) 32/74, getNumericCellValue(vertVert, 32, 4), DELTA);
      assertEquals("Division: Empty", getStringCellValue(vertVert, 42, 0));
      assertEquals("City", getStringCellValue(vertVert, 43, 0));
      assertTrue(isMergedRegionPresent(vertVert, new CellRangeAddress(44, 44, 0, 4)));
      assertEquals("Division: Of Their Own", getStringCellValue(vertVert, 44, 0));
      assertEquals("Name", getStringCellValue(vertVert, 45, 1));
      assertEquals("Harlem", getStringCellValue(vertVert, 46, 0));
      //assertEquals(IndexedColors.GREY_25_PERCENT.getIndex(), getCell(vertVert, 46, 1).getCellStyle().getFillForegroundColor());
      assertEquals("After", getStringCellValue(vertVert, 47, 0));
      assertEquals("Division: Atlantic", getStringCellValue(vertVert, 48, 0));
      assertTrue(isMergedRegionPresent(vertVert, new CellRangeAddress(48, 48, 0, 4)));
      assertEquals("Boston", getStringCellValue(vertVert, 50, 0));
      assertEquals("Raptors", getStringCellValue(vertVert, 54, 1));
      assertEquals("Division: Pacific", getStringCellValue(vertVert, 76, 0));
      // getFillForegroundColor returns zero for XSSFCellStyles!!!
      //assertEquals(IndexedColors.PALE_BLUE.getIndex(), getCell(vertVert, 77, 4).getCellStyle().getFillForegroundColor());
      assertEquals(53, getNumericCellValue(vertVert, 78, 2), DELTA);
      assertEquals(36, getNumericCellValue(vertVert, 79, 3), DELTA);
      assertEquals((double) 32/74, getNumericCellValue(vertVert, 80, 4), DELTA);
      assertEquals("Division: Empty", getStringCellValue(vertVert, 90, 0));
      assertEquals("City", getStringCellValue(vertVert, 91, 0));
      assertTrue(isMergedRegionPresent(vertVert, new CellRangeAddress(92, 92, 0, 4)));
      assertEquals("Division: Of Their Own", getStringCellValue(vertVert, 92, 0));
      assertEquals("Name", getStringCellValue(vertVert, 93, 1));
      assertEquals("Harlem", getStringCellValue(vertVert, 94, 0));
      //assertEquals(IndexedColors.GREY_25_PERCENT.getIndex(), getCell(vertVert, 94, 1).getCellStyle().getFillForegroundColor());
      assertEquals("After2", getStringCellValue(vertVert, 95, 0));
      assertEquals(16, vertVert.getNumMergedRegions());

      Sheet horizVert = workbook.getSheetAt(1);
      assertEquals("Division: Atlantic", getStringCellValue(horizVert, 0, 0));
      assertTrue(isMergedRegionPresent(horizVert, new CellRangeAddress(0, 0, 0, 4)));
      assertEquals("Boston", getStringCellValue(horizVert, 2, 0));
      assertEquals("Raptors", getStringCellValue(horizVert, 6, 1));
      assertEquals("Division: Pacific", getStringCellValue(horizVert, 0, 20));
      // getFillForegroundColor returns zero for XSSFCellStyles!!!
      //assertEquals(IndexedColors.PALE_BLUE.getIndex(), getCell(horizVert, 1, 24).getCellStyle().getFillForegroundColor());
      assertEquals(53, getNumericCellValue(horizVert, 2, 22), DELTA);
      assertEquals(36, getNumericCellValue(horizVert, 3, 23), DELTA);
      assertEquals((double) 32/74, getNumericCellValue(horizVert, 4, 24), DELTA);
      assertEquals("Division: Empty", getStringCellValue(horizVert, 0, 30));
      assertEquals("City", getStringCellValue(horizVert, 1, 30));
      assertTrue(isMergedRegionPresent(horizVert, new CellRangeAddress(0, 0, 35, 39)));
      assertEquals("Division: Of Their Own", getStringCellValue(horizVert, 0, 35));
      assertEquals("Name", getStringCellValue(horizVert, 1, 36));
      assertEquals("Harlem", getStringCellValue(horizVert, 2, 35));
      //assertEquals(IndexedColors.GREY_25_PERCENT.getIndex(), getCell(horizVert, 2, 35).getCellStyle().getFillForegroundColor());
      assertEquals("After", getStringCellValue(horizVert, 0, 40));
      assertEquals("Division: Atlantic", getStringCellValue(horizVert, 9, 0));
      assertTrue(isMergedRegionPresent(horizVert, new CellRangeAddress(9, 9, 0, 4)));
      assertEquals("Boston", getStringCellValue(horizVert, 11, 0));
      assertEquals("Raptors", getStringCellValue(horizVert, 15, 1));
      assertEquals("Division: Pacific", getStringCellValue(horizVert, 9, 20));
      // getFillForegroundColor returns zero for XSSFCellStyles!!!
      //assertEquals(IndexedColors.PALE_BLUE.getIndex(), getCell(horizVert, 10, 24).getCellStyle().getFillForegroundColor());
      assertEquals(53, getNumericCellValue(horizVert, 11, 22), DELTA);
      assertEquals(36, getNumericCellValue(horizVert, 12, 23), DELTA);
      assertEquals((double) 32/74, getNumericCellValue(horizVert, 13, 24), DELTA);
      assertEquals("Division: Empty", getStringCellValue(horizVert, 9, 30));
      assertEquals("City", getStringCellValue(horizVert, 10, 30));
      assertTrue(isMergedRegionPresent(horizVert, new CellRangeAddress(9, 9, 35, 39)));
      assertEquals("Division: Of Their Own", getStringCellValue(horizVert, 9, 35));
      assertEquals("Name", getStringCellValue(horizVert, 10, 36));
      assertEquals("Harlem", getStringCellValue(horizVert, 11, 35));
      //assertEquals(IndexedColors.GREY_25_PERCENT.getIndex(), getCell(horizVert, 11, 35).getCellStyle().getFillForegroundColor());
      assertEquals("After", getStringCellValue(horizVert, 9, 40));
      assertEquals(16, horizVert.getNumMergedRegions());

      Sheet vertHoriz = workbook.getSheetAt(2);
      assertEquals("Division: Atlantic", getStringCellValue(vertHoriz, 0, 0));
      assertTrue(isMergedRegionPresent(vertHoriz, new CellRangeAddress(0, 4, 0, 0)));
      assertEquals("Boston", getStringCellValue(vertHoriz, 0, 2));
      assertEquals("Raptors", getStringCellValue(vertHoriz, 1, 6));
      assertEquals("Division: Pacific", getStringCellValue(vertHoriz, 20, 0));
      // getFillForegroundColor returns zero for XSSFCellStyles!!!
      //assertEquals(IndexedColors.PALE_BLUE.getIndex(), getCell(vertHoriz, 24, 1).getCellStyle().getFillForegroundColor());
      assertEquals(53, getNumericCellValue(vertHoriz, 22, 2), DELTA);
      assertEquals(36, getNumericCellValue(vertHoriz, 23, 3), DELTA);
      assertEquals((double) 32/74, getNumericCellValue(vertHoriz, 24, 4), DELTA);
      assertEquals("Division: Empty", getStringCellValue(vertHoriz, 30, 0));
      assertEquals("City", getStringCellValue(vertHoriz, 30, 1));
      assertTrue(isMergedRegionPresent(vertHoriz, new CellRangeAddress(35, 39, 0, 0)));
      assertEquals("Division: Of Their Own", getStringCellValue(vertHoriz, 35, 0));
      assertEquals("Name", getStringCellValue(vertHoriz, 36, 1));
      assertEquals("Harlem", getStringCellValue(vertHoriz, 35, 2));
      //assertEquals(IndexedColors.GREY_25_PERCENT.getIndex(), getCell(vertHoriz, 35, 2).getCellStyle().getFillForegroundColor());
      assertEquals("After", getStringCellValue(vertHoriz, 40, 0));
      assertEquals("Division: Atlantic", getStringCellValue(vertHoriz, 0, 9));
      assertTrue(isMergedRegionPresent(vertHoriz, new CellRangeAddress(0, 4, 9, 9)));
      assertEquals("Boston", getStringCellValue(vertHoriz, 0, 11));
      assertEquals("Raptors", getStringCellValue(vertHoriz, 1, 15));
      assertEquals("Division: Pacific", getStringCellValue(vertHoriz, 20, 9));
      // getFillForegroundColor returns zero for XSSFCellStyles!!!
      //assertEquals(IndexedColors.PALE_BLUE.getIndex(), getCell(vertHoriz, 24, 10).getCellStyle().getFillForegroundColor());
      assertEquals(53, getNumericCellValue(vertHoriz, 22, 11), DELTA);
      assertEquals(36, getNumericCellValue(vertHoriz, 23, 12), DELTA);
      assertEquals((double) 32/74, getNumericCellValue(vertHoriz, 24, 13), DELTA);
      assertEquals("Division: Empty", getStringCellValue(vertHoriz, 30, 9));
      assertEquals("City", getStringCellValue(vertHoriz, 30, 10));
      assertTrue(isMergedRegionPresent(vertHoriz, new CellRangeAddress(35, 39, 9, 9)));
      assertEquals("Division: Of Their Own", getStringCellValue(vertHoriz, 35, 9));
      assertEquals("Name", getStringCellValue(vertHoriz, 36, 10));
      assertEquals("Harlem", getStringCellValue(vertHoriz, 35, 11));
      //assertEquals(IndexedColors.GREY_25_PERCENT.getIndex(), getCell(vertHoriz, 35, 11).getCellStyle().getFillForegroundColor());
      assertEquals("After", getStringCellValue(vertHoriz, 40, 9));
      assertEquals(16, vertHoriz.getNumMergedRegions());

      Sheet horizHoriz = workbook.getSheetAt(3);
      assertEquals("Division: Atlantic", getStringCellValue(horizHoriz, 0, 0));
      assertTrue(isMergedRegionPresent(horizHoriz, new CellRangeAddress(0, 4, 0, 0)));
      assertEquals("Boston", getStringCellValue(horizHoriz, 0, 2));
      assertEquals("Raptors", getStringCellValue(horizHoriz, 1, 6));
      assertEquals("Division: Pacific", getStringCellValue(horizHoriz, 0, 28));
      // getFillForegroundColor returns zero for XSSFCellStyles!!!
      //assertEquals(IndexedColors.PALE_BLUE.getIndex(), getCell(horizHoriz, 4, 29).getCellStyle().getFillForegroundColor());
      assertEquals(53, getNumericCellValue(horizHoriz, 2, 30), DELTA);
      assertEquals(36, getNumericCellValue(horizHoriz, 3, 31), DELTA);
      assertEquals((double) 32/74, getNumericCellValue(horizHoriz, 4, 32), DELTA);
      assertEquals("Division: Empty", getStringCellValue(horizHoriz, 0, 42));
      assertEquals("City", getStringCellValue(horizHoriz, 0, 43));
      assertTrue(isMergedRegionPresent(horizHoriz, new CellRangeAddress(0, 4, 44, 44)));
      assertEquals("Division: Of Their Own", getStringCellValue(horizHoriz, 0, 44));
      assertEquals("Name", getStringCellValue(horizHoriz, 1, 45));
      assertEquals("Harlem", getStringCellValue(horizHoriz, 0, 46));
      //assertEquals(IndexedColors.GREY_25_PERCENT.getIndex(), getCell(horizHoriz, 1, 46).getCellStyle().getFillForegroundColor());
      assertEquals("After", getStringCellValue(horizHoriz, 0, 47));
      assertEquals("Division: Atlantic", getStringCellValue(horizHoriz, 0, 48));
      assertTrue(isMergedRegionPresent(horizHoriz, new CellRangeAddress(0, 4, 48, 48)));
      assertEquals("Boston", getStringCellValue(horizHoriz, 0, 50));
      assertEquals("Raptors", getStringCellValue(horizHoriz, 1, 54));
      assertEquals("Division: Pacific", getStringCellValue(horizHoriz, 0, 76));
      // getFillForegroundColor returns zero for XSSFCellStyles!!!
      //assertEquals(IndexedColors.PALE_BLUE.getIndex(), getCell(horizHoriz, 4, 77).getCellStyle().getFillForegroundColor());
      assertEquals(53, getNumericCellValue(horizHoriz, 2, 78), DELTA);
      assertEquals(36, getNumericCellValue(horizHoriz, 3, 79), DELTA);
      assertEquals((double) 32/74, getNumericCellValue(horizHoriz, 4, 80), DELTA);
      assertEquals("Division: Empty", getStringCellValue(horizHoriz, 0, 90));
      assertEquals("City", getStringCellValue(horizHoriz, 0, 91));
      assertTrue(isMergedRegionPresent(horizHoriz, new CellRangeAddress(0, 4, 92, 92)));
      assertEquals("Division: Of Their Own", getStringCellValue(horizHoriz, 0, 92));
      assertEquals("Name", getStringCellValue(horizHoriz, 1, 93));
      assertEquals("Harlem", getStringCellValue(horizHoriz, 0, 94));
      //assertEquals(IndexedColors.GREY_25_PERCENT.getIndex(), getCell(horizHoriz, 1, 94).getCellStyle().getFillForegroundColor());
      assertEquals("After", getStringCellValue(horizHoriz, 0, 95));
      assertEquals(16, horizHoriz.getNumMergedRegions());

      Sheet indexVar = workbook.getSheetAt(4);
      assertEquals("Division: Atlantic", getStringCellValue(indexVar, 0, 0));
      assertEquals("1.", getStringCellValue(indexVar, 2, 0));
      assertEquals("2.", getStringCellValue(indexVar, 3, 0));
      assertEquals("3.", getStringCellValue(indexVar, 4, 0));
      assertEquals("4.", getStringCellValue(indexVar, 5, 0));
      assertEquals("5.", getStringCellValue(indexVar, 6, 0));
      assertEquals("1.", getStringCellValue(indexVar, 9, 0));
      assertEquals("2.", getStringCellValue(indexVar, 10, 0));
      assertEquals("3.", getStringCellValue(indexVar, 11, 0));
      assertEquals("4.", getStringCellValue(indexVar, 12, 0));
      assertEquals("5.", getStringCellValue(indexVar, 13, 0));
      assertEquals("1.", getStringCellValue(indexVar, 46, 0));

      Sheet where = workbook.getSheetAt(5);
      assertEquals("Boston", getStringCellValue(where, 2, 0));
      assertEquals("Philadelphia", getStringCellValue(where, 3, 0));
      assertEquals("Division: Central - Teams Above 0.500", getStringCellValue(where, 4, 0));
      assertEquals("Chicago", getStringCellValue(where, 6, 0));
      assertEquals("Miami", getStringCellValue(where, 9, 0));
      assertEquals("Atlanta", getStringCellValue(where, 11, 0));
      assertEquals("Oklahoma City", getStringCellValue(where, 14, 0));
      assertEquals("Portland", getStringCellValue(where, 16, 0));
      assertEquals("Lakers", getStringCellValue(where, 19, 1));
      assertEquals("San Antonio", getStringCellValue(where, 22, 0));
      assertEquals("Houston", getStringCellValue(where, 26, 0));
      assertEquals("Division: Of Their Own - Teams Above 0.500", getStringCellValue(where, 27, 0));
      assertEquals("Harlem", getStringCellValue(where, 29, 0));
      assertEquals("After", getStringCellValue(where, 30, 0));

      Sheet limit = workbook.getSheetAt(6);
      assertEquals("Celtics", getStringCellValue(limit, 2, 1));
      assertEquals("Knicks", getStringCellValue(limit, 4, 1));
      assertEquals("Bulls", getStringCellValue(limit, 7, 1));
      assertEquals("Bucks", getStringCellValue(limit, 9, 1));
      assertEquals("Heat", getStringCellValue(limit, 12, 1));
      assertEquals("Hawks", getStringCellValue(limit, 14, 1));
      assertEquals("Thunder", getStringCellValue(limit, 17, 1));
      assertEquals("Trailblazers", getStringCellValue(limit, 19, 1));
      assertEquals("Lakers", getStringCellValue(limit, 22, 1));
      assertEquals("Warriors", getStringCellValue(limit, 24, 1));
      assertEquals("Spurs", getStringCellValue(limit, 27, 1));
      assertEquals("Hornets", getStringCellValue(limit, 29, 1));
      assertTrue(isCellBlank(limit, 32, 1));
      assertTrue(isCellBlank(limit, 33, 1));
      assertTrue(isCellBlank(limit, 34, 1));
      assertEquals("Globetrotters", getStringCellValue(limit, 37, 1));
      assertTrue(isCellBlank(limit, 38, 1));
      assertTrue(isCellBlank(limit, 39, 1));
      assertEquals("After", getStringCellValue(limit, 40, 0));
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
      return TestUtility.getDivisionData();
   }
}
package net.sf.jett.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This JUnit Test class tests the evaluation of the "span" tag (always
 * bodiless).
 */
public class SpanTagTest extends TestCase
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
      return "SpanTag";
   }

   /**
    * Validate the newly created resultant <code>Workbook</code> with JUnit
    * assertions.
    * @param workbook A <code>Workbook</code>.
    */
   protected void check(Workbook workbook)
   {
      Sheet vert = workbook.getSheetAt(0);
      assertEquals("Case vert cell factor=3", getStringCellValue(vert, 0, 0));
      assertTrue(isMergedRegionPresent(vert, new CellRangeAddress(0, 2, 0, 0)));
      assertEquals("Case vert row factor=3", getStringCellValue(vert, 0, 1));
      assertTrue(isMergedRegionPresent(vert, new CellRangeAddress(0, 2, 1, 6)));
      assertEquals("Case vert col factor=3", getStringCellValue(vert, 3, 0));
      assertTrue(isMergedRegionPresent(vert, new CellRangeAddress(3, 20, 0, 0)));
      assertEquals("Case vert block factor=3", getStringCellValue(vert, 3, 1));
      assertTrue(isMergedRegionPresent(vert, new CellRangeAddress(3, 20, 1, 6)));
      assertEquals("After1", getStringCellValue(vert, 21, 0));
      assertEquals("After2", getStringCellValue(vert, 21, 6));

      assertEquals("Case vert cell factor=1", getStringCellValue(vert, 22, 0));
      assertFalse(isMergedRegionPresent(vert, new CellRangeAddress(22, 22, 0, 0)));
      assertEquals("Case vert row factor=1", getStringCellValue(vert, 22, 1));
      assertTrue(isMergedRegionPresent(vert, new CellRangeAddress(22, 22, 1, 6)));
      assertEquals("Case vert col factor=1", getStringCellValue(vert, 23, 0));
      assertTrue(isMergedRegionPresent(vert, new CellRangeAddress(23, 28, 0, 0)));
      assertEquals("Case vert block factor=1", getStringCellValue(vert, 23, 1));
      assertTrue(isMergedRegionPresent(vert, new CellRangeAddress(23, 28, 1, 6)));
      assertEquals("After3", getStringCellValue(vert, 29, 0));
      assertEquals("After4", getStringCellValue(vert, 29, 6));

      assertEquals("After5", getStringCellValue(vert, 30, 0));
      assertEquals("After6", getStringCellValue(vert, 30, 6));

      assertEquals(7, vert.getNumMergedRegions());

      Sheet horiz = workbook.getSheetAt(1);
      assertEquals("Case horiz cell factor=3", getStringCellValue(horiz, 0, 0));
      assertTrue(isMergedRegionPresent(horiz, new CellRangeAddress(0, 0, 0, 2)));
      assertEquals("Case horiz row factor=3", getStringCellValue(horiz, 0, 3));
      assertTrue(isMergedRegionPresent(horiz, new CellRangeAddress(0, 0, 3, 20)));
      assertEquals("Case horiz col factor=3", getStringCellValue(horiz, 1, 0));
      assertTrue(isMergedRegionPresent(horiz, new CellRangeAddress(1, 6, 0, 2)));
      assertEquals("Case horiz block factor=3", getStringCellValue(horiz, 1, 3));
      assertTrue(isMergedRegionPresent(horiz, new CellRangeAddress(1, 6, 3, 20)));
      assertEquals("After1", getStringCellValue(horiz, 0, 21));
      assertEquals("After2", getStringCellValue(horiz, 6, 21));

      assertEquals("Case horiz cell factor=1", getStringCellValue(horiz, 0, 22));
      assertFalse(isMergedRegionPresent(horiz, new CellRangeAddress(0, 0, 22, 22)));
      assertEquals("Case horiz row factor=1", getStringCellValue(horiz, 0, 23));
      assertTrue(isMergedRegionPresent(horiz, new CellRangeAddress(0, 0, 23, 28)));
      assertEquals("Case horiz col factor=1", getStringCellValue(horiz, 1, 22));
      assertTrue(isMergedRegionPresent(horiz, new CellRangeAddress(1, 6, 22, 22)));
      assertEquals("Case horiz block factor=1", getStringCellValue(horiz, 1, 23));
      assertTrue(isMergedRegionPresent(horiz, new CellRangeAddress(1, 6, 23, 28)));
      assertEquals("After3", getStringCellValue(horiz, 0, 29));
      assertEquals("After4", getStringCellValue(horiz, 6, 29));

      assertEquals("After5", getStringCellValue(horiz, 0, 30));
      assertEquals("After6", getStringCellValue(horiz, 6, 30));

      assertEquals(7, horiz.getNumMergedRegions());
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
      Map<String, Object> beans = new HashMap<String, Object>();
      beans.put("expand", 3);
      beans.put("nothing", 1);
      beans.put("remove", 0);
      return beans;
   }
}
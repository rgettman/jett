package net.sf.jett.test;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This JUnit Test class tests the evaluation of the "if" tag in entire rows,
 * block area, and bodiless modes.
 */
public class IfTagTest extends TestCase
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
      return "IfTag";
   }

   /**
    * Validate the newly created resultant <code>Workbook</code> with JUnit
    * assertions.
    * @param workbook A <code>Workbook</code>.
    */
   protected void check(Workbook workbook)
   {
      Sheet vertical = workbook.getSheetAt(0);
      assertEquals("This", getStringCellValue(vertical, 1, 0));
      assertEquals("is", getStringCellValue(vertical, 1, 1));
      assertEquals("Block1", getStringCellValue(vertical, 1, 2));
      assertEquals("Merged Region", getStringCellValue(vertical, 1, 3));
      assertTrue(isMergedRegionPresent(vertical, new CellRangeAddress(1, 1, 3, 4)));
      assertEquals("After1", getStringCellValue(vertical, 2, 0));
      assertEquals("After2", getStringCellValue(vertical, 3, 0));
      assertFalse(isMergedRegionPresent(vertical, new CellRangeAddress(3, 3, 3, 4)));
      assertEquals("After3", getStringCellValue(vertical, 4, 0));
      assertTrue(isCellBlank(vertical, 5, 0));
      assertTrue(isCellBlank(vertical, 5, 1));
      assertTrue(isCellBlank(vertical, 5, 2));
      assertTrue(isCellBlank(vertical, 5, 3));
      assertTrue(isCellBlank(vertical, 5, 4));
      assertFalse(isMergedRegionPresent(vertical, new CellRangeAddress(5, 5, 3, 4)));
      assertEquals("After4", getStringCellValue(vertical, 6, 0));
      assertTrue(isCellBlank(vertical, 7, 0));
      assertTrue(isCellBlank(vertical, 7, 1));
      assertTrue(isCellBlank(vertical, 7, 2));
      assertTrue(isCellBlank(vertical, 7, 3));
      assertTrue(isCellBlank(vertical, 7, 4));
      assertTrue(isMergedRegionPresent(vertical, new CellRangeAddress(7, 7, 3, 4)));
      assertEquals("After5", getStringCellValue(vertical, 8, 0));
      assertEquals("After6", getStringCellValue(vertical, 9, 0));
      assertTrue(isCellBlank(vertical, 10, 0));
      assertFalse(isMergedRegionPresent(vertical, new CellRangeAddress(11, 11, 3, 4)));
      assertTrue(isCellBlank(vertical, 11, 0));
      assertEquals("After7", getStringCellValue(vertical, 12, 0));
      assertTrue(isCellBlank(vertical, 13, 0));
      assertTrue(isMergedRegionPresent(vertical, new CellRangeAddress(14, 14, 3, 4)));
      assertTrue(isCellBlank(vertical, 14, 0));
      assertEquals("After8", getStringCellValue(vertical, 15, 0));
      assertTrue(isCellBlank(vertical, 16, 0));
      assertFalse(isMergedRegionPresent(vertical, new CellRangeAddress(17, 17, 3, 4)));
      assertTrue(isCellBlank(vertical, 17, 0));
      assertEquals("After9", getStringCellValue(vertical, 18, 0));
      assertEquals("Entire", getStringCellValue(vertical, 20, 0));
      assertEquals("Rows", getStringCellValue(vertical, 20, 1));
      assertEquals("Block10", getStringCellValue(vertical, 20, 2));
      assertEquals("Merged Region", getStringCellValue(vertical, 20, 3));
      assertTrue(isMergedRegionPresent(vertical, new CellRangeAddress(20, 20, 3, 4)));
      assertEquals("After10", getStringCellValue(vertical, 21, 0));
      assertEquals(4, vertical.getNumMergedRegions());

      Sheet horizontal = workbook.getSheetAt(1);
      assertEquals("This", getStringCellValue(horizontal, 1, 0));
      assertEquals("is", getStringCellValue(horizontal, 1, 1));
      assertEquals("Block1", getStringCellValue(horizontal, 1, 2));
      assertEquals("Merged Region 1", getStringCellValue(horizontal, 1, 3));
      assertTrue(isMergedRegionPresent(horizontal, new CellRangeAddress(1, 1, 3, 4)));
      assertEquals("!", getStringCellValue(horizontal, 1, 5));
      assertEquals("After1", getStringCellValue(horizontal, 1, 6));
      assertEquals("This", getStringCellValue(horizontal, 2, 0));
      assertEquals("is a", getStringCellValue(horizontal, 2, 1));
      assertEquals("multi-row", getStringCellValue(horizontal, 2, 2));
      assertEquals("Merged Region 2", getStringCellValue(horizontal, 2, 3));
      assertTrue(isMergedRegionPresent(horizontal, new CellRangeAddress(2, 3, 3, 4)));
      assertEquals("Right", getStringCellValue(horizontal, 2, 5));
      assertEquals("After1", getStringCellValue(horizontal, 2, 6));
      assertEquals("block", getStringCellValue(horizontal, 3, 0));
      assertEquals("area", getStringCellValue(horizontal, 3, 1));
      assertEquals("block.", getStringCellValue(horizontal, 3, 2));
      assertEquals("!", getStringCellValue(horizontal, 3, 5));
      assertEquals("After1", getStringCellValue(horizontal, 3, 6));
      assertEquals("After2", getStringCellValue(horizontal, 4, 0));
      assertEquals("After4", getStringCellValue(horizontal, 5, 0));
      assertTrue(isCellBlank(horizontal, 5, 3));
      assertEquals("After3", getStringCellValue(horizontal, 5, 6));
      assertFalse(isMergedRegionPresent(horizontal, new CellRangeAddress(5, 5, 3, 4)));
      assertEquals("After6", getStringCellValue(horizontal, 6, 0));
      assertTrue(isCellBlank(horizontal, 6, 3));
      assertEquals("After3", getStringCellValue(horizontal, 6, 6));
      assertEquals("After3", getStringCellValue(horizontal, 7, 0));
      assertTrue(isCellBlank(horizontal, 8, 0));
      assertEquals("After5", getStringCellValue(horizontal, 9, 0));
      assertTrue(isCellBlank(horizontal, 9, 6));
      assertEquals("After8", getStringCellValue(horizontal, 10, 0));
      assertTrue(isCellBlank(horizontal, 10, 3));
      assertEquals("After5", getStringCellValue(horizontal, 10, 6));
      assertTrue(isCellBlank(horizontal, 11, 0));
      assertTrue(isCellBlank(horizontal, 11, 3));
      assertTrue(isMergedRegionPresent(horizontal, new CellRangeAddress(11, 11, 3, 4)));
      assertTrue(isCellBlank(horizontal, 12, 0));
      assertTrue(isCellBlank(horizontal, 12, 3));
      assertTrue(isMergedRegionPresent(horizontal, new CellRangeAddress(12, 13, 3, 4)));
      assertTrue(isCellBlank(horizontal, 13, 0));
      assertEquals("After7", getStringCellValue(horizontal, 13, 6));
      assertEquals("After10", getStringCellValue(horizontal, 14, 0));
      assertEquals("After7", getStringCellValue(horizontal, 14, 6));
      assertTrue(isCellBlank(horizontal, 15, 0));
      assertEquals("Top", getStringCellValue(horizontal, 15, 2));
      assertEquals("After7", getStringCellValue(horizontal, 15, 6));
      assertEquals("Showme1", getStringCellValue(horizontal, 16, 1));
      assertEquals("Showme2", getStringCellValue(horizontal, 16, 2));
      assertEquals("Showme3", getStringCellValue(horizontal, 16, 3));
      assertTrue(isCellBlank(horizontal, 16, 6));
      assertEquals("Left", getStringCellValue(horizontal, 17, 0));
      assertEquals("Showme4", getStringCellValue(horizontal, 17, 1));
      assertEquals("Showme5", getStringCellValue(horizontal, 17, 2));
      assertEquals("Showme6", getStringCellValue(horizontal, 17, 3));
      assertEquals("Right", getStringCellValue(horizontal, 17, 4));
      assertEquals("After9", getStringCellValue(horizontal, 17, 6));
      assertEquals("Showme7", getStringCellValue(horizontal, 18, 1));
      assertEquals("Showme8", getStringCellValue(horizontal, 18, 2));
      assertEquals("Showme9", getStringCellValue(horizontal, 18, 3));
      assertEquals("After9", getStringCellValue(horizontal, 18, 6));
      assertEquals("Bottom", getStringCellValue(horizontal, 19, 2));
      assertEquals("Corner", getStringCellValue(horizontal, 19, 4));
      assertEquals("After9", getStringCellValue(horizontal, 19, 6));
      assertEquals("After11", getStringCellValue(horizontal, 20, 0));
      assertEquals("Bottom", getStringCellValue(horizontal, 21, 1));
      assertFalse(isMergedRegionPresent(horizontal, new CellRangeAddress(21, 22, 1, 2)));
      assertTrue(isCellBlank(horizontal, 21, 3));
      assertEquals("After12", getStringCellValue(horizontal, 22, 0));
      assertTrue(isCellBlank(horizontal, 23, 1));
      assertTrue(isCellBlank(horizontal, 23, 3));
      assertTrue(isCellBlank(horizontal, 24, 0));
      assertFalse(isMergedRegionPresent(horizontal, new CellRangeAddress(23, 24, 1, 2)));
      assertEquals("Bottom", getStringCellValue(horizontal, 25, 1));
      assertEquals("After13", getStringCellValue(horizontal, 26, 0));
      assertTrue(isCellBlank(horizontal, 27, 1));
      assertTrue(isMergedRegionPresent(horizontal, new CellRangeAddress(27, 28, 1, 2)));
      assertTrue(isCellBlank(horizontal, 27, 3));
      assertEquals("Bottom", getStringCellValue(horizontal, 29, 1));
      assertEquals("After14", getStringCellValue(horizontal, 30, 0));
      assertEquals(5, horizontal.getNumMergedRegions());

      Sheet bodiless = workbook.getSheetAt(2);
      assertEquals("I'm true!", getStringCellValue(bodiless, 1, 0));
      assertEquals("Right1", getStringCellValue(bodiless, 1, 1));
      assertEquals("I'm false!", getStringCellValue(bodiless, 2, 0));
      assertEquals("Right2", getStringCellValue(bodiless, 2, 1));
      assertEquals("I'm true!", getStringCellValue(bodiless, 3, 0));
      assertEquals("Right3", getStringCellValue(bodiless, 3, 1));
      assertTrue(isCellBlank(bodiless, 4, 0));
      assertEquals("Right4", getStringCellValue(bodiless, 4, 1));
      assertEquals("After", getStringCellValue(bodiless, 5, 0));
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
      beans.put("condTrue", true);
      beans.put("condFalse", false);

      return beans;
   }
}
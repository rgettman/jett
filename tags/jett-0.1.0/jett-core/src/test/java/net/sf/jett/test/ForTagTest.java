package net.sf.jett.test;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This JUnit Test class tests the evaluation of the "for" tag in various
 * cases.
 */
public class ForTagTest extends TestCase
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
      return "ForTag";
   }

   /**
    * Validate the newly created resultant <code>Workbook</code> with JUnit
    * assertions.
    * @param workbook A <code>Workbook</code>.
    */
   protected void check(Workbook workbook)
   {
      Sheet multiplication = workbook.getSheetAt(0);
      for (int c = 1; c < 20; c++)
      {
         assertEquals(c, getNumericCellValue(multiplication, 0, c), DELTA);
      }
      assertTrue(isCellBlank(multiplication, 0, 21));
      for (int r = 1; r < 20; r++)
      {
         assertEquals(r, getNumericCellValue(multiplication, r, 0), DELTA);
         for (int c = 1; c < 20; c++)
         {
            assertEquals(c * r, getNumericCellValue(multiplication, r, c), DELTA);
         }
         assertTrue(isCellBlank(multiplication, 4, 21));
      }
      for (int c = 0; c < 21; c++)
      {
         assertTrue(isCellBlank(multiplication, 21, c));
      }

      Sheet oneOrZero = workbook.getSheetAt(1);
      assertEquals(23, getNumericCellValue(oneOrZero, 0, 0), DELTA);
      assertEquals("is the only element!", getStringCellValue(oneOrZero, 0, 1));
      assertEquals("After", getStringCellValue(oneOrZero, 1, 0));
      assertEquals("After2", getStringCellValue(oneOrZero, 2, 0));

      Sheet end = workbook.getSheetAt(2);
      for (int c = 1; c < 27; c++)
      {
         int x = 100 - 4 * (c - 1);
         assertEquals(x, getNumericCellValue(end, 0, c), DELTA);
         assertEquals(x * x, getNumericCellValue(end, 1, c), DELTA);
      }
      assertTrue(isCellBlank(end, 0, 27));
      for (int c = 1; c < 21; c++)
      {
         int y = 1 + 5 * (c - 1);
         boolean isMultOf3 = (y % 3) == 0;
         assertEquals(y, getNumericCellValue(end, 3, c), DELTA);
         if (isMultOf3)
            assertEquals("true", getStringCellValue(end, 4, c));
         else
            assertEquals("false", getStringCellValue(end, 4, c));
      }
      assertTrue(isCellBlank(end, 3, 21));
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
      return new HashMap<String, Object>();
   }
}

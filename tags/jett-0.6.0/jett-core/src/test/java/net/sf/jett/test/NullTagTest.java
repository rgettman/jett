package net.sf.jett.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jett.util.RichTextStringUtil;

/**
 * This JUnit Test class tests the evaluation of the "null" tag in bodiless and
 * normal modes.
 *
 * @author Randy Gettman
 */
public class NullTagTest extends TestCase
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
      return "NullTag";
   }

   /**
    * Validate the newly created resultant <code>Workbook</code> with JUnit
    * assertions.
    * @param workbook A <code>Workbook</code>.
    */
   protected void check(Workbook workbook)
   {
      Sheet sheet = workbook.getSheetAt(0);
      assertEquals("${doesNotExist}", TestUtility.getStringCellValue(sheet, 2, 0));
      RichTextString rts = TestUtility.getRichTextStringCellValue(sheet, 2, 0);
      Font font = TestUtility.convertToFont(RichTextStringUtil.getFontAtIndex(rts, 0), workbook);
      assertEquals("ff0000", TestUtility.getFontColorString(workbook, font));
      assertEquals("${doesNotExistEither}", TestUtility.getStringCellValue(sheet, 4, 0));
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
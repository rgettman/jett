package net.sf.jett.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.RichTextString;
import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jett.test.model.Team;

/**
 * This JUnit Test class tests the evaluation of expressions and replacement
 * in spreadsheet cells.
 */
public class ExpressionsTest extends TestCase
{
   /**
    * Tests the .xls template spreadsheet.
    * @throws IOException If an I/O error occurs.
    * @throws InvalidFormatException If the input spreadsheet is invalid.
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
      return "ExprTest";
   }

   /**
    * Validate the newly created resultant <code>Workbook</code> with JUnit
    * assertions.
    * @param workbook A <code>Workbook</code>.
    */
   protected void check(Workbook workbook)
   {
      Sheet sheet = workbook.getSheetAt(0);
      assertEquals("Cell A3", getStringCellValue(sheet, 2, 0));
      assertEquals(3.14, getNumericCellValue(sheet, 2, 2), DELTA);
      assertEquals("Hello World!", getStringCellValue(sheet, 3, 1));
      assertEquals("JETT", getStringCellValue(sheet, 4, 1));
      assertEquals("JETT: Hello World!", getStringCellValue(sheet, 5, 1));
      assertEquals("Springfield", getStringCellValue(sheet, 7, 2));
      assertEquals("Isotopes", getStringCellValue(sheet, 8, 2));
      assertEquals(38, getNumericCellValue(sheet, 9, 2), DELTA);
      assertEquals(4, getNumericCellValue(sheet, 10, 2), DELTA);
      double numberListAvg = (double) (1 + 3 + 7 + 10 + 11 + 23) / 6;
      assertEquals(numberListAvg, getNumericCellValue(sheet, 12, 1), DELTA);
                  //         10        20        30        40        50        60        70        80        90       100
                  //01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789
      assertEquals("I can use bold, italic, underline, strikeout, different fonts, superscript, and subscript within one cell!",
         getStringCellValue(sheet, 14, 0));
      RichTextString rts = getRichTextStringCellValue(sheet, 14, 0);
      assertEquals(106, rts.length());

      int formattingRunIndices[] = new int[]
       // Bold  , Italic, Underline, Strikeout, Fonts , Superscript, Subscript, One
         {10, 14, 16, 22, 24, 33   , 35, 44   , 56, 61, 63, 74     , 80, 89   , 97, 100};
      
      // HSSF (.xls) does not count the initial run as a different formatting
      // run; it is the formatting of the actual Cell.
      int adjust;
      if (sheet instanceof HSSFSheet)
      {
         adjust = 0;
      }
      else
      {
         // XSSFSheet
         adjust = 1;
         assertEquals(0, rts.getIndexOfFormattingRun(0));
      }
      assertEquals(16 + adjust, rts.numFormattingRuns());
      for (int i = 0; i < formattingRunIndices.length; i++)
      {
         assertEquals(formattingRunIndices[i], rts.getIndexOfFormattingRun(i + adjust));
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
      Map<String, Object> beans = new HashMap<String, Object>();
      beans.put("testBean1", "Hello World!");
      beans.put("testBean2", "JETT");
      Team team = new Team();
      team.setName("Isotopes");
      team.setCity("Springfield");
      team.setWins(38);
      team.setLosses(4);
      beans.put("team", team);
      List<Integer> numberList = new ArrayList<Integer>();
      numberList.add(1);
      numberList.add(3);
      numberList.add(7);
      numberList.add(10);
      numberList.add(11);
      numberList.add(23);
      beans.put("numberList", numberList);
      beans.put("feat1", "bold");
      beans.put("feat2", "italic");
      beans.put("feat3", "underline");
      beans.put("feat4", "strikeout");
      beans.put("feat5", "fonts");
      beans.put("feat6", "superscript");
      beans.put("feat7", "subscript");
      beans.put("feat8", "one");

      return beans;
   }
}
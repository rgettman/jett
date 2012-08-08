package net.sf.jett.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jett.transform.ExcelTransformer;

/**
 * This JUnit Test class tests the Formulas feature of JETT.
 */
public class FormulaTest extends TestCase
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
      return "Formula";
   }

   /**
    * Call certain setup-related methods on the <code>ExcelTransformer</code>
    * before template sheet transformation.
    * @param transformer The <code>ExcelTransformer</code> that will transform
    *    the template worksheet(s).
    */
   protected void setupTransformer(ExcelTransformer transformer)
   {
   }

   /**
    * Validate the newly created resultant <code>Workbook</code> with JUnit
    * assertions.
    * @param workbook A <code>Workbook</code>.
    */
   protected void check(Workbook workbook)
   {
      Sheet formulaTest = workbook.getSheetAt(0);
      assertEquals("SUM(B3:B60)", TestUtility.getFormulaCellValue(formulaTest, 60, 1));
      assertEquals("SUM(C3:C60)", TestUtility.getFormulaCellValue(formulaTest, 60, 2));
      assertEquals("\"Counties: \"&COUNTA(E3:E60)", TestUtility.getFormulaCellValue(formulaTest, 60, 4));
      assertEquals("SUM(H3:H60)", TestUtility.getFormulaCellValue(formulaTest, 60, 7));
      assertEquals("SUM(I3:I60)", TestUtility.getFormulaCellValue(formulaTest, 60, 8));
      assertEquals("\"Counties: \"&COUNTA(K3:K60)", TestUtility.getFormulaCellValue(formulaTest, 60, 10));

      for (int i = 1; i <= 6; i++)
      {
         Sheet division = workbook.getSheetAt(i);
         assertEquals("SUM(C3,D3)", TestUtility.getFormulaCellValue(division, 2, 4));
         assertEquals("SUM(C4,D4)", TestUtility.getFormulaCellValue(division, 3, 4));
         assertEquals("SUM(C5,D5)", TestUtility.getFormulaCellValue(division, 4, 4));
         assertEquals("SUM(C6,D6)", TestUtility.getFormulaCellValue(division, 5, 4));
         assertEquals("SUM(C7,D7)", TestUtility.getFormulaCellValue(division, 6, 4));
         assertEquals("COUNTA(B3:B7)", TestUtility.getFormulaCellValue(division, 7, 1));
         assertEquals("SUM(C3:C7)", TestUtility.getFormulaCellValue(division, 7, 2));
         assertEquals("SUM(D3:D7)", TestUtility.getFormulaCellValue(division, 7, 3));
         assertEquals("SUM(C3:C7,D3:D7)", TestUtility.getFormulaCellValue(division, 7, 4));
         assertEquals("SUM(C3:C7)/SUM(E3:E7)", TestUtility.getFormulaCellValue(division, 7, 5));
      }

      Sheet empty = workbook.getSheetAt(7);
      assertEquals("COUNTA($Z$1)", TestUtility.getFormulaCellValue(empty, 2, 1));
      assertEquals("SUM(0)", TestUtility.getFormulaCellValue(empty, 2, 2));
      assertEquals("SUM(0)", TestUtility.getFormulaCellValue(empty, 2, 3));
      assertEquals("SUM(0,0)", TestUtility.getFormulaCellValue(empty, 2, 4));
      assertEquals("SUM(0)/SUM(1)", TestUtility.getFormulaCellValue(empty, 2, 5));

      Sheet ofTheirOwn = workbook.getSheetAt(8);
      assertEquals("SUM(C3,D3)", TestUtility.getFormulaCellValue(ofTheirOwn, 2, 4));
      assertEquals("COUNTA(B3)", TestUtility.getFormulaCellValue(ofTheirOwn, 3, 1));
      assertEquals("SUM(C3)", TestUtility.getFormulaCellValue(ofTheirOwn, 3, 2));
      assertEquals("SUM(D3)", TestUtility.getFormulaCellValue(ofTheirOwn, 3, 3));
      assertEquals("SUM(C3,D3)", TestUtility.getFormulaCellValue(ofTheirOwn, 3, 4));
      assertEquals("SUM(C3)/SUM(E3)", TestUtility.getFormulaCellValue(ofTheirOwn, 3, 5));

      Sheet multiLevel = workbook.getSheetAt(9);
      assertEquals("COUNTA('Formula Test'!E3:E60)", TestUtility.getFormulaCellValue(multiLevel, 0, 8));
      assertEquals("COUNTA('Formula Test'!K3:K60)", TestUtility.getFormulaCellValue(multiLevel, 1, 8));

      assertEquals("SUM(C3,D3)", TestUtility.getFormulaCellValue(multiLevel, 2, 4));
      assertEquals("SUM(C4,D4)", TestUtility.getFormulaCellValue(multiLevel, 3, 4));
      assertEquals("SUM(C5,D5)", TestUtility.getFormulaCellValue(multiLevel, 4, 4));
      assertEquals("SUM(C6,D6)", TestUtility.getFormulaCellValue(multiLevel, 5, 4));
      assertEquals("SUM(C7,D7)", TestUtility.getFormulaCellValue(multiLevel, 6, 4));
      assertEquals("COUNTA(B3:B7)", TestUtility.getFormulaCellValue(multiLevel, 7, 1));
      assertEquals("SUM(C3:C7)", TestUtility.getFormulaCellValue(multiLevel, 7, 2));
      assertEquals("SUM(D3:D7)", TestUtility.getFormulaCellValue(multiLevel, 7, 3));
      assertEquals("SUM(E3:E7)", TestUtility.getFormulaCellValue(multiLevel, 7, 4));
      assertEquals("SUM(C3:C7)/SUM(E3:E7)", TestUtility.getFormulaCellValue(multiLevel, 7, 5));

      assertEquals("SUM(C11,D11)", TestUtility.getFormulaCellValue(multiLevel, 10, 4));
      assertEquals("SUM(C12,D12)", TestUtility.getFormulaCellValue(multiLevel, 11, 4));
      assertEquals("SUM(C13,D13)", TestUtility.getFormulaCellValue(multiLevel, 12, 4));
      assertEquals("SUM(C14,D14)", TestUtility.getFormulaCellValue(multiLevel, 13, 4));
      assertEquals("SUM(C15,D15)", TestUtility.getFormulaCellValue(multiLevel, 14, 4));
      assertEquals("COUNTA(B11:B15)", TestUtility.getFormulaCellValue(multiLevel, 15, 1));
      assertEquals("SUM(C11:C15)", TestUtility.getFormulaCellValue(multiLevel, 15, 2));
      assertEquals("SUM(D11:D15)", TestUtility.getFormulaCellValue(multiLevel, 15, 3));
      assertEquals("SUM(E11:E15)", TestUtility.getFormulaCellValue(multiLevel, 15, 4));
      assertEquals("SUM(C11:C15)/SUM(E11:E15)", TestUtility.getFormulaCellValue(multiLevel, 15, 5));

      assertEquals("SUM(C19,D19)", TestUtility.getFormulaCellValue(multiLevel, 18, 4));
      assertEquals("SUM(C20,D20)", TestUtility.getFormulaCellValue(multiLevel, 19, 4));
      assertEquals("SUM(C21,D21)", TestUtility.getFormulaCellValue(multiLevel, 20, 4));
      assertEquals("SUM(C22,D22)", TestUtility.getFormulaCellValue(multiLevel, 21, 4));
      assertEquals("SUM(C23,D23)", TestUtility.getFormulaCellValue(multiLevel, 22, 4));
      assertEquals("COUNTA(B19:B23)", TestUtility.getFormulaCellValue(multiLevel, 23, 1));
      assertEquals("SUM(C19:C23)", TestUtility.getFormulaCellValue(multiLevel, 23, 2));
      assertEquals("SUM(D19:D23)", TestUtility.getFormulaCellValue(multiLevel, 23, 3));
      assertEquals("SUM(E19:E23)", TestUtility.getFormulaCellValue(multiLevel, 23, 4));
      assertEquals("SUM(C19:C23)/SUM(E19:E23)", TestUtility.getFormulaCellValue(multiLevel, 23, 5));

      assertEquals("SUM(C27,D27)", TestUtility.getFormulaCellValue(multiLevel, 26, 4));
      assertEquals("SUM(C28,D28)", TestUtility.getFormulaCellValue(multiLevel, 27, 4));
      assertEquals("SUM(C29,D29)", TestUtility.getFormulaCellValue(multiLevel, 28, 4));
      assertEquals("SUM(C30,D30)", TestUtility.getFormulaCellValue(multiLevel, 29, 4));
      assertEquals("SUM(C31,D31)", TestUtility.getFormulaCellValue(multiLevel, 30, 4));
      assertEquals("COUNTA(B27:B31)", TestUtility.getFormulaCellValue(multiLevel, 31, 1));
      assertEquals("SUM(C27:C31)", TestUtility.getFormulaCellValue(multiLevel, 31, 2));
      assertEquals("SUM(D27:D31)", TestUtility.getFormulaCellValue(multiLevel, 31, 3));
      assertEquals("SUM(E27:E31)", TestUtility.getFormulaCellValue(multiLevel, 31, 4));
      assertEquals("SUM(C27:C31)/SUM(E27:E31)", TestUtility.getFormulaCellValue(multiLevel, 31, 5));

      assertEquals("SUM(C35,D35)", TestUtility.getFormulaCellValue(multiLevel, 34, 4));
      assertEquals("SUM(C36,D36)", TestUtility.getFormulaCellValue(multiLevel, 35, 4));
      assertEquals("SUM(C37,D37)", TestUtility.getFormulaCellValue(multiLevel, 36, 4));
      assertEquals("SUM(C38,D38)", TestUtility.getFormulaCellValue(multiLevel, 37, 4));
      assertEquals("SUM(C39,D39)", TestUtility.getFormulaCellValue(multiLevel, 38, 4));
      assertEquals("COUNTA(B35:B39)", TestUtility.getFormulaCellValue(multiLevel, 39, 1));
      assertEquals("SUM(C35:C39)", TestUtility.getFormulaCellValue(multiLevel, 39, 2));
      assertEquals("SUM(D35:D39)", TestUtility.getFormulaCellValue(multiLevel, 39, 3));
      assertEquals("SUM(E35:E39)", TestUtility.getFormulaCellValue(multiLevel, 39, 4));
      assertEquals("SUM(C35:C39)/SUM(E35:E39)", TestUtility.getFormulaCellValue(multiLevel, 39, 5));

      assertEquals("SUM(C43,D43)", TestUtility.getFormulaCellValue(multiLevel, 42, 4));
      assertEquals("SUM(C44,D44)", TestUtility.getFormulaCellValue(multiLevel, 43, 4));
      assertEquals("SUM(C45,D45)", TestUtility.getFormulaCellValue(multiLevel, 44, 4));
      assertEquals("SUM(C46,D46)", TestUtility.getFormulaCellValue(multiLevel, 45, 4));
      assertEquals("SUM(C47,D47)", TestUtility.getFormulaCellValue(multiLevel, 46, 4));
      assertEquals("COUNTA(B43:B47)", TestUtility.getFormulaCellValue(multiLevel, 47, 1));
      assertEquals("SUM(C43:C47)", TestUtility.getFormulaCellValue(multiLevel, 47, 2));
      assertEquals("SUM(D43:D47)", TestUtility.getFormulaCellValue(multiLevel, 47, 3));
      assertEquals("SUM(E43:E47)", TestUtility.getFormulaCellValue(multiLevel, 47, 4));
      assertEquals("SUM(C43:C47)/SUM(E43:E47)", TestUtility.getFormulaCellValue(multiLevel, 47, 5));

      assertEquals("COUNTA($Z$1)", TestUtility.getFormulaCellValue(multiLevel, 50, 1));
      assertEquals("SUM(0)", TestUtility.getFormulaCellValue(multiLevel, 50, 2));
      assertEquals("SUM(0)", TestUtility.getFormulaCellValue(multiLevel, 50, 3));
      assertEquals("SUM(0)", TestUtility.getFormulaCellValue(multiLevel, 50, 4));
      assertEquals("SUM(0)/SUM(1)", TestUtility.getFormulaCellValue(multiLevel, 50, 5));

      assertEquals("SUM(C54,D54)", TestUtility.getFormulaCellValue(multiLevel, 53, 4));
      assertEquals("COUNTA(B54)", TestUtility.getFormulaCellValue(multiLevel, 54, 1));
      assertEquals("SUM(C54)", TestUtility.getFormulaCellValue(multiLevel, 54, 2));
      assertEquals("SUM(D54)", TestUtility.getFormulaCellValue(multiLevel, 54, 3));
      assertEquals("SUM(E54)", TestUtility.getFormulaCellValue(multiLevel, 54, 4));
      assertEquals("SUM(C54)/SUM(E54)", TestUtility.getFormulaCellValue(multiLevel, 54, 5));

      assertEquals("COUNTA(B3:B7,B11:B15,B19:B23,B27:B31,B35:B39,B43:B47,B54)", TestUtility.getFormulaCellValue(multiLevel, 55, 1));
      assertEquals("SUM(C3:C7,C11:C15,C19:C23,C27:C31,C35:C39,C43:C47,C54)", TestUtility.getFormulaCellValue(multiLevel, 55, 2));
      assertEquals("SUM(D3:D7,D11:D15,D19:D23,D27:D31,D35:D39,D43:D47,D54)", TestUtility.getFormulaCellValue(multiLevel, 55, 3));
      assertEquals("SUM(E3:E7,E11:E15,E19:E23,E27:E31,E35:E39,E43:E47,E54)", TestUtility.getFormulaCellValue(multiLevel, 55, 4));
      assertEquals("SUM(C3:C7,C11:C15,C19:C23,C27:C31,C35:C39,C43:C47,C54)/SUM(E3:E7,E11:E15,E19:E23,E27:E31,E35:E39,E43:E47,E54)",
         TestUtility.getFormulaCellValue(multiLevel, 55, 5));
   }

   /**
    * This test is a single map test.
    * @return <code>false</code>.
    */
   protected boolean isMultipleBeans()
   {
      return true;
   }

/**
    * For multiple beans map tests, return the <code>List</code> of template
    * sheet names.
    * @return A <code>List</code> of template sheet names.
    */
   protected List<String> getListOfTemplateSheetNames()
   {
      String[] templateSheetNameArray = new String[10];
      Arrays.fill(templateSheetNameArray, "Cloning");
      templateSheetNameArray[0] = "Formula Test";
      templateSheetNameArray[9] = "MultiLevel";
      return Arrays.asList(templateSheetNameArray);
   }

   /**
    * For multiple beans map tests, return the <code>List</code> of result
    * sheet names.
    * @return A <code>List</code> of result sheet names.
    */
   protected List<String> getListOfResultSheetNames()
   {
      return Arrays.asList("Formula Test", "Atlantic", "Central", "Southeast", "Northwest",
         "Pacific", "Southwest", "Empty", "Of Their Own", "MultiLevel");
   }

   /**
    * For multiple beans map tests, return the <code>List</code> of beans maps,
    * which map bean names to bean values for each corresponsing sheet.
    * @return A <code>List</code> of <code>Maps</code> of bean names to bean
    *    values.
    */
   protected List<Map<String, Object>> getListOfBeansMaps()
   {
      List<Map<String, Object>> beansList = new ArrayList<Map<String, Object>>();
      beansList.add(TestUtility.getStateData());
      for (int i = 0; i < 8; i++)
         beansList.add(TestUtility.getSpecificDivisionData(i));
      beansList.add(TestUtility.getDivisionData());
      return beansList;
   }
}

package net.sf.jett.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jett.jdbc.JDBCExecutor;
import net.sf.jett.test.jdbc.MockConnection;

/**
 * This JUnit Test class tests the <code>JDBCExecutor</code> and
 * <code>ResultSetRow</code> classes.
 *
 * @author Randy Gettman
 * @since 0.6.0
 */
public class JDBCExecutorTest extends TestCase
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
      return "JDBCExecutor";
   }

   /**
    * Validate the newly created resultant <code>Workbook</code> with JUnit
    * assertions.
    * @param workbook A <code>Workbook</code>.
    */
   protected void check(Workbook workbook)
   {
      // To test the JDBCExecutor, I didn't bundle a DB along with drivers.
      // I minimally implemented some JDBC interfaces in net.sf.jett.test.jdbc
      // here in test/java.
      Sheet query = workbook.getSheetAt(0);
      assertEquals("Robert", TestUtility.getStringCellValue(query, 1, 0));
      assertEquals("Queue", TestUtility.getStringCellValue(query, 2, 1));
      assertEquals(800, TestUtility.getNumericCellValue(query, 3, 2), Double.MIN_VALUE);
      assertEquals("Cartoon Character", TestUtility.getStringCellValue(query, 4, 3));
      assertTrue(TestUtility.isCellBlank(query, 1, 4));
      assertEquals("I'm hunting wabbits!  Huh-uh-uh!", TestUtility.getStringCellValue(query, 3, 5));
      assertTrue(TestUtility.getBooleanCellValue(query, 4, 6));

      Sheet prepared = workbook.getSheetAt(1);
      assertEquals("Cartoon Character", TestUtility.getStringCellValue(prepared, 0, 0));
      assertEquals("Cartoon Character", TestUtility.getStringCellValue(prepared, 2, 3));
      assertEquals("Cartoon Character", TestUtility.getStringCellValue(prepared, 3, 3));
      assertEquals("Data Structures Programmer", TestUtility.getStringCellValue(prepared, 4, 0));
      assertEquals("Data Structures Programmer", TestUtility.getStringCellValue(prepared, 6, 3));
      assertEquals("Data Structures Programmer", TestUtility.getStringCellValue(prepared, 7, 3));
      assertEquals("Nonexistent Title", TestUtility.getStringCellValue(prepared, 8, 0));
      assertTrue(TestUtility.isCellBlank(prepared, 10, 3));
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
      Connection conn = new MockConnection();
      JDBCExecutor jdbc = new JDBCExecutor(conn);
      beans.put("jdbc", jdbc);

      List<String> titleSearches = Arrays.asList("Cartoon Character", "Data Structures Programmer", "Nonexistent Title");
      beans.put("titleSearches", titleSearches);
      return beans;
   }
}

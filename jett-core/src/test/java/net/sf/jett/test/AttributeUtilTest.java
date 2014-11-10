package net.sf.jett.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import net.sf.jett.exception.AttributeExpressionException;
import net.sf.jett.test.model.Division;
import net.sf.jett.test.model.Employee;
import net.sf.jett.util.AttributeUtil;

/**
 * Tests the <code>AttributeUtil</code> class.
 *
 * @author Randy Gettman
 * @since 0.6.0
 */
public class AttributeUtilTest
{
   private Map<String, Object> myBeans;

   /**
    * We don't have a <code>Workbook</code> here, but we can create our own
    * <code>RichTextString</code>.
    */
   private static class TestCreationHelper implements CreationHelper
   {
      public ClientAnchor createClientAnchor() { return null; }
      public DataFormat createDataFormat() { return null; }
      public FormulaEvaluator createFormulaEvaluator() { return null; }
      public Hyperlink createHyperlink(int type) { return null; }
      public RichTextString createRichTextString(String text)
      {
         return new XSSFRichTextString(text);
      }
   }


   /**
    * Set up by creating the beans map.
    */
   @Before
   public void setup()
   {
      myBeans = new HashMap<String, Object>();
      myBeans.put("t", true);
      myBeans.put("f", false);
      myBeans.put("answer", 42);
      myBeans.put("zero", 0);
      myBeans.put("isquared", -1);
      myBeans.put("question", 8.6);
      myBeans.put("project", "JETT");
      myBeans.put("null", null);
      Employee bugs = new Employee();
      bugs.setFirstName("Bugs");
      bugs.setLastName("Bunny");
      bugs.setSalary(1500);
      myBeans.put("bugs", bugs);
      myBeans.put("acronym", Arrays.asList("Java", "Excel", "Template", "Translator"));
      myBeans.put("integerArray", new Integer[] {4, 8, 15, 16, 23, 42});
      myBeans.put("integerArrayArray", new Integer[][] {new Integer[] {4, 8}, new Integer[] {15, 16, 23}, new Integer[] {42}});
   }

   /**
    * Make sure it's evaluated as <code>true</code>.
    */
   @Test
   public void testBooleanTrue()
   {
      assertTrue(AttributeUtil.evaluateBoolean(null, new XSSFRichTextString("${t}"), myBeans, false));
   }

   /**
    * Make sure it's evaluated as <code>false</code>.
    */
   @Test
   public void testBooleanFalse()
   {
      assertFalse(AttributeUtil.evaluateBoolean(null, new XSSFRichTextString("${f}"), myBeans, true));
   }

   /**
    * Make sure that a bad expression with an undefined variable yields an
    * <code>AttributeExpressionException</code>.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testBooleanDNE()
   {
      AttributeUtil.evaluateBoolean(null, new XSSFRichTextString("${dne}"), myBeans, true);
   }

   /**
    * Tests integer resolution.
    */
   @Test
   public void testEvaluateInt()
   {
      assertEquals(42, AttributeUtil.evaluateInt(null, new XSSFRichTextString("${answer}"), myBeans, "attr_name", 0));
   }

   /**
    * Proper exception must be thrown for unparseable integer.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateIntBad()
   {
      AttributeUtil.evaluateInt(null, new XSSFRichTextString("${t}"), myBeans, "attr_name", 0);
   }

   /**
    * Make sure that a bad expression with an undefined variable yields an
    * <code>AttributeExpressionException</code>.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateIntDNE()
   {
      AttributeUtil.evaluateInt(null, new XSSFRichTextString("${dne}"), myBeans, "attr_name", 0);
   }

   /**
    * Throw positive number at int method testing for being non-negative.
    */
   @Test
   public void testEvaluateNonNegativeIntPositive()
   {
      assertEquals(42, AttributeUtil.evaluateNonNegativeInt(null, new XSSFRichTextString("${answer}"), myBeans, "attr_name", -1));
   }

   /**
    * Throw zero at int method testing for being non-negative.
    */
   @Test
   public void testEvaluateNonNegativeIntZero()
   {
      assertEquals(0, AttributeUtil.evaluateNonNegativeInt(null, new XSSFRichTextString("${zero}"), myBeans, "attr_name", -1));
   }

   /**
    * Throw negative number at int method testing for being non-negative.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateNonNegativeNegative()
   {
      AttributeUtil.evaluateNonNegativeInt(null, new XSSFRichTextString("${isquared}"), myBeans, "attr_name", 0);
   }

   /**
    * Throw positive number at int method testing for being positive.
    */
   @Test
   public void testEvaluatePositiveIntPositive()
   {
      assertEquals(42, AttributeUtil.evaluatePositiveInt(null, new XSSFRichTextString("${answer}"), myBeans, "attr_name", -1));
   }

   /**
    * Throw zero at int method testing for being positive.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluatePositiveIntZero()
   {
      AttributeUtil.evaluatePositiveInt(null, new XSSFRichTextString("${zero}"), myBeans, "attr_name", -1);
   }

   /**
    * Throw negative number at int method testing for being positive.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluatePositiveIntNegative()
   {
      AttributeUtil.evaluatePositiveInt(null, new XSSFRichTextString("${isquared}"), myBeans, "attr_name", 0);
   }

   /**
    * Throw positive number at int method testing for being non-zero.
    */
   @Test
   public void testEvaluateNonZeroIntPositive()
   {
      assertEquals(42, AttributeUtil.evaluateNonZeroInt(null, new XSSFRichTextString("${answer}"), myBeans, "attr_name", -1));
   }

   /**
    * Throw zero at int method testing for being non-zero.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateNonZeroIntZero()
   {
      AttributeUtil.evaluateNonZeroInt(null, new XSSFRichTextString("${zero}"), myBeans, "attr_name", -1);
   }

   /**
    * Throw negative number at int method testing for being non-zero.
    */
   @Test
   public void testEvaluateNonZeroIntNegative()
   {
      assertEquals(-1, AttributeUtil.evaluateNonZeroInt(null, new XSSFRichTextString("${isquared}"), myBeans, "attr_name", 0));
   }

   /**
    * Tests double resolution.
    */
   @Test
   public void testEvaluateDouble()
   {
      assertEquals(8.6, AttributeUtil.evaluateDouble(null, new XSSFRichTextString("${question}"), myBeans, "attr_name", 0), 0.0000001);
   }

   /**
    * Proper exception must be thrown for unparseable double.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateDoubleBad()
   {
      AttributeUtil.evaluateDouble(null, new XSSFRichTextString("${t}"), myBeans, "attr_name", 0);
   }

   /**
    * Make sure that a bad expression with an undefined variable yields an
    * <code>AttributeExpressionException</code>.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateDoubleDNE()
   {
      AttributeUtil.evaluateDouble(null, new XSSFRichTextString("${dne}"), myBeans, "attr_name", 0);
   }

   /**
    * Tests RichTextStrings.
    * @since 0.9.0
    */
   @Test
   public void testEvaluateRichTextStringNotNull()
   {
      RichTextString result = (RichTextString) AttributeUtil.evaluateRichTextStringNotNull(null,
         new XSSFRichTextString("Name: ${bugs.lastName}, ${bugs.firstName}"),
         new TestCreationHelper(), myBeans, "attr_name", "");
      assertEquals("Name: Bunny, Bugs", result.toString());
   }

   /**
    * Ensures that if a <code>null</code> is passed, the exception is thrown.
    * @since 0.9.0
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateRichTextStringNull()
   {
      AttributeUtil.evaluateRichTextStringNotNull(null, new XSSFRichTextString("${null}"),
              new TestCreationHelper(), myBeans, "attr_name", "");
   }

   /**
    * Tests String resolution.
    */
   @Test
   public void testEvaluateString()
   {
      assertEquals("JETT", AttributeUtil.evaluateString(null, new XSSFRichTextString("${project}"), myBeans, null));
   }

   /**
    * Tests if a <code>null</code> comes out.
    */
   @Test
   public void testEvaluateStringNull()
   {
      assertNull(AttributeUtil.evaluateString(null, new XSSFRichTextString("${null}"), myBeans, "notNullDefault"));
   }

   // Can't have this test, because we have to have null be a valid possible result.
   //@Test(expected = AttributeExpressionException.class)
   //public void testEvaluateStringDNE()
   //{
   //   AttributeUtil.evaluateString(new XSSFRichTextString("${dne}"), myBeans, null);
   //}

   /**
    * Catches the null result.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateStringNotNull()
   {
      AttributeUtil.evaluateStringNotNull(null, new XSSFRichTextString("${null}"), myBeans, "attr_name", "notNullDefault");
   }

   /**
    * Tests that a result is contained in a set of specific values.
    */
   @Test
   public void testEvaluateStringSpecificValues()
   {
      assertEquals("JETT", AttributeUtil.evaluateStringSpecificValues(null, new XSSFRichTextString("${project}"), myBeans, "attr_name",
              Arrays.asList("Apache POI", "JETT", "jAgg"), null));
   }

   /**
    * Tests that an exception results when a result is not contained in a set
    * of specific values.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateStringSpecificValuesNotFound()
   {
      AttributeUtil.evaluateStringSpecificValues(null, new XSSFRichTextString("${project}"), myBeans, "attr_name",
              Arrays.asList("Apache POI", "jAgg"), null);
   }

   /**
    * Make sure that a bad expression with an undefined variable yields an
    * <code>AttributeExpressionException</code>.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateObjectDNE()
   {
      AttributeUtil.evaluateObject(null, "${dne}", myBeans, "attr_name", String.class, "notNullDefault");
   }

   /**
    * Test whether we can get an object of a specific class.
    */
   @Test
   public void testEvaluateObject()
   {
      Object obj = AttributeUtil.evaluateObject(null, "${bugs}", myBeans, "attr_name", Employee.class, null);
      assertNotNull(obj);
      assertTrue(obj instanceof Employee);
   }

   /**
    * Test whether we can detect the wrong class.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateObjectWrongClass()
   {
      AttributeUtil.evaluateObject(null, "${bugs}", myBeans, "attr_name", Division.class, null);
   }

   /**
    * Test whether we can instantiate the correct class.
    */
   @Test
   public void testEvaluateObjectInstantiate()
   {
      Object obj = AttributeUtil.evaluateObject(null, "net.sf.jett.test.model.Employee", myBeans, "attr_name", Employee.class, null);
      assertNotNull(obj);
      assertTrue(obj instanceof Employee);
   }

   /**
    * Test whether we can detect the wrongly instantiated class.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateObjectInstantiateWrongClass()
   {
      AttributeUtil.evaluateObject(null, "net.sf.jett.test.model.Employee", myBeans, "attr_name", Division.class, null);
   }

   /**
    * Make sure that a bad expression with an undefined variable yields an
    * <code>AttributeExpressionException</code>.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateListDNE()
   {
      AttributeUtil.evaluateList(null, new XSSFRichTextString("${dne}"), myBeans, null);
   }

   /**
    * Test whether we can get resolve a <code>List</code>.
    */
   @Test
   public void testEvaluateList()
   {
      Object obj = AttributeUtil.evaluateList(null, new XSSFRichTextString("${acronym}"), myBeans, null);
      assertNotNull(obj);
      assertTrue(obj instanceof List);
      List list = (List) obj;
      assertEquals(4, list.size());
   }

   /**
    * Test whether we can create a list from a semicolon-separated string.
    */
   @Test
   //@SuppressWarnings("unchecked")
   public void testEvaluateListSemicolonSeparated()
   {
      Object obj = AttributeUtil.evaluateList(null, new XSSFRichTextString("four;eight;fifteen;sixteen;twenty-three;forty-two"), myBeans, null);
      assertNotNull(obj);
      assertTrue(obj instanceof List);
      List list = (List) obj;
      assertEquals(6, list.size());
      List<String> expected = Arrays.asList("four", "eight", "fifteen", "sixteen", "twenty-three", "forty-two");
      for (int i = 0; i < expected.size(); i++)
      {
         assertEquals(expected.get(i), list.get(i).toString());
      }
   }

   /**
    * Make sure that a bad expression with an undefined variable yields an
    * <code>AttributeExpressionException</code>.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateIntegerArrayDNE()
   {
      AttributeUtil.evaluateIntegerArray(null, new XSSFRichTextString("${dne}"), myBeans, Arrays.asList(1));
   }

   /**
    * Resolve an <code>int[]</code>.
    */
   @Test
   public void testEvaluateIntegerArray()
   {
      List<Integer> intList = AttributeUtil.evaluateIntegerArray(null, new XSSFRichTextString("${[4, 8, 15, 16, 23, 42]}"), myBeans, null);
      assertNotNull(intList);
      assertEquals(6, intList.size());
      List<Integer> expected = Arrays.asList(4, 8, 15, 16, 23, 42);
      for (int i = 0; i < expected.size(); i++)
      {
         assertEquals(expected.get(i), intList.get(i));
      }
   }

   /**
    * Resolve an <code>Integer[]</code>.
    */
   @Test
   public void testEvaluateIntegerArrayIntegerArray()
   {
      List<Integer> intList = AttributeUtil.evaluateIntegerArray(null, new XSSFRichTextString("${integerArray}"), myBeans, null);
      assertNotNull(intList);
      assertEquals(6, intList.size());
      List<Integer> expected = Arrays.asList(4, 8, 15, 16, 23, 42);
      for (int i = 0; i < expected.size(); i++)
      {
         assertEquals(expected.get(i), intList.get(i));
      }
   }

   /**
    * Parse an integer array from a semicolon-delimited string.
    */
   @Test
   public void testEvaluateIntegerArrayParse()
   {
      List<Integer> intList = AttributeUtil.evaluateIntegerArray(null, new XSSFRichTextString("4;8;15;16;23;42"), myBeans, null);
      assertNotNull(intList);
      assertEquals(6, intList.size());
      List<Integer> expected = Arrays.asList(4, 8, 15, 16, 23, 42);
      for (int i = 0; i < expected.size(); i++)
      {
         assertEquals(expected.get(i), intList.get(i));
      }
   }

   /**
    * Make sure that a bad expression with an undefined variable yields an
    * <code>AttributeExpressionException</code>.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateIntegerArrayArrayDNE()
   {
      List<List<Integer>> def = new ArrayList<List<Integer>>();
      def.add(Arrays.asList(1));
      AttributeUtil.evaluateIntegerArrayArray(null, new XSSFRichTextString("${dne}"), myBeans, def);
   }

   /**
    * Resolve an <code>int[][]</code>.
    */
   @Test
   public void testEvaluateIntegerArrayArray()
   {
      List<List<Integer>> intList = AttributeUtil.evaluateIntegerArrayArray(null, new XSSFRichTextString("${[[4, 8], [15, 16, 23], [42]]}"), myBeans, null);
      assertNotNull(intList);
      assertEquals(3, intList.size());

      List<List<Integer>> expected = new ArrayList<List<Integer>>();
      expected.add(Arrays.asList(4, 8));
      expected.add(Arrays.asList(15, 16, 23));
      expected.add(Arrays.asList(42));

      for (int i = 0; i < expected.size(); i++)
      {
         List<Integer> expectedInternalList = expected.get(i);
         List<Integer> internalList = intList.get(i);
         assertEquals(expectedInternalList.size(), internalList.size());
         for (int j = 0; j < expectedInternalList.size(); j++)
         {
            assertEquals(expectedInternalList.get(j), internalList.get(j));
         }
      }
   }

   /**
    * Resolve an <code>Integer[][]</code>.
    */
   @Test
   public void testEvaluateIntegerArrayArrayIntegerArrayArray()
   {
      List<List<Integer>> intList = AttributeUtil.evaluateIntegerArrayArray(null, new XSSFRichTextString("${integerArrayArray}"), myBeans, null);
      assertNotNull(intList);
      assertEquals(3, intList.size());

      List<List<Integer>> expected = new ArrayList<List<Integer>>();
      expected.add(Arrays.asList(4, 8));
      expected.add(Arrays.asList(15, 16, 23));
      expected.add(Arrays.asList(42));

      for (int i = 0; i < expected.size(); i++)
      {
         List<Integer> expectedInternalList = expected.get(i);
         List<Integer> internalList = intList.get(i);
         assertEquals(expectedInternalList.size(), internalList.size());
         for (int j = 0; j < expectedInternalList.size(); j++)
         {
            assertEquals(expectedInternalList.get(j), internalList.get(j));
         }
      }
   }

   /**
    * Parse an integer array from a semicolon-delimited string.
    */
   @Test
   public void testEvaluateIntegerArrayArrayParse()
   {
      List<List<Integer>> intList = AttributeUtil.evaluateIntegerArrayArray(null, new XSSFRichTextString("4,8;15,16,23;42"), myBeans, null);
      assertNotNull(intList);
      assertEquals(3, intList.size());

      List<List<Integer>> expected = new ArrayList<List<Integer>>();
      expected.add(Arrays.asList(4, 8));
      expected.add(Arrays.asList(15, 16, 23));
      expected.add(Arrays.asList(42));

      for (int i = 0; i < expected.size(); i++)
      {
         List<Integer> expectedInternalList = expected.get(i);
         List<Integer> internalList = intList.get(i);
         assertEquals(expectedInternalList.size(), internalList.size());
         for (int j = 0; j < expectedInternalList.size(); j++)
         {
            assertEquals(expectedInternalList.get(j), internalList.get(j));
         }
      }
   }
}

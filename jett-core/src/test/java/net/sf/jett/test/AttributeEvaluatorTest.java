package net.sf.jett.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import net.sf.jett.exception.AttributeExpressionException;
import net.sf.jett.test.model.Division;
import net.sf.jett.test.model.Employee;
import net.sf.jett.util.AttributeEvaluator;

/**
 * Tests the <code>AttributeEvaluator</code> class.
 *
 * @author Randy Gettman
 * @since 0.6.0
 */
public class AttributeEvaluatorTest
{
   private Map<String, Object> myBeans;

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
      assertTrue(new AttributeEvaluator(null).evaluateBoolean(new XSSFRichTextString("${t}"), myBeans, false));
   }

   /**
    * Make sure it's evaluated as <code>false</code>.
    */
   @Test
   public void testBooleanFalse()
   {
      assertFalse(new AttributeEvaluator(null).evaluateBoolean(new XSSFRichTextString("${f}"), myBeans, true));
   }

   /**
    * Make sure that a bad expression with an undefined variable yields an
    * <code>AttributeExpressionException</code>.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testBooleanDNE()
   {
      new AttributeEvaluator(null).evaluateBoolean(new XSSFRichTextString("${dne}"), myBeans, true);
   }

   /**
    * Tests integer resolution.
    */
   @Test
   public void testEvaluateInt()
   {
      assertEquals(42, new AttributeEvaluator(null).evaluateInt(new XSSFRichTextString("${answer}"), myBeans, "attr_name", 0));
   }

   /**
    * Proper exception must be thrown for unparseable integer.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateIntBad()
   {
      new AttributeEvaluator(null).evaluateInt(new XSSFRichTextString("${t}"), myBeans, "attr_name", 0);
   }

   /**
    * Make sure that a bad expression with an undefined variable yields an
    * <code>AttributeExpressionException</code>.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateIntDNE()
   {
      new AttributeEvaluator(null).evaluateInt(new XSSFRichTextString("${dne}"), myBeans, "attr_name", 0);
   }

   /**
    * Throw positive number at int method testing for being non-negative.
    */
   @Test
   public void testEvaluateNonNegativeIntPositive()
   {
      assertEquals(42, new AttributeEvaluator(null).evaluateNonNegativeInt(new XSSFRichTextString("${answer}"), myBeans, "attr_name", -1));
   }

   /**
    * Throw zero at int method testing for being non-negative.
    */
   @Test
   public void testEvaluateNonNegativeIntZero()
   {
      assertEquals(0, new AttributeEvaluator(null).evaluateNonNegativeInt(new XSSFRichTextString("${zero}"), myBeans, "attr_name", -1));
   }

   /**
    * Throw negative number at int method testing for being non-negative.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateNonNegativeNegative()
   {
      new AttributeEvaluator(null).evaluateNonNegativeInt(new XSSFRichTextString("${isquared}"), myBeans, "attr_name", 0);
   }

   /**
    * Throw positive number at int method testing for being positive.
    */
   @Test
   public void testEvaluatePositiveIntPositive()
   {
      assertEquals(42, new AttributeEvaluator(null).evaluatePositiveInt(new XSSFRichTextString("${answer}"), myBeans, "attr_name", -1));
   }

   /**
    * Throw zero at int method testing for being positive.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluatePositiveIntZero()
   {
      new AttributeEvaluator(null).evaluatePositiveInt(new XSSFRichTextString("${zero}"), myBeans, "attr_name", -1);
   }

   /**
    * Throw negative number at int method testing for being positive.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluatePositiveIntNegative()
   {
      new AttributeEvaluator(null).evaluatePositiveInt(new XSSFRichTextString("${isquared}"), myBeans, "attr_name", 0);
   }

   /**
    * Throw positive number at int method testing for being non-zero.
    */
   @Test
   public void testEvaluateNonZeroIntPositive()
   {
      assertEquals(42, new AttributeEvaluator(null).evaluateNonZeroInt(new XSSFRichTextString("${answer}"), myBeans, "attr_name", -1));
   }

   /**
    * Throw zero at int method testing for being non-zero.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateNonZeroIntZero()
   {
      new AttributeEvaluator(null).evaluateNonZeroInt(new XSSFRichTextString("${zero}"), myBeans, "attr_name", -1);
   }

   /**
    * Throw negative number at int method testing for being non-zero.
    */
   @Test
   public void testEvaluateNonZeroIntNegative()
   {
      assertEquals(-1, new AttributeEvaluator(null).evaluateNonZeroInt(new XSSFRichTextString("${isquared}"), myBeans, "attr_name", 0));
   }

   /**
    * Tests double resolution.
    */
   @Test
   public void testEvaluateDouble()
   {
      assertEquals(8.6, new AttributeEvaluator(null).evaluateDouble(new XSSFRichTextString("${question}"), myBeans, "attr_name", 0), 0.0000001);
   }

   /**
    * Proper exception must be thrown for unparseable double.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateDoubleBad()
   {
      new AttributeEvaluator(null).evaluateDouble(new XSSFRichTextString("${t}"), myBeans, "attr_name", 0);
   }

   /**
    * Make sure that a bad expression with an undefined variable yields an
    * <code>AttributeExpressionException</code>.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateDoubleDNE()
   {
      new AttributeEvaluator(null).evaluateDouble(new XSSFRichTextString("${dne}"), myBeans, "attr_name", 0);
   }

   /**
    * Tests String resolution.
    */
   @Test
   public void testEvaluateString()
   {
      assertEquals("JETT", new AttributeEvaluator(null).evaluateString(new XSSFRichTextString("${project}"), myBeans, null));
   }

   /**
    * Tests if a <code>null</code> comes out.
    */
   @Test
   public void testEvaluateStringNull()
   {
      assertNull(new AttributeEvaluator(null).evaluateString(new XSSFRichTextString("${null}"), myBeans, "notNullDefault"));
   }

   // Can't have this test, because we have to have null be a valid possible result.
   //@Test(expected = AttributeExpressionException.class)
   //public void testEvaluateStringDNE()
   //{
   //   new AttributeEvaluator(null).evaluateString(new XSSFRichTextString("${dne}"), myBeans, null);
   //}

   /**
    * Catches the null result.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateStringNotNull()
   {
      new AttributeEvaluator(null).evaluateStringNotNull(new XSSFRichTextString("${null}"), myBeans, "attr_name", "notNullDefault");
   }

   /**
    * Tests that a result is contained in a set of specific values.
    */
   @Test
   public void testEvaluateStringSpecificValues()
   {
      assertEquals("JETT", new AttributeEvaluator(null).evaluateStringSpecificValues(new XSSFRichTextString("${project}"), myBeans, "attr_name",
              Arrays.asList("Apache POI", "JETT", "jAgg"), null));
   }

   /**
    * Tests that an exception results when a result is not contained in a set
    * of specific values.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateStringSpecificValuesNotFound()
   {
      new AttributeEvaluator(null).evaluateStringSpecificValues(new XSSFRichTextString("${project}"), myBeans, "attr_name",
              Arrays.asList("Apache POI", "jAgg"), null);
   }

   /**
    * Make sure that a bad expression with an undefined variable yields an
    * <code>AttributeExpressionException</code>.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateObjectDNE()
   {
      new AttributeEvaluator(null).evaluateObject("${dne}", myBeans, "attr_name", String.class, "notNullDefault");
   }

   /**
    * Test whether we can get an object of a specific class.
    */
   @Test
   public void testEvaluateObject()
   {
      Object obj = new AttributeEvaluator(null).evaluateObject("${bugs}", myBeans, "attr_name", Employee.class, null);
      assertNotNull(obj);
      assertTrue(obj instanceof Employee);
   }

   /**
    * Test whether we can detect the wrong class.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateObjectWrongClass()
   {
      new AttributeEvaluator(null).evaluateObject("${bugs}", myBeans, "attr_name", Division.class, null);
   }

   /**
    * Test whether we can instantiate the correct class.
    */
   @Test
   public void testEvaluateObjectInstantiate()
   {
      Object obj = new AttributeEvaluator(null).evaluateObject("net.sf.jett.test.model.Employee", myBeans, "attr_name", Employee.class, null);
      assertNotNull(obj);
      assertTrue(obj instanceof Employee);
   }

   /**
    * Test whether we can detect the wrongly instantiated class.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateObjectInstantiateWrongClass()
   {
      new AttributeEvaluator(null).evaluateObject("net.sf.jett.test.model.Employee", myBeans, "attr_name", Division.class, null);
   }

   /**
    * Make sure that a bad expression with an undefined variable yields an
    * <code>AttributeExpressionException</code>.
    */
   @Test(expected = AttributeExpressionException.class)
   public void testEvaluateListDNE()
   {
      new AttributeEvaluator(null).evaluateList(new XSSFRichTextString("${dne}"), myBeans, null);
   }

   /**
    * Test whether we can get resolve a <code>List</code>.
    */
   @Test
   public void testEvaluateList()
   {
      Object obj = new AttributeEvaluator(null).evaluateList(new XSSFRichTextString("${acronym}"), myBeans, null);
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
      Object obj = new AttributeEvaluator(null).evaluateList(new XSSFRichTextString("four;eight;fifteen;sixteen;twenty-three;forty-two"), myBeans, null);
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
      new AttributeEvaluator(null).evaluateIntegerArray(new XSSFRichTextString("${dne}"), myBeans, Arrays.asList(1));
   }

   /**
    * Resolve an <code>int[]</code>.
    */
   @Test
   public void testEvaluateIntegerArray()
   {
      List<Integer> intList = new AttributeEvaluator(null).evaluateIntegerArray(new XSSFRichTextString("${[4, 8, 15, 16, 23, 42]}"), myBeans, null);
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
      List<Integer> intList = new AttributeEvaluator(null).evaluateIntegerArray(new XSSFRichTextString("${integerArray}"), myBeans, null);
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
      List<Integer> intList = new AttributeEvaluator(null).evaluateIntegerArray(new XSSFRichTextString("4;8;15;16;23;42"), myBeans, null);
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
      new AttributeEvaluator(null).evaluateIntegerArrayArray(new XSSFRichTextString("${dne}"), myBeans, def);
   }

   /**
    * Resolve an <code>int[][]</code>.
    */
   @Test
   public void testEvaluateIntegerArrayArray()
   {
      List<List<Integer>> intList = new AttributeEvaluator(null).evaluateIntegerArrayArray(new XSSFRichTextString("${[[4, 8], [15, 16, 23], [42]]}"), myBeans, null);
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
      List<List<Integer>> intList = new AttributeEvaluator(null).evaluateIntegerArrayArray(new XSSFRichTextString("${integerArrayArray}"), myBeans, null);
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
      List<List<Integer>> intList = new AttributeEvaluator(null).evaluateIntegerArrayArray(new XSSFRichTextString("4,8;15,16,23;42"), myBeans, null);
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

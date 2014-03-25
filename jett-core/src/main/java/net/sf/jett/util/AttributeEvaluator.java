package net.sf.jett.util;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;

import net.sf.jett.exception.AttributeExpressionException;
import net.sf.jett.expression.Expression;
import net.sf.jett.tag.TagContext;
import net.sf.jett.model.Block;

/**
 * The <code>AttributeEvaluator</code> class provides methods for
 * evaluating <code>Expressions</code> that are expected to result in a
 * specific type.  Prior to 0.7.0, this was known as <code>AttributeUtil</code>.
 *
 * @author Randy Gettman
 * @since 0.3.0
 */
public class AttributeEvaluator
{
   /**
    * Separates expressions in attributes that take multiple values.  This was
    * originally defined as the same value in multiple sub-classes, but was
    * moved to BaseTag/AttributeEvaluator for 0.3.0.
    * @since 0.3.0
    */
   public static final String SPEC_SEP = ";";
   /**
    * Separates expressions in attributes that take multiple values at a second
    * level.  I.e. this is possible: "0,1;2,3" which would be interpreted as a
    * 2D array: <code>[[0, 1], [2, 3]]</code>.
    * @since 0.4.0
    */
   public static final String SPEC_SEP_2 = ",";

   private TagContext myTagContext;

   /**
    * Constructs a <code>AttributeEvaluator</code> with the given
    * <code>TagContext</code>.  The <code>TagContext</code> is only used to
    * provide a spreadsheet location in case of exception messages.
    * @param tagContext A <code>TagContext</code> (may be <code>null</code>).
    * @since 0.7.0
    */
   public AttributeEvaluator(TagContext tagContext)
   {
      myTagContext = tagContext;
   }

   /**
    * Returns the location of the attribute.
    * @return The location of the attribute, or <code>""</code> if no
    *    <code>TagContext</code> was provided.
    * @since 0.7.0
    */
   private String getLocation()
   {
      if (myTagContext == null)
         return "";

      Block block = myTagContext.getBlock();
      Sheet sheet = myTagContext.getSheet();
      int left = block.getLeftColNum();
      int top = block.getTopRowNum();
      // It should exist in this Cell; this Tag was found in it.
      Row row = sheet.getRow(top);
      Cell cell = row.getCell(left);

      return SheetUtil.getCellLocation(cell);
   }

   /**
    * Helper method to throw an <code>AttributeExpressionException</code> with
    * a common message indicating that a null value resulted, or an expected
    * variable was missing when attempting to evaluate an expression inside an
    * attribute value.
    * @param expression The original expression.
    * @return <code>AttributeExpressionException</code> with a standard message.
    */
   private AttributeExpressionException nullValueOrExpectedVariableMissing(String expression)
   {
      return new AttributeExpressionException("Null value or expected variable missing in expression \"" +
              expression + "\"." + getLocation());
   }

   /**
    * Evaluates the given text, which may have embedded
    * <code>Expressions</code>, and attempts to extract a boolean value from
    * the result, calling <code>Boolean.parseBoolean()</code> on the result if
    * necessary.  If the text is null, then the result defaults to the given
    * default boolean value.
    * @param text Text which may have embedded <code>Expressions</code>.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @param def The default value if the text is null.
    * @return The boolean result.
    */
   public boolean evaluateBoolean(RichTextString text, Map<String, Object> beans, boolean def)
   {
      boolean result;
      if (text == null)
         return def;
      Object obj = Expression.evaluateString(text.toString(), beans);
      if (obj == null)
         throw nullValueOrExpectedVariableMissing(text.toString());
      if (obj instanceof Boolean)
         result = (Boolean) obj;
      else
         result = Boolean.parseBoolean(obj.toString());
      return result;
   }

   /**
    * Evaluates the given text, which may have embedded
    * <code>Expressions</code>, and attempts to extract an integer value from
    * the result, calling <code>toString()</code> on the result and parsing it
    * if necessary.  If the text is null, then the result defaults to the given
    * default integer value.
    * @param text Text which may have embedded <code>Expressions</code>.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @param attrName The attribute name.  This is only used when constructing
    *    an exception message.
    * @param def The default value if the text is null.
    * @return The integer result.
    * @throws AttributeExpressionException If the result of the evaluation of the text is
    *    not a number.
    */
   public int evaluateInt(RichTextString text, Map<String, Object> beans, String attrName, int def)
   {
      int result;
      if (text == null)
         return def;
      Object obj = Expression.evaluateString(text.toString(), beans);
      if (obj == null)
         throw nullValueOrExpectedVariableMissing(text.toString());
      if (obj instanceof Number)
      {
         result = ((Number) obj).intValue();
      }
      else
      {
         try
         {
            result = Integer.parseInt(obj.toString());
         }
         catch (NumberFormatException e)
         {
            throw new AttributeExpressionException("The \"" + attrName + "\" attribute must be an integer: " + text);
         }
      }
      return result;
   }

   /**
    * Evaluates the given text, which may have embedded
    * <code>Expressions</code>, and attempts to extract an integer value from
    * the result, calling <code>toString()</code> on the result and parsing it
    * if necessary.  Enforce the result to be non-negative.
    * @param text Text which may have embedded <code>Expressions</code>.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @param attrName The attribute name.  This is only used when constructing
    *    an exception message.
    * @param def The default value if the text is null.
    * @return The integer result.
    * @throws AttributeExpressionException If the result of the evaluation of the text is
    *    not a number, or if the result is negative.
    */
   public int evaluateNonNegativeInt(RichTextString text, Map<String, Object> beans, String attrName, int def)
   {
      int result = evaluateInt(text, beans, attrName, def);
      if (result < 0)
      {
         throw new AttributeExpressionException("The \"" + attrName + "\" attribute must be non-negative: " + result);
      }
      return result;
   }

   /**
    * Evaluates the given text, which may have embedded
    * <code>Expressions</code>, and attempts to extract an integer value from
    * the result, calling <code>toString()</code> on the result and parsing it
    * if necessary.  Enforce the result to be positive.
    * @param text Text which may have embedded <code>Expressions</code>.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @param attrName The attribute name.  This is only used when constructing
    *    an exception message.
    * @param def The default value if the text is null.
    * @return The integer result.
    * @throws AttributeExpressionException If the result of the evaluation of the text is
    *    not a number, or if the result is negative.
    */
   public int evaluatePositiveInt(RichTextString text, Map<String, Object> beans, String attrName, int def)
   {
      int result = evaluateInt(text, beans, attrName, def);
      if (result <= 0)
      {
         throw new AttributeExpressionException("The \"" + attrName + "\" attribute must be positive: " + result);
      }
      return result;
   }

   /**
    * Evaluates the given text, which may have embedded
    * <code>Expressions</code>, and attempts to extract an integer value from
    * the result, calling <code>toString()</code> on the result and parsing it
    * if necessary.  Enforce the result to be not zero.
    * @param text Text which may have embedded <code>Expressions</code>.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @param attrName The attribute name.  This is only used when constructing
    *    an exception message.
    * @param def The default value if the text is null.
    * @return The integer result.
    * @throws AttributeExpressionException If the result of the evaluation of the text is
    *    not a number, or if the result is zero.
    */
   public int evaluateNonZeroInt(RichTextString text, Map<String, Object> beans, String attrName, int def)
   {
      int result = evaluateInt(text, beans, attrName, def);
      if (result == 0)
      {
         throw new AttributeExpressionException("The \"" + attrName + "\" attribute must not be zero: " + result);
      }
      return result;
   }

   /**
    * Evaluates the given text, which may have embedded
    * <code>Expressions</code>, and attempts to extract a double value from
    * the result, calling <code>toString()</code> on the result and parsing it
    * if necessary.
    * @param text Text which may have embedded <code>Expressions</code>.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @param attrName The attribute name.  This is only used when constructing
    *    an exception message.
    * @param def The default value if the text is null.
    * @return The double result.
    * @throws AttributeExpressionException If the result of the evaluation of the text is
    *    not a number.
    */
   public double evaluateDouble(RichTextString text, Map<String, Object> beans, String attrName, double def)
   {
      double result;
      if (text == null)
         return def;
      Object obj = Expression.evaluateString(text.toString(), beans);
      if (obj == null)
         throw nullValueOrExpectedVariableMissing(text.toString());
      if (obj instanceof Number)
      {
         result = ((Number) obj).doubleValue();
      }
      else
      {
         try
         {
            result = Double.parseDouble(obj.toString());
         }
         catch (NumberFormatException e)
         {
            throw new AttributeExpressionException("The \"" + attrName + "\" attribute must be a number: " + text);
         }
      }
      return result;
   }

   /**
    * Evaluates the given text, which may have embedded
    * <code>Expressions</code>, and attempts to extract a <code>String</code>
    * result, calling <code>toString()</code> on the result.
    * @param text Text which may have embedded <code>Expressions</code>.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @param def The default value if the text is null.
    * @return The <code>String</code> result.
    */
   public String evaluateString(RichTextString text, Map<String, Object> beans, String def)
   {
      if (text == null)
         return def;
      Object obj = Expression.evaluateString(text.toString(), beans);
      return (obj == null) ? null : obj.toString();
   }

   /**
    * Evaluates the given text, which may have embedded
    * <code>Expressions</code>, and attempts to extract a <code>String</code>
    * result, calling <code>toString()</code> on the result.
    * @param text Text which may have embedded <code>Expressions</code>.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @param attrName The attribute name.  This is only used when constructing
    *    an exception message.
    * @param def The default value if the text is null.
    * @return The <code>String</code> result.
    */
   public String evaluateStringNotNull(RichTextString text, Map<String, Object> beans, String attrName, String def)
   {
      String result = evaluateString(text, beans, def);
      if (result == null || result.length() == 0)
         throw new AttributeExpressionException("Value for \"" + attrName + "\" must not be null or empty: " + text.toString());
      return result;
   }

   /**
    * Evaluates the given text, which may have embedded
    * <code>Expressions</code>, and attempts to extract a <code>String</code>
    * result, calling <code>toString()</code> on the result.  Enforces that the
    * result is one of the given expected values, ignoring case.
    * @param text Text which may have embedded <code>Expressions</code>.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @param attrName The attribute name.  This is only used when constructing
    *    an exception message.
    * @param legalValues A <code>List</code> of expected values.
    * @param def The default value if the text is null.
    * @return The <code>String</code> result.
    * @throws AttributeExpressionException If the result isn't one of the expected legal
    *    values.
    */
   public String evaluateStringSpecificValues(RichTextString text, Map<String, Object> beans, String attrName,
      List<String> legalValues, String def)
   {
      String result = evaluateString(text, beans, def);
      for (String legalValue : legalValues)
      {
         if (legalValue.equalsIgnoreCase(result))
            return result;
      }
      throw new AttributeExpressionException("Unknown value for \"" + attrName + "\": " + result +
          " (expected one of " + legalValues.toString() + ").");
   }

   /**
    * Evaluates the given text, which may have embedded
    * <code>Expressions</code>, and attempts to extract a result, and cast it
    * to the same class as the given expected class.
    * @param text Text which may have embedded <code>Expressions</code>.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @param attrName The attribute name.  This is only used when constructing
    *    an exception message.
    * @param expectedClass The result is expected to be of the given class or
    *    of a subclass.
    * @param def The default value if the text is null.
    * @return The result.
    * @throws AttributeExpressionException If the result is not of the expected class or
    *    of a subclass.
    */
   @SuppressWarnings("unchecked")
   public <T> T evaluateObject(RichTextString text, Map<String, Object> beans, String attrName,
      Class<T> expectedClass, T def)
   {
      if (text == null)
         return def;

      return evaluateObject(text.toString(), beans, attrName, expectedClass, def);
   }

   /**
    * Evaluates the given text, which may have embedded
    * <code>Expressions</code>, and attempts to extract a result, and cast it
    * to the same class as the given expected class.
    * @param text Text which may have embedded <code>Expressions</code>.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @param attrName The attribute name.  This is only used when constructing
    *    an exception message.
    * @param expectedClass The result is expected to be of the given class or
    *    of a subclass.
    * @param def The default value if the text is null.
    * @return The result.
    * @throws AttributeExpressionException If the result is not of the expected class or
    *    of a subclass.
    */
   @SuppressWarnings("unchecked")
   public <T> T evaluateObject(String text, Map<String, Object> beans, String attrName,
      Class<T> expectedClass, T def)
   {
      T result;
      if (text == null)
         return def;
      Object obj = Expression.evaluateString(text, beans);
      if (obj == null)
         throw nullValueOrExpectedVariableMissing(text);
      Class objClass = obj.getClass();
      if (expectedClass.isAssignableFrom(objClass))
      {
         // Don't expect a ClassCastException after the above test.
         result = expectedClass.cast(obj);
      }
      else if (obj instanceof String)
      {
         String className = (String) obj;
         // Treat as a class name to instantiate.
         try
         {
            Class<T> actualClass = (Class<T>) Class.forName(className);
            result = actualClass.newInstance();
            if (!expectedClass.isInstance(result))
            {
               throw new AttributeExpressionException("Expected a \"" + expectedClass.getName() + "\" for \"" +
                  attrName + "\", but instantiated a \"" + className + "\".");
            }
         }
         catch (ClassNotFoundException e)
         {
            throw new AttributeExpressionException("Expected a \"" + expectedClass.getName() + "\" for \"" +
               attrName + "\", could not find class \"" + className + "\": " + text, e);
         }
         catch (InstantiationException e)
         {
            throw new AttributeExpressionException("Expected a \"" + expectedClass.getName() + "\" for \"" +
               attrName + "\", could not instantiate class \"" + className + "\": " + text, e);
         }
         catch (IllegalAccessException e)
         {
            throw new AttributeExpressionException("Expected a \"" + expectedClass.getName() + "\" for \"" +
               attrName + "\", could not instantiate class \"" + className + "\": " + text, e);
         }
         catch (ClassCastException e)
         {
            throw new AttributeExpressionException("Expected a \"" + expectedClass.getName() + "\" for \"" +
               attrName + "\", could not instantiate class \"" + className + "\": " + text, e);
         }
      }
      else
      {
         throw new AttributeExpressionException("Expected a \"" + expectedClass.getName() + "\" for \"" +
               attrName + "\", got a \"" + obj.getClass().getName() + "\": " + text);
      }
      return result;
   }

   /**
    * Evaluates the given text, which may have embedded
    * <code>Expressions</code>, and attempts to extract a <code>List</code> out
    * of the result, parsing a delimited list to create a list if necessary.
    * @param text Text which may have embedded <code>Expressions</code>.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @param def The default value if the text is null.
    * @return A <code>List</code>.
    */
   public List<String> evaluateList(RichTextString text, Map<String, Object> beans, List<String> def)
   {
      List<String> result;
      if (text == null)
         return def;
      Object obj = Expression.evaluateString(text.toString(), beans);
      if (obj == null)
         throw nullValueOrExpectedVariableMissing(text.toString());
      if (obj instanceof List)
      {
         List list = (List) obj;
         result = new ArrayList<String>(list.size());
         for (Object item : list)
            result.add(item.toString());
      }
      else
      {
         String[] items = obj.toString().split(SPEC_SEP);
         result = Arrays.asList(items);
      }
      return result;
   }

   /**
    * <p>Evaluates the given text, which may have embedded
    * <code>Expressions</code>, and attempts to extract a <code>List</code> of
    * <code>Integers</code> from the result, accepting an <code>int</code>
    * array or a <code>Collection</code> or delimited list of numbers.</p>
    * <p>Examples of proper input:</p>
    * <ul>
    * <li>[0, 1, 2]
    * <li>(ArrayList){0, 1, 2}
    * <li>"0; 1; 2"
    * </ul>
    * @param text Text which may have embedded <code>Expressions</code>.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @param def The default value if the text is null.
    * @return A <code>List</code> of <code>Integers</code>.
    */
   public List<Integer> evaluateIntegerArray(RichTextString text, Map<String, Object> beans, List<Integer> def)
   {
      List<Integer> result = new ArrayList<Integer>();
      if (text == null)
         return def;
      Object obj = Expression.evaluateString(text.toString(), beans);
      if (obj == null)
         throw nullValueOrExpectedVariableMissing(text.toString());
      if (obj instanceof int[])
      {
         int[] intArray = (int[]) obj;
         for (int i : intArray)
            result.add(i);
      }
      else if (obj instanceof Integer[])
      {
         Integer[] intArray = (Integer[]) obj;
         result.addAll(Arrays.asList(intArray));
      }
      else if (obj instanceof Collection)
      {
         Collection c = (Collection) obj;

         for (Object o : c)
         {
            if (o instanceof Number)
            {
               result.add(((Number) o).intValue());
            }
            else
            {
               try
               {
                  result.add(Integer.parseInt(o.toString()));
               }
               catch (NumberFormatException e)
               {
                  throw new AttributeExpressionException("Expected an integer, got " + o.toString(), e);
               }
            }
         }
      }
      else
      {
         String[] items = obj.toString().split(SPEC_SEP);
         for (String item : items)
         {
            try
            {
               result.add(Integer.parseInt(item));
            }
            catch (NumberFormatException e)
            {
               throw new AttributeExpressionException("Expected an integer, got " + item, e);
            }
         }
      }

      return result;
   }

   /**
    * <p>Evaluates the given text, which may have embedded
    * <code>Expressions</code>, and attempts to extract a <code>List</code> of
    * <code>Lists</code> of <code>Integers</code> from the result, accepting a
    * 2D <code>int</code> array or a <code>Collection</code> of
    * <code>Collections</code> or delimited list of numbers.</p>
    * <p>Examples of proper input:</p>
    * <ul>
    * <li>[[0, 1], [2]]
    * <li>(ArrayList){(ArrayList){0, 1}, (ArrayList){2}}
    * <li>"0, 1; 2"
    * </ul>
    * @param text Text which may have embedded <code>Expressions</code>.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @param def The default value if the text is null.
    * @return A <code>List</code> of <code>Lists</code> of
    *    <code>Integers</code>.
    */
   public List<List<Integer>> evaluateIntegerArrayArray(RichTextString text, Map<String, Object> beans, List<List<Integer>> def)
   {
      List<List<Integer>> result = new ArrayList<List<Integer>>();
      if (text == null)
         return def;
      Object obj = Expression.evaluateString(text.toString(), beans);
      if (obj == null)
         throw nullValueOrExpectedVariableMissing(text.toString());
      if (obj instanceof int[][])
      {
         int[][] intArray = (int[][]) obj;
         for (int[] array : intArray)
         {
            List<Integer> innerList = new ArrayList<Integer>();
            for (int i : array)
               innerList.add(i);
            result.add(innerList);
         }
      }
      else if (obj instanceof Integer[][])
      {
         Integer[][] intArray = (Integer[][]) obj;
         for (Integer[] array : intArray)
         {
            List<Integer> innerList = new ArrayList<Integer>();
            innerList.addAll(Arrays.asList(array));
            result.add(innerList);
         }
      }
      else if (obj instanceof Collection)
      {
         Collection c = (Collection) obj;

         for (Object o : c)
         {
            List<Integer> innerList = new ArrayList<Integer>();
            if (o instanceof Collection)
            {
               Collection inner = (Collection) o;
               for (Object innerObj : inner)
               {
                  if (innerObj instanceof Number)
                  {
                     innerList.add(((Number) innerObj).intValue());
                  }
                  else
                  {
                     try
                     {
                        innerList.add(Integer.parseInt(innerObj.toString()));
                     }
                     catch (NumberFormatException e)
                     {
                        throw new AttributeExpressionException("Expected an integer, got " + o.toString(), e);
                     }
                  }
               }
            }
            result.add(innerList);
         }
      }
      else
      {
         String[] items = obj.toString().split(SPEC_SEP);
         for (String item : items)
         {
            List<Integer> innerList = new ArrayList<Integer>();
            String[] innerItems = item.split(SPEC_SEP_2);
            for (String innerItem : innerItems)
            {
               try
               {
                  innerList.add(Integer.parseInt(innerItem));
               }
               catch (NumberFormatException e)
               {
                  throw new AttributeExpressionException("Expected an integer, got " + item, e);
               }
            }
            result.add(innerList);
         }
      }

      return result;
   }

   /**
    * Ensures that exactly one of the given attribute values exists.
    * @param attrValues A <code>List</code> of attribute values.
    * @param attrNames A <code>List</code> of attribute names.
    * @throws AttributeExpressionException If none of the attribute values is not null, or
    *    if more than one attribute value is not null.
    */
   public void ensureExactlyOneExists(List<RichTextString> attrValues, List<String> attrNames)
   {
      int exists = 0;
      for (RichTextString text : attrValues)
      {
         if (text != null)
         {
            exists++;
            if (exists > 1)
            {
               throw new AttributeExpressionException("Exactly one attribute must be specified: " + attrNames.toString());
            }
         }
      }
      if (exists != 1)
      {
         throw new AttributeExpressionException("Exactly one attribute must be specified: " + attrNames.toString());
      }
   }

   /**
    * Ensures that at most one of the given attribute values exists.
    * @param attrValues A <code>List</code> of attribute values.
    * @param attrNames A <code>List</code> of attribute names.
    * @throws AttributeExpressionException If more than one of the attribute values is not
    *    null.
    * @since 0.4.0
    */
   public void ensureAtMostOneExists(List<RichTextString> attrValues, List<String> attrNames)
   {
      int exists = 0;
      for (RichTextString text : attrValues)
      {
         if (text != null)
         {
            exists++;
            if (exists > 1)
            {
               throw new AttributeExpressionException("At most one attribute must be specified: " + attrNames.toString());
            }
         }
      }
      if (exists != 1 && exists != 0)
      {
         throw new AttributeExpressionException("At most one attribute must be specified: " + attrNames.toString());
      }
   }

   /**
    * Ensures that at least one of the given attribute values exists.
    * @param attrValues A <code>List</code> of attribute values.
    * @param attrNames A <code>List</code> of attribute names.
    * @throws AttributeExpressionException If all of the attribute values are null.
    * @since 0.4.0
    */
   public void ensureAtLeastOneExists(List<RichTextString> attrValues, List<String> attrNames)
   {
      for (RichTextString text : attrValues)
      {
         if (text != null)
            return;
      }
      throw new AttributeExpressionException("At least one attribute must be specified: " + attrNames.toString());
   }
}

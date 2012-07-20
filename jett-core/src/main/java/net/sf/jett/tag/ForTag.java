package net.sf.jett.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jett.exception.TagParseException;
import net.sf.jett.expression.Expression;

/**
 * <p>A <code>ForTag</code> represents a repetitively placed <code>Block</code>
 * of <code>Cells</code>, with each repetition corresponding to an increment of
 * an index.</p>
 *
 * <br>Attributes:
 * <ul>
 * <li>copyRight (optional): <code>boolean</code>
 * <li>fixed (optional): <code>boolean</code>
 * <li>pastEndAction (optional): <code>String</code>
 * <li>var (required): <code>String</code>
 * <li>start (required): <code>int</code>
 * <li>end (required): <code>int</code>
 * <li>step (optional): <code>int</code>
 * </ul>
 */
public class ForTag extends BaseLoopTag
{
   /**
    * Attribute for specifying the name of the looping variable.
    */
   public static final String ATTR_VAR = "var";
   /**
    * Attribute for specifying the starting value.
    */
   public static final String ATTR_START = "start";
   /**
    * Attribute for specifying the ending value (included in the range).
    */
   public static final String ATTR_END = "end";
   /**
    * Attribute for specifying how much the value increments per iteration.
    */
   public static final String ATTR_STEP = "step";
   private static final List<String> REQ_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_VAR, ATTR_START, ATTR_END));
   private static final List<String> OPT_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_STEP));

   private String myVarName;
   private int myStart;
   private int myEnd;
   private int myStep;

   /**
    * Returns this <code>Tag's</code> name.
    * @return This <code>Tag's</code> name.
    */
   public String getName()
   {
      return "for";
   }

   /**
    * Returns the <code>List</code> of required attribute names.
    * @return The <code>List</code> of required attribute names.
    */
   @Override
   public List<String> getRequiredAttributes()
   {
      List<String> reqAttrs = super.getRequiredAttributes();
      if (reqAttrs == null)
         return REQ_ATTRS;
      else
      {
         reqAttrs.addAll(REQ_ATTRS);
         return reqAttrs;
      }
   }

   /**
    * Returns the <code>List</code> of optional attribute names.
    * @return The <code>List</code> of optional attribute names.
    */
   @Override
   public List<String> getOptionalAttributes()
   {
      List<String> optAttrs = super.getOptionalAttributes();
      if (optAttrs == null)
         return OPT_ATTRS;
      else
      {
         optAttrs.addAll(OPT_ATTRS);
         return optAttrs;
      }
   }

   /**
    * Validates the attributes for this <code>Tag</code>.  The "start", "end",
    * and "step" attributes must evaluate to <code>int</code>s.  If "step" is
    * not present, then it defaults to <code>1</code>.  The "step" must not be
    * zero.  It is possible for no loops to be processed if "step" is positive
    * and "start" is greater than "end", or if "step" is negative and "start"
    * is less than "end".
    */
   @Override
   public void validateAttributes() throws TagParseException
   {
      super.validateAttributes();
      if (isBodiless())
         throw new TagParseException("For tags must have a body.");

      TagContext context = getContext();
      Map<String, Object> beans = context.getBeans();

      Map<String, String> attributes = getAttributes();
      String attrVar = attributes.get(ATTR_VAR);
      myVarName = Expression.evaluateString(attrVar, beans).toString();
      String attrStart = attributes.get(ATTR_START);
      try
      {
         myStart = Integer.parseInt(Expression.evaluateString(attrStart, beans).toString());
      }
      catch (RuntimeException e)
      {
         throw new TagParseException("Start value must be an integer: " + attrStart, e);
      }
      String attrEnd = attributes.get(ATTR_END);
      try
      {
         myEnd = Integer.parseInt(Expression.evaluateString(attrEnd, beans).toString());
      }
      catch (RuntimeException e)
      {
         throw new TagParseException("End value must be an integer: " + attrEnd, e);
      }
      String attrStep = attributes.get(ATTR_STEP);
      if (attrStep == null)
      {
         myStep = 1;
      }
      else
      {
         try
         {
            myStep = Integer.parseInt(Expression.evaluateString(attrStep, beans).toString());
         }
         catch (RuntimeException e)
         {
            throw new TagParseException("Step value must be an integer: " + attrStep, e);
         }
      }
      if (myStep == 0)
      {
         throw new TagParseException("Step must not be zero.");
      }
   }

   /**
    * Returns the names of the <code>Collections</code> that are being used in
    * this <code>ForTag</code>.
    * @return <code>null</code>, no collections are being used.
    */
   protected List<String> getCollectionNames()
   {
      return null;
   }

   /**
    * Returns the number of iterations.  Note that this effectively disables
    * the "limit" attribute for <code>ForTags</code>.
    * @return The number of iterations.
    */
   protected int getNumIterations()
   {
      if ((myStep > 0 && myStart <= myEnd) || (myStep < 0 && myStart >= myEnd))
         return (myEnd - myStart) / myStep + 1;
      return 0;
   }

   /**
    * Returns the number of iterations.
    * @return The number of iterations.
    */
   protected int getCollectionSize()
   {
      return getNumIterations();
   }

   /**
    * Returns an <code>Iterator</code> that iterates over the desired values.
    * @return An <code>Iterator</code>.
    */
   protected Iterator<Integer> getLoopIterator()
   {
      return new ForTagIterator();
   }

   /**
    * Place the index "item" into the <code>Map</code> of beans.
    *
    * @param context The <code>TagContext</code>.
    * @param currBlock The <code>Block</code> that is about to processed.
    * @param item The <code>Object</code> that resulted from the iterator.
    * @param index The iteration index (0-based).
    */
   protected void beforeBlockProcessed(TagContext context, Block currBlock, Object item, int index)
   {
      Map<String, Object> beans = context.getBeans();
      beans.put(myVarName, item);
   }

   /**
    * Remove the index "item" from the <code>Map</code> of beans.
    *
    * @param context The <code>TagContext</code>.
    * @param index The iteration index (0-based).
    * @param item The <code>Object</code> that resulted from the iterator.
    * @param currBlock The <code>Block</code> that was just processed.
    */
   protected void afterBlockProcessed(TagContext context, Block currBlock, Object item, int index)
   {
      Map<String, Object> beans = context.getBeans();
      beans.remove(myVarName);
   }

   /**
    * The <code>Iterator</code> over the index values.
    */
   private class ForTagIterator implements Iterator<Integer>
   {
      private int myValue;

      /**
       * Construct a <code>ForTagIterator</code> that is initialized to the
       * start value.
       */
      private ForTagIterator()
      {
         myValue = myStart;
      }

      /**
       * It doesn't make sense to remove values.
       */
      public void remove()
      {
         throw new UnsupportedOperationException("ForTagIterator: Remove not supported!");
      }

      /**
       * Returns the next value.
       * @return The next value.
       */
      public Integer next()
      {
         int value = myValue;
         // Prepare the next value.
         myValue += myStep;
         return value;
      }

      /**
       * Returns <code>true</code> if there are more items to process;
       * <code>false</code> otherwise.
       * @return <code>true</code> if there are more items to process;
       *    <code>false</code> otherwise.
       */
      public boolean hasNext()
      {
         return ((myStep > 0 && myValue <= myEnd) || (myStep < 0 && myValue >= myEnd));
      }
   }
}

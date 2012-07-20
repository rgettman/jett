package net.sf.jett.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jett.exception.TagParseException;
import net.sf.jett.expression.Expression;

/**
 * <p>A <code>ForEachTag</code> represents a repetitively placed
 * <code>Block</code> of <code>Cells</code>, with each repetition corresponding
 * to an element of a <code>Collection</code>.  The <code>var</code> attribute
 * represents the variable name by which a collection item is known. The
 * optional <code>indexVar</code> attribute is the name of the variable that
 * holds the iterator index.  The optional <code>where</code> attribute filters
 * the collection by the given condition.  The optional <code>limit</code>
 * attribute specifies a limit to the number of iterations to be run from the
 * collection.  If the limit is greater than the number of items in the
 * collection, then blank blocks will result, with the exact result dependent
 * on "past end action" rules.</p>
 *
 * <br>Attributes:
 * <ul>
 * <li>copyRight (optional): <code>boolean</code>
 * <li>fixed (optional): <code>boolean</code>
 * <li>pastEndAction (optional): <code>String</code>
 * <li>items (required): <code>Collection</code>
 * <li>var (required): <code>String</code>
 * <li>indexVar (optional): <code>String</code>
 * <li>where (optional): <code>boolean</code>
 * <li>limit (optional): <code>int</code>
 * </ul>
 */
public class ForEachTag extends BaseLoopTag
{
   private static final boolean DEBUG = false;

   /**
    * Attribute for specifying the <code>Collection</code> over which to
    * iterate.
    */
   public static final String ATTR_ITEMS = "items";
   /**
    * Attribute for specifying the "looping variable" name.
    */
   public static final String ATTR_VAR = "var";
   /**
    * Attribute for specifying the name of the variable to be exposed that
    * indicates the 0-based index position into the <code>Collection</code>.
    */
   public static final String ATTR_INDEXVAR = "indexVar";
   /**
    * Attribute for specifying the condition that filters the
    * <code>Collection</code> contents before display.
    */
   public static final String ATTR_WHERE = "where";
   /**
    * Attribute for specifying the number of iterations to be displayed.
    */
   public static final String ATTR_LIMIT = "limit";
   private static final List<String> REQ_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_ITEMS, ATTR_VAR));
   private static final List<String> OPT_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_INDEXVAR, ATTR_WHERE, ATTR_LIMIT));

   private Collection<Object> myCollection = null;
   private String myCollectionName = null;
   private String myVarName = null;
   private String myIndexVarName = null;
   private int myLimit = 0;

   /**
    * Returns this <code>Tag's</code> name.
    * @return This <code>Tag's</code> name.
    */
   public String getName()
   {
      return "forEach";
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
    * Validates the attributes for this <code>Tag</code>.  The "items"
    * attribute must be a <code>Collection</code>.  The "limit", if present,
    * must be a non-negative integer.
    */
   @Override
   @SuppressWarnings("unchecked")
   public void validateAttributes() throws TagParseException
   {
      super.validateAttributes();
      if (isBodiless())
         throw new TagParseException("ForEach tags must have a body.");

      TagContext context = getContext();
      Map<String, Object> beans = context.getBeans();

      Map<String, String> attributes = getAttributes();
      String attrItems = attributes.get(ATTR_ITEMS);
      Object items = Expression.evaluateString(attrItems, beans);
      if (items == null)
      {
         // Allow null to be interpreted as an empty collection.
         items = new ArrayList<Object>(0);
      }
      if (!(items instanceof Collection))
         throw new TagParseException("The \"items\" attribute is not a Collection: " + attrItems);
      myCollection = (Collection<Object>) items;
      // Collection name.
      int beginExprIdx = attrItems.indexOf(Expression.BEGIN_EXPR);
      int endExprIdx = attrItems.indexOf(Expression.END_EXPR);
      if (beginExprIdx != -1 && endExprIdx != -1 && endExprIdx > beginExprIdx)
      {
         myCollectionName = attrItems.substring(beginExprIdx +
            Expression.BEGIN_EXPR.length(), endExprIdx);
      }

      if (DEBUG)
         System.err.println("ForEachTag: Collection \"" + attrItems + "\" has size " + myCollection.size());

      String attrVarName = attributes.get(ATTR_VAR);
      myVarName = Expression.evaluateString(attrVarName, beans).toString();

      String attrIndexVarName = attributes.get(ATTR_INDEXVAR);
      if (attrIndexVarName != null)
         myIndexVarName = Expression.evaluateString(attrIndexVarName, beans).toString();

      String strCondition = attributes.get(ATTR_WHERE);

      if (strCondition != null)
      {
         // Create a new Collection containing only those items where the given
         // condition is true.
         ArrayList<Object> newCollection = new ArrayList<Object>();
         for (Object item : myCollection)
         {
            beans.put(myVarName, item);
            Object condition = Expression.evaluateString(strCondition, beans);
            if (condition instanceof Boolean)
            {
               if ((Boolean) condition)
                 newCollection.add(item);
            }
            // Note: parseBoolean returns true if the String argument is not
            // null, and it equals "true", ignoring case.
            else if (Boolean.parseBoolean(condition.toString()))
               newCollection.add(item);
         }
         beans.remove(myVarName);
         myCollection = newCollection;
      }

      String strLimit = attributes.get(ATTR_LIMIT);
      if (strLimit != null)
      {
         try
         {
            Object limit = Expression.evaluateString(strLimit, beans);
            myLimit = Integer.parseInt(limit.toString());
         }
         catch (NumberFormatException e)
         {
            throw new TagParseException("The limit attribute must be an integer: " + strLimit);
         }
         if (myLimit < 0)
         {
            throw new TagParseException("The limit attribute must be non-negative: " + myLimit);
         }
      }
      else
         myLimit = myCollection.size();

      if (DEBUG)
         System.err.println("ForEachTag.vA: myLimit=" + myLimit);
   }

   /**
    * Returns the names of the <code>Collections</code> that are being used in
    * this <code>ForEachTag</code>.
    * @return A <code>List</code> of one collection name.
    */
   protected List<String> getCollectionNames()
   {
      return Arrays.asList(myCollectionName);
   }

   /**
    * Returns the number of iterations.
    * @return The number of iterations.
    */
   protected int getNumIterations()
   {
      return myLimit;
   }

   /**
    * Returns the size of the collection being iterated.
    * @return The size of the collection being iterated.
    */
   protected int getCollectionSize()
   {
      return myCollection.size();
   }

   /**
    * Returns an <code>Iterator</code> that iterates over some
    * <code>Collection</code> of objects.
    * @return An <code>Iterator</code>.
    */
   protected Iterator<Object> getLoopIterator()
   {
      return new ForEachTagIterator();
   }

   /**
    * Place the <code>Iterator</code> item into the <code>Map</code> of beans.
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

      if (DEBUG)
         System.err.println("ForEachTag.beforeBP: index=" + index);

      // Optional index counter variable.
      if (myIndexVarName != null && myIndexVarName.length() > 0)
         beans.put(myIndexVarName, index);
   }

   /**
    * Remove the <code>Iterator</code> item from the <code>Map</code> of beans.
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

      // Optional index counter variable.
      if (myIndexVarName != null && myIndexVarName.length() > 0)
         beans.remove(myIndexVarName);
   }

   /**
    * The <code>Iterator</code> over the collection items, which can be
    * extended by a large limit to return <code>nulls</code> beyond the limit
    * of the <code>Collection</code>.
    */
   private class ForEachTagIterator implements Iterator<Object>
   {
      private int myIndex;
      private Iterator<Object> myInternalIterator;

      /**
       * Construct a <code>ForEachTagIterator</code> whose index is initialized
       * to zero.
       */
      private ForEachTagIterator()
      {
         myIndex = 0;
         myInternalIterator = myCollection.iterator();
      }

      /**
       * It doesn't make sense to remove values.
       */
      public void remove()
      {
         throw new UnsupportedOperationException("ForEachTagIterator: Remove not supported!");
      }

      /**
       * Returns the next value.
       * @return The next value.
       */
      public Object next()
      {
         Object next = null;
         myIndex++;
         if (myIndex <= myCollection.size())
         next = myInternalIterator.next();
         if (DEBUG)
            System.err.println("ForEachTag: next: \"" + ((next == null) ? "(null)" : next.toString()) + "\".");
         return next;
      }

      /**
       * Determines if there are any items left, possibly <code>null</code>
       * items if the limit is larger than the collection size.
       * @return <code>true</code> if there are more items to process;
       *    <code>false</code> otherwise.
       */
      public boolean hasNext()
      {
         return myIndex < myLimit;
      }
   }
}


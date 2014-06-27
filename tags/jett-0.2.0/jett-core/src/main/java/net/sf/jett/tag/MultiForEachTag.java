package net.sf.jett.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.RichTextString;

import net.sf.jett.exception.TagParseException;
import net.sf.jett.expression.Expression;
import net.sf.jett.util.SheetUtil;

/**
 * <p>A <code>MultiForEachTag</code> represents a repetitively placed
 * <code>Block</code> of <code>Cells</code>, with each repetition corresponding
 * to the same index into multiple <code>Collections</code>.
 * The <code>vars</code> attribute represents the variable names corresponding
 * to what each <code>Collection</code>'s item is known by.  The optional
 * <code>indexVar</code> attribute is the name of the variable that holds the
 * iterator index.  The optional <code>limit</code> attribute specifies a limit
 * to the number of iterations to be run for the <code>Collections</code>.  If
 * the limit is greater than the number of items in any of the collections,
 * then blank blocks will result, with the exact result dependent on "past end
 * action" rules.</p>
 *
 * <br>Attributes:
 * <ul>
 * <li>copyRight (optional): <code>boolean</code>
 * <li>fixed (optional): <code>boolean</code>
 * <li>pastEndAction (optional): <code>String</code>
 * <li>collections (required): <code>Collection</code>
 * <li>var (required): <code>String</code>
 * <li>indexVar (optional): <code>String</code>
 * <li>limit (optional): <code>int</code>
 * </ul>
 */
public class MultiForEachTag extends BaseLoopTag
{
   private static final boolean DEBUG = false;

   /**
    * Attribute for specifying the <code>Collections</code> over which to
    * iterate.
    */
   public static final String ATTR_COLLECTIONS = "collections";
   /**
    * Attribute for specifying the "looping variable" names.
    */
   public static final String ATTR_VARS = "vars";
   /**
    * Attribute for specifying the name of the variable to be exposed that
    * indicates the 0-based index position into the <code>Collection</code>.
    */
   public static final String ATTR_INDEXVAR = "indexVar";
   /**
    * Attribute for specifying the number of iterations to be displayed.
    */
   public static final String ATTR_LIMIT = "limit";
   private static final List<String> REQ_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_COLLECTIONS, ATTR_VARS));
   private static final List<String> OPT_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_INDEXVAR, ATTR_LIMIT));

   /**
    * Separates <code>Collection</code> expression strings and "vars" variable
    * name strings.
    */
   public static final String SPEC_SEP = ";";

   private List<Collection<Object>> myCollections = null;
   private List<String> myCollectionNames = null;
   private List<String> myVarNames = null;
   private String myIndexVarName = null;
   private int myLimit = 0;
   private int myMaxSize = 0;

   /**
    * Returns this <code>Tag's</code> name.
    * @return This <code>Tag's</code> name.
    */
   public String getName()
   {
      return "multiForEach";
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
         throw new TagParseException("MultiForEach tags must have a body.");

      TagContext context = getContext();
      Map<String, Object> beans = context.getBeans();

      Map<String, RichTextString> attributes = getAttributes();
      String attrCollExpressions = attributes.get(ATTR_COLLECTIONS).getString();
      String[] collExpressions = attrCollExpressions.split(SPEC_SEP);
      myCollections = new ArrayList<Collection<Object>>();
      myCollectionNames = new ArrayList<String>();
      for (String collExpression : collExpressions)
      {
         Object items = Expression.evaluateString(collExpression.trim(), beans);
         if (items == null)
         {
            // Allow null to be interpreted as an empty collection.
            items = new ArrayList<Object>(0);
         }
         if (!(items instanceof Collection))
            throw new TagParseException("One of the items in the \"collections\" attribute is not a Collection: " +
               collExpression);
         Collection<Object> collection = (Collection<Object>) items;
         myCollections.add(collection);
         // Collection names.
         int beginExprIdx = collExpression.indexOf(Expression.BEGIN_EXPR);
         int endExprIdx = collExpression.indexOf(Expression.END_EXPR);
         if (beginExprIdx != -1 && endExprIdx != -1 && endExprIdx > beginExprIdx)
         {
            myCollectionNames.add(collExpression.substring(beginExprIdx +
               Expression.BEGIN_EXPR.length(), endExprIdx));
         }
         if (DEBUG)
            System.err.println("MultiForEachTag: Collection \"" + collExpression + "\" has size " + collection.size());

      }

      myVarNames = new ArrayList<String>();
      String attrVarNames = attributes.get(ATTR_VARS).getString();
      String[] varExpressions = attrVarNames.split(SPEC_SEP);
      for (String varExpression : varExpressions)
      {
         myVarNames.add(Expression.evaluateString(varExpression.trim(), beans).toString());
      }

      if (myCollections.size() < 1)
         throw new TagParseException("Must specify at least one Collection.");
      if (myCollections.size() != myVarNames.size())
         throw new TagParseException("The number of collections and the number of variable names must be the same.");

      RichTextString rtsIndexVarName = attributes.get(ATTR_INDEXVAR);
      String attrIndexVarName = (rtsIndexVarName != null) ? rtsIndexVarName.getString() : null;
      if (attrIndexVarName != null)
         myIndexVarName = Expression.evaluateString(attrIndexVarName, beans).toString();

      // Determine the maximum size of all collections.
      myMaxSize = 0;
      for (Collection<Object> collection : myCollections)
      {
         int size = collection.size();
         if (size > myMaxSize)
            myMaxSize = size;
      }

      myLimit = 0;
      RichTextString rtsAttrLimit = attributes.get(ATTR_LIMIT);
      String strLimit = (rtsAttrLimit != null) ? rtsAttrLimit.getString() : null;
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
      {
         // Limit defaults to maximum collection size.
         myLimit = myMaxSize;
      }

      if (DEBUG)
         System.err.println("ForEachTag.vA: myLimit=" + myLimit);
   }

   /**
    * Returns the names of the <code>Collections</code> that are being used in
    * this <code>MultiForEachTag</code>.
    * @return A <code>List</code> of one collection name.
    */
   protected List<String> getCollectionNames()
   {
      return myCollectionNames;
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
    * Returns the maximum size of the collections being iterated.
    * @return The maximum size of the collections being iterated.
    */
   protected int getCollectionSize()
   {
      return myMaxSize;
   }

   /**
    * Returns an <code>Iterator</code> that iterates over all the items of all
    * specified <code>Collections</code> of values.  Its item is a
    * <code>List</code> of items created by pulling values from all
    * <code>Collections</code> using the same index for each
    * <code>Collection</code>.
    * @return An <code>Iterator</code>.
    */
   protected Iterator<List<Object>> getLoopIterator()
   {
      return new MultiForEachTagIterator();
   }

   /**
    * Place the values from the <code>List</code> of collection item values
    * into the <code>Map</code> of beans.
    *
    * @param context The <code>TagContext</code>.
    * @param currBlock The <code>Block</code> that is about to processed.
    * @param item The <code>Object</code> that resulted from the iterator.
    * @param index The iteration index (0-based).
    */
   @SuppressWarnings("unchecked")
   protected void beforeBlockProcessed(TagContext context, Block currBlock, Object item, int index)
   {
      Map<String, Object> beans = context.getBeans();
      List<Object> listOfValues = (List<Object>) item;
      List<String> pastEndRefs = new ArrayList<String>();
      for (int i = 0; i < myCollections.size(); i++)
      {
         String varName = myVarNames.get(i);
         Object value = listOfValues.get(i);
         if (value != null && value instanceof PastEndValue)
            pastEndRefs.add(varName);
         else
            beans.put(varName, value);
      }

      if (DEBUG)
         System.err.println("MultiForEachTag.beforeBP: index=" + index);
      // If not past the "collection" size, but a Collection is exhausted, then
      // take "past end actions" on individual Cells in tbe Block.
      if (index < getCollectionSize())
         SheetUtil.takePastEndAction(context.getSheet(), currBlock, pastEndRefs, getPastEndAction());

      // Optional index counter variable.
      if (myIndexVarName != null && myIndexVarName.length() > 0)
         beans.put(myIndexVarName, index);
   }

   /**
    * Remove the values from the <code>List</code> of collection item values
    * from the <code>Map</code> of beans.
    *
    * @param context The <code>TagContext</code>.
    * @param index The iteration index (0-based).
    * @param item The <code>Object</code> that resulted from the iterator.
    * @param currBlock The <code>Block</code> that was just processed.
    */
   protected void afterBlockProcessed(TagContext context, Block currBlock, Object item, int index)
   {
      Map<String, Object> beans = context.getBeans();
      for (int i = 0; i < myCollections.size(); i++)
         beans.remove(myVarNames.get(i));

      // Optional index counter variable.
      if (myIndexVarName != null && myIndexVarName.length() > 0)
         beans.remove(myIndexVarName);
   }

   /**
    * The <code>Iterator</code> over the items in all collections, which can be
    * extended by a large limit to return <code>nulls</code> beyond the size
    * of each <code>Collection</code>.
    */
   private class MultiForEachTagIterator implements Iterator<List<Object>>
   {
      private int myIndex;
      private List<Iterator<Object>> myIterators;

      /**
       * Construct a <code>MultiForEachTagIterator</code> that is initialized to
       * zero.
       */
      private MultiForEachTagIterator()
      {
         myIndex = 0;
         myIterators = new ArrayList<Iterator<Object>>();
         for (Collection<Object> collection : myCollections)
            myIterators.add(collection.iterator());
      }

      /**
       * It doesn't make sense to remove values.
       */
      public void remove()
      {
         throw new UnsupportedOperationException("MultiForEachTagIterator: Remove not supported!");
      }

      /**
       * Returns the next value.  Each iteration produces a <code>List</code>
       * of variable values.  The values are the <code>Collection</code> values
       * from all specified collections, using the same index into all
       * <code>Collections</code>.
       * @return A <code>List</code> of variable values.
       */
      public List<Object> next()
      {
         List<Object> next = new ArrayList<Object>();
         for (int i = 0; i < myCollections.size(); i++)
         {
            Object value = PastEndValue.PAST_END_VALUE;
            Iterator<Object> iterator = myIterators.get(i);
            if (iterator.hasNext())
               value = iterator.next();
            next.add(value);
         }
         myIndex++;
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

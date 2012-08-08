package net.sf.jett.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.RichTextString;

import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;

import net.sf.jett.exception.TagParseException;
import net.sf.jett.expression.Expression;
import net.sf.jett.transform.BlockTransformer;

/**
 * <p>An <code>AggTag</code> represents possibly many aggregate values
 * calculated from a <code>List</code> of values already exposed to the
 * context.  It uses <code>jAgg</code> functionality and exposes the results
 * and <code>Aggregators</code> used for display later.</p>
 *
 * <br>Attributes:
 * <ul>
 * <li>items (required): <code>List</code>
 * <li>aggs (required): <code>String</code>
 * <li>aggsVar (optional): <code>String</code>
 * <li>valuesVar (required): <code>String</code>
 * <li>groupBy (optional): <code>String</code>
 * <li>parallel (optional): <code>int</code>
 * </ul>
 */
public class AggTag extends BaseTag
{
   /**
    * Attribute that specifies the <code>List</code> of items to aggregate.
    */
   public static final String ATTR_ITEMS = "items";
   /**
    * Attribute that specifies the <code>List</code> of Aggregators to use.
    */
   public static final String ATTR_AGGS = "aggs";
   /**
    * Attribute that specifies the name of the <code>List</code> of exposed
    * aggregators.
    */
   public static final String ATTR_AGGS_VAR = "aggsVar";
   /**
    * Attribute that specifies the <code>List</code> of exposed aggregation
    * values.
    */
   public static final String ATTR_VALUES_VAR = "valuesVar";
   /**
    * Attribute that specifies the <code>List</code> of group-by properties.
    */
   public static final String ATTR_GROUP_BY = "groupBy";
   /**
    * Attribute that specifies the degree of parallelism to use.
    */
   public static final String ATTR_PARALLEL = "parallel";
   private static final List<String> REQ_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_ITEMS, ATTR_AGGS, ATTR_VALUES_VAR));
   private static final List<String> OPT_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_AGGS_VAR, ATTR_GROUP_BY, ATTR_PARALLEL));

   /**
    * Separates <code>Aggregator</code> specification strings and "group by"
    * property strings.
    */
   public static final String SPEC_SEP = ";";

   private List<Object> myList = null;
   private List<Aggregator> myAggs = null;
   private String myAggsVar = null;
   private String myValuesVar = null;
   private List<String> myGroupByProps = null;
   private int myParallelism = 1;

   /**
    * Returns this <code>Tag's</code> name.
    * @return This <code>Tag's</code> name.
    */
   public String getName()
   {
      return "agg";
   }

   /**
    * Returns a <code>List</code> of required attribute names.
    * @return A <code>List</code> of required attribute names.
    */
   protected List<String> getRequiredAttributes()
   {
      return REQ_ATTRS;
   }

   /**
    * Returns a <code>List</code> of optional attribute names.
    * @return A <code>List</code> of optional attribute names.
    */
   protected List<String> getOptionalAttributes()
   {
      return OPT_ATTRS;
   }

   /**
    * Validates the attributes for this <code>Tag</code>.  The "items"
    * attribute must be a <code>List</code>.  The "parallel" attribute must be
    * a positive integer (defaults to 1).  The "aggs" attribute must be a
    * semicolon-separated list of valid <code>Aggregator</code> specification
    * strings.  The "valuesVar" attribute must be a string that indicates the
    * name to which the aggregate values will be exposed in the
    * <code>Map</code> of beans.  The "aggsVar" attribute must be a string that
    * indicates the name of the <code>List</code> that contains all created
    * <code>Aggregators</code> and to which that will be exposed in the
    * <code>Map</code> of beans.  The "groupBy" attribute must be a semicolon-
    * separated list of properties with which to "group" aggregated
    * calculations (defaults to no "group by" properties).  The "agg" tag must
    * have a body.
    */
   @SuppressWarnings("unchecked")
   public void validateAttributes() throws TagParseException
   {
      if (isBodiless())
         throw new TagParseException("Agg tags must have a body.");

      TagContext context = getContext();
      Map<String, Object> beans = context.getBeans();
      Map<String, RichTextString> attributes = getAttributes();

      String attrItems = attributes.get(ATTR_ITEMS).getString();
      Object items = Expression.evaluateString(attrItems, beans);
      if (!(items instanceof List))
         throw new TagParseException("The \"items\" expression is not a List: " + attrItems);
      myList = (List<Object>) items;

      String attrAggs = attributes.get(ATTR_AGGS).getString();
      Object aggs = Expression.evaluateString(attrAggs, beans);
      // Allow delimited list in a String, or an actual List.
      if (aggs instanceof String)
      {
         String[] aggSpecs = ((String) aggs).split(SPEC_SEP);
         myAggs = new ArrayList<Aggregator>(aggSpecs.length);
         for (String aggSpec : aggSpecs)
            myAggs.add(Aggregator.getAggregator(aggSpec));
      }
      else if (aggs instanceof List)
      {
         List aggsList = (List) aggs;
         myAggs = new ArrayList<Aggregator>(aggsList.size());
         for (Object aggSpec : aggsList)
            myAggs.add(Aggregator.getAggregator(aggSpec.toString()));
      }
      else
         throw new TagParseException("Attribute \"" + ATTR_AGGS +
            "\" must be a delimited String of, or a List of, Aggregator specification Strings.");

      RichTextString strAggsVar = attributes.get(ATTR_AGGS_VAR);
      String attrAggsVar = (strAggsVar != null) ? strAggsVar.getString() : null;
      if (attrAggsVar != null)
         myAggsVar = Expression.evaluateString(attrAggsVar, beans).toString();

      String attrValuesVar = attributes.get(ATTR_VALUES_VAR).getString();
      myValuesVar = Expression.evaluateString(attrValuesVar, beans).toString();

      myGroupByProps = new ArrayList<String>();
      Object attrGroupBy = attributes.get(ATTR_GROUP_BY);
      // Allow delimited list in a String, or an actual List.
      if (attrGroupBy != null)
      {
         Object groupBys = Expression.evaluateString(attrGroupBy.toString(), beans);
         if (groupBys instanceof String)
         {
            myGroupByProps.addAll(Arrays.asList(((String) groupBys).split(SPEC_SEP)));
         }
         else if (attrGroupBy instanceof List)
         {
            for (Object groupBy : (List) attrGroupBy)
               myGroupByProps.add(groupBy.toString());
         }
         else
            throw new TagParseException("Attribute \"" + ATTR_GROUP_BY +
               "\" must be a delimited String of, or a List of, \"group by\" property Strings.");
      }

      RichTextString rtsParallelism = attributes.get(ATTR_PARALLEL);
      String attrParallelism = (rtsParallelism != null) ? rtsParallelism.getString() : null;
      if (attrParallelism != null)
      {
         String parallelism = Expression.evaluateString(attrParallelism, beans).toString();
         try
         {
            myParallelism = Integer.parseInt(parallelism);
         }
         catch (NumberFormatException e)
         {
            throw new TagParseException("Parallel attribute must be a number: " + parallelism);
         }
         if (myParallelism <= 0)
         {
            throw new TagParseException("Parallel attribute must be positive: " + parallelism);
         }
      }
   }

   /**
    * Run a "group by" operation on the specified <code>Aggregators</code>, get
    * the results, and expose the aggregate values and the
    * <code>Aggregators</code> used.
    * @return Whether the first <code>Cell</code> in the <code>Block</code>
    *    associated with this <code>Tag</code> was processed.
    */
   public boolean process()
   {
      TagContext context = getContext();
      Map<String, Object> beans = context.getBeans();

      List<AggregateValue<Object>> aggValues =
         Aggregations.groupBy(myList, myGroupByProps, myAggs, myParallelism);

      beans.put(myValuesVar, aggValues);
      if (myAggsVar != null)
         beans.put(myAggsVar, myAggs);

      BlockTransformer transformer = new BlockTransformer();
      transformer.transform(context, getWorkbookContext());

      return true;
   }
}
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
import net.sf.jett.transform.BlockTransformer;
import net.sf.jett.util.AttributeUtil;

/**
 * <p>An <code>AggTag</code> represents possibly many aggregate values
 * calculated from a <code>List</code> of values already exposed to the
 * context.  It uses <code>jAgg</code> functionality and exposes the results
 * and <code>Aggregators</code> used for display later.</p>
 *
 * <br>Attributes:
 * <ul>
 * <li><em>Inherits all attributes from {@link BaseTag}.</em>
 * <li>items (required): <code>List</code>
 * <li>aggs (required): <code>String</code>
 * <li>aggsVar (optional): <code>String</code>
 * <li>valuesVar (required): <code>String</code>
 * <li>groupBy (optional): <code>String</code>
 * <li>parallel (optional): <code>int</code>
 * </ul>
 *
 * @author Randy Gettman
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
   @Override
   protected List<String> getRequiredAttributes()
   {
      List<String> reqAttrs = super.getRequiredAttributes();
      reqAttrs.addAll(REQ_ATTRS);
      return reqAttrs;
   }

   /**
    * Returns a <code>List</code> of optional attribute names.
    * @return A <code>List</code> of optional attribute names.
    */
   @Override
   protected List<String> getOptionalAttributes()
   {
      List<String> optAttrs = super.getOptionalAttributes();
      optAttrs.addAll(OPT_ATTRS);
      return optAttrs;
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
      super.validateAttributes();
      if (isBodiless())
         throw new TagParseException("Agg tags must have a body.");

      TagContext context = getContext();
      Map<String, Object> beans = context.getBeans();
      Map<String, RichTextString> attributes = getAttributes();

      myList = AttributeUtil.evaluateObject(attributes.get(ATTR_ITEMS), beans, ATTR_ITEMS, List.class, null);

      List<String> aggsList = AttributeUtil.evaluateList(attributes.get(ATTR_AGGS), beans, null);
      myAggs = new ArrayList<Aggregator>(aggsList.size());
      for (String aggSpec : aggsList)
         myAggs.add(Aggregator.getAggregator(aggSpec));

      myAggsVar = AttributeUtil.evaluateString(attributes.get(ATTR_AGGS_VAR), beans, null);

      myValuesVar = AttributeUtil.evaluateString(attributes.get(ATTR_VALUES_VAR), beans, null);

      myGroupByProps = AttributeUtil.evaluateList(attributes.get(ATTR_GROUP_BY), beans, new ArrayList<String>());

      myParallelism = AttributeUtil.evaluatePositiveInt(attributes.get(ATTR_PARALLEL), beans, ATTR_PARALLEL, 1);
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
package net.sf.jett.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import net.sf.jett.exception.TagParseException;
import net.sf.jett.expression.Expression;
import net.sf.jett.transform.BlockTransformer;
import net.sf.jett.util.SheetUtil;

/**
 * <p>A <code>TotalTag</code> represents an aggregate value calculated from a
 * <code>List</code> of values already exposed to the context.  This uses
 * <code>jAgg</code> functionality.</p>
 *
 * <br>Attributes:
 * <ul>
 * <li>items (required): <code>List</code>
 * <li>value (required): <code>String</code>
 * <li>parallel (optional): <code>int</code>
 * </ul>
 */
public class TotalTag extends BaseTag
{
   /**
    * Attribute that specifies the <code>List</code> of items to aggregate.
    */
   public static final String ATTR_ITEMS = "items";
   /**
    * Attribute that specifies the aggregator to use.
    */
   public static final String ATTR_VALUE = "value";
   /**
    * Attribute that specifies the degree of parallelism to use.
    */
   public static final String ATTR_PARALLEL = "parallel";
   private static final List<String> REQ_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_ITEMS, ATTR_VALUE));
   private static final List<String> OPT_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_PARALLEL));

   private List<Object> myList = null;
   private Aggregator myAggregator = null;
   private int myParallelism = 1;

   /**
    * Returns this <code>Tag's</code> name.
    * @return This <code>Tag's</code> name.
    */
   public String getName()
   {
      return "total";
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
    * a positive integer (defaults to 1).  The "value" attribute must be a
    * valid <code>Aggregator</code> specification string.  The "total" tag must
    * not have a body.
    */
   @SuppressWarnings("unchecked")
   public void validateAttributes() throws TagParseException
   {
      if (!isBodiless())
         throw new TagParseException("Total tags must not have a body.");

      TagContext context = getContext();
      Map<String, Object> beans = context.getBeans();
      Map<String, String> attributes = getAttributes();

      String attrItems = attributes.get(ATTR_ITEMS);
      Object items = Expression.evaluateString(attrItems, beans);
      if (!(items instanceof List))
         throw new TagParseException("The \"items\" expression is not a List: " + attrItems);
      myList = (List<Object>) items;

      String attrParallelism = attributes.get(ATTR_PARALLEL);
      if (attrParallelism != null)
      {
         String parallelism = Expression.evaluateString(attrParallelism, beans).toString();
         try
         {
            myParallelism = Integer.parseInt(parallelism);
         }
         catch (NumberFormatException e)
         {
            throw new TagParseException("Parallel attribute must be an integer: " + parallelism);
         }
         if (myParallelism <= 0)
         {
            throw new TagParseException("Parallel attribute must be positive: " + parallelism);
         }
      }

      String aggSpec = Expression.evaluateString(attributes.get(ATTR_VALUE), beans).toString();
      myAggregator = Aggregator.getAggregator(aggSpec);
   }

   /**
    * Run a "group by" operation on the specified <code>Aggregator</code>, get
    * the result, and set the cell value appropriately.
    * @return Whether the first <code>Cell</code> in the <code>Block</code>
    *    associated with this <code>Tag</code> was processed.
    */
   public boolean process()
   {
      TagContext context = getContext();
      Sheet sheet = context.getSheet();
      Block block = context.getBlock();

      List<String> propsList = new ArrayList<String>(0);
      List<Aggregator> aggList = new ArrayList<Aggregator>(1);
      aggList.add(myAggregator);
      List<AggregateValue<Object>> aggValues =
         Aggregations.groupBy(myList, propsList, aggList, myParallelism);
      // There should be only one AggregateValue with no properties to group by.
      AggregateValue aggValue = aggValues.get(0);
      Object value = aggValue.getAggregateValue(myAggregator);
      // Replace the bodiless tag text with the proper result.
      Row row = sheet.getRow(block.getTopRowNum());
      Cell cell = row.getCell(block.getLeftColNum());
      SheetUtil.setCellValue(cell, value);

      BlockTransformer transformer = new BlockTransformer();
      transformer.transform(context, getWorkbookContext());
      return true;
   }
}
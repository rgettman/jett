package net.sf.jett.expression;

import java.util.ArrayList;
import java.util.List;

import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;

/**
 * A <code>JaggFuncs</code> object is an object that represents jAgg aggregate
 * functionality in the JEXL world.
 */
public class JaggFuncs
{
   /**
    * Have jAgg evaluate an Aggregate Expression.
    * @param values A <code>List</code> of values to aggregate.
    * @param aggSpecString An <em>aggregator specification string</em>, e.g.
    *    "Count(*)", "Sum(quantity)".
    * @return The result of the aggregate operation.
    */
   public static Object eval(List<Object> values, String aggSpecString)
   {
      List<Aggregator> aggs = new ArrayList<Aggregator>(1);
      Aggregator agg = Aggregator.getAggregator(aggSpecString.trim());
      aggs.add(agg);
      List<String> props = new ArrayList<String>(0);
      List<AggregateValue<Object>> aggValues = Aggregations.groupBy(values, props, aggs);
      // There should be only one AggregateValue returned (no group-by properties!)
      return aggValues.get(0).getAggregateValue(agg);
   }
}

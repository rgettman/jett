package net.sf.jett.expression;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jexl2.JexlEngine;

/**
 * An <code>ExpressionFactory</code> is a singleton factory class that creates
 * and uses a <code>JexlEngine</code> to create JEXL <code>Expressions</code>.
 */
public class ExpressionFactory
{
   private static final ExpressionFactory theFactory = new ExpressionFactory();

   private JexlEngine myEngine;

   /**
    * Singleton constructor.
    */
   private ExpressionFactory()
   {
      myEngine = new JexlEngine();
      myEngine.setLenient(true);
      myEngine.setSilent(false);
      Map<String, Object> funcs = new HashMap<String, Object>();
      myEngine.setFunctions(funcs);
      funcs.put("jagg", JaggFuncs.class);
   }

   /**
    * Passes the given "lenient" flag on to the internal
    * <code>JexlEngine</code>.
    * @param lenient Whether the internal <code>JexlEngine</code> should be
    *    "lenient".
    */
   public void setLenient(boolean lenient)
   {
      myEngine.setLenient(lenient);
   }

   /**
    * Returns the internal <code>JexlEngine's</code> "lenient" flag.
    * @return Whether the internal <code>JexlEngine</code> is currently
    *    "lenient".
    */
   public boolean isLenient()
   {
      return myEngine.isLenient();
   }

   /**
    * Passes the given "silent" flag on to the internal
    * <code>JexlEngine</code>.
    * @param silent Whether the internal <code>JexlEngine</code> should be
    *    "silent".
    */
   public void setSilent(boolean silent)
   {
      myEngine.setSilent(silent);
   }

   /**
    * Returns the internal <code>JexlEngine's</code> "silent" flag.
    * @return Whether the internal <code>JexlEngine</code> is currently
    *    "silent".
    */
   public boolean isSilent()
   {
      return myEngine.isSilent();
   }

   /**
    * Returns the singleton <code>ExpressionFactory</code>.
    * @return The singleton <code>ExpressionFactory</code>.
    */
   public static ExpressionFactory getExpressionFactory()
   {
      return theFactory;
   }

   /**
    * Create a JEXL <code>Expression</code> from a string.
    * @param expression The expression as a <code>String</code>.
    * @return A JEXL <code>Expression</code>.
    */
   public org.apache.commons.jexl2.Expression createExpression(String expression)
   {
      return myEngine.createExpression(expression);
   }
}


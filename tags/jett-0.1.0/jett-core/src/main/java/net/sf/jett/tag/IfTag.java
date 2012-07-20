package net.sf.jett.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import net.sf.jett.exception.TagParseException;
import net.sf.jett.expression.Expression;
import net.sf.jett.transform.BlockTransformer;
import net.sf.jett.util.SheetUtil;

/**
 * <p>An <code>IfTag</code> represents a conditionally placed
 * <code>Block</code> of <code>Cells</code>.</p>
 *
 * <br>Attributes:
 * <ul>
 * <li>test (required): <code>boolean</code>
 * <li>then (optional, bodiless only): <code>String</code>
 * <li>else (optional, bodiless only): <code>String</code>
 * <li>elseAction (optional, body only): <code>String</code>
 * </ul>
 */
public class IfTag extends BaseTag
{
   /**
    * Value for the "elseAction" attribute indicating to remove the block by
    * shifting cells up, if the test condition is false.
    */
   public static final String ELSE_ACTION_SHIFT_UP = "shiftup";
   /**
    * Value for the "elseAction" attribute indicating to remove the block by
    * shifting cells left, if the test condition is false.
    */
   public static final String ELSE_ACTION_SHIFT_LEFT = "shiftleft";
   /**
    * Value for the "elseAction" attribute indicating to remove the block by
    * clearing cell contents and not shifting cells, if the test condition is
    * false.
    */
   public static final String ELSE_ACTION_CLEAR = "clear";
   /**
    * Value for the "elseAction" attribute indicating to clear the block by
    * remove the cells, but not shifting other cells, if the test condition is
    * false.
    */
   public static final String ELSE_ACTION_REMOVE = "remove";

   /**
    * Attribute for specifying the <code>boolean</code> test condition.
    */
   public static final String ATTR_TEST = "test";
   /**
    * Attribute for specifying the value of the <code>Cell</code> if the
    * condition is <code>true</code> (bodiless if-tag only).
    */
   public static final String ATTR_THEN = "then";
   /**
    * Attribute for specifying the value of the <code>Cell</code> if the
    * condition is <code>false</code> (bodiless if-tag only).
    */
   public static final String ATTR_ELSE = "else";
   /**
    * Attribute for specifying the action to be taken if the condition is
    * <code>false</code> (if-tags with a body only).
    */
   public static final String ATTR_ELSE_ACTION = "elseAction";
   private static final List<String> REQ_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_TEST));
   private static final List<String> OPT_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_ELSE_ACTION));
   private static final List<String> REQ_ATTRS_BODILESS =
      new ArrayList<String>(Arrays.asList(ATTR_TEST, ATTR_THEN));
   private static final List<String> OPT_ATTRS_BODILESS =
      new ArrayList<String>(Arrays.asList(ATTR_ELSE));

   private String myElseAction;

   /**
    * Returns this <code>Tag's</code> name.
    * @return This <code>Tag's</code> name.
    */
   public String getName()
   {
      return "if";
   }

   /**
    * Returns a <code>List</code> of required attribute names.
    * @return A <code>List</code> of required attribute names.
    */
   protected List<String> getRequiredAttributes()
   {
      if (isBodiless())
         return REQ_ATTRS_BODILESS;
      else
         return REQ_ATTRS;
   }

   /**
    * Returns a <code>List</code> of optional attribute names.
    * @return A <code>List</code> of optional attribute names.
    */
   protected List<String> getOptionalAttributes()
   {
      if (isBodiless())
         return OPT_ATTRS_BODILESS;
      else
         return OPT_ATTRS;
   }

   /**
    * Validates the attributes for this <code>Tag</code>.  Some optional
    * attributes are only valid for bodiless tags, and others are only valid
    * for tags without bodies.
    */
   public void validateAttributes()
   {
      TagContext context = getContext();
      Map<String, Object> beans = context.getBeans();
      Map<String, String> attributes = getAttributes();
      Block block = context.getBlock();

      String attrElseAction = attributes.get(ATTR_ELSE_ACTION);
      if (attrElseAction != null)
      {
         String elseAction = Expression.evaluateString(attrElseAction, beans).toString();
         if (elseAction != null)
         {
            if (ELSE_ACTION_SHIFT_UP.equalsIgnoreCase(elseAction))
               block.setDirection(Block.Direction.VERTICAL);
            else if (ELSE_ACTION_SHIFT_LEFT.equalsIgnoreCase(elseAction))
               block.setDirection(Block.Direction.HORIZONTAL);
            else if (ELSE_ACTION_CLEAR.equalsIgnoreCase(elseAction) ||
                     ELSE_ACTION_REMOVE.equalsIgnoreCase(elseAction))
               block.setDirection(Block.Direction.NONE);
            else
               throw new TagParseException("IfTag: Illegal value for elseAction: \"" + elseAction + "\".");

            myElseAction = elseAction;
         }
      }
   }

   /**
    * <p>Evaluate the condition.</p>
    * <p>With Body: If it's true, transform the block of <code>Cells</code>.
    * If it's false, take the "elseAction", which defaults to removing the
    * block.</p>
    * <p>Bodiless: If it's true, evaluate the "then" condition.  If it's false,
    * evaluate the "else" condition, which defaults to a value of null.</p>
    * @return Whether the first <code>Cell</code> in the <code>Block</code>
    *    associated with this <code>Tag</code> was processed.
    */
   public boolean process()
   {
      TagContext context = getContext();
      Sheet sheet = context.getSheet();
      Block block = context.getBlock();
      Map<String, Object> beans = context.getBeans();

      Map<String, String> attributes = getAttributes();
      String testValue = attributes.get(ATTR_TEST);
      Object test = Expression.evaluateString(testValue, beans);
      boolean condition;
      if (test instanceof Boolean)
         condition = (Boolean) test;
      else
         condition = Boolean.parseBoolean(test.toString());

      if (isBodiless())
      {
         String result;
         if (condition)
            result = attributes.get(ATTR_THEN);
         else
            result = attributes.get(ATTR_ELSE);
         // Replace the bodiless tag text with the proper result.
         Row row = sheet.getRow(block.getTopRowNum());
         Cell cell = row.getCell(block.getLeftColNum());
         SheetUtil.setCellValue(cell, result);

         BlockTransformer transformer = new BlockTransformer();
         transformer.transform(context, getWorkbookContext());
      }
      else
      {
         if (condition)
         {
            BlockTransformer transformer = new BlockTransformer();
            transformer.transform(context, getWorkbookContext());
         }
         else
         {
            if (ELSE_ACTION_CLEAR.equals(myElseAction))
               clearBlock();
            else
               removeBlock();  // Takes care of remove, shiftLeft, and shiftUp.
            return false;
         }
      }
      return true;
   }
}

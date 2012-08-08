package net.sf.jett.test.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;

import net.sf.jett.expression.Expression;
import net.sf.jett.exception.TagParseException;
import net.sf.jett.tag.BaseTag;
import net.sf.jett.tag.Block;
import net.sf.jett.tag.TagContext;
import net.sf.jett.util.SheetUtil;

/**
 * An <code>AddOneTag</code> is a custom <code>Tag</code> that adds 1 to the
 * numeric "value" attribute.  The main purpose of this <code>Tag</code> is to
 * demonstrate custom tags and custom tag libraries.
 *
 * <br>Attributes:
 * <ul>
 * <li>value (required): <code>Number</code>
 * </ul>
 */
public class AddOneTag extends BaseTag
{
   /**
    * Attribute for specifying the value.
    */
   public static final String ATTR_VALUE = "value";

   private static final List<String> REQ_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_VALUE));

   private double myValue;

   /**
    * Returns this <code>Tag's</code> name.
    * @return This <code>Tag's</code> name.
    */
   public String getName()
   {
      return "addOne";
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
      return null;
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
      Map<String, RichTextString> attributes = getAttributes();

      if (!isBodiless())
         throw new TagParseException("AddOne tags must not have a body.");

      String attrValue = attributes.get(ATTR_VALUE).getString();
      if (attrValue != null)
      {
         Object value = Expression.evaluateString(attrValue, beans);
         if (value != null)
         {
            if (value instanceof Number)
               myValue = ((Number) value).doubleValue();
            else
            {
               try
               {
                  myValue = Double.parseDouble(value.toString());
               }
               catch (NumberFormatException e)
               {
                  throw new TagParseException("AddOneTag: Value not a number: \"" + attrValue + "\".", e);
               }
            }
         }
         else
            throw new TagParseException("AddOneTag: Null value found: \"" + attrValue + "\".");
      }
   }

   /**
    * Replace the cell's content with the value plus one.
    * @return <code>true</code>, this cell's content was processed.
    */
   public boolean process()
   {
      TagContext context = getContext();
      Sheet sheet = context.getSheet();
      Block block = context.getBlock();

      // Replace the bodiless tag text with the proper result.
      Cell cell = sheet.getRow(block.getTopRowNum()).getCell(block.getLeftColNum());
      SheetUtil.setCellValue(cell, myValue + 1, getAttributes().get(ATTR_VALUE));

      return true;
   }
}

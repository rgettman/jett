package net.sf.jett.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import net.sf.jett.transform.BlockTransformer;
import net.sf.jett.util.SheetUtil;

/**
 * <p>A <code>NullTag</code> does nothing to its <code>Block</code> except mark
 * its Cells as processed.  It can't have any attributes in body mode.</p>
 *
 * <br>Attributes:
 * <ul>
 * <li>text (required, bodiless only): <code>String</code>
 * </ul>
 */
public class NullTag extends BaseTag
{
   /**
    * Attribute that specifies the un-process text to display (bodiless only).
    */
   public static final String ATTR_TEXT = "text";
   private static final List<String> REQ_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_TEXT));

   /**
    * Returns this <code>Tag's</code> name.
    * @return This <code>Tag's</code> name.
    */
   public String getName()
   {
      return "null";
   }

   /**
    * Returns a <code>List</code> of required attribute names.
    * @return A <code>List</code> of required attribute names.
    */
   protected List<String> getRequiredAttributes()
   {
      if (isBodiless())
         return REQ_ATTRS;
      else
         return null;
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
    * No validation.
    */
   public void validateAttributes() {}

   /**
    * Just mark all <code>Cells</code> in this <code>Block</code> as processed.
    * @return Whether the first <code>Cell</code> in the <code>Block</code>
    *    associated with this <code>Tag</code> was processed.
    */
   public boolean process()
   {
      TagContext context = getContext();
      Sheet sheet = context.getSheet();
      Block block = context.getBlock();
      int left = block.getLeftColNum();
      int top = block.getTopRowNum();

      if (isBodiless())
      {
         // It should exist in this Cell; this Tag was found in it.
         Row row = sheet.getRow(top);
         Cell cell = row.getCell(left);
         SheetUtil.setCellValue(cell, getAttributes().get(ATTR_TEXT));
      }
      else
      {
         BlockTransformer transformer = new BlockTransformer();
         transformer.transform(context, getWorkbookContext(), false);
      }  // End else of isBodiless
      return true;
   }
}

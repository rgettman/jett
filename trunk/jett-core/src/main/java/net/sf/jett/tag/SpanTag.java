package net.sf.jett.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import net.sf.jett.exception.TagParseException;
import net.sf.jett.expression.Expression;
import net.sf.jett.transform.BlockTransformer;
import net.sf.jett.util.SheetUtil;

/**
 * <p>A <code>SpanTag</code> represents a cell or merged region that will span
 * extra rows and/or extra columns, depending on growth factors.  If this tag
 * is applied to a cell that is not part of a merged region, then it may result
 * in the creation of a merged region.</p>
 *
 * <br>Attributes:
 * <ul>
 * <li>factor (required): <code>int</code>
 * <li>value (required): <code>RichTextString</code>
 * <li>expandRight (optional): <code>boolean</code>
 * </ul>
 */
public class SpanTag extends BaseTag
{
   private static final boolean DEBUG = false;

   /**
    * Attribute for specifying the growth factor.
    */
   public static final String ATTR_FACTOR = "factor";
   /**
    * Attribute for forcing "expand right" behavior.  (Default is expand down.)
    */
   public static final String ATTR_EXPAND_RIGHT = "expandRight";
   /**
    * Attribute that specifies the value of the cell/merged region.
    */
   public static final String ATTR_VALUE = "value";

   private static final List<String> REQ_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_FACTOR, ATTR_VALUE));
   private static final List<String> OPT_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_EXPAND_RIGHT));

   private int myFactor = 1;
   private boolean amIExplicitlyExpandingRight = false;
   private RichTextString myValue;

   /**
    * Returns this <code>Tag's</code> name.
    * @return This <code>Tag's</code> name.
    */
   public String getName()
   {
      return "span";
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
    * Validates the attributes for this <code>Tag</code>.  Some optional
    * attributes are only valid for bodiless tags, and others are only valid
    * for tags without bodies.
    */
   public void validateAttributes()
   {
      TagContext context = getContext();
      Map<String, Object> beans = context.getBeans();
      Map<String, RichTextString> attributes = getAttributes();
      Block block = context.getBlock();

      if (!isBodiless())
         throw new TagParseException("SpanTag: Must be bodiless");

      myValue = attributes.get(ATTR_VALUE);

      String attrFactor = attributes.get(ATTR_FACTOR).getString();
      if (attrFactor != null)
      {
         Object factor = Expression.evaluateString(attrFactor, beans);
         if (factor instanceof Number)
         {
            myFactor = ((Number) factor).intValue();
            if (myFactor < 0)
               throw new TagParseException("SpanTag: factor cannot be negative: " + attrFactor);
         }
         else
            throw new TagParseException("SpanTag: factor must be a non-negative integer: " + attrFactor);
      }

      RichTextString rtsExpandRight = attributes.get(ATTR_EXPAND_RIGHT);
      String attrExpandRight = (rtsExpandRight != null) ? rtsExpandRight.getString() : null;
      if (attrExpandRight != null)
      {
         Object expandRight = Expression.evaluateString(attrExpandRight, beans);
         if (expandRight != null)
         {
            if (expandRight instanceof Boolean)
               amIExplicitlyExpandingRight = (Boolean) expandRight;
            else
               amIExplicitlyExpandingRight = Boolean.parseBoolean(expandRight.toString());
         }
      }
      if (amIExplicitlyExpandingRight)
         block.setDirection(Block.Direction.HORIZONTAL);
      else
         block.setDirection(Block.Direction.VERTICAL);
   }

   /**
    * <p>If not already part of a merged region, and one of the factors is
    * greater than 1, then create a merged region.  Else, replace the current
    * merged region with a new merged region.</p>
    * @return Whether the first <code>Cell</code> in the <code>Block</code>
    *    associated with this <code>Tag</code> was processed.
    */
   public boolean process()
   {
      TagContext context = getContext();
      Sheet sheet = context.getSheet();
      Block block = context.getBlock();

      if (DEBUG)
         System.err.println("SpanTag.process: factor=" + myFactor + ", expandRight is " + amIExplicitlyExpandingRight);

      int left = block.getLeftColNum();
      int right = left;
      int top = block.getTopRowNum();
      int bottom = top;
      // Assume a "merged region" of 1 X 1 for now.
      int height = 1;
      int width = 1;
      int index = findMergedRegionAtCell(sheet, left, top);
      if (index != -1)
      {
         // Get the height/width and remove the old merged region.
         CellRangeAddress remove = sheet.getMergedRegion(index);
         right = remove.getLastColumn();
         bottom = remove.getLastRow();
         height = remove.getLastRow() - remove.getFirstRow() + 1;
         width = remove.getLastColumn() - remove.getFirstColumn() + 1;
         if (DEBUG)
            System.err.println("  Removing region: " + remove + ", height=" + height + ", width=" + width);
         sheet.removeMergedRegion(index);
      }

      // The block for which to shift content out of the way or to remove is
      // actually the old merged region.
      Block mergedBlock = new Block(block.getParent(), left, right, top, bottom);
      mergedBlock.setDirection(block.getDirection());

      // Determine new height or width, plus new bottom or right.
      if (block.getDirection() == Block.Direction.VERTICAL)
      {
         bottom += height * (myFactor - 1);
         height *= myFactor;
      }
      else
      {
         right += width * (myFactor - 1);
         width *= myFactor;
      }

      if (myFactor == 0)
      {
         if (DEBUG)
            System.err.println("  Calling removeBlock on block: " + mergedBlock);
         SheetUtil.removeBlock(sheet, mergedBlock, getWorkbookContext());
         return false;
      }
      // At this point, myFactor >= 1.

      if (myFactor > 1)
      {
         if (DEBUG)
            System.err.println("  Calling shiftForBlock on block: " + mergedBlock + " with factor " + myFactor);
         SheetUtil.shiftForBlock(sheet, mergedBlock, getWorkbookContext(), myFactor);
      }

      // Set the value.
      Row row = sheet.getRow(top);
      Cell cell = row.getCell(left);
      SheetUtil.setCellValue(cell, myValue);

      // Create the replacement merged region, or the new merged region if it
      // didn't exist before.
      if (height > 1 || width > 1)
      {
         CellRangeAddress create = new CellRangeAddress(top, bottom, left, right);
         if (DEBUG)
            System.err.println("  Adding region: " + create);
         sheet.addMergedRegion(create);
      }

      BlockTransformer transformer = new BlockTransformer();
      transformer.transform(context, getWorkbookContext());

      return true;
   }

   /**
    * Identify the merged region on the given <code>Sheet</code> whose top-left
    * corner is at the specified column and row indexes.
    * @param sheet A <code>Sheet</code>.
    * @param col The 0-based column index of the top-left corner.
    * @param row The 0-based row index of the top-left corner.
    * @return A 0-based index into the <code>Sheet's</code> list of merged
    *    regions, or -1 if not found.
    */
   private int findMergedRegionAtCell(Sheet sheet, int col, int row)
   {
      for (int i = 0; i < sheet.getNumMergedRegions(); i++)
      {
         CellRangeAddress candidate = sheet.getMergedRegion(i);
         if (candidate.getFirstRow() == row && candidate.getFirstColumn() == col)
            return i;
      }
      return -1;
   }
}

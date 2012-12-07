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
import net.sf.jett.model.Block;
import net.sf.jett.transform.BlockTransformer;
import net.sf.jett.util.AttributeUtil;
import net.sf.jett.util.SheetUtil;

/**
 * <p>A <code>SpanTag</code> represents a cell or merged region that will span
 * extra rows and/or extra columns, depending on growth and/or adjustment
 * factors.  If this tag is applied to a cell that is not part of a merged
 * region, then it may result in the creation of a merged region.  If this tag
 * is applied to a cell that is part of a merged region, then it may result in
 * the removal of the merged region.</p>
 *
 * <br/>Attributes:
 * <ul>
 * <li><em>Inherits all attributes from {@link BaseTag}.</em></li>
 * <li>factor (optional): <code>int</code></li>
 * <li>adjust (optional): <code>int</code></li>
 * <li>value (required): <code>RichTextString</code></li>
 * <li>expandRight (optional): <code>boolean</code></li>
 * </ul>
 *
 * <p>Either one or both of the <code>factor</code>
 *
 * @author Randy Gettman
 */
public class SpanTag extends BaseTag
{
   private static final boolean DEBUG = false;

   /**
    * Attribute for specifying the growth factor.
    */
   public static final String ATTR_FACTOR = "factor";
   /**
    * Attribute for specifying an adjustment to the size of the merged region.
    * @since 0.4.0
    */
   public static final String ATTR_ADJUST = "adjust";
   /**
    * Attribute for forcing "expand right" behavior.  (Default is expand down.)
    */
   public static final String ATTR_EXPAND_RIGHT = "expandRight";
   /**
    * Attribute that specifies the value of the cell/merged region.
    */
   public static final String ATTR_VALUE = "value";

   private static final List<String> REQ_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_VALUE));
   private static final List<String> OPT_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_EXPAND_RIGHT, ATTR_FACTOR, ATTR_ADJUST));

   private int myFactor = 1;
   private int myAdjust = 0;
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
   @Override
   protected List<String> getRequiredAttributes()
   {
      List<String> reqAttrs = new ArrayList<String>(super.getRequiredAttributes());
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
      List<String> optAttrs = new ArrayList<String>(super.getOptionalAttributes());
      optAttrs.addAll(OPT_ATTRS);
      return optAttrs;
   }

   /**
    * Validates the attributes for this <code>Tag</code>.  Some optional
    * attributes are only valid for bodiless tags, and others are only valid
    * for tags without bodies.
    */
   public void validateAttributes()
   {
      super.validateAttributes();
      TagContext context = getContext();
      Map<String, Object> beans = context.getBeans();
      Map<String, RichTextString> attributes = getAttributes();
      Block block = context.getBlock();

      if (!isBodiless())
         throw new TagParseException("SpanTag: Must be bodiless");

      myValue = attributes.get(ATTR_VALUE);

      List<RichTextString> atLeastOne = Arrays.asList(attributes.get(ATTR_FACTOR), attributes.get(ATTR_ADJUST));
      AttributeUtil.ensureAtLeastOneExists(atLeastOne, Arrays.asList(ATTR_FACTOR, ATTR_ADJUST));
      myFactor = AttributeUtil.evaluateNonNegativeInt(attributes.get(ATTR_FACTOR), beans, ATTR_FACTOR, 1);
      myAdjust = AttributeUtil.evaluateInt(attributes.get(ATTR_ADJUST), beans, ATTR_ADJUST, 0);

      boolean explicitlyExpandingRight = AttributeUtil.evaluateBoolean(attributes.get(ATTR_EXPAND_RIGHT), beans, false);
      if (explicitlyExpandingRight)
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
         System.err.println("SpanTag.process: factor=" + myFactor + ", block direction is " + block.getDirection());

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
      int change;
      if (block.getDirection() == Block.Direction.VERTICAL)
      {
         change = height * (myFactor - 1) + myAdjust;
         bottom += change;
         height = bottom - top + 1;
      }
      else
      {
         change = width * (myFactor - 1) + myAdjust;
         right += change;
         width = right - left + 1;
      }

      // Remove.
      if (height <= 0 || width <= 0)
      {
         if (DEBUG)
            System.err.println("  Calling removeBlock on block: " + mergedBlock);
         SheetUtil.removeBlock(sheet, mergedBlock, getWorkbookContext());
         return false;
      }
      // Shrink.
      if (change < 0)
      {
         Block remove;
         if (block.getDirection() == Block.Direction.VERTICAL)
            remove = new Block(block.getParent(), left, right, bottom + 1, bottom - change);
         else
            remove = new Block(block.getParent(), right + 1, right - change, top, bottom);
         remove.setDirection(block.getDirection());
         if (DEBUG)
            System.err.println("  Calling removeBlock on fabricated block: " + remove + " (change " + change + ")");
         SheetUtil.removeBlock(sheet, remove, getWorkbookContext());
      }
      // Expand.
      if (change > 0)
      {
         Block expand;
         if (block.getDirection() == Block.Direction.VERTICAL)
            expand = new Block(block.getParent(), left, right, bottom - change, bottom - change);
         else
            expand = new Block(block.getParent(), right - change, right - change, top, bottom);
         expand.setDirection(block.getDirection());
         if (DEBUG)
            System.err.println("  Calling shiftForBlock on fabricated block: " + expand + " with change " + (change + 1));
         SheetUtil.shiftForBlock(sheet, expand, getWorkbookContext(), change + 1);
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

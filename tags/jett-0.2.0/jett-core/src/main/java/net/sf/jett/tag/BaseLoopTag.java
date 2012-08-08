package net.sf.jett.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;

import net.sf.jett.exception.TagParseException;
import net.sf.jett.expression.Expression;
import net.sf.jett.transform.BlockTransformer;
import net.sf.jett.transform.WorkbookContext;
import net.sf.jett.util.SheetUtil;

/**
 * <p>The abstract class <code>BaseLoopTag</code> is the base class for all tags
 * that represent loops.
 * </p>
 *
 * <br>Attributes:
 * <ul>
 * <li>copyRight (optional): <code>boolean</code>
 * <li>fixed (optional): <code>boolean</code>
 * <li>pastEndAction (optional): <code>String</code>
 * <li>groupDir (optional): <code>String</code>
 * <li>collapse (optional): <code>boolean</code>
 * </ul>
 */
public abstract class BaseLoopTag extends BaseTag
{
   private static final boolean DEBUG = false;

   /**
    * Attribute for forcing "copy right" behavior.  (Default is copy down.)
    */
   public static final String ATTR_COPY_RIGHT = "copyRight";
   /**
    * Attribute for not shifting other content out of the way; works the same
    * as "fixed size collections".
    */
   public static final String ATTR_FIXED = "fixed";
   /**
    * Attribute for specifying the "past end action", an action for dealing
    * with content beyond the range of looping content.
    * @see #PAST_END_ACTION_CLEAR
    * @see #PAST_END_ACTION_REMOVE
    */
   public static final String ATTR_PAST_END_ACTION = "pastEndAction";
   /**
    * Attribute for specifying the direction of the grouping.  This defaults to
    * no grouping.
    * @since 0.2.0
    * @see #GROUP_DIR_ROWS
    * @see #GROUP_DIR_COLS
    * @see #GROUP_DIR_NONE
    */
   public static final String ATTR_GROUP_DIR = "groupDir";
   /**
    * Attribute for specifying whether the group should be displayed collapsed.
    * The default is <code>false</code>, for not collapsed.  It is ignored if
    * neither rows nor columns are being grouped.
    * @since 0.2.0
    */
   public static final String ATTR_COLLAPSE = "collapse";

   /**
    * The "past end action" value to clear the content of cells.
    */
   public static final String PAST_END_ACTION_CLEAR = "clear";
   /**
    * The "past end action" value to remove the cells, including things like
    * borders and formatting.
    */
   public static final String PAST_END_ACTION_REMOVE = "remove";

   /**
    * The "group dir" value to specify that columns should be grouped.
    * @since 0.2.0
    */
   public static final String GROUP_DIR_COLS = "cols";
   /**
    * The "group dir" value to specify that rows should be grouped.
    * @since 0.2.0
    */
   public static final String GROUP_DIR_ROWS = "rows";
   /**
    * The "group dir" value to specify that neither rows nor columns should be
    * grouped.
    * @since 0.2.0
    */
   public static final String GROUP_DIR_NONE = "none";

   private static final List<String> OPT_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_COPY_RIGHT, ATTR_FIXED, ATTR_PAST_END_ACTION,
         ATTR_GROUP_DIR, ATTR_COLLAPSE));

   private boolean amIExplicitlyCopyingRight = false;
   private boolean amIFixed = false;
   private PastEndAction myPastEndAction = PastEndAction.CLEAR_CELL;
   private Block.Direction myGroupDir;
   private boolean amICollapsed;

   /**
    * There are no required attributes that all <code>BaseLoopTags</code>
    * support.
    * @return An empty <code>List</code>.
    */
   protected List<String> getRequiredAttributes()
   {
      return new ArrayList<String>();
   }

   /**
    * All <code>BaseLoopTags</code> support the optional copy down tag.
    * @return A <code>List</code> of optional attribute names.
    */
   protected List<String> getOptionalAttributes()
   {
      return OPT_ATTRS;
   }

   /**
    * Ensure that the past end action (if specified) is a valid value.  Ensure
    * that the group direction (if specified) is a valid value.
    * @throws TagParseException If the attribute values are illegal or
    *    unacceptable.
    */
   protected void validateAttributes() throws TagParseException
   {
      TagContext context = getContext();
      Map<String, Object> beans = context.getBeans();
      Map<String, RichTextString> attributes = getAttributes();
      Block block = context.getBlock();

      RichTextString rtsCopyRight = attributes.get(ATTR_COPY_RIGHT);
      String attrCopyRight = (rtsCopyRight != null) ? rtsCopyRight.getString() : null;
      if (attrCopyRight != null)
      {
         Object copyRight = Expression.evaluateString(attrCopyRight, beans);
         if (copyRight != null)
         {
            if (copyRight instanceof Boolean)
               amIExplicitlyCopyingRight = (Boolean) copyRight;
            else
               amIExplicitlyCopyingRight = Boolean.parseBoolean(copyRight.toString());
         }
      }
      if (amIExplicitlyCopyingRight)
         block.setDirection(Block.Direction.HORIZONTAL);

      RichTextString rtsFixed = attributes.get(ATTR_FIXED);
      String attrFixed = (rtsFixed != null) ? rtsFixed.getString() : null;
      if (attrFixed != null)
      {
         Object fixed = Expression.evaluateString(attrFixed, beans);
         if (fixed != null)
         {
            if (fixed instanceof Boolean)
               amIFixed = (Boolean) fixed;
            else
               amIFixed = Boolean.parseBoolean(fixed.toString());
         }
      }

      RichTextString rtsPastEndAction = attributes.get(ATTR_PAST_END_ACTION);
      String attrPastEndAction = (rtsPastEndAction != null) ? rtsPastEndAction.getString() : null;
      if (attrPastEndAction != null)
      {
         Object pastEndAction = Expression.evaluateString(attrPastEndAction, beans);
         if (pastEndAction != null)
         {
            String strPastEndAction = pastEndAction.toString();
            if (PAST_END_ACTION_CLEAR.equalsIgnoreCase(strPastEndAction))
               myPastEndAction = PastEndAction.CLEAR_CELL;
            else if (PAST_END_ACTION_REMOVE.equalsIgnoreCase(strPastEndAction))
               myPastEndAction = PastEndAction.REMOVE_CELL;
            else
               throw new TagParseException("Unknown past end action: " + strPastEndAction +
                  " found at " + attrPastEndAction);
         }
         else
            throw new TagParseException("Past end action can't be null: " + attrPastEndAction);
      }

      RichTextString rtsGroupDir = attributes.get(ATTR_GROUP_DIR);
      String attrGroupDir = (rtsGroupDir != null) ? rtsGroupDir.getString() : null;
      if (attrGroupDir != null)
      {
         String groupDir = Expression.evaluateString(attrGroupDir, beans).toString().toLowerCase();
         if (GROUP_DIR_ROWS.equals(groupDir))
            myGroupDir = Block.Direction.VERTICAL;
         else if (GROUP_DIR_COLS.equals(groupDir))
            myGroupDir = Block.Direction.HORIZONTAL;
         else if (GROUP_DIR_NONE.equals(groupDir))
            myGroupDir = Block.Direction.NONE;
         else
            throw new TagParseException("Unknown group direction: " + groupDir +
                  " found at " + attrGroupDir);
      }
      else
      {
         myGroupDir = Block.Direction.NONE;
      }

      RichTextString rtsCollapse = attributes.get(ATTR_COLLAPSE);
      String attrCollapse = (rtsCollapse != null) ? rtsCollapse.getString() : null;
      if (attrCollapse != null)
      {
         Object test = Expression.evaluateString(attrCollapse, beans);
         if (test instanceof Boolean)
            amICollapsed = (Boolean) test;
         else
            amICollapsed = Boolean.parseBoolean(test.toString());
      }
   }

   /**
    * Returns the <code>PastEndAction</code>, which is controlled by the
    * attribute specified by <code>ATTR_PAST_END_ACTION</code>.  It defaults to
    * <code>CLEAR_CELL</code>.
    * @return A <code>PastEndAction</code>.
    * @see PastEndAction
    */
   protected PastEndAction getPastEndAction()
   {
      return myPastEndAction;
   }

   /**
    * <p>Provide a generic way to process a tag that loops, with the Template
    * Method pattern.</p>
    * <ol>
    * <li>Decide whether content needs to be shifted out of the way, and shift
    * the content out of the way if necessary.  This involves calling
    * <code>getCollectionNames()</code> to determine if any of the collection
    * names are "fixed".</li>
    * <li>Call <code>getNumIterations</code> to determine the number of Blocks
    * needed.</li>
    * <li>Copy the Block the needed number of times.</li>
    * <li>Get the loop iterator by calling <code>getLoopIterator()</code>.</li>
    * <li>Over each loop of the iterator...</li>
    * <ol>
    * <li>Create a <code>Block</code> for the iteration.</li>
    * <li>If the collection values are exhaused, apply any "past end actions".
    * </li>
    * <li>Call <code>beforeBlockProcessed()</li>.
    * <li>Process the current <code>Block</code> with a
    * <code>BlockTransformer</code>.</li>
    * <li>Call <code>afterBlockProcessed()</li>.
    * </ol>
    * </ol>
    * @return Whether the first <code>Cell</code> in the <code>Block</code>
    *    associated with this <code>Tag</code> was processed.
    * @see #getCollectionNames
    * @see #getNumIterations
    * @see #getLoopIterator
    * @see #beforeBlockProcessed
    * @see #afterBlockProcessed
    */
   public boolean process()
   {
      TagContext context = getContext();
      WorkbookContext workbookContext = getWorkbookContext();
      // Important for formulas, so different cell reference map entries from
      // different loops can be distinguished.
      workbookContext.incrSequenceNbr();

      Sheet sheet = context.getSheet();
      Map<String, Object> beans = context.getBeans();

      // Decide whether this is "fixed" in 2 ways:
      // 1. A fixed size collection name was specified and is present.
      // 2. The "fixed" attribute is true.
      boolean fixed = amIFixed;
      if (!fixed)
      {
         // Shallow copy.
         List<String> fixedSizeCollNames = new ArrayList<String>(
            workbookContext.getFixedSizedCollectionNames());
         List<String> collNames = getCollectionNames();
         if (collNames != null)
         {
            if (DEBUG)
            {
               for (String collName : collNames)
                  System.err.println("BaseLoopTag: collName found: \"" + collName + "\".");
            }
            // Remove all collection names not found.
            for (Iterator<String> itr = fixedSizeCollNames.iterator(); itr.hasNext(); )
            {
               String fixedSizeCollName = itr.next();
               if (!collNames.contains(fixedSizeCollName))
                  itr.remove();
            }
         }
         else
         {
            fixedSizeCollNames.clear();
         }
         fixed = !fixedSizeCollNames.isEmpty();
      }

      int numIterations = getNumIterations();
      if (DEBUG)
         System.err.println("BaseLoopTag: numIterations=" + numIterations);
      if (numIterations == 0)
      {
         // If fixed, no shifting is to occur for the removed block.
         if (fixed)
         {
            switch(myPastEndAction)
            {
            case CLEAR_CELL:
               clearBlock();
               break;
            case REMOVE_CELL:
               deleteBlock();
               break;
            }
         }
         else
            removeBlock();
         return false;
      }
      else
      {
         BlockTransformer transformer = new BlockTransformer();
         List<Block> blocksToProcess = new ArrayList<Block>(numIterations);
         // Create room for the additional Blocks; the Block knows the proper
         // direction (right or down).
         // Don't create room if the collection is "fixed size", i.e. we can
         // assume that room exists already.
         if (!fixed)
            shiftForBlock();

         // Copy the Block.
         for (int i = 0; i < numIterations; i++)
         {
            Block copy = copyBlock(i);
            if (DEBUG)
               System.err.println("  Adding copied block: " + copy);
            blocksToProcess.add(copy);
         }

         int index = 0;
         Iterator<?> iterator = getLoopIterator();
         int right, bottom, colGrowth, rowGrowth;
         while(iterator.hasNext())
         {
            Object item = iterator.next();
            Block currBlock = blocksToProcess.get(index);

            // Off the end of the collection!
            if (index >= getCollectionSize())
            {
               switch(myPastEndAction)
               {
               case CLEAR_CELL:
                  SheetUtil.clearBlock(sheet, currBlock, getWorkbookContext());
                  break;
               case REMOVE_CELL:
                  SheetUtil.deleteBlock(sheet, currBlock, getWorkbookContext());
                  break;
               }
            }

            // Before Block Processing.
            beforeBlockProcessed(context, currBlock, item, index);
            // Process the block.
            TagContext blockContext = new TagContext();
            blockContext.setSheet(sheet);
            blockContext.setBeans(beans);
            blockContext.setBlock(currBlock);
            blockContext.setProcessedCellsMap(context.getProcessedCellsMap());
            blockContext.setDrawing(context.getDrawing());
            if (DEBUG)
               System.err.println("  Block Before: " + currBlock);
            right = currBlock.getRightColNum();
            bottom = currBlock.getBottomRowNum();

            transformer.transform(blockContext, workbookContext);
            // See if the block transformation grew or shrunk the current block.
            if (DEBUG)
               System.err.println("  Block After: " + currBlock);
            colGrowth = currBlock.getRightColNum() - right;
            rowGrowth = currBlock.getBottomRowNum() - bottom;
            // If it did, then all pending blocks must react!
            if (colGrowth != 0 || rowGrowth != 0)
            {
               if (DEBUG)
                  System.err.println("  colGrowth is " + colGrowth + ", rowGrowth is " + rowGrowth);
               for (int j = index + 1; j < numIterations; j++)
               {
                  Block pendingBlock = blocksToProcess.get(j);
                  if (DEBUG)
                     System.err.println("    Reacting Block: " + pendingBlock);
                  pendingBlock.reactToGrowth(currBlock, colGrowth, rowGrowth);
               }
            }

            // After Block Processing.
            afterBlockProcessed(context, currBlock, item, index);

            // End of loop processing.
            index++;
         }

         // Grouping - only if there was at least one item to process.
         groupRowsOrCols(sheet, context.getBlock(), blocksToProcess.get(blocksToProcess.size() - 1));
      }
      return true;
   }

   /**
    * Decide to and place an Excel Group for rows, columns, or nothing,
    * depending on attribute settings and the first and last
    * <code>Blocks</code>.
    * @param sheet The <code>Sheet</code> on which to group rows or columns.
    * @param first The first <code>Block</code>.
    * @param last The last <code>Block</code>.
    */
   private void groupRowsOrCols(Sheet sheet, Block first, Block last)
   {
      int begin, end;
      switch(myGroupDir)
      {
      case VERTICAL:
         begin = first.getTopRowNum();
         end = last.getBottomRowNum();
         SheetUtil.groupRows(sheet, begin, end, amICollapsed);
         break;
      case HORIZONTAL:
         begin = first.getLeftColNum();
         end = last.getRightColNum();
         SheetUtil.groupColumns(sheet, begin, end, amICollapsed);
         break;
      // Do nothing on NONE.
      }
   }

   /**
    * Shifts cells out of the way of where copied blocks will go.
    */
   private void shiftForBlock()
   {
      TagContext context = getContext();
      Block block = context.getBlock();
      Sheet sheet = context.getSheet();
      int numIterations = getNumIterations();
      SheetUtil.shiftForBlock(sheet, block, getWorkbookContext(), numIterations);
   }

   /**
    * Copies the <code>Block</code> in a particular direction.
    * @param numBlocksAway How many blocks away the <code>Block</code> will be
    *    copied.
    * @return The newly copied <code>Block</code>.
    */
   private Block copyBlock(int numBlocksAway)
   {
      TagContext context = getContext();
      Block block = context.getBlock();
      Sheet sheet = context.getSheet();
      return SheetUtil.copyBlock(sheet, block, getWorkbookContext(), numBlocksAway);
   }

   /**
    * Returns the names of the <code>Collections</code> that are being used in
    * this <code>BaseLoopTag</code>.
    * @return A <code>List</code> collection names, or <code>null</code> if
    *    not operating on any <code>Collections</code>.
    */
   protected abstract List<String> getCollectionNames();

   /**
    * Returns the number of iterations.
    * @return The number of iterations.
    */
   protected abstract int getNumIterations();

   /**
    * Returns the size of the collection being iterated.  This may be different
    * than the number of iterations because of the "limit" attribute.
    * @return The size of the collection being iterated.
    */
   protected abstract int getCollectionSize();

   /**
    * Returns an <code>Iterator</code> that iterates over some
    * <code>Collection</code> of objects.  The <code>Iterator</code> doesn't
    * need to support the <code>remove</code> operation.
    * @return An <code>Iterator</code>.
    */
   protected abstract Iterator<?> getLoopIterator();

   /**
    * This method is called once per iteration loop, immediately before the
    * given <code>Block</code> is processed.  An iteration index is supplied as
    * well.
    * @param context The <code>TagContext</code>.
    * @param currBlock The <code>Block</code> that is about to processed.
    * @param item The <code>Object</code> that resulted from the iterator.
    * @param index The iteration index (0-based).
    */
   protected abstract void beforeBlockProcessed(TagContext context, Block currBlock, Object item, int index);

   /**
    * This method is called once per iteration loop, immediately after the
    * given <code>Block</code> is processed.  An iteration index is supplied as
    * well.
    * @param context The <code>TagContext</code>.
    * @param currBlock The <code>Block</code> that was just processed.
    * @param item The <code>Object</code> that resulted from the iterator.
    * @param index The iteration index (0-based).
    */
   protected abstract void afterBlockProcessed(TagContext context, Block currBlock, Object item, int index);
}

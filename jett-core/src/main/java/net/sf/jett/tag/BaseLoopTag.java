package net.sf.jett.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;

import net.sf.jett.event.TagLoopListener;
import net.sf.jett.event.TagLoopEvent;
import net.sf.jett.exception.TagParseException;
import net.sf.jett.model.Block;
import net.sf.jett.model.PastEndAction;
import net.sf.jett.model.WorkbookContext;
import net.sf.jett.transform.BlockTransformer;
import net.sf.jett.util.AttributeEvaluator;
import net.sf.jett.util.SheetUtil;

/**
 * <p>The abstract class <code>BaseLoopTag</code> is the base class for all tags
 * that represent loops.
 * </p>
 *
 * <br/>Attributes:
 * <ul>
 * <li><em>Inherits all attributes from {@link BaseTag}.</em></li>
 * <li>copyRight (optional): <code>boolean</code></li>
 * <li>fixed (optional): <code>boolean</code></li>
 * <li>pastEndAction (optional): <code>String</code></li>
 * <li>groupDir (optional): <code>String</code></li>
 * <li>collapse (optional): <code>boolean</code></li>
 * <li>onLoopProcessed (optional): <code>TagLoopListener</code></li>
 * </ul>
 *
 * @author Randy Gettman
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
    * Attribute for specifying a <code>TagLoopListener</code> to listen for
    * <code>TagLoopEvents</code>.
    * @since 0.3.0
    */
   public static final String ATTR_ON_LOOP_PROCESSED = "onLoopProcessed";

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
         ATTR_GROUP_DIR, ATTR_COLLAPSE, ATTR_ON_LOOP_PROCESSED));

   private boolean amIExplicitlyCopyingRight = false;
   private boolean amIFixed = false;
   private PastEndAction myPastEndAction = PastEndAction.CLEAR_CELL;
   private Block.Direction myGroupDir;
   private boolean amICollapsed;
   private TagLoopListener myTagLoopListener;

   /**
    * Sets whether the repeated blocks are to be copied to the right (true) or
    * downward (default, false).
    * @param copyRight Whether the repeated blocks are to be copied to the right (true) or
    *    downward (default, false).
    * @since 0.3.0
    */
   public void setCopyRight(boolean copyRight)
   {
      amIExplicitlyCopyingRight = copyRight;
   }

   /**
    * Sets "fixed" mode, which doesn't shift other content out of the way when
    * copying repeated blocks of cells.
    * @param fixed Whether to execute in "fixed" mode.
    * @since 0.3.0
    */
   public void setFixed(boolean fixed)
   {
      amIFixed = fixed;
   }

   /**
    * Sets the <code>PastEndAction</code>.
    * @param pae The <code>PastEndAction</code>.
    * @since 0.3.0
    */
   public void setPastEndAction(PastEndAction pae)
   {
      myPastEndAction = pae;
   }

   /**
    * Sets the directionality of the Excel Group to be created, if any.
    * @param direction The directionality.
    * @since 0.3.0
    */
   public void setGroupDirection(Block.Direction direction)
   {
      myGroupDir = direction;
   }

   /**
    * Sets whether any Excel Group created is collapsed.
    * @param collapsed Whether any Excel group created is collapsed.
    * @since 0.3.0
    */
   public void setCollapsed(boolean collapsed)
   {
      amICollapsed = collapsed;
   }

   /**
    * Sets the <code>TagLoopListener</code>.
    * @param listener The <code>TagLoopListener</code>.
    * @since 0.3.0
    */
   public void setOnLoopProcessed(TagLoopListener listener)
   {
      myTagLoopListener = listener;
   }

   /**
    * There are no required attributes that all <code>BaseLoopTags</code>
    * support.
    * @return An empty <code>List</code>.
    */
   protected List<String> getRequiredAttributes()
   {
      return super.getRequiredAttributes();
   }

   /**
    * All <code>BaseLoopTags</code> support the optional copy down tag.
    * @return A <code>List</code> of optional attribute names.
    */
   protected List<String> getOptionalAttributes()
   {
      List<String> optAttrs = new ArrayList<String>(super.getOptionalAttributes());
      optAttrs.addAll(OPT_ATTRS);
      return optAttrs;
   }

   /**
    * Ensure that the past end action (if specified) is a valid value.  Ensure
    * that the group direction (if specified) is a valid value.
    * @throws TagParseException If the attribute values are illegal or
    *    unacceptable.
    */
   protected void validateAttributes() throws TagParseException
   {
      super.validateAttributes();
      TagContext context = getContext();
      Map<String, Object> beans = context.getBeans();
      Map<String, RichTextString> attributes = getAttributes();
      Block block = context.getBlock();

      AttributeEvaluator eval = new AttributeEvaluator(context);

      amIExplicitlyCopyingRight = eval.evaluateBoolean(attributes.get(ATTR_COPY_RIGHT), beans, false);
      if (amIExplicitlyCopyingRight)
         block.setDirection(Block.Direction.HORIZONTAL);

      amIFixed = eval.evaluateBoolean(attributes.get(ATTR_FIXED), beans, false);

      String strPastEndAction = eval.evaluateStringSpecificValues(attributes.get(ATTR_PAST_END_ACTION), beans,
         ATTR_PAST_END_ACTION, Arrays.asList(PAST_END_ACTION_CLEAR, PAST_END_ACTION_REMOVE), PAST_END_ACTION_CLEAR);
      if (PAST_END_ACTION_CLEAR.equalsIgnoreCase(strPastEndAction))
         myPastEndAction = PastEndAction.CLEAR_CELL;
      else if (PAST_END_ACTION_REMOVE.equalsIgnoreCase(strPastEndAction))
         myPastEndAction = PastEndAction.REMOVE_CELL;

      String strGroupDir = eval.evaluateStringSpecificValues(attributes.get(ATTR_GROUP_DIR), beans,
         ATTR_GROUP_DIR, Arrays.asList(GROUP_DIR_ROWS, GROUP_DIR_COLS, GROUP_DIR_NONE), GROUP_DIR_NONE);
      if (GROUP_DIR_ROWS.equals(strGroupDir))
         myGroupDir = Block.Direction.VERTICAL;
      else if (GROUP_DIR_COLS.equals(strGroupDir))
         myGroupDir = Block.Direction.HORIZONTAL;
      else if (GROUP_DIR_NONE.equals(strGroupDir))
            myGroupDir = Block.Direction.NONE;

      amICollapsed = eval.evaluateBoolean(attributes.get(ATTR_COLLAPSE), beans, false);

      myTagLoopListener = eval.evaluateObject(attributes.get(ATTR_ON_LOOP_PROCESSED), beans,
         ATTR_ON_LOOP_PROCESSED, TagLoopListener.class, null);
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
         int maxRight = 0;
         int maxBottom = 0;
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
            // Get max right/bottom to expand the tag's block later.
            if (currBlock.getRightColNum() > maxRight)
               maxRight = currBlock.getRightColNum();
            if (currBlock.getBottomRowNum() > maxBottom)
               maxBottom = currBlock.getBottomRowNum();

            // Fire a tag loop event here, before the After Block Processing
            // occurs.
            fireTagLoopEvent(currBlock, index);

            // After Block Processing.
            afterBlockProcessed(context, currBlock, item, index);

            // End of loop processing.
            index++;
         }

         // Expand the tag block.
         Block block = context.getBlock();
         block.expand(maxRight - block.getRightColNum(), maxBottom - block.getBottomRowNum());

         // Grouping - only if there was at least one item to process.
         groupRowsOrCols(sheet, context.getBlock(), blocksToProcess.get(blocksToProcess.size() - 1));
      }
      return true;
   }

   /**
    * If there is a <code>TagLoopListener</code>, then create and fire a
    * <code>TagLoopEvent</code>, with beans and sheet taken from this
    * <code>BaseLoopTag</code>, and with the given loop index and given
    * <code>Block</code>.
    * @param block The current <code>Block</code>.
    * @param index The zero-based loop index.
    */
   private void fireTagLoopEvent(Block block, int index)
   {
      if (myTagLoopListener != null)
      {
         TagLoopEvent tagLoopEvent = new TagLoopEvent();
         TagContext context = getContext();
         tagLoopEvent.setBeans(context.getBeans());
         tagLoopEvent.setSheet(context.getSheet());
         tagLoopEvent.setBlock(block);
         tagLoopEvent.setLoopIndex(index);
         myTagLoopListener.onTagLoopProcessed(tagLoopEvent);
      }
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
      if (DEBUG)
         System.err.println("BLT.gROC: " + myGroupDir + ", " + amICollapsed);
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

package net.sf.jett.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import net.sf.jett.exception.TagParseException;
import net.sf.jett.expression.Expression;
import net.sf.jett.expression.ExpressionFactory;
import net.sf.jett.model.Block;
import net.sf.jett.model.WorkbookContext;
import net.sf.jett.parser.MetadataParser;
import net.sf.jett.tag.BaseLoopTag;
import net.sf.jett.tag.BaseTag;
import net.sf.jett.tag.MultiForEachTag;
import net.sf.jett.tag.TagContext;
import net.sf.jett.util.RichTextStringUtil;
import net.sf.jett.util.SheetUtil;

/**
 * A <code>CollectionsTransformer</code> knows how to perform implicit
 * collections processing on a group of <code>Collections</code>, processing an
 * implicit <code>MultiForEachTag</code>.
 *
 * @author Randy Gettman
 */
public class CollectionsTransformer
{
   private static boolean DEBUG = false;

   private static final String IMPL_ITEM_NAME_SUFFIX = "__JettItem__";

   /**
    * Transform a <code>Block</code> of <code>Cells</code> around the given
    * <code>Cell</code>, which has declared implicit collection processing
    * behavior using the given collection expression.
    * @param cell The <code>Cell</code> on which the collection expression was
    *    first found.
    * @param workbookContext The <code>WorkbookContext</code>.
    * @param cellContext The <code>TagContext</code> of <code>cell</code>.
    */
   public void transform(Cell cell, WorkbookContext workbookContext, TagContext cellContext)
   {
      Block parentBlock = cellContext.getBlock();
      Map<String, Object> beans = cellContext.getBeans();
      Map<String, Cell> processedCells = cellContext.getProcessedCellsMap();
      Sheet sheet = cellContext.getSheet();
      CreationHelper helper = sheet.getWorkbook().getCreationHelper();

      MetadataParser parser = null;
      RichTextString richString = cell.getRichStringCellValue();
      String value = richString.getString();
      int metadataIndIdx = value.indexOf(MetadataParser.BEGIN_METADATA);
      if (metadataIndIdx != -1)
      {
         // Evaluate any Expressions in the metadata.
         String metadata = value.substring(metadataIndIdx + MetadataParser.BEGIN_METADATA.length());
         if (DEBUG)
         {
            System.err.println("  CT: Metadata found: " + metadata + " on sheet " + sheet.getSheetName() +
               " at row " + cell.getRowIndex() + ", cell " + cell.getColumnIndex());
         }
         // Parse the Metadata.
         parser = new MetadataParser(metadata);
         parser.setCell(cell);
         parser.parse();
         // Remove the metadata text from the Cell.
         RichTextString metadataRemoved = RichTextStringUtil.replaceAll(richString,
            helper, MetadataParser.BEGIN_METADATA + metadata, "");
         SheetUtil.setCellValue(cell, metadataRemoved);
      }

      // Construct a Block with this context's Block as its parent.
      // It will inherit its parent's column range unless overridden later.
      int left = parentBlock.getLeftColNum();
      int right = parentBlock.getRightColNum();
      int top = cell.getRowIndex();
      int bottom = top;
      String copyRight = null;
      String fixed = null;
      String pastEndAction = null;
      String replacementValue = "";
      String groupDir = null;
      String collapse = null;
      String tagLoopListener = null;
      String tagListener = null;
      String indexVarName = null;
      String limit = null;
      if (parser != null)
      {
         // Gather parser properties.
         String lexeme = parser.getExtraRows();
         if (lexeme != null)
         {
            bottom += evaluateInt(lexeme, beans, "extraRows", cell);
         }
         lexeme = parser.getColsLeft();
         if (lexeme != null)
         {
            left = cell.getColumnIndex() - evaluateInt(lexeme, beans, "left", cell);
         }
         lexeme = parser.getColsRight();
         if (lexeme != null)
         {
            right = cell.getColumnIndex() + evaluateInt(lexeme, beans, "right", cell);
         }

         copyRight = parser.getCopyingRight();
         fixed = parser.getFixed();
         pastEndAction = parser.getPastEndAction();
         replacementValue = parser.getReplacementValue();
         groupDir = parser.getGroupDir();
         collapse = parser.getCollapsingGroup();
         tagLoopListener = parser.getTagLoopListener();
         tagListener = parser.getTagListener();
         indexVarName = parser.getIndexVarName();
         limit = parser.getLimit();

         if (parser.isDefiningCols())
         {
            // Column range can't go outside parent's column range.
            if (left < parentBlock.getLeftColNum())
               left = parentBlock.getLeftColNum();
            if (right > parentBlock.getRightColNum())
               right = parentBlock.getRightColNum();
         }
      }
      Block containingBlock = new Block(parentBlock, left, right, top, bottom);
      if (DEBUG)
         System.err.println("CT: Impl MultiForEach Block: " + containingBlock);

      // Find all Collection names in the Block.
      List<String> collectionNames = findCollectionsInBlock(cell, containingBlock,
         workbookContext.getNoImplicitProcessingCollectionNames(), beans);
      List<String> vars = new ArrayList<String>(collectionNames.size());
      List<String> fixedSizeCollNames = workbookContext.getFixedSizedCollectionNames();
      // Shallow copy.
      List<String> fixedSizeCollNamesCopy = new ArrayList<String>(fixedSizeCollNames);
      for (String collectionName : collectionNames)
      {
         if (DEBUG)
            System.err.println("  CollT: collection name found: " + collectionName);
         // Create name under which the items for this Collection will be known.
         String varName = collectionName.replaceAll("\\.", "_");
         varName += IMPL_ITEM_NAME_SUFFIX;
         vars.add(varName);
         // Setup the Block for the implicit for each loop by replacing
         // all occurrences of the Collection expression with the
         // implicit item name.
         SheetUtil.setUpBlockForImplicitCollectionAccess(sheet,
            containingBlock, collectionName, varName);

         // All fixed size collection names that start with this collection
         // name also must have the "JETTized" collection name also placed in
         // the fixed size collection name list, so that any nested implicit
         // collections processing can recognize those expressions as fixed
         // size as well.  E.g.
         // "list1.list2.list3" => "list1" + IMPL_ITEM_NAME_SUFFIX + ".list2.list3".
         List<String> additions = new ArrayList<String>();
         for (String fixedCollName : fixedSizeCollNamesCopy)
         {
            if (fixedCollName.startsWith(collectionName))
            {
               String addition = varName + fixedCollName.substring(collectionName.length());
               if (!fixedSizeCollNamesCopy.contains(addition))
                  additions.add(addition);
            }
         }
         fixedSizeCollNames.addAll(additions);
      }

      // Determine if any of the collection names we found are marked as
      // "fixed".
      // Remove all collection names not found.
      for (Iterator<String> itr = fixedSizeCollNamesCopy.iterator(); itr.hasNext(); )
      {
         String fixedSizeCollName = itr.next();
         if (DEBUG)
            System.err.println("  CollT: fixed size collection name: " + fixedSizeCollName);
         if (!collectionNames.contains(fixedSizeCollName))
            itr.remove();
      }
      if (!fixedSizeCollNamesCopy.isEmpty())
         fixed = "true";
      if (DEBUG)
      {
         if (!fixedSizeCollNamesCopy.isEmpty())
            System.err.println("CollT: Setting implicit tag to fixed: " + fixed +
               " based on fixed size collection name: " + fixedSizeCollNamesCopy.get(0));
         else
            System.err.println("CollT: Setting implicit tag to fixed: " + fixed +
               " based on no fixed size collection names found.");
      }

      TagContext context = new TagContext();
      context.setBeans(beans);
      context.setBlock(containingBlock);
      context.setSheet(sheet);
      context.setProcessedCellsMap(processedCells);
      context.setDrawing(cellContext.getDrawing());

      // Create an implicit MultiForEach tag.
      MultiForEachTag tag = new MultiForEachTag();
      tag.setContext(context);
      tag.setWorkbookContext(workbookContext);
      // Set the Tag's attributes.
      Map<String, RichTextString> attributes = new HashMap<String, RichTextString>();
      StringBuilder buf = new StringBuilder();
      // Construct the attributes.
      for (int i = 0; i < collectionNames.size(); i++)
      {
         if (i > 0)
            buf.append(MultiForEachTag.SPEC_SEP);
         buf.append(Expression.BEGIN_EXPR);
         buf.append(collectionNames.get(i));
         buf.append(Expression.END_EXPR);
      }
      attributes.put(MultiForEachTag.ATTR_COLLECTIONS, helper.createRichTextString(buf.toString()));

      buf.setLength(0);
      for (int i = 0; i < vars.size(); i++)
      {
         if (i > 0)
            buf.append(MultiForEachTag.SPEC_SEP);
         buf.append(vars.get(i));
      }
      attributes.put(MultiForEachTag.ATTR_VARS, helper.createRichTextString(buf.toString()));
      if (copyRight != null)
         attributes.put(BaseLoopTag.ATTR_COPY_RIGHT, helper.createRichTextString(copyRight));
      if (fixed != null)
         attributes.put(BaseLoopTag.ATTR_FIXED, helper.createRichTextString(fixed));
      if (pastEndAction != null)
         attributes.put(BaseLoopTag.ATTR_PAST_END_ACTION, helper.createRichTextString(pastEndAction));
      if (replacementValue != null)
         attributes.put(BaseLoopTag.ATTR_REPLACE_VALUE, helper.createRichTextString(replacementValue));
      if (groupDir != null)
         attributes.put(BaseLoopTag.ATTR_GROUP_DIR, helper.createRichTextString(groupDir));
      if (collapse != null)
         attributes.put(BaseLoopTag.ATTR_COLLAPSE, helper.createRichTextString(collapse));
      if (tagLoopListener != null)
         attributes.put(BaseLoopTag.ATTR_ON_LOOP_PROCESSED, helper.createRichTextString(tagLoopListener));
      if (tagListener != null)
         attributes.put(BaseTag.ATTR_ON_PROCESSED, helper.createRichTextString(tagListener));
      if (indexVarName != null)
         attributes.put(MultiForEachTag.ATTR_INDEXVAR, helper.createRichTextString(indexVarName));
      if (limit != null)
         attributes.put(MultiForEachTag.ATTR_LIMIT, helper.createRichTextString(limit));
      if (DEBUG)
      {
         for (String attribute : attributes.keySet())
         {
            System.err.println("CoT: attr: " + attribute + " => " + attributes.get(attribute));
         }
      }
      tag.setAttributes(attributes);
      tag.setBodiless(false);

      // Process the implicit MultiForEach tag.
      // No need to remove the non-existent tag text.
      tag.processTag();
   }

   /**
    * Evaluates the given expression, given the <code>Map</code> of bean names
    * to bean values, expecting an integer value for the given key.
    * @param lexeme The expression.
    * @param beans A <code>Map</code> of bean names to bean values.
    * @param keyName The key name.
    * @param cell The <code>Cell</code> on which the metadata is found.
    * @return The integer value.
    */
   private int evaluateInt(String lexeme, Map<String, Object> beans, String keyName, Cell cell)
   {
      Object obj = Expression.evaluateString(lexeme, beans);
      int change;
      if (obj instanceof Number)
      {
         change = ((Number) obj).intValue();
      }
      else
      {
         try
         {
            change = Integer.parseInt(obj.toString());
         }
         catch (NumberFormatException e)
         {
            throw new TagParseException("Metadata key \"" + keyName + "\" needs to be a non-negative integer: " + lexeme
               + SheetUtil.getCellLocation(cell));
         }
         if (change < 0)
         {
            throw new TagParseException("Metadata key \"" + keyName + "\" needs to be a non-negative integer: " + lexeme
               + SheetUtil.getCellLocation(cell));
         }
      }
      return change;
   }

   /**
    * Finds all <code>Collection</code> names in the given <code>Block</code>,
    * starting with the given <code>Cell</code>.  Ignores
    * <code>Collections</code> in the given ignore list.
    *
    * @param startTag The <code>Cell</code> where the first
    *    <code>Collection</code> was found.
    * @param block The <code>Block</code> that was determined by the parent
    *    <code>Block</code> and any metadata found on <code>startTag</code>.
    * @param noImplProcCollNames A <code>List</code> of collection names to
    *    ignore.
    * @param beans The <code>Map</code> of beans.
    * @return A <code>List</code> of all <code>Collection</code> names found.
    */
   private List<String> findCollectionsInBlock(Cell startTag, Block block,
      List<String> noImplProcCollNames, Map<String, Object> beans)
   {
      int startColumnIndex = startTag.getColumnIndex();
      int startRowIndex = startTag.getRowIndex();
      int left = block.getLeftColNum();
      int right = block.getRightColNum();
      int bottom = block.getBottomRowNum();
      if (DEBUG)
         System.err.println("fCIB: Finding Collections in Block: " +
            block + ", starting tag found at row " + startRowIndex + ", cell " + startColumnIndex);
      List<String> collectionNames = new ArrayList<String>();
      List<String> collectionNamesToIgnore = new ArrayList<String>(noImplProcCollNames);

      // Don't report errors for some expressions whose identifiers haven't
      // been defined yet, e.g. a looping variable defined in a subsequent
      // forEach tag.  Store the current silent/lenient flags for restoration
      // later.
      ExpressionFactory factory = ExpressionFactory.getExpressionFactory();
      boolean lenient = factory.isLenient();
      boolean silent = factory.isSilent();
      factory.setLenient(true);
      factory.setSilent(true);

      Row startRow = startTag.getRow();
      int startCellNum = startColumnIndex;
      int endCellNum = right;
      for (int cellNum = startCellNum; cellNum <= endCellNum; cellNum++)
      {
         if (DEBUG)
            System.err.println("  Trying same row: row " + startRowIndex + ", col " + cellNum);
         // First, check remaining Cells in the same row.
         Cell cell = startRow.getCell(cellNum);
         if (cell != null)
         {
            RichTextString richString = cell.getRichStringCellValue();
            List<String> collExprs = Expression.getImplicitCollectionExpr(richString.toString(),
               beans, collectionNamesToIgnore);
            if (!collExprs.isEmpty())
            {
               // Collection Expression(s) found.  Add them if they weren't
               // already found.
               for (String collExpr : collExprs)
                  if (!collectionNames.contains(collExpr))
                     collectionNames.add(collExpr);
            }
         }
      }
      // Examine all following rows in the block.
      Sheet sheet = startTag.getSheet();
      for (int rowNum = startRowIndex + 1; rowNum <= bottom; rowNum++)
      {
         Row row = sheet.getRow(rowNum);
         if (row != null)
         {
            startCellNum = left;
            endCellNum = right;
            for (int cellNum = startCellNum; cellNum <= endCellNum; cellNum++)
            {
               Cell cell = row.getCell(cellNum);
               if (cell != null)
               {
                  RichTextString richString = cell.getRichStringCellValue();
                  List<String> collExprs = Expression.getImplicitCollectionExpr(richString.toString(),
                     beans, collectionNamesToIgnore);
                  if (!collExprs.isEmpty())
                  {
                     // Collection Expression(s) found.  Add them if they weren't
                     // already found.
                     for (String collExpr : collExprs)
                        if (!collectionNames.contains(collExpr))
                           collectionNames.add(collExpr);
                  }
               }
            }
         }
      }  // End loop through rows.

      // Restore old settings.
      factory.setLenient(lenient);
      factory.setSilent(silent);

      return collectionNames;
   }
}
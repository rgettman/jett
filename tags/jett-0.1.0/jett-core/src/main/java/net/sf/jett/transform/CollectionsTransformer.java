package net.sf.jett.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import net.sf.jett.expression.Expression;
import net.sf.jett.expression.ExpressionFactory;
import net.sf.jett.parser.MetadataParser;
import net.sf.jett.tag.BaseLoopTag;
import net.sf.jett.tag.Block;
import net.sf.jett.tag.MultiForEachTag;
import net.sf.jett.tag.TagContext;
import net.sf.jett.util.RichTextStringUtil;
import net.sf.jett.util.SheetUtil;

/**
 * A <code>CollectionsTransformer</code> knows how to perform implicit
 * collections processing on a group of <code>Collections</code>, processing an
 * implicit <code>MultiForEachTag</code>.
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
      Sheet sheet = cell.getSheet();

      MetadataParser parser = null;
      RichTextString richString = cell.getRichStringCellValue();
      String value = richString.getString();
      int metadataIndIdx = value.indexOf(MetadataParser.BEGIN_METADATA);
      if (metadataIndIdx != -1)
      {
         // Evaluate any Expressions in the metadata.
         String metadataExpr = value.substring(metadataIndIdx + MetadataParser.BEGIN_METADATA.length());
         String metadata = Expression.evaluateString(metadataExpr, beans).toString();
         if (DEBUG)
         {
            System.err.println("  CT: Metadata found: " + metadata + " on sheet " + sheet.getSheetName() +
               " at row " + cell.getRowIndex() + ", cell " + cell.getColumnIndex());
         }
         // Parse the Metadata.
         parser = new MetadataParser(metadata);
         parser.parse();
         // Remove the metadata text from the Cell.
         RichTextString metadataRemoved = RichTextStringUtil.replaceAll(richString,
            sheet.getWorkbook().getCreationHelper(), MetadataParser.BEGIN_METADATA + metadataExpr, "");
         SheetUtil.setCellValue(cell, metadataRemoved);
      }

      // Construct a Block with this context's Block as its parent.
      // It will inherit its parent's column range unless overridden later.
      int left = parentBlock.getLeftColNum();
      int right = parentBlock.getRightColNum();
      int top = cell.getRowIndex();
      int bottom = top;
      boolean copyRight = false;
      boolean fixed = false;
      if (parser != null)
      {
         bottom += parser.getExtraRows();
         if (parser.isDefiningCols())
         {
            left = cell.getColumnIndex() - parser.getColsLeft();
            right = cell.getColumnIndex() + parser.getColsRight();
            // Column range can't go outside parent's column range.
            if (left < parentBlock.getLeftColNum())
               left = parentBlock.getLeftColNum();
            if (right > parentBlock.getRightColNum())
               right = parentBlock.getRightColNum();
         }
         copyRight = parser.isCopyingRight();
         fixed = parser.isFixed();
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
      if (!fixed)
      {
         // Remove all collection names not found.
         for (Iterator<String> itr = fixedSizeCollNamesCopy.iterator(); itr.hasNext(); )
         {
            String fixedSizeCollName = itr.next();
            if (DEBUG)
               System.err.println("  CollT: fixed size collection name: " + fixedSizeCollName);
            if (!collectionNames.contains(fixedSizeCollName))
               itr.remove();
         }
         fixed = !fixedSizeCollNamesCopy.isEmpty();
         if (DEBUG)
         {
            if (fixed)
               System.err.println("CollT: Setting implicit tag to fixed: " + fixed +
                  " based on fixed size collection name: " + fixedSizeCollNamesCopy.get(0));
            else
               System.err.println("CollT: Setting implicit tag to fixed: " + fixed +
                  " based on no fixed size collection names found.");
         }
      }

      TagContext context = new TagContext();
      context.setBeans(beans);
      context.setBlock(containingBlock);
      context.setSheet(sheet);
      context.setProcessedCellsMap(processedCells);

      // Create an implicit MultiForEach tag.
      MultiForEachTag tag = new MultiForEachTag();
      tag.setContext(context);
      tag.setWorkbookContext(workbookContext);
      // Set the Tag's attributes.
      Map<String, String> attributes = new HashMap<String, String>();
      StringBuilder buf = new StringBuilder();
      // Construct the "collections" attribute.
      for (int i = 0; i < collectionNames.size(); i++)
      {
         if (i > 0)
            buf.append(MultiForEachTag.SPEC_SEP);
         buf.append(Expression.BEGIN_EXPR);
         buf.append(collectionNames.get(i));
         buf.append(Expression.END_EXPR);
      }
      attributes.put(MultiForEachTag.ATTR_COLLECTIONS, buf.toString());
      // Construct the "vars" attribute.
      buf.setLength(0);
      for (int i = 0; i < vars.size(); i++)
      {
         if (i > 0)
            buf.append(MultiForEachTag.SPEC_SEP);
         buf.append(vars.get(i));
      }
      attributes.put(MultiForEachTag.ATTR_VARS, buf.toString());
      if (copyRight)
         attributes.put(BaseLoopTag.ATTR_COPY_RIGHT, "true");
      if (fixed)
         attributes.put(BaseLoopTag.ATTR_FIXED, "true");

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
      tag.checkAttributes();
      // No need to remove the non-existent tag text.
      tag.process();
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
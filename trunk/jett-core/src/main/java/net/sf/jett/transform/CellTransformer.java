package net.sf.jett.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import net.sf.jett.event.CellEvent;
import net.sf.jett.event.CellListener;
import net.sf.jett.exception.TagParseException;
import net.sf.jett.expression.Expression;
import net.sf.jett.parser.TagParser;
import net.sf.jett.tag.Block;
import net.sf.jett.tag.Tag;
import net.sf.jett.tag.TagContext;
import net.sf.jett.tag.TagLibraryRegistry;
import net.sf.jett.util.RichTextStringUtil;
import net.sf.jett.util.SheetUtil;

/**
 * A <code>CellTransformer</code> knows how to transform a <code>Cell</code>
 * inside of a <code>Sheet</code>.  If a <code>Tag</code> is found, then the
 * <code>CellTransformer</code> will process it.
 */
public class CellTransformer
{
   private static final boolean DEBUG_TAG = false;
   private static final boolean DEBUG_GENERAL = false;

   /**
    * Transforms the given <code>Cell</code>, using the given <code>Map</code>
    * of bean names to bean objects.
    * @param cell The <code>Cell</code> to transform.
    * @param workbookContext The <code>WorkbookContext</code> that provides the
    *    <code>Map</code> of <code>Formulas</code>, the
    *    <code>TagLibraryRegistry</code>, the <code>CellListeners</code>, the
    *    fixed size collection names, and the turned off implicit collection
    *    names.
    * @param cellContext The <code>TagContext</code> that provides the
    *    <code>Map</code> of beans data,  the <code>Map</code> of processed
    *    <code>Cells</code>, and the parent <code>Block</code>.
    * @return <code>true</code> if this <code>Cell</code> was transformed,
    *    <code>false</code> if it needs to be transformed again.  This may
    *    happen if the <code>Block</code> associated with the <code>Tag</code>
    *    was removed.
    */
   public boolean transform(Cell cell, WorkbookContext workbookContext, TagContext cellContext)
   {
      List<CellListener> cellListeners = workbookContext.getCellListeners();
      Map<String, Object> beans = cellContext.getBeans();
      Map<String, Cell> processedCells = cellContext.getProcessedCellsMap();

      // Make sure this Cell hasn't already been processed.
      String key = SheetUtil.getCellKey(cell);
      if (processedCells.containsKey(key))
         return true;

      if (DEBUG_GENERAL)
      {
         System.err.println("Processing row=" + cell.getRowIndex() + ", col=" + cell.getColumnIndex() +
            " on sheet " + cell.getSheet().getSheetName());
         Block parentBlock = cellContext.getBlock();
         System.err.println("Parent Block: " + parentBlock);
      }

      Sheet sheet = cell.getSheet();
      boolean cellProcessed = true;
      Object oldValue = null;
      Object newValue = null;
      switch(cell.getCellType())
      {
      case Cell.CELL_TYPE_STRING:
         oldValue = cell.getStringCellValue();

         TagParser parser = new TagParser(cell);
         parser.parse();

         if (parser.isTag() && !parser.isEndTag())
         {
            // Transform the Tag.
            if (DEBUG_GENERAL)
               System.err.println("  CT: Transforming tag cell tag.");
            cellProcessed = transformCellTag(cell, workbookContext, cellContext, parser);
         }
         else
         {
            // Not a tag.  Evaluate any Expressions embedded in the value.
            RichTextString richString = cell.getRichStringCellValue();
            List<String> noImplProcCollNames =
               workbookContext.getNoImplicitProcessingCollectionNames();
            List<String> collExprs = Expression.getImplicitCollectionExpr(richString.toString(),
               beans, noImplProcCollNames);
            if (!collExprs.isEmpty())
            {
               if (DEBUG_GENERAL)
                  System.err.println("  CT: Transforming implicit collection(s).");
               CollectionsTransformer collTransformer = new CollectionsTransformer();
               collTransformer.transform(cell, workbookContext, cellContext);
               // The implicit collection processing has already processed this Cell.
               cellProcessed = false;
            }
            else
            {
               // Evaluate.
               if (DEBUG_GENERAL)
                  System.err.println("  CT: Transforming string cell.");
               Object result = Expression.evaluateString(richString,
                  sheet.getWorkbook().getCreationHelper(), beans);
               newValue = SheetUtil.setCellValue(cell, result);
            }
         }
         break;
      case Cell.CELL_TYPE_NUMERIC:
         if (DateUtil.isCellDateFormatted(cell))
            oldValue = newValue = cell.getDateCellValue();  // java.util.Date
         else
            oldValue = newValue = cell.getNumericCellValue();  // double
         break;
      case Cell.CELL_TYPE_BLANK:
         oldValue = newValue = null;
         break;
      case Cell.CELL_TYPE_FORMULA:
         oldValue = newValue = cell.getCellFormula();  // java.lang.String
         break;
      case Cell.CELL_TYPE_BOOLEAN:
         oldValue = newValue = cell.getBooleanCellValue();  // boolean
         break;
      case Cell.CELL_TYPE_ERROR:
         oldValue = newValue = cell.getErrorCellValue();  // byte
      }  // End switch on cell type
      if (cellProcessed)
      {
         CellEvent event = new CellEvent(cell, beans, oldValue, newValue);
         for (CellListener listener : cellListeners)
            listener.cellProcessed(event);
         // Only mark it as processed if the Cell has actually been processed.
         processedCells.put(key, cell);
      }
      return cellProcessed;
   }

   /**
    * Transforms the <code>Tag</code> defined in the given <code>Cell</code>.
    * @param cell The <code>Cell</code> on which the <code>Tag</code> is defined.
    * @param workbookContext The <code>WorkbookContext</code>.
    * @param cellContext The <code>CellContext</code>.
    * @param parser The <code>TagParser</code> used to parse the tag's text.
    * @return <code>true</code> if this <code>Cell</code> was transformed,
    *    <code>false</code> if it needs to be transformed again.  This may
    *    happen if the <code>Block</code> associated with the <code>Tag</code>
    *    was removed.
    */
   private boolean transformCellTag(Cell cell, WorkbookContext workbookContext,
      TagContext cellContext, TagParser parser)
   {
      Block parentBlock = cellContext.getBlock();
      TagLibraryRegistry registry = workbookContext.getRegistry();
      Map<String, Object> beans = cellContext.getBeans();
      Map<String, Cell> processedCells = cellContext.getProcessedCellsMap();
      String value = cell.getStringCellValue();
      RichTextString richTextString = cell.getRichStringCellValue();
      Block newBlock;
      if (parser.isBodiless())
      {
         // Results in a 1x1 block of 1 cell.
         newBlock = new Block(parentBlock, cell);
      }
      else
      {
         // Remove start tag text.
         SheetUtil.setCellValue(cell, RichTextStringUtil.replaceAll(richTextString,
            cell.getSheet().getWorkbook().getCreationHelper(), parser.getTagText(), "", true));
         if (DEBUG_TAG)
            System.err.println("Cell text after tag removal is \"" + cell.getStringCellValue() + "\".");
         // Search for matching end tag.
         Cell match = findMatchingEndTag(cell, parentBlock, parser.getNamespaceAndTagName());
         if (match == null)
            throw new TagParseException("Matching tag not found for tag: " + parser.getTagText() +
               ", located at " + cell.getSheet().getSheetName() + ", row " + cell.getRowIndex() +
               ", col " + cell.getColumnIndex() + ", within block " + parentBlock);

         if (DEBUG_TAG)
         {
            System.err.println("  Match found at row " + match.getRowIndex() +
               " and column " + match.getColumnIndex());
         }
         newBlock = new Block(parentBlock, cell, match);
      }
      TagContext context = new TagContext();
      context.setBeans(beans);
      context.setBlock(newBlock);
      context.setSheet(cell.getSheet());
      context.setProcessedCellsMap(processedCells);

      Tag tag = registry.createTag(parser, context, workbookContext);
      if (tag == null)
         throw new TagParseException("Invalid tag: " + value);

      // Process the Tag.
      tag.checkAttributes();

      return tag.process();
   }

   /**
    * Finds the end tag that matches the given start tag.  The end tag must
    * reside inside the given <code>parentBlock</code>.
    * @param startTag The <code>Cell</code> with the start tag.
    * @param parentBlock The parent <code>Block</code> in which the given
    *    <code>Cell</code> is contained.  The end tag must also be contained
    *    within this <code>Block</code>.
    * @param namespaceAndTagName The namespace and tag name of the start tag,
    *    e.g. "namespace:tagName".
    * @return The <code>Cell</code> containing the matching end tag, or
    *    <code>null</code> if there is no matching end tag.
    */
   private Cell findMatchingEndTag(Cell startTag, Block parentBlock, String namespaceAndTagName)
   {
      int startColumnIndex = startTag.getColumnIndex();
      int startRowIndex = startTag.getRowIndex();
      int right = parentBlock.getRightColNum();
      int bottom = parentBlock.getBottomRowNum();

      if (DEBUG_TAG)
         System.err.println("fMET: Matching tag " + namespaceAndTagName + " in " +
            parentBlock + ", starting tag found at row " + startRowIndex + ", cell " + startColumnIndex);

      List<TagParser> innerTags = new ArrayList<TagParser>();

      // Look for candidate matches in current Cell, to its right, below it, or
      // both.
      Sheet sheet = startTag.getSheet();
      for (int rowNum = startRowIndex; rowNum <= bottom; rowNum++)
      {
         Row row = sheet.getRow(rowNum);
         if (row != null)
         {
            for (int cellNum = startColumnIndex; cellNum <= right; cellNum++)
            {
               if (DEBUG_TAG)
                  System.err.println("  Trying row: row " + rowNum + ", col " + cellNum);
               Cell candidate = row.getCell(cellNum);
               if (candidate != null && isMatchingEndTag(candidate, namespaceAndTagName, innerTags))
                  return candidate;
            }
         }
      }
      // If we got here, then there wasn't a match.
      return null;
   }

   /**
    * Helper method to determine if the given candidate <code>Cell</code> is an
    * end tag that matches the given namespace and tag name, considering the
    * given <code>List</code> of unmatched inner tags already encountered.
    *
    * @param candidate The candidate <code>Cell</code>.
    * @param namespaceAndTagName The namespace and tag name to match.
    * @param innerTags A <code>List</code> of inner tags which must be matched
    *    prior to matching the given namespace and tag name.  This stack may be
    *    modified if <code>candidate</code> is itself a start tag, or if
    *    <code>candidate</code> is an end tag that matches an inner tag.
    * @return <code>true</code> if it matches, <code>false</code> otherwise.
    */
   private boolean isMatchingEndTag(Cell candidate, String namespaceAndTagName,
      List<TagParser> innerTags)
   {
      if (candidate.getCellType() != Cell.CELL_TYPE_STRING)
         return false;
      TagParser candidateParser = new TagParser(candidate);
      candidateParser.parse();
      int rightMostCol = candidate.getColumnIndex();
      int afterTagIdx = 0;
      if (DEBUG_TAG)
         System.err.println("    iMET: afterTagIdx=" + afterTagIdx + ", parser's tag text is \"" +
            candidateParser.getTagText() + "\".");

      // Look for possibly multiple tags on the same Cell.
      while (candidateParser.isTag())
      {
         if (candidateParser.isEndTag())
         {
            // Found matching end tag with no unclosed intervening start tags.
            if (namespaceAndTagName.equals(candidateParser.getNamespaceAndTagName()) &&
                doAllInnerTagsMatch(innerTags, rightMostCol))
            {
               // This is the matching end tag.  Remove it from the Cell.
               SheetUtil.setCellValue(candidate, RichTextStringUtil.replaceAll(candidate.getRichStringCellValue(),
                  candidate.getSheet().getWorkbook().getCreationHelper(), candidateParser.getTagText(), "", true, afterTagIdx));
               return true;
            }
            else
            {
               // End tag matches an intervening start tag.
               if (innerTags.isEmpty())
               {
                  throw new TagParseException("End tag found \"" + candidateParser.getNamespaceAndTagName() +
                     "\" does not match start tag \"" + namespaceAndTagName + "\".");
               }
               if (DEBUG_TAG)
                  System.err.println("    iMET: Adding end tag to list: " + candidateParser.getNamespaceAndTagName());
               innerTags.add(candidateParser);
            }
         }
         else if (!candidateParser.isEndTag())
         {
            // Found another start tag.  If bodiless, don't bother pushing it.
            // If it is not bodiless, then it now needs to be matched BEFORE we
            // can match the original start tag.  Push it onto the "stack".
            if (!candidateParser.isBodiless())
            {
               if (DEBUG_TAG)
                  System.err.println("    iMET: Adding start tag to list: " + candidateParser.getNamespaceAndTagName());
               innerTags.add(candidateParser);
            }
         }
         // Setup for next loop.  Advance past this tag.
         afterTagIdx += candidateParser.getAfterTagIdx();
         candidateParser = new TagParser(candidate, afterTagIdx);
         candidateParser.parse();
         if (DEBUG_TAG)
            System.err.println("    iMET: afterTagIdx is now " + afterTagIdx + ", parser's tag text is \"" +
               candidateParser.getTagText() + "\".");
      }
      // If we got here, then we did not match.
      return false;
   }

   /**
    * Determines whether all tags in the given <code>List</code>, disregarding
    * any tags found to the right of the given column index, i.e.
    * <code>parser.getCell().getColumnIndex() &gt; rightMostCol</code>.
    *
    * @param innerTags The <code>List</code> of <code>TagParsers</code>
    *    containing tags to match.
    * @param rightMostCol Disregard all tags found to the right of this column
    *    index (0-based).  Pass -1 to consider all tags, no matter how far to
    *    the right they are.
    * @return <code>true</code> if all considered tags match,
    *    <code>false</code> otherwise.
    */
   private boolean doAllInnerTagsMatch(List<TagParser> innerTags, int rightMostCol)
   {
      Stack<TagParser> tagsToMatch = new Stack<TagParser>();
      if (DEBUG_TAG)
         System.err.println("    dAITM:");
      for (TagParser parser : innerTags)
      {
         Cell candidateCell = parser.getCell();
         if (candidateCell.getColumnIndex() <= rightMostCol)
         {
            if (DEBUG_TAG)
              System.err.println("      dAITM: Considering tag: " + parser.getNamespaceAndTagName() + " at row " +
                   parser.getCell().getRowIndex() + ", col " + parser.getCell().getColumnIndex());
            if (parser.isEndTag())
            {
               // Unmatched end tag.
               if (tagsToMatch.isEmpty())
               {
                  if (DEBUG_TAG)
                     System.err.println("      dAITM: Unmatched end tag.");
                  return false;
               }

               String namespaceAndTagName = parser.getNamespaceAndTagName();
               TagParser startParser = tagsToMatch.peek();
               if (DEBUG_TAG)
                  System.err.println("      dAITM: Comparing start: " + startParser.getNamespaceAndTagName() +
                     " to end: " + namespaceAndTagName);
               if (namespaceAndTagName.equals(startParser.getNamespaceAndTagName()))
               {
                  if (DEBUG_TAG)
                     System.err.println("      dAITM: Popped " + startParser.getNamespaceAndTagName());
                  tagsToMatch.pop();
               }
            }
            else if (!parser.isBodiless())
            {
               if (DEBUG_TAG)
                  System.err.println("      dAITM: Pushed " + parser.getNamespaceAndTagName());
               tagsToMatch.push(parser);
            }
         }
      }
      return tagsToMatch.isEmpty();
   }
}

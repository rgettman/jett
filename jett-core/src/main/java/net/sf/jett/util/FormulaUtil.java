package net.sf.jett.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jett.formula.CellRef;
import net.sf.jett.formula.CellRefRange;
import net.sf.jett.formula.Formula;
import net.sf.jett.model.WorkbookContext;

/**
 * The <code>FormulaUtil</code> utility class provides methods for Excel
 * formula creation and manipulation.
 *
 * @author Randy Gettman
 */
public class FormulaUtil
{
   private static final boolean DEBUG = false;

   // Prevents a mapping of "A1" => "A21" and "A2" => "A22" from yielding
   // "A1 + A2" => "A21 + A2" => "A221 + A22".
   private static final String NEGATIVE_LOOKBEHIND_ALPHA = "(?<![A-Za-z])";
   private static final String NEGATIVE_LOOKAHEAD_ALPHAN = "(?![A-Za-z0-9])";

   /**
    * Finds unique cell references in all <code>Formulas</code> in the given
    * formula map.
    * @param formulaMap A formula map.
    * @return A cell reference map, a <code>Map</code> of cell key strings to
    *    <code>Lists</code> of <code>CellRefs</code>.  Each <code>List</code>
    *    is initialized to contain only one <code>CellRef</code>, the original
    *    from the cell key string, e.g. "Sheet1!C2" => [Sheet1!C2]
    */
   public static Map<String, List<CellRef>> createCellRefMap(Map<String, Formula> formulaMap)
   {
      if (DEBUG)
      {
         System.err.println("FU.cCRM");
      }
      Map<String, List<CellRef>> cellRefMap = new HashMap<String, List<CellRef>>();
      for (String key : formulaMap.keySet())
      {
         Formula formula = formulaMap.get(key);
         if (DEBUG)
         {
            System.err.println("  Processing key " + key + " => " + formula);
         }
         // Formula keys always are of the format "Sheet!CellRef".
         // The key was created internally, so "!" is expected.
         String keySheetName = key.substring(0, key.indexOf("!"));

         for (CellRef cellRef : formula.getCellRefs())
         {
            String sheetName = cellRef.getSheetName();
            String cellKey;
            // If no sheet name, then prepend the sheet name from the Formula key.
            if (sheetName == null || "".equals(sheetName))
            {
               // Prepend sheet name from formula key.
               cellKey = keySheetName + "!" + cellRef.formatAsString();
            }
            else
            {
               // Single quotes may be in the cell reference.
               // Don't store single-quotes in cell key:
               // "'Test Sheet'!C3" => "Test Sheet!C3"
               cellKey = cellRef.formatAsString().replace("'", "");
            }
            if (!cellRefMap.containsKey(cellKey))
            {
               List<CellRef> cellRefs = new ArrayList<CellRef>();
               CellRef mappedCellRef;
               if (sheetName == null || "".equals(sheetName))
               {
                  // Local sheet reference.
                  mappedCellRef = new CellRef(cellRef.getRow(), cellRef.getCol(),
                     cellRef.isRowAbsolute(), cellRef.isColAbsolute());
               }
               else
               {
                  // Refer to the template sheet name in the CellRef for now.
                  // If necessary, it will be translated into resultant sheet
                  // name(s) later in "updateSheetNameRefsAfterClone".
                  mappedCellRef = new CellRef(sheetName, cellRef.getRow(), cellRef.getCol(),
                     cellRef.isRowAbsolute(), cellRef.isColAbsolute());
               }
               cellRefs.add(mappedCellRef);
               if (DEBUG)
               {
                  System.err.println("    New CellRefMap entry: " + cellKey + " => [" +
                     mappedCellRef.formatAsString() + "]");
               }
               cellRefMap.put(cellKey, cellRefs);
            }
         }
      }
      return cellRefMap;
   }

   /**
    * Replaces cell references in the given formula text with the translated
    * cell references, and returns the formula string.
    * @param formulaText The <code>Formula</code> text, e.g. "SUM(C2)".
    * @param formula The <code>Formula</code, for its access to its original
    *    <code>CellRefs</code>.
    * @param sheetName The name of the <code>Sheet</code> on which the formula
    *    exists.
    * @param context The <code>WorkbookContext</code>, for its access to the
    *    cell reference map.
    * @return A string suitable for an Excel formula, for use in the method
    *    <code>Cell.setCellFormula()</code>.
    */
   public static String createExcelFormulaString(String formulaText, Formula formula,
      String sheetName, WorkbookContext context)
   {
      Map<String, List<CellRef>> cellRefMap = context.getCellRefMap();
      List<CellRef> origCellRefs = formula.getCellRefs();
      StringBuffer buf = new StringBuffer();
      String excelFormula, suffix;
      int idx = formulaText.indexOf("[", Formula.BEGIN_FORMULA.length());  // Get pos of any suffixes (e.g. "[0,0]").
      if (idx > -1)
      {
         excelFormula = formulaText.substring(0, idx);
         suffix = formulaText.substring(idx);
      }
      else
      {
         excelFormula = formulaText;
         suffix = "";
      }
      // Strip any $[ and ] off the Excel Formula, which at this point has been
      // stripped of any suffixes already.
      if (excelFormula.startsWith(Formula.BEGIN_FORMULA) && excelFormula.endsWith(Formula.END_FORMULA))
         excelFormula = excelFormula.substring(Formula.BEGIN_FORMULA.length(),
            excelFormula.length() - Formula.END_FORMULA.length());

      if (DEBUG)
      {
         System.err.println("FU.cEFS: Formula text:\"" + formulaText + "\" on sheet " + sheetName);
      }

      for (CellRef origCellRef : origCellRefs)
      {
         if (DEBUG)
         {
            System.err.println("  Original cell ref: " + origCellRef.formatAsString());
         }
         // Look up the translated cells by cell key, which requires a sheet name.
         String cellKey;
         String origCellRefSheetName = origCellRef.getSheetName();
         if (origCellRefSheetName != null)
            cellKey = origCellRef.formatAsString().replace("'", "");  // Lose any single quotes in the sheet name.
         else
            cellKey = sheetName + "!" + origCellRef.formatAsString();
         // Append the suffix to the cell key to look up the correct references.
         cellKey += suffix;

         // Find the appropriate cell references.
         // It may be necessary to remove suffixes iteratively, if the cell key
         // represents a formula cell reference outside of a looping tag.
         List<CellRef> transCellRefs;
         do
         {
            transCellRefs = cellRefMap.get(cellKey);
            if (DEBUG)
            {
               System.err.println("  cellKey: " + cellKey + " => " + transCellRefs);
            }

            // Remove suffixes, one at a time if it's not found.
            if (transCellRefs == null)
            {
               int lastSuffixIdx = cellKey.lastIndexOf("[");
               if (lastSuffixIdx != -1)
               {
                  cellKey = cellKey.substring(0, lastSuffixIdx);
               }
               else
               {
                  throw new IllegalStateException("Unable to find cell references for cell key \"" + cellKey + "\"!");
               }
            }
         }
         while (transCellRefs == null);

         // Construct the replacement string.
         String cellRefs;
         // Avoid re-allocation of the internal buffer.
         buf.delete(0, buf.length());
         int numCellRefs = transCellRefs.size();
         if (DEBUG)
         {
            System.err.println("  Number of translated cell refs: " + numCellRefs);
         }
         if (numCellRefs > 0)
         {
            for (int i = 0; i < numCellRefs; i++)
            {
               if (i > 0)
                  buf.append(",");
               String cellRef = transCellRefs.get(i).formatAsString();
               if (DEBUG)
               {
                  System.err.println("    Appending cell ref string: \"" + cellRef + "\".");
               }
               buf.append(cellRef);
            }
            cellRefs = buf.toString();
         }
         else
         {
            // All cell references were deleted.  Must use the cell reference's
            // default value.  If that doesn't exist, that means that a default
            // value wasn't specified.  Use the "default" default.
            cellRefs = origCellRef.getDefaultValue();
            if (cellRefs == null)
               cellRefs = CellRef.DEF_DEFAULT_VALUE;
            if (DEBUG)
            {
               System.err.println("    Appending default value: \"" + cellRefs + "\".");
            }
         }
         // Replace the formula text, including any default value, with the
         // updated cell references.
         if(DEBUG)
         {
            System.err.println("Regex: " + NEGATIVE_LOOKBEHIND_ALPHA +
               Pattern.quote(origCellRef.formatAsStringWithDef()) +
               NEGATIVE_LOOKAHEAD_ALPHAN);
            System.err.println("cellRefs: " + Matcher.quoteReplacement(cellRefs));
         }
         excelFormula = excelFormula.replaceAll(
             NEGATIVE_LOOKBEHIND_ALPHA + 
                 Pattern.quote(origCellRef.formatAsStringWithDef()) +
                 NEGATIVE_LOOKAHEAD_ALPHAN,
             Matcher.quoteReplacement(cellRefs));
      }
      return excelFormula;
   }

   /**
    * Examines all <code>CellRefs</code> in each <code>List</code>.  If a group
    * of <code>CellRefs</code> represent a linear range, horizontally or
    * vertically, then they are replaced with a <code>CellRefRange</code>.
    * @param cellRefMap The cell reference map.
    */
   public static void findAndReplaceCellRanges(Map<String, List<CellRef>> cellRefMap)
   {
      for (String key : cellRefMap.keySet())
      {
         List<CellRef> cellRefs = cellRefMap.get(key);
         // This will put cells that should be part of a range in consecutive
         // positions.
         Collections.sort(cellRefs);
         if (DEBUG)
         {
            System.err.println("FU.fARCR: Replacing cell ref ranges for \"" + key + "\".");
            System.err.println("  cellRefs: " + cellRefs);
         }
         boolean vertical = false;
         boolean horizontal = false;
         CellRef first = null, prev = null;
         int firstIdx = -1;
         int size = cellRefs.size();

         for (int i = 0; i < size; i++)
         {
            CellRef curr = cellRefs.get(i);
            if (DEBUG)
            {
               System.err.println("  curr is " + curr.formatAsString());
            }
            if (first == null)
            {
               vertical = false;
               horizontal = false;
               first = curr;
               firstIdx = i;
               if (DEBUG)
               {
                  System.err.println("    Case first was null; first: " + first.formatAsString() + ", firstIdx = " + firstIdx);
               }
            }
            else if (vertical)
            {
               if (DEBUG)
               {
                  System.err.println("    Case vertical; first: " + first.formatAsString() + ", firstIdx = " + firstIdx);
               }
               if (!isBelow(prev, curr))
               {
                  // End of range.  Replace sequence of vertically arranged
                  // CellRefs with a single CellRefRange.
                  replaceRange(cellRefs, firstIdx, i - 1);
                  // The list has shrunk.
                  int shrink = size - cellRefs.size();
                  size -= shrink;
                  i -= shrink;
                  // Setup for next range.
                  vertical = false;
                  first = curr;
                  firstIdx = i;
               }
            }
            else if (horizontal)
            {
               if (DEBUG)
               {
                  System.err.println("    Case horizontal; first: " + first.formatAsString() + ", firstIdx = " + firstIdx);
               }
               if (!isRightOf(prev, curr))
               {
                  // End of range.  Replace sequence of vertically arranged
                  // CellRefs with a single CellRefRange.
                  replaceRange(cellRefs, firstIdx, i - 1);
                  // The list has shrunk.
                  int shrink = size - cellRefs.size();
                  size -= shrink;
                  i -= shrink;
                  // Setup for next range.
                  horizontal = false;
                  first = curr;
                  firstIdx = i;
               }
            }
            else
            {
               // Decide on the proper direction, if any.
               if (isRightOf(prev, curr))
                  horizontal = true;
               else if (isBelow(prev, curr))
                  vertical = true;
               else
               {
                  first = curr;
                  firstIdx = i;
               }
               if (DEBUG)
               {
                  System.err.println("    Case none; first: " + first.formatAsString() + ", firstIdx = " + firstIdx +
                     ", horizontal=" + horizontal + ", vertical = " + vertical);
               }
            }
            prev = curr;
         }

         // Don't forget the last one!
         if (horizontal || vertical)
            replaceRange(cellRefs, firstIdx, size - 1);
      }
   }

   /**
    * After sheets have been cloned, all sheets could have been renamed,
    * leaving the situation where in the cell ref map, all cell keys are of the
    * new sheet names, but the <code>CellRefs</code> still refer to the
    * template sheet names.  This updates all <code>CellRefs</code> in the cell
    * ref map to the new sheet names, cloning the references if necessary.
    * @param context The <code>WorkbookContext</code>, which contains the cell
    *    rep map, the template sheet names, and the new sheet names.
    * @since 0.8.0
    */
   public static void updateSheetNameRefsAfterClone(WorkbookContext context)
   {
      Map<String, List<CellRef>> cellRefMap = context.getCellRefMap();
      List<String> templateSheetNamesList = context.getTemplateSheetNames();
      List<String> newSheetNamesList = context.getSheetNames();
      if (DEBUG)
      {
         System.err.println("FU.uSNRAC...");
      }
      for (String key : cellRefMap.keySet())
      {
         if (DEBUG)
         {
            System.err.println("key: \"" + key + "\".");
         }
         // Formula keys always are of the format "Sheet!CellRef".
         // The key was created internally, so "!" is expected.
         // 1. templateSheet!cellKey => templateSheet!cellRef
         // Must update cell refs to resultant sheet cell ref(s).
         // 2. resultantSheet!cellKey => cellRef
         // Don't update these.
         String templateSheetName = key.substring(0, key.indexOf("!"));
         // Determine if it's a template sheet name.
         int index = templateSheetNamesList.indexOf(templateSheetName);
         if (index == -1)
         {
            // Assumed to be a resultant sheet name; skip.
            continue;
         }
         // At this point, keySheetName is known to be a template sheet name.
         // No transformation has taken place yet, so there should be exactly
         // one cell ref in the list.
         List<CellRef> cellRefs = cellRefMap.get(key);
         List<CellRef> addedCellRefs = new ArrayList<CellRef>();
         CellRef cellRef = cellRefs.get(0);
         if (DEBUG)
         {
            System.err.println("  cellRef: \"" + cellRef + "\".");
         }
         String templateRefSheetName = cellRef.getSheetName();

         // No cell ref sheet reference means a simple reference, e.g "B2",
         // meaning "this sheet", which means don't update.
         if (templateRefSheetName != null)
         {
            // Update the reference, plus clone the reference too, if more
            // than one template sheet name matches.
            boolean updatedFirstAlready = false;
            for (int j = 0; j < templateSheetNamesList.size(); j++)
            {
               String sheetName = templateSheetNamesList.get(j);
               if (sheetName.equals(templateSheetName))
               {
                  String newSheetName = newSheetNamesList.get(j);
                  CellRef newCellRef = new CellRef(newSheetName, cellRef.getRow(), cellRef.getCol(),
                     cellRef.isRowAbsolute(), cellRef.isColAbsolute());
                  if (updatedFirstAlready)
                  {
                     if (DEBUG)
                     {
                        System.err.println("    refers to other sheet: Adding \"" + newCellRef + "\".");
                     }
                     addedCellRefs.add(newCellRef);
                  }
                  else
                  {
                     if (DEBUG)
                     {
                        System.err.println("    refers to other sheet: Replacing \"" + cellRef + "\" with \"" +
                           newCellRef + "\".");
                     }
                     cellRefs.set(0, newCellRef);  // The only one so far.
                     updatedFirstAlready = true;
                  }
               }
            }  // End for loop on template sheet names
         }  // End null check on templateSheetRefName
         cellRefs.addAll(addedCellRefs);
      }  // End for loop on cell keys.
   }

   /**
    * When a <code>Sheet</code> is renamed, then this updates all
    * <code>CellRefs</code> in the cell reference map need to be updated too.
    * @param context The <code>WorkbookContext</code>, on which the formula map
    *    and the cell ref map can be found.
    * @param oldSheetName The old sheet name.
    * @param newSheetName The new sheet name.
    * @since 0.8.0
    */
   public static void replaceSheetNameRefs(WorkbookContext context, String oldSheetName, String newSheetName)
   {
      // Update new sheet name list (local copy, doesn't affect the original
      // list passed in to ExcelTransformer.transform).
      List<String> newSheetNames = context.getSheetNames();
      int index = newSheetNames.indexOf(oldSheetName);
      if (index != -1)
      {
         newSheetNames.set(index, newSheetName);
      }

      // Formula map: update keys.
      Map<String, Formula> formulaMap = context.getFormulaMap();
      List<String> removeFromFormulaMap = new ArrayList<String>();
      Map<String, Formula> addToFormulaMap = new HashMap<String, Formula>();
      for (String key : formulaMap.keySet())
      {
         if (key.startsWith(oldSheetName))
         {
            Formula formula = formulaMap.get(key);
            removeFromFormulaMap.add(key);
            index = key.indexOf("!");  // Expected to be present in all cell keys
            String newKey = newSheetName + "!" + key.substring(index + 1);
            if (DEBUG)
            {
               System.err.println("FU.rSNR: Replacing formula map key " + key + " with " + newKey);
            }
            addToFormulaMap.put(newKey, formula);
         }
      }
      // Now remove all the keys marked to be removed, now that we're past the
      // Iterator and a possible ConcurrentModificationException.
      for (String key : removeFromFormulaMap)
      {
         formulaMap.remove(key);
      }
      // Put back all the replacements.
      formulaMap.putAll(addToFormulaMap);

      // Cell Ref Map: update keys and cell refs.
      Map<String, List<CellRef>> cellRefMap = context.getCellRefMap();
      List<String> removeFromCellRefMap = new ArrayList<String>();
      Map<String, List<CellRef>> addToCellRefMap = new HashMap<String, List<CellRef>>();
      for (String key : cellRefMap.keySet())
      {
         List<CellRef> cellRefs = cellRefMap.get(key);
         // If the sheet name in the key changed, then we must replace the entry.
         // This occurs with JETT formulas in a sheet whose name was changed
         // via an expression, when those formulas refer to local sheet cells.
         if (key.startsWith(oldSheetName))
         {
            removeFromCellRefMap.add(key);
            index = key.indexOf("!");  // Expected to be present in all cell keys
            String newKey = newSheetName + "!" + key.substring(index + 1);
            if (DEBUG)
            {
               System.err.println("FU.rSNR: Replacing cell ref map key " + key + " with " + newKey);
            }
            addToCellRefMap.put(newKey, cellRefs);
         }
         for (int i = 0; i < cellRefs.size(); i++)
         {
            CellRef cellRef = cellRefs.get(i);
            String cellRefSheetName = cellRef.getSheetName();
            if (cellRefSheetName != null && cellRefSheetName.equals(oldSheetName))
            {
               CellRef newCellRef = new CellRef(newSheetName, cellRef.getRow(), cellRef.getCol(),
                       cellRef.isRowAbsolute(), cellRef.isColAbsolute());
               if (DEBUG)
               {
                  System.err.println("FU.rSNR: replacing cell ref " + cellRef + " with " + newCellRef);
               }
               cellRefs.set(i, newCellRef);
            }
         }
      }
      // Now remove all the keys marked to be removed, now that we're past the
      // Iterator and a possible ConcurrentModificationException.
      for (String key : removeFromCellRefMap)
      {
         cellRefMap.remove(key);
      }
      // Put back all the replacements.
      cellRefMap.putAll(addToCellRefMap);
   }

   /**
    * Returns <code>true</code> if <code>curr</code> is directly to the right
    * of <code>prev</code>, i.e., all of the following are true:
    * <ul>
    * <li>The sheet names match or they are both <code>null</code>.
    * <li>The row indexes match.
    * <li>The column index of <code>curr</code> is one more than the column
    *    index of <code>prev</code>.
    * </ul>
    * @param prev The previous <code>CellRef</code>.
    * @param curr The current <code>CellRef</code>.
    * @return <code>true</code> if <code>curr</code> is directly to the right
    *    of <code>prev</code>, else <code>false</code>.
    */
   private static boolean isRightOf(CellRef prev, CellRef curr)
   {
      return (curr.getRow() == prev.getRow() && curr.getCol() == prev.getCol() + 1 &&
                   ((curr.getSheetName() == null && prev.getSheetName() == null) ||
                    (curr.getSheetName() != null && curr.getSheetName().equals(prev.getSheetName()))));
   }

   /**
    * Returns <code>true</code> if <code>curr</code> is directly below
    * <code>prev</code>, i.e., all of the following are true:
    * <ul>
    * <li>The sheet names match or they are both <code>null</code>.
    * <li>The column indexes match.
    * <li>The row index of <code>curr</code> is one more than the row
    *    index of <code>prev</code>.
    * </ul>
    * @param prev The previous <code>CellRef</code>.
    * @param curr The current <code>CellRef</code>.
    * @return <code>true</code> if <code>curr</code> is directly below
    *    <code>prev</code>, else <code>false</code>.
    */
   private static boolean isBelow(CellRef prev, CellRef curr)
   {
      return (curr.getCol() == prev.getCol() && curr.getRow() == prev.getRow() + 1 &&
                   ((curr.getSheetName() == null && prev.getSheetName() == null) ||
                    (curr.getSheetName() != null && curr.getSheetName().equals(prev.getSheetName()))));
   }

   /**
    * Replace the <code>CellRefs</code> in the given <code>List</code> of
    * <code>CellRefs</code>, in the range of indexes between
    * <code>startIdx</code> and <code>endIdx</code> with a single
    * <code>CellRefRange</code>.
    * @param cellRefs Modifies this <code>List</code> of <code>CellRefs</code>.
    * @param startIdx The <code>CellRef</code> at this index is treated as the
    *    start of the range (inclusive).
    * @param endIdx The <code>CellRef</code> at this index is treated as the
    *    end of the range (inclusive).
    */
   private static void replaceRange(List<CellRef> cellRefs, int startIdx, int endIdx)
   {
      // Create the range.
      CellRef first = cellRefs.get(startIdx);
      CellRef prev = cellRefs.get(endIdx);
      CellRefRange range = new CellRefRange(first.getSheetName(), first.getRow(), first.getCol(),
         first.isRowAbsolute(), first.isColAbsolute());
      range.setRangeEndCellRef(prev);
      if (DEBUG)
      {
         System.err.println("  Replacing " + first.formatAsString() + " through " +
            prev.formatAsString() + " with " + range.formatAsString());
      }
      // Replace the first with the range.
      cellRefs.set(startIdx, range);
      // Remove the others in the range.  The end index for the "subList"
      // method is exclusive.
      cellRefs.subList(startIdx + 1, endIdx + 1).clear();
   }

   /**
    * Shifts all <code>CellRefs</code> that are in range and on the same
    * <code>Sheet</code> by the given number of rows and/or columns (usually
    * one of those two will be zero).  Modifies the <code>Lists</code> that are
    * the values of <code>cellRefMap</code>.
    * @param sheetName The name of the <code>Sheet</code> on which to shift
    *    cell references.
    * @param context The <code>WorkbookContext</code> which holds the cell ref
    *    map, template sheet names, and new sheet names.
    * @param left The 0-based index of the column on which to start shifting
    *    cell references.
    * @param right The 0-based index of the column on which to end shifting
    *    cell references.
    * @param top The 0-based index of the row on which to start shifting
    *    cell references.
    * @param bottom The 0-based index of the row on which to end shifting
    *    cell references.
    * @param numCols The number of columns to shift the cell reference (can be
    *    negative).
    * @param numRows The number of rows to shift the cell reference (can be
    *    negative).
    * @param remove Determines whether to remove the old cell reference,
    *    resulting in a shift, or not to remove the old cell reference,
    *    resulting in a copy.
    * @param add Determines whether to add the new cell reference, resulting in
    *    a copy, or not to add the new cell reference, resulting in a shift.
    */
   public static void shiftCellReferencesInRange(String sheetName, WorkbookContext context,
      int left, int right, int top, int bottom, int numCols, int numRows,
      boolean remove, boolean add)
   {
      if (DEBUG)
      {
         System.err.println("    FU.sCRIR: left " + left + ", right " + right +
            ", top " + top + ", bottom " + bottom + ", numCols " + numCols +
            ", numRows " + numRows + ", remove " + remove + ", add " + add);
      }
      Map<String, List<CellRef>> cellRefMap = context.getCellRefMap();
      List<String> templateSheetNames = context.getTemplateSheetNames();
      List<String> newSheetNames = context.getSheetNames();
      if (numCols == 0 && numRows == 0 && remove && add)
         return;
      for (String cellKey : cellRefMap.keySet())
      {
         // All cell keys have the sheet name in them.
         String keySheetName = cellKey.substring(0, cellKey.indexOf("!"));
         if (!keySheetName.equals(sheetName))
         {
            // No exact match.  Check the corresponding template sheet name, if
            // it exists.
            int index = newSheetNames.indexOf(sheetName);
            if (index != -1 && keySheetName.equals(templateSheetNames.get(index)))
            {
               // Template sheet name match.
               // Update keySheetName (the template sheet name) to the new sheet name.
               keySheetName = sheetName;
            }
            else
            {
               continue;
            }
         }

         List<CellRef> cellRefs = cellRefMap.get(cellKey);
         List<CellRef> delete = new ArrayList<CellRef>();
         List<CellRef> insert = new ArrayList<CellRef>();
         for (CellRef cellRef : cellRefs)
         {
            String cellRefSheetName = cellRef.getSheetName();
            int row = cellRef.getRow();
            int col = cellRef.getCol();
            if ((cellRefSheetName == null || keySheetName.equals(cellRefSheetName)) &&
                    (row >= top && row <= bottom && col >= left && col <= right))
            {
               if (remove)
               {
                  if (DEBUG)
                  {
                     System.err.println("      Deleting cell reference: " + cellRef.formatAsString() +
                             " for cell key " + cellKey);
                  }
                  delete.add(cellRef);
               }
               if (add)
               {
                  CellRef adjCellRef = new CellRef(cellRefSheetName, row + numRows, col + numCols,
                          cellRef.isRowAbsolute(), cellRef.isColAbsolute());
                  if (DEBUG)
                  {
                     System.err.println("      Adding cell reference: " + adjCellRef.formatAsString() +
                             " for cell key " + cellKey);
                  }
                  insert.add(adjCellRef);
               }
            }
         }
         if (remove)
            cellRefs.removeAll(delete);
         if (add)
            cellRefs.addAll(insert);
      }
   }

   /**
    * Copies cell references that are on the same <code>Sheet</code> in the
    * given cell reference map by the given number of rows and/or columns
    * (usually one of those two will be zero).  Modifies the <code>Lists</code>
    * that are the values of <code>cellRefMap</code>.
    * @param sheetName The name of the <code>Sheet</code> on which to copy
    *    references.
    * @param context The <code>WorkbookContext</code> which holds the cell ref
    *    map, template sheet names, and new sheet names.
    * @param left The 0-based index of the column on which to start shifting
    *    cell references.
    * @param right The 0-based index of the column on which to end shifting
    *    cell references.
    * @param top The 0-based index of the row on which to start shifting
    *    cell references.
    * @param bottom The 0-based index of the row on which to end shifting
    *    cell references.
    * @param numCols The number of columns to shift the cell reference (can be
    *    negative).
    * @param numRows The number of rows to shift the cell reference (can be
    *    negative).
    * @param currSuffix The current "[loop,iter]*" suffix we're already in.
    * @param newSuffix The new "[loop,iter]" suffix to add for new entries.
    */
   public static void copyCellReferencesInRange(String sheetName, WorkbookContext context,
      int left, int right, int top, int bottom, int numCols, int numRows, String currSuffix, String newSuffix)
   {
      if (DEBUG)
      {
         System.err.println("    FU.cCRIR: left " + left + ", right " + right +
            ", top " + top + ", bottom " + bottom + ", numCols " + numCols +
            ", numRows " + numRows + ", currSuffix: \"" + currSuffix + "\", newSuffix: \"" + newSuffix + "\"");
      }
      Map<String, List<CellRef>> cellRefMap = context.getCellRefMap();
      Map<String, List<CellRef>> newCellRefEntries = new HashMap<String, List<CellRef>>();
      List<String> templateSheetNames = context.getTemplateSheetNames();
      List<String> newSheetNames = context.getSheetNames();
      for (String cellKey : cellRefMap.keySet())
      {
         // All cell keys have the sheet name in them.
         String keySheetName = cellKey.substring(0, cellKey.indexOf("!"));
         if (!keySheetName.equals(sheetName))
         {
            // No exact match.  Check the corresponding template sheet name, if
            // it exists.
            int index = newSheetNames.indexOf(sheetName);
            if (index != -1 && keySheetName.equals(templateSheetNames.get(index)))
            {
               // Template sheet name match.
               // Update keySheetName (the template sheet name) to the new sheet name.
               keySheetName = sheetName;
            }
            else
            {
               continue;
            }
         }

         // A cell key may have a suffix, e.g. [0,1].
         String keySuffix = "";
         int idx = cellKey.indexOf("[");
         if (idx > -1)
            keySuffix = cellKey.substring(idx);
         if (currSuffix.startsWith(keySuffix)) // Suffix matches
         {
            List<CellRef> cellRefs = cellRefMap.get(cellKey);
            List<CellRef> insert = new ArrayList<CellRef>();
            for (CellRef cellRef : cellRefs)
            {
               String cellRefSheetName = cellRef.getSheetName();
               int row = cellRef.getRow();
               int col = cellRef.getCol();
               if ((cellRefSheetName == null || keySheetName.equals(cellRefSheetName)) &&    // Sheet matches
                   (row >= top && row <= bottom && col >= left && col <= right))             // In cell range
               {
                  CellRef adjCellRef = new CellRef(cellRefSheetName, row + numRows, col + numCols,
                     cellRef.isRowAbsolute(), cellRef.isColAbsolute());
                  // Only add the reference if being translated!
                  if (numRows != 0 || numCols != 0)
                  {
                     if (DEBUG)
                        System.err.println("      Adding cell reference: " + adjCellRef.formatAsString() +
                           " for cell key " + cellKey);
                     insert.add(adjCellRef);
                  }
                  // Introduce new mappings with the new suffix, e.g. [2,0], appended to
                  // the current suffix, e.g. [0,1][2,0].
                  // Look for formulas in the range.
                  // Only do this once (pick out those without suffixes to accomplish this).
                  if (idx == -1)
                  {
                     String newCellKey = cellKey + currSuffix + newSuffix;
                     List<CellRef> newCellRefs = new ArrayList<CellRef>();
                     newCellRefs.add(adjCellRef);
                     if (DEBUG)
                     {
                        System.err.println("      Adding new entry: " + newCellKey + " => [" +
                           adjCellRef.formatAsString() + "]");
                     }
                     newCellRefEntries.put(newCellKey, newCellRefs);
                  }
               }
            }
            cellRefs.addAll(insert);
         }
      }
      cellRefMap.putAll(newCellRefEntries);
   }
}

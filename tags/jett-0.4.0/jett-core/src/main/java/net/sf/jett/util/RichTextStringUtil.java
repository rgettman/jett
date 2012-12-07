package net.sf.jett.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

/**
 * The <code>RichTextStringUtil</code> utility class provides methods for
 * RichTextString manipulation.
 *
 * @author Randy Gettman
 */
public class RichTextStringUtil
{
   private static final boolean DEBUG = false;

   /**
    * Replaces all occurrences of the given target string with the replacement
    * string.
    * Preserves rich text formatting as much as possible.
    * @param richTextString The <code>RichTextString</code> to manipulate.
    * @param helper A <code>CreationHelper</code> that can create the proper
    *    <code>RichTextString</code>.
    * @param target The string to replace.
    * @param replacement The replacement string.
    * @return A new <code>RichTextString</code> with replaced values, or the
    *    same <code>RichTextString</code> if <code>replace</code> is
    *    <code>null</code> or empty.
    */
   public static RichTextString replaceAll(RichTextString richTextString,
      CreationHelper helper, String target, String replacement)
   {
      return replaceAll(richTextString, helper, target, replacement, false);
   }

   /**
    * Replaces all occurrences of the given target string with the replacement
    * string.
    * Preserves rich text formatting as much as possible.
    * @param richTextString The <code>RichTextString</code> to manipulate.
    * @param helper A <code>CreationHelper</code> that can create the proper
    *    <code>RichTextString</code>.
    * @param target The string to replace.
    * @param replacement The replacement string.
    * @param firstOnly Whether to stop after replacing the first found instance
    *    of <code>target</code>.
    * @return A new <code>RichTextString</code> with replaced values, or the
    *    same <code>RichTextString</code> if <code>replace</code> is
    *    <code>null</code> or empty.
    */
   public static RichTextString replaceAll(RichTextString richTextString,
      CreationHelper helper, String target, String replacement, boolean firstOnly)
   {
      return replaceAll(richTextString, helper, target, replacement, firstOnly, 0);
   }

   /**
    * Replaces all occurrences of the given target string with the replacement
    * string.
    * Preserves rich text formatting as much as possible.
    * @param richTextString The <code>RichTextString</code> to manipulate.
    * @param helper A <code>CreationHelper</code> that can create the proper
    *    <code>RichTextString</code>.
    * @param target The string to replace.
    * @param replacement The replacement string.
    * @param firstOnly Whether to stop after replacing the first found instance
    *    of <code>target</code>.
    * @param startIdx Start replacing after this 0-based index into the
    *    <code>richTextString</code>.
    * @return A new <code>RichTextString</code> with replaced values, or the
    *    same <code>RichTextString</code> if <code>replace</code> is
    *    <code>null</code> or empty.
    */
   public static RichTextString replaceAll(RichTextString richTextString,
      CreationHelper helper, String target, String replacement, boolean firstOnly, int startIdx)
   {
      if (target == null || target.length() == 0)
         return richTextString;

      int numFormattingRuns = richTextString.numFormattingRuns();
      String value = richTextString.getString();
      if (DEBUG)
         System.err.println("RTSU.replaceAll: \"" + value + "\" (" + value.length() +
            "): numFormattingRuns=" + numFormattingRuns + ", replacing \"" + target +
            "\" with \"" + replacement + "\".");
      ArrayList<Integer> beginList = new ArrayList<Integer>();
      ArrayList<Integer> lengthList = new ArrayList<Integer>();
      ArrayList<Object> fontList = new ArrayList<Object>();

      determineFormattingRunStats(richTextString, beginList, lengthList, fontList);
      ArrayList<Integer> newLengthList = new ArrayList<Integer>(lengthList);

      // Replace target(s) with the replacement.
      if (DEBUG)
         System.err.println("  Replacing \"" + target + "\" with \"" + replacement + "\".");
      int change = replacement.length() - target.length();
      int beginIdx = value.indexOf(target, startIdx);

      while (beginIdx != -1)
      {
         if (DEBUG)
            System.err.println("    beginIdx=" + beginIdx);
         // Take care to skip any already processed part of the value string.
         value = value.substring(0, beginIdx) + replacement + value.substring(beginIdx + target.length());

         // Find the formatting run that applies at beginIdx.
         int fmtIndex = -1;
         for (int j = 0; j < numFormattingRuns; j++)
         {
            int currBeginIdx = beginList.get(j);
            int currLength = newLengthList.get(j);
            if (DEBUG)
               System.err.println("    j=" + j + ", currBeginIdx=" + currBeginIdx +
                  ", currLength=" + currLength + ", beginIdx=" + beginIdx);
            // Don't pick a zero-length run.
            if (beginIdx >= currBeginIdx && beginIdx < currBeginIdx + currLength)
            {
               fmtIndex = j;
               break;
            }
         }
         // Found a run to apply.  The length has changed.
         if (fmtIndex != -1)
         {
            // Change the length of the formatting run.
            newLengthList.set(fmtIndex, newLengthList.get(fmtIndex) + change);

            // This affects the beginning positions of all subsequent runs!
            for (int j = fmtIndex + 1; j < numFormattingRuns; j++)
            {
               if (DEBUG)
                  System.err.println("    RTSU.replaceAll: Changing beginning of formatting run " +
                     j + " from " + beginList.get(j) + " to " + (beginList.get(j) + change));
               beginList.set(j, beginList.get(j) + change);
            }

            if (DEBUG)
               System.err.println("  RTSU.replaceAll: Formatting run length changed (" +
                  fmtIndex + "): oldLength=" + lengthList.get(fmtIndex) +
                  ", newLength=" + newLengthList.get(fmtIndex));
         }
         else
         {
            // It's possible for there to be no formatting run if it's an
            // HSSFRichTextString and the value to replace is before any
            // formatting.  The first formatting "run" is considered to be the
            // format of the Cell and is not present in the formatting runs.

            // This affects the beginning positions of all runs!
            for (int j = 0; j < numFormattingRuns; j++)
            {
               if (DEBUG)
                  System.err.println("    RTSU.replaceAll: changing beginning of formatting run " +
                     j + " from " + beginList.get(j) + " to " + (beginList.get(j) + change));
               beginList.set(j, beginList.get(j) + change);
            }
         }

         if (firstOnly)
            break;
         // Setup for next loop.
         // Look for the next occurrence of the target, taking care to skip the
         // replacement string.  That avoids an infinite loop if the target
         // string can be found inside the replacement string.
         beginIdx = value.indexOf(target, beginIdx + replacement.length());
      }

      return createFormattedString(numFormattingRuns, helper, value, beginList, newLengthList, fontList);
   }

   /**
    * Replaces all strings in the given <code>List</code> of strings to replace
    * with the corresponding replacement string in the given <code>List</code>.
    * Preserves rich text formatting as much as possible.
    * @param richTextString The <code>RichTextString</code> to manipulate.
    * @param helper A <code>CreationHelper</code> that can create the proper
    *    <code>RichTextString</code>.
    * @param targets The <code>List</code> of strings to replace.
    * @param replacements The corresponding <code>List</code> of replacement
    *    strings.
    * @return A new <code>RichTextString</code> with replaced values, or the
    *    same <code>RichTextString</code> if <code>replace</code> is
    *    <code>null</code> or empty.
    */
   public static RichTextString replaceValues(RichTextString richTextString,
      CreationHelper helper, List<String> targets, List<String> replacements)
   {
       if (targets == null || targets.size() == 0)
         return richTextString;

      int numFormattingRuns = richTextString.numFormattingRuns();
      String value = richTextString.getString();
      if (DEBUG)
         System.err.println("RTSU.replaceValues: \"" + value + "\" (" + value.length() +
            "): numFormattingRuns=" + numFormattingRuns + ", replacements: " + targets.size());
      ArrayList<Integer> beginList = new ArrayList<Integer>();
      ArrayList<Integer> lengthList = new ArrayList<Integer>();
      ArrayList<Object> fontList = new ArrayList<Object>();

      determineFormattingRunStats(richTextString, beginList, lengthList, fontList);
      ArrayList<Integer> newLengthList = new ArrayList<Integer>(lengthList);

      // Replace targets with replacements.
      for (int i = 0; i < targets.size(); i++)
      {
         int beginIdx = value.indexOf(targets.get(i));
         if (beginIdx != -1)
         {
            String replaceMe = targets.get(i);
            String replaceWith = replacements.get(i);
            if (DEBUG)
               System.err.println("  Replacing \"" + replaceMe + "\" with \"" + replaceWith + "\".");
            value = value.replace(replaceMe, replaceWith);

            // Find the formatting run that applies at beginIdx.
            int fmtIndex = -1;
            for (int j = 0; j < numFormattingRuns; j++)
            {
               int currBeginIdx = beginList.get(j);
               int currLength = newLengthList.get(j);
               if (DEBUG)
                  System.err.println("    j=" + j + ", currBeginIdx=" + currBeginIdx +
                     ", currLength=" + currLength + ", beginIdx=" + beginIdx);
               // Don't pick a zero-length run.
               if (beginIdx >= currBeginIdx && beginIdx < currBeginIdx + currLength)
               {
                  fmtIndex = j;
                  break;
               }
            }
            // Found a run to apply.  The length has changed.
            if (fmtIndex != -1)
            {
               // Change the length of the formatting run.
               int change = replaceWith.length() - replaceMe.length();
               newLengthList.set(fmtIndex, newLengthList.get(fmtIndex) + change);

               // This affects the beginning positions of all subsequent runs!
               for (int j = fmtIndex + 1; j < numFormattingRuns; j++)
               {
                  if (DEBUG)
                     System.err.println("    RTSU.replaceValues: Changing beginning of formatting run " +
                        j + " from " + beginList.get(j) + " to " + (beginList.get(j) + change));
                  beginList.set(j, beginList.get(j) + change);
               }

               if (DEBUG)
                  System.err.println("  RTSU.replaceValues: Formatting run length changed (" +
                     fmtIndex + "): oldLength=" + lengthList.get(fmtIndex) +
                     ", newLength=" + newLengthList.get(fmtIndex));
            }
         }
         else
            break;
      }

      return createFormattedString(numFormattingRuns, helper, value, beginList, newLengthList, fontList);
   }

   /**
    * Extracts a substring of a <code>RichTextString</code> as another
    * <code>RichTextString</code>.  Preserves the formatting that is in place
    * from the given string.
    * @param richTextString The <code>RichTextString</code> of which to take a
    *    substring.
    * @param helper A <code>CreationHelper</code> that can create the proper
    *    <code>RichTextString</code>.
    * @param beginIndex The beginning index, inclusive.
    * @param endIndex The ending index, exclusive.
    * @return The specified substring as a <code>RichTextString</code>, with 
    *    the original formatting from the original string intact.
    * @since 0.2.0
    */
   public static RichTextString substring(RichTextString richTextString,
      CreationHelper helper, int beginIndex, int endIndex)
   {
      int numFormattingRuns = richTextString.numFormattingRuns();
      String value = richTextString.getString();
      if (DEBUG)
         System.err.println("RTSU.substring: \"" + value + "\" (" + value.length() +
            "): numFormattingRuns=" + numFormattingRuns + ", beginIndex: " + beginIndex +
            ", endIndex: " + endIndex);
      ArrayList<Integer> beginList = new ArrayList<Integer>();
      ArrayList<Integer> lengthList = new ArrayList<Integer>();
      ArrayList<Object> fontList = new ArrayList<Object>();

      determineFormattingRunStats(richTextString, beginList, lengthList, fontList);

      // Determine which runs apply in the new substring's range.
      ArrayList<Integer> substrBeginList = new ArrayList<Integer>();
      ArrayList<Integer> substrLengthList = new ArrayList<Integer>();
      ArrayList<Object> substrFontList = new ArrayList<Object>();
      int begin, end;
      for (int i = 0; i < numFormattingRuns; i++)
      {
         begin = beginList.get(i);
         end = begin + lengthList.get(i);
         if ((begin < beginIndex && end < beginIndex) ||
             (begin >= endIndex && end >= endIndex))
         {
            // Not copied to the new substring.
            continue;
         }
         if (begin < beginIndex && end >= beginIndex)
         {
            // Partial cover at beginning.
            begin = beginIndex;
         }
         if (begin < endIndex && end >= endIndex)
         {
            // Partial cover at end.
            end = endIndex;
         }
         substrBeginList.add(begin - beginIndex);
         substrLengthList.add(end - begin);
         substrFontList.add(fontList.get(i));
      }
      return createFormattedString(substrBeginList.size(), helper, value.substring(beginIndex, endIndex),
         substrBeginList, substrLengthList, substrFontList);
   }

   /**
    * Determine formatting run statistics for the given
    * <code>RichTextString</code>.  Adds elements to the arrays.
    * @param richTextString The <code>RichTextString</code>.
    * @param beginList Adds to this <code>List</code> the beginning indexes of
    *    all formatting runs found.
    * @param lengthList Adds to this <code>List</code> the length of all
    *    formatting runs found.
    * @param fontList Adds to this <code>List</code> the fonts of all
    *    formatting runs found.  If HSSF, stores <code>short</code> font
    *    indexes.  If XSSF, stores <code>XSSFFont</code> objects.
    */
   private static void determineFormattingRunStats(RichTextString richTextString,
      ArrayList<Integer> beginList, ArrayList<Integer> lengthList, ArrayList<Object> fontList)
   {
      int numFormattingRuns = richTextString.numFormattingRuns();
      if (richTextString instanceof HSSFRichTextString)
      {
         HSSFRichTextString hssfRichTextString = (HSSFRichTextString) richTextString;
         // Determine formatting run statistics.
         for (int fmtIdx = 0; fmtIdx < numFormattingRuns; fmtIdx++)
         {
            int begin = richTextString.getIndexOfFormattingRun(fmtIdx);
            short fontIndex = (Short) getFontOfFormattingRun(hssfRichTextString, fmtIdx);

            // Determine font formatting run length.
            int length = 0;
            for (int j = begin; j < richTextString.length(); j++)
            {
               short currFontIndex = (Short) getFontAtIndex(hssfRichTextString, j);
               if (DEBUG)
                  System.err.println("    Comparing j=" + j + ", currFont=" + currFontIndex +
                     ", font=" + fontIndex);
               if (currFontIndex == fontIndex)
                  length++;
               else
                  break;
            }

            if (DEBUG)
            {
               System.err.println("  RTSU.dFRS: HSSF Formatting run found: (" + fmtIdx +
                  ") begin=" + begin + ", length=" + length + ", font=" + fontIndex);
            }
            beginList.add(begin);
            lengthList.add(length);
            fontList.add(fontIndex);
         }
      }
      else if (richTextString instanceof XSSFRichTextString)
      {
         XSSFRichTextString xssfRichTextString = (XSSFRichTextString) richTextString;
         // Determine formatting run statistics.
         for (int fmtIdx = 0; fmtIdx < numFormattingRuns; fmtIdx++)
         {
            int begin = richTextString.getIndexOfFormattingRun(fmtIdx);
            if (DEBUG)
            {
               System.err.println("  fmtIdx: " + fmtIdx);
               System.err.println("    begin: " + begin);
            }
            XSSFFont fontIndex = (XSSFFont) getFontOfFormattingRun(xssfRichTextString, fmtIdx);

            // Determine font formatting run length.
            int length = 0;
            for (int j = begin; j < richTextString.length(); j++)
            {
               XSSFFont currFontIndex = (XSSFFont) getFontAtIndex(xssfRichTextString, j);
               if (DEBUG)
                  System.err.println("    Comparing j=" + j + ", currFont=" + currFontIndex +
                     ", font=" + fontIndex);
               if ((currFontIndex == null && fontIndex == null) ||
                   (currFontIndex != null && currFontIndex.equals(fontIndex)))
                  length++;
               else
                  break;
            }

            if (DEBUG)
            {
               System.err.println("  RTSU.dFRS: XSSF Formatting run found: (" + fmtIdx +
                  ") begin=" + begin + ", length=" + length + ", font=" + fontIndex);
            }
            beginList.add(begin);
            lengthList.add(length);
            fontList.add(fontIndex);
         }
      }
   }

   /**
    * Construct a <code>RichTextString</code> of the same type as
    * <code>richTextString</code>, format it, and return it.
    * @param numFormattingRuns The number of formatting runs.
    * @param value The new string value of the new <code>RichTextString</code>
    *    to construct.
    * @param helper A <code>CreationHelper</code> that can create the proper
    *    <code>RichTextString</code>.
    * @param beginList A <code>List</code> of beginning indexes of formatting
    *    runs.
    * @param newLengthList A <code>List</code> of run lengths of formatting
    *    runs.
    * @param fontList A <code>List</code> of fonts of formatting runs.  If
    *    HSSF, the items are <code>shorts</code>.  If XSSF, the items are
    *    <code>XSSFFonts</code>.
    * @return A new <code>RichTextString</code>, the same type as
    *    <code>richTextString</code>, with <code>value</code> as it contents,
    *    formatted as specified.
    */
   private static RichTextString createFormattedString(int numFormattingRuns,
      CreationHelper helper, String value, ArrayList<Integer> beginList,
      ArrayList<Integer> newLengthList, ArrayList<Object> fontList)
   {
      // Construct the proper RichTextString.
      RichTextString newString = helper.createRichTextString(value);

      // Apply the formatting runs.
      for (int i = 0; i < numFormattingRuns; i++)
      {
         int begin = beginList.get(i);
         int end = begin + newLengthList.get(i);
         Object font = fontList.get(i);
         if (DEBUG)
         {
            System.err.println("  RTSU.cFS: Applying format (" + i + "): begin=" +
               begin + ", length=" + newLengthList.get(i) + ", font=" + font +
               " to string \"" + value + "\".");
         }
         if (newString instanceof HSSFRichTextString)
            newString.applyFont(begin, end, (Short) font);
         else if (newString instanceof XSSFRichTextString)
         {
            if (font != null)
               newString.applyFont(begin, end, (XSSFFont) font);
         }
         else throw new IllegalArgumentException("Unexpected RichTextString type: " +
            newString.getClass().getName() + ": " + newString.getString());
      }
      return newString;
   }

   /**
    * Gets the font index of the specified formatting run in the given
    * <code>RichTextString</code>.
    * @param richTextString The <code>RichTextString</code>.
    * @param fmtIndex The 0-based index of the formatting run.
    * @return The font index.  If HSSF, a <code>short</code>.  If XSSF, an
    *    <code>XSSFFont</code>.
    */
   private static Object getFontOfFormattingRun(RichTextString richTextString, int fmtIndex)
   {
      if (richTextString instanceof HSSFRichTextString)
      {
         return ((HSSFRichTextString) richTextString).getFontOfFormattingRun(fmtIndex);
      }
      else if (richTextString instanceof XSSFRichTextString)
      {
         try
         {
            // Instead of returning null, getFontOfFormattingRun (eventually)
            // throws a NullPointerException.  It extracts a "CTRElt" from an
            // array, and it extracts a "CTRPrElt" from the "CTRElt".  The
            // "CTRprElt" can be null if there is no font at the formatting
            // run.  Then, when creating a "CTFont", it calls a method on the
            // null "CTRPrElt".
            // Return the XSSFFont.
            return ((XSSFRichTextString) richTextString).getFontOfFormattingRun(fmtIndex);
         }
         catch (NullPointerException e)
         {
            // Detect this case and return null.
            if (DEBUG)
               System.err.println("    NullPointerException caught!");
            return null;
         }
      }
      else
         throw new IllegalArgumentException("Unexpected RichTextString type: " +
            richTextString.getClass().getName() + ": " + richTextString.getString());
   }

   /**
    * Gets the font index of the <code>Font</code> in use at the specified
    * position in the given <code>RichTextString</code>.
    * @param richTextString The <code>RichTextString</code>.
    * @param fmtIndex The 0-based index of the formatting run.
    * @return The font index: If HSSF, a <code>short</code>.  If XSSF, an
    *    <code>XSSFFont</code>.
    */
   public static Object getFontAtIndex(RichTextString richTextString, int fmtIndex)
   {
      if (richTextString instanceof HSSFRichTextString)
      {
         // Returns a short.
         return ((HSSFRichTextString) richTextString).getFontAtIndex(fmtIndex);
      }
      else if (richTextString instanceof XSSFRichTextString)
      {
         try
         {
            // Instead of returning null, getFontAtIndex (eventually) throws a
            // NullPointerException.  It extracts a "CTRElt" from an array, and
            // it extracts a "CTRPrElt" from the "CTRElt".  The "CTRprElt" can
            // be null if there is no font at the formatting run.  Then, when
            // creating a "CTFont", it calls a method on the null "CTRPrElt".
            // Return an XSSFFont.
            return ((XSSFRichTextString) richTextString).getFontAtIndex(fmtIndex);
         }
         catch (NullPointerException e)
         {
            // Detect this case and return null.
            if (DEBUG)
               System.err.println("    NullPointerException caught!");
            return null;
         }
      }
      else
         throw new IllegalArgumentException("Unexpected RichTextString type: " +
            richTextString.getClass().getName() + ": " + richTextString.getString());
   }

   /**
    * Take the first <code>Font</code> from the given
    * <code>RichTextString</code> and apply it to the given <code>Cell's</code>
    * <code>CellStyle</code>.
    * @param cell The <code>Cell</code>.
    * @param richTextString The <code>RichTextString</code> that contains the
    *    desired <code>Font</code>.
    * @since 0.2.0
    */
   public static void applyFont(RichTextString richTextString, Cell cell)
   {
      if (DEBUG)
         System.err.println("RTSU.aF: richTextString = " + richTextString +
            ", sheet " + cell.getSheet().getSheetName() + ", cell at row " +
            cell.getRowIndex() + ", col " + cell.getColumnIndex());
      Font font;
      short fontIdx;
      if (richTextString == null)
         return;
      if (richTextString instanceof HSSFRichTextString)
      {
         fontIdx = ((HSSFRichTextString) richTextString).getFontAtIndex(0);
         font = cell.getSheet().getWorkbook().getFontAt(fontIdx);
      }
      else if (richTextString instanceof XSSFRichTextString)
      {
         try
         {
            // Instead of returning null, getFontAtIndex (eventually) throws a
            // NullPointerException.  It extracts a "CTRElt" from an array, and
            // it extracts a "CTRPrElt" from the "CTRElt".  The "CTRprElt" can
            // be null if there is no font at the formatting run.  Then, when
            // creating a "CTFont", it calls a method on the null "CTRPrElt".
            font = ((XSSFRichTextString) richTextString).getFontAtIndex(0);
         }
         catch (NullPointerException e)
         {
            if (DEBUG)
               System.err.println("    NullPointerException caught!");
            font = null;
         }
      }
      else
      {
         throw new IllegalArgumentException("Unexpected RichTextString type: " +
            richTextString.getClass().getName() + ": " + richTextString.getString());
      }
      if (font != null)
      {
         if (DEBUG)
         {
            fontIdx = font.getIndex();
            System.err.println("  Font is " + font.toString() + ", index " + fontIdx);
         }
         CellStyle cellStyle = cell.getCellStyle();
         Workbook workbook = cell.getSheet().getWorkbook();
         CellStyle newCellStyle = findCellStyle(workbook, cellStyle, font);
         if (newCellStyle == null)
         {
            newCellStyle = workbook.createCellStyle();
            newCellStyle.cloneStyleFrom(cellStyle);
            // For some reason, just setting the Font directly doesn't work.
            //newCellStyle.setFont(font);
            Font foundFont = findFont(workbook, font);
            newCellStyle.setFont(foundFont);
         }
         cell.setCellStyle(newCellStyle);
      }
   }

   /**
    * Find a <code>CellStyle</code> with all the same attributes as the given
    * <code>CellStyle</code> but with the given font index.
    * @param workbook The <code>Workbook</code>.
    * @param cellStyle The <code>CellStyle</code> to find.
    * @param font The <code>Font</code> to find.
    * @return The <code>CellStyle</code> from the <code>Workbook</code> if
    *    found, or <code>null</code> if not found.
    */
   private static CellStyle findCellStyle(Workbook workbook, CellStyle cellStyle, Font font)
   {
      int numCellStyles = workbook.getNumCellStyles();
      for (short i = 0; i < numCellStyles; i++)
      {
         CellStyle cs = workbook.getCellStyleAt(i);
         Font f = workbook.getFontAt(cs.getFontIndex());
         if (cs.getFillForegroundColor() == cellStyle.getFillForegroundColor() &&
             cs.getFillBackgroundColor() == cellStyle.getFillBackgroundColor() &&
             cs.getDataFormat() == cellStyle.getDataFormat() &&
             cs.getAlignment() == cellStyle.getAlignment() &&
             cs.getBorderBottom() == cellStyle.getBorderBottom() &&
             cs.getBorderLeft() == cellStyle.getBorderLeft() &&
             cs.getBorderRight() == cellStyle.getBorderRight() &&
             cs.getBorderTop() == cellStyle.getBorderTop() &&
             cs.getFillPattern() == cellStyle.getFillPattern() &&
             cs.getWrapText() == cellStyle.getWrapText() &&
             cs.getRotation() == cellStyle.getRotation() &&
             cs.getBottomBorderColor() == cellStyle.getBottomBorderColor() &&
             cs.getTopBorderColor() == cellStyle.getTopBorderColor() &&
             cs.getLeftBorderColor() == cellStyle.getLeftBorderColor() &&
             cs.getRightBorderColor() == cellStyle.getRightBorderColor() &&
             cs.getVerticalAlignment() == cellStyle.getVerticalAlignment() &&
             cs.getIndention() == cellStyle.getIndention() &&
             cs.getLocked() == cellStyle.getLocked() &&
             cs.getHidden() == cellStyle.getHidden() &&
             f.getBoldweight() == font.getBoldweight() &&
             f.getItalic() == font.getItalic() &&
             f.getColor() == font.getColor() &&
             f.getFontHeight() == font.getFontHeight() &&
             f.getUnderline() == font.getUnderline() &&
             f.getFontName().equals(font.getFontName()) &&
             f.getTypeOffset() == font.getTypeOffset()
            )
         {
            if (!(font instanceof XSSFFont && f instanceof XSSFFont) ||
                ((XSSFFont) font).getXSSFColor().getARGBHex().equals(((XSSFFont) f).getXSSFColor().getARGBHex()))
            {
               if (DEBUG)
                  System.err.println("    Found existing, matching CellStyle with the Font!");
               return cs;
            }
         }
      }
      if (DEBUG)
         System.err.println("    Did NOT find existing, matching CellStyle with the Font!");
      return null;
   }

   private static Font findFont(Workbook workbook, Font font)
   {
      int numFonts = workbook.getNumberOfFonts();
      for (short i = 0; i < numFonts; i++)
      {
         Font f = workbook.getFontAt(i);
         if (f.getBoldweight() == font.getBoldweight() &&
             f.getItalic() == font.getItalic() &&
             f.getColor() == font.getColor() &&
             f.getFontHeight() == font.getFontHeight() &&
             f.getUnderline() == font.getUnderline() &&
             f.getFontName().equals(font.getFontName()) &&
             f.getTypeOffset() == font.getTypeOffset()
            )
         {
            if (!(font instanceof XSSFFont && f instanceof XSSFFont) ||
                ((XSSFFont) font).getXSSFColor().getARGBHex().equals(((XSSFFont) f).getXSSFColor().getARGBHex()))
            {
               if (DEBUG)
                  System.err.println("    Found existing, matching Font!");
               return f;
            }
         }
      }
      if (DEBUG)
         System.err.println("    Did NOT find existing, matching Font!");
      return null;
   }
}

package net.sf.jett.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

/**
 * The <code>RichTextStringUtil</code> utility class provides methods for
 * RichTextString manipulation.
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

         if (firstOnly)
            break;
         // Setup for next loop.
         // Look for the next occurrence of the target, taking care to skip the
         // replacement string.  That avoids an infinite loop if the target
         // string can be found inside the replacement string.
         beginIdx = value.indexOf(target, beginIdx + replacement.length());
      }

      return createFormattedString(richTextString, helper, value, beginList, newLengthList, fontList);
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

      return createFormattedString(richTextString, helper, value, beginList, newLengthList, fontList);
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
    * @param richTextString The original <code>RichTextString</code>.
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
   private static RichTextString createFormattedString(RichTextString richTextString,
      CreationHelper helper, String value, ArrayList<Integer> beginList,
      ArrayList<Integer> newLengthList, ArrayList<Object> fontList)
   {
      int numFormattingRuns = richTextString.numFormattingRuns();
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
               begin + ", length=" + newLengthList.get(i) + ", font=" + font);
         }
         if (newString instanceof HSSFRichTextString)
            newString.applyFont(begin, end, (Short) font);
         else if (newString instanceof XSSFRichTextString)
         {
            if (font != null)
               newString.applyFont(begin, end, (XSSFFont) font);
         }
         else throw new IllegalArgumentException("Unexpected RichTextString type: " +
            richTextString.getClass().getName() + ": " + richTextString.getString());
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
   private static Object getFontAtIndex(RichTextString richTextString, int fmtIndex)
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
}

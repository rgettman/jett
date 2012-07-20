package net.sf.jett.parser;

import java.util.ArrayList;
import java.util.List;

import net.sf.jett.exception.FormulaParseException;
import net.sf.jett.formula.CellRef;

/**
 * A <code>FormulaParser</code> parses formulas in formula text, extracting out
 * all cell references.  A cell reference consists of an optional sheet name
 * (optionally enclosed in single quotes) followed by an exclamation ("!"),
 * followed by a legal cell reference (alpha-number format), optionally
 * followed by a default value clause, which is two pipes followed by the
 * default value: "||value".
 */
public class FormulaParser
{
   private static final boolean DEBUG = false;

   private static final String CELL_REF_PATTERN = "[A-Za-z]+[1-9][0-9]*(\\|\\|.*)?";

   private String myFormulaText;
   private List<CellRef> myCellReferences;
   private String mySheetName;
   private String myCellReference;
   private boolean amIInsideSingleQuotes;

   /**
    * Create a <code>FormulaParser</code>.
    */
   public FormulaParser()
   {
      setFormulaText("");
   }

   /**
    * Create a <code>FormulaParser</code> object that will parse the given
    * formula text.
    * @param formulaText The text of the formula.
    */
   public FormulaParser(String formulaText)
   {
      setFormulaText(formulaText);
   }

   /**
    * Sets the formula text to the given formula text and resets the parser.
    * @param formulaText The new formula text.
    */
   public void setFormulaText(String formulaText)
   {
      myFormulaText = formulaText;
      reset();
   }

   /**
    * Resets this <code>FormulaParser</code>, usually at creation time and
    * when new input arrives.
    */
   private void reset()
   {
      myCellReferences = new ArrayList<CellRef>();
      mySheetName = null;
      myCellReference = null;
      amIInsideSingleQuotes = false;
   }

   /**
    * Parses the formula text.
    */
   public void parse()
   {
      FormulaScanner scanner = new FormulaScanner(myFormulaText);

      FormulaScanner.Token token = scanner.getNextToken();
      if (token == FormulaScanner.Token.TOKEN_WHITESPACE)
         token = scanner.getNextToken();

      // Parse any cell references found.
      while (token.getCode() >= 0 && token != FormulaScanner.Token.TOKEN_EOI)
      {
         switch(token)
         {
         case TOKEN_WHITESPACE:
            // Ignore.
            break;
         case TOKEN_STRING:
            // For now, store it in the cell reference field.  Upon finding an
            // exclamation,  the value will be stored in the sheet name field.
            myCellReference = scanner.getCurrLexeme();
            break;
         case TOKEN_EXCLAMATION:
            // If we had text from before the "!", it's the sheet reference.
            // Move it to the sheet name field.
            mySheetName = myCellReference;
            break;
         case TOKEN_LEFT_PAREN:
            // This can turn a potential cell reference into a function call!
            mySheetName = null;
            break;
         case TOKEN_RIGHT_PAREN:
         case TOKEN_COMMA:
         case TOKEN_DOUBLE_QUOTE:
            // Just delimiters between strings.  Validate the cell reference.
            if (DEBUG)
               System.err.println("  FP: Trying to match \"" + myCellReference + "\".");
            if (myCellReference != null && myCellReference.matches(CELL_REF_PATTERN))
            {
               CellRef ref;
               String cellReference;
               int pipesIdx = myCellReference.indexOf(CellRef.DEFAULT_VALUE_IND);

               if (pipesIdx != -1)
                  cellReference = myCellReference.substring(0, pipesIdx);
               else
                  cellReference = myCellReference;
               if (DEBUG)
                  System.err.println("    FP: Cell Reference is \"" + cellReference + "\".");
               if (mySheetName != null)
                  ref = new CellRef(mySheetName + "!" + cellReference);
               else
                  ref = new CellRef(cellReference);
               if (pipesIdx != -1)
               {
                  String defaultValue = myCellReference.substring(pipesIdx + 2);
                  if (DEBUG)
                     System.err.println("    FP: Default value found is \"" + defaultValue + "\".");
                  ref.setDefaultValue(defaultValue);
               }

               if (DEBUG)
                  System.err.println("    FP: Cell Reference detected: " + ref.formatAsString());
               // Don't add duplicates.
               if (!myCellReferences.contains(ref))
               {
                  if (DEBUG)
                     System.err.println("      FP: Not in list, adding ref: row=" + ref.getRow() +
                        ", col=" + ref.getCol() + ", rowAbs=" + ref.isRowAbsolute() + ", colAbs=" +
                        ref.isColAbsolute() + ".");
                  myCellReferences.add(ref);
               }
            }
            mySheetName = null;
            myCellReference = null;
            break;
         case TOKEN_SINGLE_QUOTE:
            // Must keep track of whether a sheet reference occurs within single quotes.
            amIInsideSingleQuotes = !amIInsideSingleQuotes;
            break;
         default:
            throw new FormulaParseException("Parse error occurred: " + myFormulaText);
         }
         token = scanner.getNextToken();

         if (token == FormulaScanner.Token.TOKEN_EOI)
            break;
      }
      // Found end of input before attribute value found.
      if (token.getCode() < 0)
         throw new FormulaParseException("Found end of input while scanning formula text: " + myFormulaText);
   }

   /**
    * Returns a <code>List</code> of <code>CellRefs</code> that this parser
    * found in the formula text.
    * @return A <code>List</code> of <code>CellRefs</code>, possibly empty.
    */
   public List<CellRef> getCellReferences()
   {
      return myCellReferences;
   }
}
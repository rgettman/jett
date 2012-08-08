package net.sf.jett.parser;

import net.sf.jett.exception.MetadataParseException;
import net.sf.jett.tag.BaseLoopTag;
import net.sf.jett.tag.GroupTag;

/**
 * A <code>MetadataParser</code> parses metadata at the end of cell text.
 */
public class MetadataParser
{
   private static boolean DEBUG = false;

   /**
    * Metadata variable name for extra rows in the Block.
    */
   public static final String VAR_NAME_EXTRA_ROWS = "extraRows";
   /**
    * Metadata variable name for additional columns to the left in the Block.
    */
   public static final String VAR_NAME_LEFT = "left";
   /**
    * Metadata variable name for additional columns to the right in the Block.
    */
   public static final String VAR_NAME_RIGHT = "right";
   /**
    * Metadata variable name for copying right instead of down.  This is
    * ignored if neither "left" nor "right" is specified.
    */
   public static final String VAR_NAME_COPY_RIGHT = "copyRight";
   /**
    * Metadata variable name for not shifting other content out of the way of a
    * looping block.  This turns on the implicit version of the "fixed"
    * attribute of looping tags.
    */
   public static final String VAR_NAME_FIXED = "fixed";
   /**
    * Metadata variable name specifying the "past end action" to take whenever
    * a <code>Collection</code> is exhausted before the end of iteration.
    * @since 0.2.0
    */
   public static final String VAR_NAME_PAST_END_ACTION = "pastEndAction";
   /**
    * Metadata variable name specifying to create an Excel grouping for rows,
    * columns, or no grouping.
    * @since 0.2.0
    */
   public static final String VAR_NAME_GROUP_DIR = "groupDir";
   /**
    * Metadata variable name specifying whether any Excelt grouping created
    * should be collapsed, defaulting to <code>false</code>.
    */
   public static final String VAR_NAME_COLLAPSE = "collapse";
   /**
    * Determines the beginning of metadata text.
    */
   public static final String BEGIN_METADATA = "?@";

   private String myMetadataText;
   private int myExtraRows;
   private int myColsLeft;
   private int myColsRight;
   private boolean amIDefiningCols;
   private boolean amICopyingRight;
   private boolean amIFixed;
   private boolean amIIExpectingAValue;
   private String myPastEndActionValue = BaseLoopTag.PAST_END_ACTION_CLEAR;
   private String myGroupDir;
   private boolean amICollapsingGroup;

   /**
    * Create a <code>MetadataParser</code>.
    */
   public MetadataParser()
   {
      setMetadataText("");
   }

   /**
    * Create a <code>MetadataParser</code> object that will parse the given
    * metadata text.
    * @param metadataText The text of the metadata.
    */
   public MetadataParser(String metadataText)
   {
      setMetadataText(metadataText);
   }

   /**
    * Sets the tag text to the given tag text and resets the parser.
    * @param metadataText The new metadata text.
    */
   public void setMetadataText(String metadataText)
   {
      myMetadataText = metadataText;
      reset();
   }

   /**
    * Resets this <code>MetadataParser</code>, usually at creation time and
    * when new input arrives.
    */
   private void reset()
   {
      myExtraRows = 0;  // Default value for no extra rows.
      myColsLeft = 0;  // Default value for no extra columns left.
      myColsRight = 0;  // Default value for no extra columns right.
      amIDefiningCols = false;
      amICopyingRight = false;
      amIFixed = false;
      amIIExpectingAValue = false;
      myPastEndActionValue = BaseLoopTag.PAST_END_ACTION_CLEAR;
      myGroupDir = GroupTag.GROUP_DIR_NONE;
      amICollapsingGroup = false;
   }

   /**
    * Parses the metadata text.
    */
   public void parse()
   {
      MetadataScanner scanner = new MetadataScanner(myMetadataText);

      MetadataScanner.Token token = scanner.getNextToken();
      if (token == MetadataScanner.Token.TOKEN_WHITESPACE)
         token = scanner.getNextToken();

      // Parse any metadata variable name/value pairs: varName=value.
      String varName = null;
      while (token.getCode() >= 0 && token != MetadataScanner.Token.TOKEN_EOI)
      {
         switch(token)
         {
         case TOKEN_WHITESPACE:
            // Ignore.
            break;
         case TOKEN_STRING:
            if (amIIExpectingAValue)
            {
               String value = scanner.getCurrLexeme();

               // Add newly complete variable name/value pair.
               if (VAR_NAME_EXTRA_ROWS.equals(varName))
               {
                  try
                  {
                     myExtraRows = Integer.parseInt(value);
                     if (DEBUG)
                        System.err.println("MDP: myExtraRows set to " + myExtraRows);
                  }
                  catch (NumberFormatException e)
                  {
                     throw new MetadataParseException("Value for variable \"" +
                        VAR_NAME_EXTRA_ROWS + "\" must be an integer: " + value, e);
                  }
                  if (myExtraRows < 0)
                  {
                     throw new MetadataParseException("Value for variable \"" +
                        VAR_NAME_EXTRA_ROWS + "\" must not be negative: " + value);
                  }
               }
               else if (VAR_NAME_LEFT.equals(varName))
               {
                  try
                  {
                     myColsLeft = Integer.parseInt(value);
                     amIDefiningCols = true;
                     if (DEBUG)
                        System.err.println("MDP: myColsLeft set to " + myColsLeft);
                  }
                  catch (NumberFormatException e)
                  {
                     throw new MetadataParseException("Value for variable \"" +
                        VAR_NAME_LEFT + "\" must be an integer: " + value, e);
                  }
                  if (myColsLeft < 0)
                  {
                     throw new MetadataParseException("Value for variable \"" +
                        VAR_NAME_LEFT + "\" must not be negative: " + value);
                  }
               }
               else if (VAR_NAME_RIGHT.equals(varName))
               {
                  try
                  {
                     myColsRight = Integer.parseInt(value);
                     amIDefiningCols = true;
                     if (DEBUG)
                        System.err.println("MDP: myColsRight set to " + myColsRight);
                  }
                  catch (NumberFormatException e)
                  {
                     throw new MetadataParseException("Value for variable \"" +
                        VAR_NAME_RIGHT + "\" must be an integer: " + value, e);
                  }
                  if (myColsRight < 0)
                  {
                     throw new MetadataParseException("Value for variable \"" +
                        VAR_NAME_RIGHT + "\" must not be negative: " + value);
                  }
               }
               else if (VAR_NAME_COPY_RIGHT.equals(varName))
               {
                  amICopyingRight = Boolean.parseBoolean(value);
                  if (DEBUG)
                     System.err.println("MDP: amICopyingRight set to " + amICopyingRight);
               }
               else if (VAR_NAME_FIXED.equals(varName))
               {
                  amIFixed = Boolean.parseBoolean(value);
                  if (DEBUG)
                     System.err.println("MDP: amIFixed set to " + amIFixed);
               }
               else if (VAR_NAME_PAST_END_ACTION.equals(varName))
               {
                  myPastEndActionValue = value;
                  if (!BaseLoopTag.PAST_END_ACTION_REMOVE.equals(value) &&
                      !BaseLoopTag.PAST_END_ACTION_CLEAR.equals(value))
                  {
                     throw new MetadataParseException("Unrecognized pastEndAction value: " + value);
                  }
               }
               else if (VAR_NAME_GROUP_DIR.equals(varName))
               {
                  myGroupDir = value;
                  if (!GroupTag.GROUP_DIR_ROWS.equals(value) &&
                      !GroupTag.GROUP_DIR_COLS.equals(value) &&
                      !GroupTag.GROUP_DIR_NONE.equals(value))
                  {
                     throw new MetadataParseException("Unrecognized groupDir value: " + value);
                  }
               }
               else if (VAR_NAME_COLLAPSE.equals(varName))
               {
                  amICollapsingGroup = Boolean.parseBoolean(value);
                  if (DEBUG)
                     System.err.println("MDP: amICollapsingGroup set to " + amICollapsingGroup);
               }
               else
               {
                  throw new MetadataParseException("Unrecognized variable name: \"" +
                     varName + "\".");
               }
               varName = null;
               amIIExpectingAValue = false;
            }
            else
               varName = scanner.getCurrLexeme();
            break;
         case TOKEN_EQUALS:
            if (varName == null)
               throw new MetadataParseException("Variable name missing before \"=\": " + myMetadataText);
            amIIExpectingAValue = true;
            break;
         case TOKEN_SEMICOLON:
            // Just a delimiter between var/value pairs.
            break;
         case TOKEN_DOUBLE_QUOTE:
         case TOKEN_SINGLE_QUOTE:
            // These just delimit Strings.
            break;
         default:
            throw new MetadataParseException("Parse error occurred: " + myMetadataText);
         }
         token = scanner.getNextToken();
      }
      // Found end of input before attribute value found.
      if (varName != null)
         throw new MetadataParseException("Found end of metadata before equals sign: " + myMetadataText);
      if (amIIExpectingAValue)
         throw new MetadataParseException("Found end of metadata before variable value: " + myMetadataText);
      if (token.getCode() < 0)
         throw new MetadataParseException("Found end of input while scanning metadata value: " + myMetadataText);
   }

   /**
    * Returns the number of extra rows to add to the <code>Block</code>.
    * @return The number of extra rows to add to the <code>Block</code>.
    */
   public int getExtraRows()
   {
      return myExtraRows;
   }

   /**
    * Returns the number of columns to add to the left of the <code>Block</code>.
    * @return The number of columns to add to the left of the <code>Block</code>.
    */
   public int getColsLeft()
   {
      return myColsLeft;
   }

   /**
    * Returns the number of columns to add to the right of the <code>Block</code>.
    * @return The number of columns to add to the right of the <code>Block</code>.
    */
   public int getColsRight()
   {
      return myColsRight;
   }

   /**
    * Returns whether column definitions are present.
    * @return Whether column definitions are present.
    */
   public boolean isDefiningCols()
   {
      return amIDefiningCols;
   }

   /**
    * Returns whether the implicit block will be copied right instead of down.
    * The variables "copyRight" and either "left" or "right" must be specified
    * for this to return <code>true</code>.
    * @return Whether the implicit block will be copied right instead of down.
    */
   public boolean isCopyingRight()
   {
      return isDefiningCols() && amICopyingRight;
   }

   /**
    * Returns whether the implicit block is "fixed", that is, not shifting
    * other content out of the way, like the "fixed collection name" feature.
    * @return Whether the implicit block is fixed.
    */
   public boolean isFixed()
   {
      return amIFixed;
   }

   /**
    * Returns the "past end action" value, which defaults to
    * <code>BaseLoopTag.PAST_END_ACTION_CLEAR</code>.
    * @return The "past end action" value.
    * @see BaseLoopTag#PAST_END_ACTION_CLEAR
    * @see BaseLoopTag#PAST_END_ACTION_REMOVE
    * @since 0.2.0
    */
   public String getPastEndAction()
   {
      return myPastEndActionValue;
   }

   /**
    * Returns the "group dir" value, which defaults to
    * <code>GroupTag.GROUP_DIR_NONE</code>.
    * @return The "group dir" value.
    * @see GroupTag#GROUP_DIR_NONE
    * @see GroupTag#GROUP_DIR_ROWS
    * @see GroupTag#GROUP_DIR_COLS
    * @since 0.2.0
    */
   public String getGroupDir()
   {
      return myGroupDir;
   }

   /**
    * Returns whether the Excel group to be created will be collapsed.
    * @return Whether the Excel group to be created will be collapsed.  If the
    *    "group direction" is "none", then this will always be
    *    <code>false</code>.
    * @since 0.2.0
    */
   public boolean isCollapsingGroup()
   {
      return !myGroupDir.equals(GroupTag.GROUP_DIR_NONE) && amICollapsingGroup;
   }
}
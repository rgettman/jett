package net.sf.jett.model;

import org.apache.poi.ss.usermodel.Font;

/**
 * <p><code>FontBoldweights</code> represent the built-in boldweight names
 * that correspond with Excel's font boldness scheme.  These are used in
 * conjunction with the type boldweight property in the style tag.  Legal
 * values are the names of the enumeration objects, without underscores, case
 * insensitive, e.g. "bold" == "Bold" == "BOLD".</p>
 *
 * @author Randy Gettman
 * @since 0.4.0
 * @see net.sf.jett.tag.StyleTag
 * @see net.sf.jett.parser.StyleParser#PROPERTY_FONT_BOLDWEIGHT
 */
public enum FontBoldweight
{
   NORMAL(Font.BOLDWEIGHT_NORMAL),
   BOLD  (Font.BOLDWEIGHT_BOLD);

   private short myIndex;

   /**
    * Constructs a <code>FontBoldweight</code>.
    * @param index The index.
    */
   FontBoldweight(short index)
   {
      myIndex = index;
   }

   /**
    * Returns the index.
    * @return The index.
    */
   public short getIndex()
   {
      return myIndex;
   }

   /**
    * Returns the name, in all lowercase, no underscores or spaces.
    * @return The name, in all lowercase, no underscores or spaces.
    */
   public String toString()
   {
      return name().trim().toLowerCase().replace("_", "");
   }
}
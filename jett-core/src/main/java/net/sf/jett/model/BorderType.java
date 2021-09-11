package net.sf.jett.model;

import org.apache.poi.ss.usermodel.BorderStyle;

/**
 * <p><code>BorderTypes</code> represent the built-in border type names that
 * correspond with Excel's border type scheme.  These are used in
 * conjunction with the border-related properties in the style tag.  Legal
 * values are the names of the enumeration objects, without underscores, case
 * insensitive, e.g. "thin" == "Thin" == "THIN".</p>
 *
 * @author Randy Gettman
 * @since 0.4.0
 * @see net.sf.jett.tag.StyleTag
 * @see net.sf.jett.parser.StyleParser#PROPERTY_BORDER
 * @see net.sf.jett.parser.StyleParser#PROPERTY_BORDER_BOTTOM
 * @see net.sf.jett.parser.StyleParser#PROPERTY_BORDER_LEFT
 * @see net.sf.jett.parser.StyleParser#PROPERTY_BORDER_RIGHT
 * @see net.sf.jett.parser.StyleParser#PROPERTY_BORDER_TOP
 */
public enum BorderType
{
    NONE            (BorderStyle.NONE),
    THIN            (BorderStyle.THIN),
    MEDIUM          (BorderStyle.MEDIUM),
    DASHED          (BorderStyle.DASHED),
    HAIR            (BorderStyle.HAIR),
    THICK           (BorderStyle.THICK),
    DOUBLE          (BorderStyle.DOUBLE),
    DOTTED          (BorderStyle.DOTTED),
    MEDIUMDASHED    (BorderStyle.MEDIUM_DASHED),
    DASHDOT         (BorderStyle.DASH_DOT),
    MEDIUMDASHDOT   (BorderStyle.MEDIUM_DASH_DOT),
    DASHDOTDOT      (BorderStyle.DASH_DOT_DOT),
    MEDIUMDASHDOTDOT(BorderStyle.MEDIUM_DASH_DOT_DOT),
    SLANTEDDASHDOT  (BorderStyle.SLANTED_DASH_DOT);

    private BorderStyle borderStyle;

    /**
     * Constructs a <code>BorderType</code>.
     * @param index The index.
     */
    BorderType(BorderStyle borderStyle)
    {
        this.borderStyle = borderStyle;
    }

    /**
     * Returns the index.
     * @return The index.
     */
    public BorderStyle getBorderStyle()
    {
        return borderStyle;
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

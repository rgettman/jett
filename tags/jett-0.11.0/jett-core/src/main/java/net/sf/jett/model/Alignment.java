package net.sf.jett.model;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * <p><code>Alignments</code> represent the built-in horizontal alignment names
 * that correspond with Excel's horizontal alignment scheme.  These are used in
 * conjunction with the alignment property in the style tag.  Legal values are
 * the names of the enumeration objects, without underscores, case insensitive,
 * e.g. "center" == "Center" == "CENTER".</p>
 *
 * @author Randy Gettman
 * @since 0.4.0
 * @see net.sf.jett.tag.StyleTag
 * @see net.sf.jett.parser.StyleParser#PROPERTY_ALIGNMENT
 */
public enum Alignment
{
    CENTER         ((short) HorizontalAlignment.CENTER.ordinal()),
    CENTERSELECTION((short) HorizontalAlignment.CENTER_SELECTION.ordinal()),
    DISTRIBUTED    ((short) HorizontalAlignment.DISTRIBUTED.ordinal()),
    FILL           ((short) HorizontalAlignment.FILL.ordinal()),
    GENERAL        ((short) HorizontalAlignment.GENERAL.ordinal()),
    JUSTIFY        ((short) HorizontalAlignment.JUSTIFY.ordinal()),
    LEFT           ((short) HorizontalAlignment.LEFT.ordinal()),
    RIGHT          ((short) HorizontalAlignment.RIGHT.ordinal());

    private short myIndex;

    /**
     * Constructs an <code>Alignment</code>.
     * @param index The index.
     */
    Alignment(short index)
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
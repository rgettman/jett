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
    CENTER         (HorizontalAlignment.CENTER),
    CENTERSELECTION(HorizontalAlignment.CENTER_SELECTION),
    DISTRIBUTED    (HorizontalAlignment.DISTRIBUTED),
    FILL           (HorizontalAlignment.FILL),
    GENERAL        (HorizontalAlignment.GENERAL),
    JUSTIFY        (HorizontalAlignment.JUSTIFY),
    LEFT           (HorizontalAlignment.LEFT),
    RIGHT          (HorizontalAlignment.RIGHT);

    private final HorizontalAlignment alignment;

    /**
     * Constructs an <code>Alignment</code>.
     * @param alignment The horizontal alignment.
     */
    Alignment(HorizontalAlignment alignment)
    {
        this.alignment = alignment;
    }

    /**
     * Returns the alignment.
     * @return The alignment.
     */
    public HorizontalAlignment getAlignment()
    {
        return alignment;
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
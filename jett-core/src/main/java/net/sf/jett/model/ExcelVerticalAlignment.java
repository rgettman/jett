package net.sf.jett.model;

import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * <p><code>VerticalAlignments</code> represent the built-in vertical alignment
 * names that correspond with Excel's vertical alignment scheme.  These are
 * used in conjunction with the vertical alignment property in the style tag.
 * Legal values are the names of the enumeration objects, without underscores,
 * case insensitive, e.g. "justify" == "Justify" == "JUSTIFY".</p>
 *
 * @author Randy Gettman
 * @since 0.4.0
 * @see net.sf.jett.tag.StyleTag
 * @see net.sf.jett.parser.StyleParser#PROPERTY_VERTICAL_ALIGNMENT
 */
public enum ExcelVerticalAlignment
{
    BOTTOM     (VerticalAlignment.BOTTOM),
    CENTER     (VerticalAlignment.CENTER),
    DISTRIBUTED(VerticalAlignment.DISTRIBUTED),
    JUSTIFY    (VerticalAlignment.JUSTIFY),
    TOP        (VerticalAlignment.TOP);

    private VerticalAlignment verticalAlignment;

    /**
     * Constructs a <code>VerticalAlignment</code>.
     * @param verticalAlignment The index.
     */
    ExcelVerticalAlignment(VerticalAlignment verticalAlignment)
    {
        this.verticalAlignment = verticalAlignment;
    }

    /**
     * Returns the vertical alignment.
     * @return The vertical alignment.
     */
    public VerticalAlignment getAlignment()
    {
        return verticalAlignment;
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

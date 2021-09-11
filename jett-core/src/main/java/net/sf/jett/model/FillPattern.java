package net.sf.jett.model;

import org.apache.poi.ss.usermodel.FillPatternType;

/**
 * <p><code>FillPatterns</code> represent the built-in fill pattern names that
 * correspond with Excel's indexed fill pattern scheme.  These are used in
 * conjunction with the fill pattern property in the style tag.  Legal values
 * are the names of the enumeration objects, without underscores, case
 * insensitive, e.g. "gray50percent" == "Gray50Percent" == "GRAY50PERCENT".</p>
 *
 * @author Randy Gettman
 * @since 0.4.0
 * @see net.sf.jett.tag.StyleTag
 * @see net.sf.jett.parser.StyleParser#PROPERTY_FILL_PATTERN
 */
public enum FillPattern
{
    NOFILL                   (FillPatternType.NO_FILL),
    SOLID                    (FillPatternType.SOLID_FOREGROUND),
    GRAY50PERCENT            (FillPatternType.FINE_DOTS),
    GRAY75PERCENT            (FillPatternType.ALT_BARS),
    GRAY25PERCENT            (FillPatternType.SPARSE_DOTS),
    HORIZONTALSTRIPE         (FillPatternType.THICK_HORZ_BANDS),
    VERTICALSTRIPE           (FillPatternType.THICK_VERT_BANDS),
    REVERSEDIAGONALSTRIPE    (FillPatternType.THICK_BACKWARD_DIAG),
    DIAGONALSTRIPE           (FillPatternType.THICK_FORWARD_DIAG),
    DIAGONALCROSSHATCH       (FillPatternType.BIG_SPOTS),
    THICKDIAGONALCROSSHATCH  (FillPatternType.BRICKS),
    THINHORIZONTALSTRIPE     (FillPatternType.THIN_HORZ_BANDS),
    THINVERTICALSTRIPE       (FillPatternType.THIN_VERT_BANDS),
    THINREVERSEDIAGONALSTRIPE(FillPatternType.THIN_BACKWARD_DIAG),
    THINDIAGONALSTRIPE       (FillPatternType.THIN_FORWARD_DIAG),
    THINHORIZONTALCROSSHATCH (FillPatternType.SQUARES),
    THINDIAGONALCROSSHATCH   (FillPatternType.DIAMONDS),
    GRAY12PERCENT            (FillPatternType.LESS_DOTS),
    GRAY6PERCENT             (FillPatternType.LEAST_DOTS);

    private FillPatternType fillPatternType;

    /**
     * Constructs a <code>FillPattern</code>.
     * @param fillPatternType The fill pattern type.
     */
    FillPattern(FillPatternType fillPatternType)
    {
        this.fillPatternType = fillPatternType;
    }

    /**
     * Returns the fill Pattern type.
     * @return The fill Pattern Type.
     */
    public FillPatternType getFillPatternType()
    {
        return fillPatternType;
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

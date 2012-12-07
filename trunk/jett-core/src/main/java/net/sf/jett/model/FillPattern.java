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
 * @see net.sf.jett.tag.StyleTag#PROPERTY_FILL_PATTERN
 */
public enum FillPattern
{
   NOFILL                   ((short) FillPatternType.NO_FILL.ordinal()),
   SOLID                    ((short) FillPatternType.SOLID_FOREGROUND.ordinal()),
   GRAY50PERCENT            ((short) FillPatternType.FINE_DOTS.ordinal()),
   GRAY75PERCENT            ((short) FillPatternType.ALT_BARS.ordinal()),
   GRAY25PERCENT            ((short) FillPatternType.SPARSE_DOTS.ordinal()),
   HORIZONTALSTRIPE         ((short) FillPatternType.THICK_HORZ_BANDS.ordinal()),
   VERTICALSTRIPE           ((short) FillPatternType.THICK_VERT_BANDS.ordinal()),
   REVERSEDIAGONALSTRIPE    ((short) FillPatternType.THICK_BACKWARD_DIAG.ordinal()),
   DIAGONALSTRIPE           ((short) FillPatternType.THICK_FORWARD_DIAG.ordinal()),
   DIAGONALCROSSHATCH       ((short) FillPatternType.BIG_SPOTS.ordinal()),
   THICKDIAGONALCROSSHATCH  ((short) FillPatternType.BRICKS.ordinal()),
   THINHORIZONTALSTRIPE     ((short) FillPatternType.THIN_HORZ_BANDS.ordinal()),
   THINVERTICALSTRIPE       ((short) FillPatternType.THIN_VERT_BANDS.ordinal()),
   THINREVERSEDIAGONALSTRIPE((short) FillPatternType.THIN_BACKWARD_DIAG.ordinal()),
   THINDIAGONALSTRIPE       ((short) FillPatternType.THIN_FORWARD_DIAG.ordinal()),
   THINHORIZONTALCROSSHATCH ((short) FillPatternType.SQUARES.ordinal()),
   THINDIAGONALCROSSHATCH   ((short) FillPatternType.DIAMONDS.ordinal()),
   GRAY12PERCENT            ((short) FillPatternType.LESS_DOTS.ordinal()),
   GRAY6PERCENT             ((short) FillPatternType.LEAST_DOTS.ordinal());

   private short myIndex;

   /**
    * Constructs a <code>FillPattern</code>.
    * @param index The index.
    */
   FillPattern(short index)
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

package net.sf.jett.model;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;

/**
 * <p><code>Colors</code> represent the built-in color names that correspond
 * with Excel's indexed color scheme.  These are used in conjunction with
 * several property names defined for the style tag.  These color names do NOT
 * necessarily correspond with HTML/CSS standard color names.  Legal values are
 * the names of the enumeration objects, without underscores, case insensitive,
 * e.g. "center" == "Center" == "CENTER".</p>
 *
 * @author Randy Gettman
 * @since 0.4.0
 * @see net.sf.jett.tag.StyleTag
 * @see net.sf.jett.parser.StyleParser#PROPERTY_BORDER_COLOR
 * @see net.sf.jett.parser.StyleParser#PROPERTY_BOTTOM_BORDER_COLOR
 * @see net.sf.jett.parser.StyleParser#PROPERTY_LEFT_BORDER_COLOR
 * @see net.sf.jett.parser.StyleParser#PROPERTY_RIGHT_BORDER_COLOR
 * @see net.sf.jett.parser.StyleParser#PROPERTY_TOP_BORDER_COLOR
 * @see net.sf.jett.parser.StyleParser#PROPERTY_FILL_BACKGROUND_COLOR
 * @see net.sf.jett.parser.StyleParser#PROPERTY_FILL_FOREGROUND_COLOR
 * @see net.sf.jett.parser.StyleParser#PROPERTY_FONT_COLOR
 */
public enum ExcelColor
{
    AQUA               (HSSFColorPredefined.AQUA.getColor()                 , IndexedColors.AQUA                 , 51, 204, 204),
    AUTOMATIC          (HSSFColorPredefined.AUTOMATIC.getColor()            , IndexedColors.AUTOMATIC            , 0, 0, 0),
    BLACK              (HSSFColorPredefined.BLACK.getColor()                , IndexedColors.BLACK                , 0, 0, 0),
    BLUE               (HSSFColorPredefined.BLUE.getColor()                 , IndexedColors.BLUE                 , 0, 0, 255),
    BLUEGREY           (HSSFColorPredefined.BLUE_GREY.getColor()            , IndexedColors.BLUE_GREY            , 102, 102, 153),
    BRIGHTGREEN        (HSSFColorPredefined.BRIGHT_GREEN.getColor()         , IndexedColors.BRIGHT_GREEN         , 0, 255, 0),
    BROWN              (HSSFColorPredefined.BROWN.getColor()                , IndexedColors.BROWN                , 153, 51, 0),
    CORAL              (HSSFColorPredefined.CORAL.getColor()                , IndexedColors.CORAL                , 255, 128, 128),
    CORNFLOWERBLUE     (HSSFColorPredefined.CORNFLOWER_BLUE.getColor()      , IndexedColors.CORNFLOWER_BLUE      , 153, 153, 255),
    DARKBLUE           (HSSFColorPredefined.DARK_BLUE.getColor()            , IndexedColors.DARK_BLUE            , 0, 0, 128),
    DARKGREEN          (HSSFColorPredefined.DARK_GREEN.getColor()           , IndexedColors.DARK_GREEN           , 0, 51, 0),
    DARKRED            (HSSFColorPredefined.DARK_RED.getColor()             , IndexedColors.DARK_RED             , 128, 0, 0),
    DARKTEAL           (HSSFColorPredefined.DARK_TEAL.getColor()            , IndexedColors.DARK_TEAL            , 0, 51, 102),
    DARKYELLOW         (HSSFColorPredefined.DARK_YELLOW.getColor()          , IndexedColors.DARK_YELLOW          , 128, 128, 0),
    GOLD               (HSSFColorPredefined.GOLD.getColor()                 , IndexedColors.GOLD                 , 255, 204, 0),
    GREEN              (HSSFColorPredefined.GREEN.getColor()                , IndexedColors.GREEN                , 0, 128, 0),
    GREY25PERCENT      (HSSFColorPredefined.GREY_25_PERCENT.getColor()      , IndexedColors.GREY_25_PERCENT      , 192, 192, 192),
    GREY40PERCENT      (HSSFColorPredefined.GREY_40_PERCENT.getColor()      , IndexedColors.GREY_40_PERCENT      , 150, 150, 150),
    GREY50PERCENT      (HSSFColorPredefined.GREY_50_PERCENT.getColor()      , IndexedColors.GREY_50_PERCENT      , 128, 128, 128),
    GREY80PERCENT      (HSSFColorPredefined.GREY_80_PERCENT.getColor()      , IndexedColors.GREY_80_PERCENT      , 51, 51, 51),
    INDIGO             (HSSFColorPredefined.INDIGO.getColor()               , IndexedColors.INDIGO               , 51, 51, 153),
    LAVENDER           (HSSFColorPredefined.LAVENDER.getColor()             , IndexedColors.LAVENDER             , 204, 153, 255),
    LEMONCHIFFON       (HSSFColorPredefined.LEMON_CHIFFON.getColor()        , IndexedColors.LEMON_CHIFFON        , 255, 255, 204),
    LIGHTBLUE          (HSSFColorPredefined.LIGHT_BLUE.getColor()           , IndexedColors.LIGHT_BLUE           , 51, 102, 255),
    LIGHTCORNFLOWERBLUE(HSSFColorPredefined.LIGHT_CORNFLOWER_BLUE.getColor(), IndexedColors.LIGHT_CORNFLOWER_BLUE, 204, 204, 255),
    LIGHTGREEN         (HSSFColorPredefined.LIGHT_GREEN.getColor()          , IndexedColors.LIGHT_GREEN          , 204, 255, 204),
    LIGHTORANGE        (HSSFColorPredefined.LIGHT_ORANGE.getColor()         , IndexedColors.LIGHT_ORANGE         , 255, 153, 0),
    LIGHTTURQUOISE     (HSSFColorPredefined.LIGHT_TURQUOISE.getColor()      , IndexedColors.LIGHT_TURQUOISE      , 204, 255, 255),
    LIGHTYELLOW        (HSSFColorPredefined.LIGHT_YELLOW.getColor()         , IndexedColors.LIGHT_YELLOW         , 255, 255, 153),
    LIME               (HSSFColorPredefined.LIME.getColor()                 , IndexedColors.LIME                 , 153, 204, 0),
    MAROON             (HSSFColorPredefined.MAROON.getColor()               , IndexedColors.MAROON               , 128, 0, 0),
    OLIVEGREEN         (HSSFColorPredefined.OLIVE_GREEN.getColor()          , IndexedColors.OLIVE_GREEN          , 51, 51, 0),
    ORANGE             (HSSFColorPredefined.ORANGE.getColor()               , IndexedColors.ORANGE               , 255, 102, 0),
    ORCHID             (HSSFColorPredefined.ORCHID.getColor()               , IndexedColors.ORCHID               , 102, 0, 102),
    PALEBLUE           (HSSFColorPredefined.PALE_BLUE.getColor()            , IndexedColors.PALE_BLUE            , 153, 204, 255),
    PINK               (HSSFColorPredefined.PINK.getColor()                 , IndexedColors.PINK                 , 255, 0, 255),
    PLUM               (HSSFColorPredefined.PLUM.getColor()                 , IndexedColors.PLUM                 , 153, 51, 102),
    RED                (HSSFColorPredefined.RED.getColor()                  , IndexedColors.RED                  , 255, 0, 0),
    ROSE               (HSSFColorPredefined.ROSE.getColor()                 , IndexedColors.ROSE                 , 255, 103, 204),
    ROYALBLUE          (HSSFColorPredefined.ROYAL_BLUE.getColor()           , IndexedColors.ROYAL_BLUE           , 0, 102, 204),
    SEAGREEN           (HSSFColorPredefined.SEA_GREEN.getColor()            , IndexedColors.SEA_GREEN            , 51, 153, 102),
    SKYBLUE            (HSSFColorPredefined.SKY_BLUE.getColor()             , IndexedColors.SKY_BLUE             , 0, 204, 255),
    TAN                (HSSFColorPredefined.TAN.getColor()                  , IndexedColors.TAN                  , 255, 204, 153),
    TEAL               (HSSFColorPredefined.TEAL.getColor()                 , IndexedColors.TEAL                 , 0, 128, 128),
    TURQUOISE          (HSSFColorPredefined.TURQUOISE.getColor()            , IndexedColors.TURQUOISE            , 0, 255, 255),
    VIOLET             (HSSFColorPredefined.VIOLET.getColor()               , IndexedColors.VIOLET               , 128, 0, 128),
    WHITE              (HSSFColorPredefined.WHITE.getColor()                , IndexedColors.WHITE                , 255, 255, 255),
    YELLOW             (HSSFColorPredefined.YELLOW.getColor()               , IndexedColors.YELLOW               , 255, 255, 0);

    /**
     * The "automatic" color in HSSF (.xls).
     * @since 0.9.1
     */
    public static final HSSFColor HSSF_COLOR_AUTOMATIC = HSSFColorPredefined.AUTOMATIC.getColor();

    /**
     * The color index used by comments in XSSF (.xlsx).
     * @since 0.10.0
     */
    public static final short XSSF_COLOR_COMMENT = 81;

    private HSSFColor myHssfColor;
    private IndexedColors myIndexedColor;
    private int myRed;
    private int myGreen;
    private int myBlue;

    private static HSSFColor[] hssfColors;

    static
    {
        hssfColors = new HSSFColor[65];
        for (ExcelColor excelColor : values())
        {
            HSSFColor hssfColor = excelColor.getHssfColor();
            hssfColors[hssfColor.getIndex()] = hssfColor;
        }
    }

    /**
     * Creates a <code>ExcelColor</code>.
     * @param hssfColor The <code>HSSFColor</code>.
     * @param indexedColor The <code>IndexedColor</code>.
     * @param red The red value, 0-255.
     * @param green The green value, 0-255.
     * @param blue The blue value, 0-255.
     */
    ExcelColor(HSSFColor hssfColor, IndexedColors indexedColor, int red, int green, int blue)
    {
        myHssfColor = hssfColor;
        myIndexedColor = indexedColor;
        myRed = red;
        myGreen = green;
        myBlue = blue;
    }

    /**
     * Return the <code>HSSFColor</code>.
     * @return The <code>HSSFColor</code>.
     */
    public HSSFColor getHssfColor()
    {
        return myHssfColor;
    }

    /**
     * Return the <code>XSSFColor</code>.
     * @return The <code>XSSFColor</code>.
     */
    public XSSFColor getXssfColor()
    {
        return ExcelColor.createXSSFColor(myHssfColor.getTriplet());
    }

    /**
     * Returns the index.
     * @return The index.
     */
    public int getIndex()
    {
        return myIndexedColor.getIndex();
    }

    /**
     * Returns the <code>IndexedColors</code>.
     * @return The <code>IndexedColors</code>.
     */
    public IndexedColors getIndexedColor()
    {
        return myIndexedColor;
    }

    /**
     * Returns the "distance" of the given RGB triplet from this color, as
     * defined by the sum of each of the differences for the red, green, and
     * blue values.
     * @param red The red value.
     * @param green The green value.
     * @param blue The blue value.
     * @return The sum of each of the differences for the red, green, and blue
     *    values.
     */
    public int distance(int red, int green, int blue)
    {
        return Math.abs(red - myRed) + Math.abs(green - myGreen) + Math.abs(blue - myBlue);
    }

    /**
     * Returns the color name, in all lowercase, no underscores or spaces.
     * @return The color name, in all lowercase, no underscores or spaces.
     */
    @Override
    public String toString()
    {
        return name().trim().toLowerCase().replace("_", "");
    }

    /**
     * Maps a short index color back to an <code>HSSFColor</code>.
     * @param index A short color index.
     * @return An <code>HSSFColor</code>.
     */
    public static HSSFColor getHssfColorByIndex(short index)
    {
        if (index == Font.COLOR_NORMAL || index == XSSF_COLOR_COMMENT)
        {
            return HSSF_COLOR_AUTOMATIC;
        }
        return hssfColors[index];
    }

    public static XSSFColor createXSSFColor(short[] rgb) {
        CTColor color = CTColor.Factory.newInstance();
        color.setRgb(new byte[] { (byte) rgb[0], (byte) rgb[1], (byte) rgb[2] });
        return XSSFColor.from(color, new DefaultIndexedColorMap());
    }
}

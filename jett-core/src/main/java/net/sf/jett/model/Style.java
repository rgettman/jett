package net.sf.jett.model;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * A <code>Style</code> object holds desired properties and property values for
 * later reference.  If a property value is <code>null</code>, then that
 * indicates NOT to override the current style value on a cell.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class Style
{
    private HorizontalAlignment myAlignment;
    private BorderStyle myBorderBottomStyle;
    private BorderStyle myBorderLeftStyle;
    private BorderStyle myBorderRightStyle;
    private BorderStyle myBorderTopStyle;
    private String myBorderBottomColor;
    private String myBorderLeftColor;
    private String myBorderRightColor;
    private String myBorderTopColor;
    private Integer myColumnWidth;
    private String myDataFormat;
    private String myFillBackgroundColor;
    private String myFillForegroundColor;
    private FillPatternType myFillPatternType;
    private Boolean amIHidden;
    private Short myIndention;
    private Boolean amILocked;
    private Short myRotationDegrees;
    private Short myRowHeight;
    private VerticalAlignment myVerticalAlignment;
    private Boolean amIWrappingText;
    private Boolean amIFontBold;
    private FontCharset myFontCharset;
    private String myFontColor;
    private Short myFontHeightInPoints;
    private String myFontName;
    private Boolean amIFontItalic;
    private Boolean amIFontStrikeout;
    private FontTypeOffset myFontTypeOffset;
    private FontUnderline myFontUnderline;
    private boolean doIHaveStylesToApply;

    /**
     * Construct a <code>Style</code> with no style preferences.
     */
    public Style()
    {
        myAlignment = null;
        myBorderBottomStyle = null;
        myBorderLeftStyle = null;
        myBorderRightStyle = null;
        myBorderTopStyle = null;
        myBorderBottomColor = null;
        myBorderLeftColor = null;
        myBorderRightColor = null;
        myBorderTopColor = null;
        myColumnWidth = null;
        myDataFormat = null;
        myFillBackgroundColor = null;
        myFillForegroundColor = null;
        myFillPatternType = null;
        amIHidden = null;
        myIndention = null;
        amILocked = null;
        myRotationDegrees = null;
        myRowHeight = null;
        myVerticalAlignment = null;
        amIWrappingText = null;
        amIFontBold = null;
        myFontCharset = null;
        myFontColor = null;
        myFontHeightInPoints = null;
        myFontName = null;
        amIFontItalic = null;
        amIFontStrikeout = null;
        myFontTypeOffset = null;
        myFontUnderline = null;
        doIHaveStylesToApply = false;
    }

    /**
     * Returns the horizontal alignment.
     * @return The horizontal alignment.
     */
    public HorizontalAlignment getAlignment()
    {
        return myAlignment;
    }

    /**
     * Sets the horizontal alignment.
     * @param alignment The horizontal alignment.
     */
    public void setAlignment(HorizontalAlignment alignment)
    {
        myAlignment = alignment;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the bottom border style.
     * @return The bottom border style.
     */
    public BorderStyle getBorderBottomStyle()
    {
        return myBorderBottomStyle;
    }

    /**
     * Sets the bottom border style.
     * @param borderStyle The bottom border type.
     */
    public void setBorderBottomStyle(BorderStyle borderStyle)
    {
        myBorderBottomStyle = borderStyle;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the left border style.
     * @return The left border style.
     */
    public BorderStyle getBorderLeftStyle()
    {
        return myBorderLeftStyle;
    }

    /**
     * Sets the left border style.
     * @param borderLeftStyle The left border style.
     */
    public void setBorderLeftStyle(BorderStyle borderLeftStyle)
    {
        myBorderLeftStyle = borderLeftStyle;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the right border style.
     * @return The right border style.
     */
    public BorderStyle getBorderRightStyle()
    {
        return myBorderRightStyle;
    }

    /**
     * Sets the right border style.
     * @param borderRightStyle The right border style.
     */
    public void setBorderRightType(BorderStyle borderRightStyle)
    {
        myBorderRightStyle = borderRightStyle;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the top border style.
     * @return The top border style.
     */
    public BorderStyle getBorderTopStyle()
    {
        return myBorderTopStyle;
    }

    /**
     * Sets the top border style.
     * @param borderTopStyle The top border style.
     */
    public void setBorderTopStyle(BorderStyle borderTopStyle)
    {
        myBorderTopStyle = borderTopStyle;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the bottom border color, as a color name or a hex string.
     * @return The bottom border color, as a color name or a hex string.
     */
    public String getBorderBottomColor()
    {
        return myBorderBottomColor;
    }

    /**
     * Sets the bottom border color, as a color name or a hex string.
     * @param borderBottomColor The bottom border color, as a color name or a
     *    hex string.
     */
    public void setBorderBottomColor(String borderBottomColor)
    {
        myBorderBottomColor = borderBottomColor;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the left border color, as a color name or a hex string.
     * @return The left border color, as a color name or a hex string.
     */
    public String getBorderLeftColor()
    {
        return myBorderLeftColor;
    }

    /**
     * Sets the left border color, as a color name or a hex string.
     * @param borderLeftColor The left border color, as a color name or a hex
     *    string.
     */
    public void setBorderLeftColor(String borderLeftColor)
    {
        myBorderLeftColor = borderLeftColor;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the right border color, as a color name or a hex string.
     * @return The right border color, as a color name or a hex string.
     */
    public String getBorderRightColor()
    {
        return myBorderRightColor;
    }

    /**
     * Sets the right border color, as a color name or a hex string.
     * @param borderRightColor The right border color, as a color name or a hex
     *    string.
     */
    public void setBorderRightColor(String borderRightColor)
    {
        myBorderRightColor = borderRightColor;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the top border color, as a color name or a hex string.
     * @return The top border color, as a color name or a hex string.
     */
    public String getBorderTopColor()
    {
        return myBorderTopColor;
    }

    /**
     * Sets the top border color, as a color name or a hex string.
     * @param borderTopColor The top border color, as a color name or a hex
     *    string.
     */
    public void setBorderTopColor(String borderTopColor)
    {
        myBorderTopColor = borderTopColor;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the column width in number of characters.
     * @return The column width in number of characters.
     */
    public Integer getColumnWidth()
    {
        return myColumnWidth;
    }

    /**
     * Sets the column width in number of characters.
     * @param columnWidth The column width in number of characters.
     */
    public void setColumnWidth(Integer columnWidth)
    {
        myColumnWidth = columnWidth;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the data format string.
     * @return The data format string.
     */
    public String getDataFormat()
    {
        return myDataFormat;
    }

    /**
     * Sets the data format string.
     * @param dataFormat The data format string.
     */
    public void setDataFormat(String dataFormat)
    {
        myDataFormat = dataFormat;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the fill background color, as a color name or a hex string.
     * @return The fill background color, as a color name or a hex string.
     */
    public String getFillBackgroundColor()
    {
        return myFillBackgroundColor;
    }

    /**
     * Sets the fill background color, as a color name or a hex string.
     * @param fillBackgroundColor The fill background color, as a color name or
     *    a hex string.
     */
    public void setFillBackgroundColor(String fillBackgroundColor)
    {
        myFillBackgroundColor = fillBackgroundColor;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the fill foreground color, as a color name or a hex string.
     * @return The fill foreground color, as a color name or a hex string.
     */
    public String getFillForegroundColor()
    {
        return myFillForegroundColor;
    }

    /**
     * Sets the fill foreground color, as a color name or a hex string.
     * @param fillForegroundColor The fill foreground color, as a color name or
     *    a hex string.
     */
    public void setFillForegroundColor(String fillForegroundColor)
    {
        myFillForegroundColor = fillForegroundColor;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the fill pattern type.
     * @return The fill pattern type.
     */
    public FillPatternType getFillPatternType()
    {
        return myFillPatternType;
    }

    /**
     * Sets the fill pattern type.
     * @param fillPatternType The fill pattern type.
     */
    public void setFillPatternType(FillPatternType fillPatternType)
    {
        myFillPatternType = fillPatternType;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns whether the cell is "hidden".
     * @return Whether the cell is "hidden".
     */
    public Boolean isHidden()
    {
        return amIHidden;
    }

    /**
     * Sets whether the cell is "hidden".
     * @param isHidden Whether the cell is "hidden".
     */
    public void setHidden(Boolean isHidden)
    {
        amIHidden = isHidden;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the number characters the text is indented.
     * @return The number characters the text is indented.
     */
    public Short getIndention()
    {
        return myIndention;
    }

    /**
     * Sets the number characters the text is indented.
     * @param indention The number characters the text is indented.
     */
    public void setIndention(Short indention)
    {
        myIndention = indention;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns whether the cell is "locked".
     * @return Whether the cell is "locked".
     */
    public Boolean isLocked()
    {
        return amILocked;
    }

    /**
     * Sets whether the cell is "locked".
     * @param isLocked Whether the cell is "locked".
     */
    public void setLocked(Boolean isLocked)
    {
        amILocked = isLocked;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the degrees the text is rotated.
     * @return The degrees the text is rotated.
     */
    public Short getRotationDegrees()
    {
        return myRotationDegrees;
    }

    /**
     * Sets the degrees the text is rotated.
     * @param rotationDegrees The degrees the text is rotated.
     */
    public void setRotationDegrees(Short rotationDegrees)
    {
        myRotationDegrees = rotationDegrees;
        doIHaveStylesToApply = true;
    }

    /**
     * Sets the row height in points.
     * @return The row height in points.
     */
    public Short getRowHeight()
    {
        return myRowHeight;
    }

    /**
     * Sets the row height in points.
     * @param rowHeight The row height in points.
     */
    public void setRowHeight(Short rowHeight)
    {
        myRowHeight = rowHeight;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the vertical alignment.
     * @return The vertical alignment.
     */
    public VerticalAlignment getVerticalAlignment()
    {
        return myVerticalAlignment;
    }

    /**
     * Sets the vertical alignment.
     * @param verticalAlignment The vertical alignment.
     */
    public void setVerticalAlignment(VerticalAlignment verticalAlignment)
    {
        myVerticalAlignment = verticalAlignment;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns whether text is wrapped on to the next line.
     * @return Whether text is wrapped on to the next line.
     */
    public Boolean isWrappingText()
    {
        return amIWrappingText;
    }

    /**
     * Sets whether text is wrapped on to the next line.
     * @param isWrappingText Whether text is wrapped on to the next line.
     */
    public void setWrappingText(Boolean isWrappingText)
    {
        amIWrappingText = isWrappingText;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns whether the font is bold.
     * @return Whether the font is bold.
     */
    public Boolean isFontBold()
    {
        return amIFontBold;
    }

    /**
     * Sets whether the font is bold.
     * @param fontBold Whether the font is bold.
     */
    public void setFontBold(Boolean fontBold)
    {
        amIFontBold = fontBold;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the font charset.
     * @return The font charset.
     */
    public FontCharset getFontCharset()
    {
        return myFontCharset;
    }

    /**
     * Returns the font charset.
     * @param fontCharset The font charset.
     */
    public void setFontCharset(FontCharset fontCharset)
    {
        myFontCharset = fontCharset;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the font color, either a color name or a hex string.
     * @return The font color, either a color name or a hex string.
     */
    public String getFontColor()
    {
        return myFontColor;
    }

    /**
     * Sets the font color, either a color name or a hex string.
     * @param fontColor The font color, either a color name or a hex string.
     */
    public void setFontColor(String fontColor)
    {
        myFontColor = fontColor;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the font height in points.
     * @return The font height in points.
     */
    public Short getFontHeightInPoints()
    {
        return myFontHeightInPoints;
    }

    /**
     * Sets the font height in points.
     * @param fontHeightInPoints The font height in points.
     */
    public void setFontHeightInPoints(Short fontHeightInPoints)
    {
        myFontHeightInPoints = fontHeightInPoints;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the font name.
     * @return The font name.
     */
    public String getFontName()
    {
        return myFontName;
    }

    /**
     * Sets the font name.
     * @param fontName The font name.
     */
    public void setFontName(String fontName)
    {
        myFontName = fontName;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns whether the font is italic.
     * @return Whether the font is italic.
     */
    public Boolean isFontItalic()
    {
        return amIFontItalic;
    }

    /**
     * Sets whether the font is italic.
     * @param isFontItalic Whether the font is italic.
     */
    public void setFontItalic(Boolean isFontItalic)
    {
        amIFontItalic = isFontItalic;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns whether the font is strikeout.
     * @return Whether the font is strikeout.
     */
    public Boolean isFontStrikeout()
    {
        return amIFontStrikeout;
    }

    /**
     * Sets whether the font is strikeout.
     * @param isFontStrikeout Whether the font is strikeout.
     */
    public void setFontStrikeout(Boolean isFontStrikeout)
    {
        amIFontStrikeout = isFontStrikeout;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the <code>FontTypeOffset</code>.
     * @return The <code>FontTypeOffset</code>.
     */
    public FontTypeOffset getFontTypeOffset()
    {
        return myFontTypeOffset;
    }

    /**
     * Sets the <code>FontTypeOffset</code>.
     * @param fontTypeOffset The <code>FontTypeOffset</code>.
     */
    public void setFontTypeOffset(FontTypeOffset fontTypeOffset)
    {
        myFontTypeOffset = fontTypeOffset;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns the <code>FontUnderLine</code>.
     * @return The <code>FontUnderLine</code>.
     */
    public FontUnderline getFontUnderline()
    {
        return myFontUnderline;
    }

    /**
     * Sets the <code>FontUnderLine</code>.
     * @param fontUnderline The <code>FontUnderLine</code>.
     */
    public void setFontUnderline(FontUnderline fontUnderline)
    {
        myFontUnderline = fontUnderline;
        doIHaveStylesToApply = true;
    }

    /**
     * Returns whether there are styles to apply, i.e. whether any styles are
     * set.
     * @return Whether there are styles to apply.
     */
    public boolean isStyleToApply()
    {
        return doIHaveStylesToApply;
    }

    /**
     * Applies the given <code>Style</code> to this <code>Style</code>,
     * overwriting any properties in common.
     * @param style Another <code>Style</code>.
     */
    public void apply(Style style)
    {
        if (style.getAlignment() != null)           setAlignment(style.getAlignment());
        if (style.getBorderBottomColor() != null)   setBorderBottomColor(style.getBorderBottomColor());
        if (style.getBorderBottomStyle() != null)    setBorderBottomStyle(style.getBorderBottomStyle());
        if (style.getBorderLeftColor() != null)     setBorderLeftColor(style.getBorderLeftColor());
        if (style.getBorderLeftStyle() != null)      setBorderLeftStyle(style.getBorderLeftStyle());
        if (style.getBorderRightColor() != null)    setBorderRightColor(style.getBorderRightColor());
        if (style.getBorderRightStyle() != null)     setBorderRightType(style.getBorderRightStyle());
        if (style.getBorderTopColor() != null)      setBorderTopColor(style.getBorderTopColor());
        if (style.getBorderTopStyle() != null)       setBorderTopStyle(style.getBorderTopStyle());
        if (style.getColumnWidth() != null)         setColumnWidth(style.getColumnWidth());
        if (style.getDataFormat() != null)          setDataFormat(style.getDataFormat());
        if (style.getFillBackgroundColor() != null) setFillBackgroundColor(style.getFillBackgroundColor());
        if (style.getFillForegroundColor() != null) setFillForegroundColor(style.getFillForegroundColor());
        if (style.getFillPatternType() != null)     setFillPatternType(style.getFillPatternType());
        if (style.isFontBold() != null)             setFontBold(style.isFontBold());
        if (style.getFontCharset() != null)         setFontCharset(style.getFontCharset());
        if (style.getFontColor() != null)           setFontColor(style.getFontColor());
        if (style.getFontHeightInPoints() != null)  setFontHeightInPoints(style.getFontHeightInPoints());
        if (style.isFontItalic() != null)           setFontItalic(style.isFontItalic());
        if (style.getFontName() != null)            setFontName(style.getFontName());
        if (style.isFontStrikeout() != null)        setFontStrikeout(style.isFontStrikeout());
        if (style.getFontTypeOffset() != null)      setFontTypeOffset(style.getFontTypeOffset());
        if (style.getFontUnderline() != null)       setFontUnderline(style.getFontUnderline());
        if (style.isHidden() != null)               setHidden(style.isHidden());
        if (style.getIndention() != null)           setIndention(style.getIndention());
        if (style.isLocked() != null)               setLocked(style.isLocked());
        if (style.getRotationDegrees() != null)     setRotationDegrees(style.getRotationDegrees());
        if (style.getRowHeight() != null)           setRowHeight(style.getRowHeight());
        if (style.getVerticalAlignment() != null)   setVerticalAlignment(style.getVerticalAlignment());
        if (style.isWrappingText() != null)         setWrappingText(style.isWrappingText());

        if (style.isStyleToApply()) doIHaveStylesToApply = true;
    }
}

package net.sf.jett.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import net.sf.jett.exception.TagParseException;
import net.sf.jett.model.Alignment;
import net.sf.jett.model.Block;
import net.sf.jett.model.BorderType;
import net.sf.jett.model.ExcelColor;
import net.sf.jett.model.FillPattern;
import net.sf.jett.model.FontBoldweight;
import net.sf.jett.model.FontCharset;
import net.sf.jett.model.FontTypeOffset;
import net.sf.jett.model.FontUnderline;
import net.sf.jett.model.VerticalAlignment;
import net.sf.jett.transform.BlockTransformer;
import net.sf.jett.util.AttributeUtil;

/**
 * <p>A <code>StyleTag</code> represents a dynamically determine style for a
 * <code>Cell</code>.  The <code>style</code> attribute works like the HTML
 * "style" attribute, in that one can specify one or more style elements in a
 * <code>property: value;property: value</code> style.  If a property is
 * specified, then it will override whatever value is already present in the
 * <code>Cell</code>.  If a property value is an empty string or the property
 * is not present, then it will be ignored and it will not override whatever
 * value is already present in the <code>Cell</code>.  Unrecognized property
 * names and unrecognized values for a property are ignored and do not override
 * whatever value is already present in the <code>Cell</code>.  Property names
 * and values may be specified in a case insensitive-fashion, i.e. "CENTER" =
 * "Center" = "center".  A <code>StyleTag</code> must have a body.</p>
 *
 * <br/>Attributes:
 * <ul>
 * <li><em>Inherits all attributes from {@link BaseTag}.</em></li>
 * <li>style (required): <code>String</code></li>
 * </ul>
 *
 * <p>Properties:  The following properties control alignment, borders, colors,
 * etc., everything but the font characteristics.
 * <ul>
 *    <li><code>alignment</code> - Controls horizontal alignment, with one of
 *    the values taken from <code>Alignment.toString()</code>.</li>
 *    <li><code>border</code> - Controls all 4 borders for the cell, with one
 *    of the values taken from <code>BorderType.toString()</code>.</li>
 *    <li><code>border-bottom</code> - Controls the bottom border for the cell,
 *    with one of the values taken from <code>BorderType.toString()</code>.</li>
 *    <li><code>border-left</code> - Controls the left border for the cell,
 *    with one of the values taken from <code>BorderType.toString()</code>.</li>
 *    <li><code>border-right</code> - Controls the right border for the cell,
 *    with one of the values taken from <code>BorderType.toString()</code>.</li>
 *    <li><code>border-top</code> - Controls the top border for the cell,
 *    with one of the values taken from <code>BorderType.toString()</code>.</li>
 *    <li><code>border-color</code> - Controls the color of all 4 borders for
 *    the cell, with a hex value ("#rrggbb") or one of 48 Excel-based color
 *    names defined by <code>ExcelColor.toString()</code>.  For ".xls" files,
 *    if a hex value is supplied, then the supported color name that is closest
 *    to the given value is used.</li>
 *    <li><code>bottom-border-color</code> - Controls the color of the bottom
 *    border for the cell, with a hex value ("#rrggbb") or one of the above 48
 *    color names mentioned above.</li>
 *    <li><code>left-border-color</code> - Controls the color of the left
 *    border for the cell, with a hex value ("#rrggbb") or one of the above 48
 *    color names mentioned above.</li>
 *    <li><code>right-border-color</code> - Controls the color of the right
 *    border for the cell, with a hex value ("#rrggbb") or one of the above 48
 *    color names mentioned above.</li>
 *    <li><code>top-border-color</code> - Controls the color of the top
 *    border for the cell, with a hex value ("#rrggbb") or one of the above 48
 *    color names mentioned above.</li>
 *    <li><code>column-width-in-chars</code> - Controls the width of the cell's
 *    column, in number of characters.</li>
 *    <li><code>data-format</code> - Controls the Excel numeric or date format
 *    string.</li>
 *    <li><code>fill-background-color</code> - Controls the "background color"
 *    of the fill pattern, with one of the color values mentioned above.</li>
 *    <li><code>fill-foreground-color</code> - Controls the "foreground color"
 *    of the fill pattern, with one of the color values mentioned above.</li>
 *    <li><code>fill-pattern</code> - Controls the "fill pattern", with one of
 *    the values taken from <code>FillPattern.toString()</code>:</li>
 *    <li><code>hidden</code> - Controls the "hidden" property with a
 *    <code>true</code> or <code>false</code> value.</li>
 *    <li><code>indention</code> - Controls the number of characters that the
 *    text is indented.</li>
 *    <li><code>locked</code> - Controls the "locked" property with a
 *    <code>true</code> or <code>false</code> value.</li>
 *    <li><code>rotation</code> - Controls the number of degrees the text is
 *    rotated, from -90 to +90, or <code>ROTATION_STACKED</code> for stacked
 *    text.</li>
 *    <li><code>row-height-in-points</code> - Controls the height of the cell's
 *    row, in points.</li>
 *    <li><code>vertical-alignment</code> - Controls horizontal alignment, with
 *    one of the values taken from <code>VerticalAlignment.toString()</code>:</li>
 *    <li><code>wrap-text</code> - Controls whether long text values are
 *    wrapped onto the next physical line with a cell, with a <code>true</code>
 *    or <code>false</code> value.</li>
 * </ul>
 * <p>Properties:  The following properties control the font characteristics.
 * <ul>
 *    <li><code>font-weight</code> - Controls how bold the text appears, with
 *    the values taken from <code>FontBoldweight.toString()</code>.</li>
 *    <li><code>font-charset</code> - Controls the character set, with the
 *    values taken from <code>Charset.toString()</code>.</li>
 *    <li><code>font-color</code> - Controls the color of the text, with a hex
 *    value ("#rrggbb") or one of the color names mentioned above.</li>
 *    <li><code>font-height-in-points</code> - Controls the font height, in
 *    points.</li>
 *    <li><code>font-name</code> - Controls the font name, e.g. "Arial".</li>
 *    <li><code>font-italic</code> - Controls whether the text is
 *    <em>italic</em>, with a <code>true</code> or <code>false</code> value.</li>
 *    <li><code>font-strikeout</code> - Controls whether the text is
 *    <span style="text-decoration: line-through">strikeout</span>, with a
 *    <code>true</code> or <code>false</code> value.</li>
 *    <li><code>font-type-offset</code> - Controls the text offset, e.g.
 *    <sup>superscript</sup> and <sub>subscript</sub>, with the values taken
 *    from <code>FontTypeOffset.toString()</code>.</li>
 *    <li><code>font-underline</code> - Controls whether and how the text is
 *    underlined, with the values taken from <code>Underline.toString()</code>.</li>
 * </ul>
 *
 * @author Randy Gettman
 * @since 0.4.0
 */
public class StyleTag extends BaseTag
{
   private static final boolean DEBUG = false;

   /**
    * Attribute that specifies the desired style property(ies) to change in the
    * current <code>Cell</code>.  Properties are specified in a string with the
    * following format: <code>property1: value1; property2: value2; ...</code>
    */
   public static final String ATTR_STYLE = "style";

   /**
    * The property to specify horizontal alignment of the text.
    * @see net.sf.jett.model.Alignment
    */
   public static final String PROPERTY_ALIGNMENT = "alignment";
   /**
    * The property to specify the type of all 4 borders.
    * @see net.sf.jett.model.BorderType
    */
   public static final String PROPERTY_BORDER = "border";
   /**
    * The property to specify the type of the bottom border.
    * @see net.sf.jett.model.BorderType
    */
   public static final String PROPERTY_BORDER_BOTTOM = "border-bottom";
   /**
    * The property to specify the type of the left border.
    * @see net.sf.jett.model.BorderType
    */
   public static final String PROPERTY_BORDER_LEFT = "border-left";
   /**
    * The property to specify the type of the right border.
    * @see net.sf.jett.model.BorderType
    */
   public static final String PROPERTY_BORDER_RIGHT = "border-right";
   /**
    * The property to specify the type of the top border.
    * @see net.sf.jett.model.BorderType
    */
   public static final String PROPERTY_BORDER_TOP = "border-top";
   /**
    * The property to specify the color of all 4 borders.
    * @see net.sf.jett.model.ExcelColor
    */
   public static final String PROPERTY_BORDER_COLOR = "border-color";
   /**
    * The property to specify the color of the bottom border.
    * @see net.sf.jett.model.ExcelColor
    */
   public static final String PROPERTY_BOTTOM_BORDER_COLOR = "bottom-border-color";
   /**
    * The property to specify the color of the left border.
    * @see net.sf.jett.model.ExcelColor
    */
   public static final String PROPERTY_LEFT_BORDER_COLOR = "left-border-color";
   /**
    * The property to specify the color of the right border.
    * @see net.sf.jett.model.ExcelColor
    */
   public static final String PROPERTY_RIGHT_BORDER_COLOR = "right-border-color";
   /**
    * The property to specify the color of the top border.
    * @see net.sf.jett.model.ExcelColor
    */
   public static final String PROPERTY_TOP_BORDER_COLOR = "top-border-color";
   /**
    * The property to specify the width of the column in number of characters.
    */
   public static final String PROPERTY_COLUMN_WIDTH_IN_CHARS = "column-width-in-chars";
   /**
    * The property to specify the numeric or date data format string.
    */
   public static final String PROPERTY_DATA_FORMAT = "data-format";
   /**
    * The property to specify the fill background color to be used in a fill
    * pattern.
    * @see net.sf.jett.model.ExcelColor
    */
   public static final String PROPERTY_FILL_BACKGROUND_COLOR = "fill-background-color";
   /**
    * The property to specify the fill foreground color to be used in a fill
    * pattern.
    * @see net.sf.jett.model.ExcelColor
    */
   public static final String PROPERTY_FILL_FOREGROUND_COLOR = "fill-foreground-color";
   /**
    * The property to specify the fill fattern to be used with the fill
    * foreground color and the fill background color.
    * @see net.sf.jett.model.FillPattern
    */
   public static final String PROPERTY_FILL_PATTERN = "fill-pattern";
   /**
    * The property to specify the "hidden" property.
    */
   public static final String PROPERTY_HIDDEN = "hidden";
   /**
    * The property to specify the number of characters that the text is
    * indented.
    */
   public static final String PROPERTY_INDENTION = "indention";
   /**
    * The property to specify the "locked" property.
    */
   public static final String PROPERTY_LOCKED = "locked";
   /**
    * The property to specify the number of degrees that the text is rotated,
    * from -90 to +90.
    */
   public static final String PROPERTY_ROTATION = "rotation";
   /**
    * The property to specify the height of the row in points.
    */
   public static final String PROPERTY_ROW_HEIGHT_IN_POINTS = "row-height-in-points";
   /**
    * The property to specify the vertical alignment of the text.
    * @see net.sf.jett.model.VerticalAlignment
    */
   public static final String PROPERTY_VERTICAL_ALIGNMENT = "vertical-alignment";
   /**
    * The property to specify whether long text values are wrapped to the next
    * physical line within the cell.
    */
   public static final String PROPERTY_WRAP_TEXT = "wrap-text";
   /**
    * The property to specify whether the font is bold.
    */
   public static final String PROPERTY_FONT_BOLDWEIGHT = "font-weight";
   /**
    * The property to specify the charset used by the font.
    * @see net.sf.jett.model.FontCharset
    */
   public static final String PROPERTY_FONT_CHARSET = "font-charset";
   /**
    * The property to specify the font color.
    * @see net.sf.jett.model.ExcelColor
    */
   public static final String PROPERTY_FONT_COLOR = "font-color";
   /**
    * The property to specify the font height in points.
    */
   public static final String PROPERTY_FONT_HEIGHT_IN_POINTS = "font-height-in-points";
   /**
    * The property to specify the font name.
    */
   public static final String PROPERTY_FONT_NAME = "font-name";
   /**
    * The property to specify whether the font is italic.
    */
   public static final String PROPERTY_FONT_ITALIC = "font-italic";
   /**
    * The property to specify whether the font is strikeout.
    */
   public static final String PROPERTY_FONT_STRIKEOUT = "font-strikeout";
   /**
    * The property to specify whether the font type is offset, and if it is,
    * whether it's superscript or subscript.
    * @see net.sf.jett.model.FontTypeOffset
    */
   public static final String PROPERTY_FONT_TYPE_OFFSET = "font-type-offset";
   /**
    * The property to specify how the font text is underlined.
    * @see net.sf.jett.model.FontUnderline
    */
   public static final String PROPERTY_FONT_UNDERLINE = "font-underline";

   /**
    * <p>Specify this value of rotation to use to produce vertically</p>
    * <br>s
    * <br>t
    * <br>a
    * <br>c
    * <br>k
    * <br>e
    * <br>d
    * <p>text.</p>
    * @see #PROPERTY_ROTATION
    */
   public static final String ROTATION_STACKED = "STACKED";
   /**
    * <p>POI value of rotation to use to produce vertically</p>
    * <br>s
    * <br>t
    * <br>a
    * <br>c
    * <br>k
    * <br>e
    * <br>d
    * <p>text.</p>
    * @see #PROPERTY_ROTATION
    */
   public static final short POI_ROTATION_STACKED = 0xFF;

   // Used so that a user can escape the separator.
   // This matches SPEC_SEP but not the concatenation "\" + SPEC_SEP.
   private static final String SPLIT_SPEC = "(?<!\\\\)" + SPEC_SEP;

   private static final List<String> REQ_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_STYLE));

   private Alignment myAlignment;
   private BorderType myBorderBottomType;
   private BorderType myBorderLeftType;
   private BorderType myBorderRightType;
   private BorderType myBorderTopType;
   private Color myBorderBottomColor;
   private Color myBorderLeftColor;
   private Color myBorderRightColor;
   private Color myBorderTopColor;
   private Integer myColumnWidth;
   private String myDataFormat;
   private Color myFillBackgroundColor;
   private Color myFillForegroundColor;
   private FillPattern myFillPatternType;
   private Boolean amIHidden;
   private Short myIndention;
   private Boolean amILocked;
   private Short myRotationDegrees;
   private Short myRowHeight;
   private VerticalAlignment myVerticalAlignment;
   private Boolean amIWrappingText;

   private FontBoldweight myFontBoldweight;
   private FontCharset myFontCharset;
   private Color myFontColor;
   private Short myFontHeightInPoints;
   private String myFontName;
   private Boolean amIFontItalic;
   private Boolean amIFontStrikeout;
   private FontTypeOffset myFontTypeOffset;
   private FontUnderline myFontUnderline;

   private boolean doIHaveStylesToApply;

   /**
    * Returns this <code>Tag's</code> name.
    * @return This <code>Tag's</code> name.
    */
   public String getName()
   {
      return "style";
   }

   /**
    * Returns a <code>List</code> of required attribute names.
    * @return A <code>List</code> of required attribute names.
    */
   protected List<String> getRequiredAttributes()
   {
      List<String> optAttrs = new ArrayList<String>(super.getRequiredAttributes());
      optAttrs.addAll(REQ_ATTRS);
      return optAttrs;
   }

   /**
    * Returns a <code>List</code> of optional attribute names.
    * @return A <code>List</code> of optional attribute names.
    */
   protected List<String> getOptionalAttributes()
   {
      return super.getOptionalAttributes();
   }

   /**
    * Validates the attributes for this <code>Tag</code>.  This tag must have a
    * body.
    */
   public void validateAttributes() throws TagParseException
   {
      super.validateAttributes();
      if (isBodiless())
         throw new TagParseException("Style tags must have a body.");

      TagContext context = getContext();
      Map<String, Object> beans = context.getBeans();
      Map<String, RichTextString> attributes = getAttributes();
      Workbook workbook = context.getSheet().getWorkbook();

      doIHaveStylesToApply = false;
      String line = AttributeUtil.evaluateString(attributes.get(ATTR_STYLE), beans, "");
      String[] styles = line.split(SPLIT_SPEC);
      for (String style : styles)
      {
         String property;
         String value;
         // Replace escaped separators with the normal character for further
         // processing.
         String[] parts = style.replace("\\" + SPEC_SEP, SPEC_SEP).split(":", 2);
         if (parts.length < 2)
         {
            continue;
         }
         property = parts[0].trim().toLowerCase();
         value = parts[1].trim().toUpperCase();

         if (value != null && value.length() >= 1)
         {
            // Try for descending order of popularity.  This order should match
            // the order of properties in examineAndApplyStyle(), but if it
            // doesn't match, then nothing will break.
            if (PROPERTY_FONT_BOLDWEIGHT.equals(property))
            {
               try
               {
                  myFontBoldweight = FontBoldweight.valueOf(value);
                  doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_FONT_ITALIC.equals(property))
            {
               amIFontItalic = Boolean.valueOf(value);
               doIHaveStylesToApply = true;
            }
            else if (PROPERTY_FONT_COLOR.equals(property))
            {
               myFontColor = getColor(workbook, value);
               if (myFontColor != null)
                  doIHaveStylesToApply = true;
            }
            else if (PROPERTY_FONT_NAME.equals(property))
            {
               myFontName = value;
               doIHaveStylesToApply = true;
            }
            else if (PROPERTY_FONT_HEIGHT_IN_POINTS.equals(property))
            {
               try
               {
                  myFontHeightInPoints = Short.valueOf(value);
                  doIHaveStylesToApply = true;
               }
               catch (NumberFormatException e)
               {
                  if (DEBUG)
                     System.err.println("NumberFormatException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_ALIGNMENT.equals(property))
            {
               try
               {
                  myAlignment = Alignment.valueOf(value);
                  doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_BORDER.equals(property))
            {
               try
               {
                  myBorderBottomType = myBorderLeftType = myBorderRightType =
                     myBorderTopType = BorderType.valueOf(value);
                  doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_DATA_FORMAT.equals(property))
            {
               if (value != null)
               {
                  myDataFormat = value;
                  doIHaveStylesToApply = true;
               }
            }
            else if (PROPERTY_FONT_UNDERLINE.equals(property))
            {
               try
               {
                  myFontUnderline = FontUnderline.valueOf(value);
                  doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_FONT_STRIKEOUT.equals(property))
            {
               amIFontStrikeout = Boolean.valueOf(value);
               doIHaveStylesToApply = true;
            }
            else if (PROPERTY_WRAP_TEXT.equals(property))
            {
               amIWrappingText = Boolean.valueOf(value);
               doIHaveStylesToApply = true;
            }
            else if (PROPERTY_FILL_BACKGROUND_COLOR.equals(property))
            {
               myFillBackgroundColor = getColor(workbook, value);
               if (myFillBackgroundColor != null)
                  doIHaveStylesToApply = true;
            }
            else if (PROPERTY_FILL_FOREGROUND_COLOR.equals(property))
            {
               myFillForegroundColor = getColor(workbook, value);
               if (myFillForegroundColor != null)
                  doIHaveStylesToApply = true;
            }
            else if (PROPERTY_FILL_PATTERN.equals(property))
            {
               try
               {
                  myFillPatternType = FillPattern.valueOf(value);
                  doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_VERTICAL_ALIGNMENT.equals(property))
            {
               try
               {
                  myVerticalAlignment = VerticalAlignment.valueOf(value);
                  doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_INDENTION.equals(property))
            {
               try
               {
                  myIndention = Short.valueOf(value);
                  doIHaveStylesToApply = true;
               }
               catch (NumberFormatException e)
               {
                  if (DEBUG)
                     System.err.println(" caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_ROTATION.equals(property))
            {
               if (ROTATION_STACKED.equals(value))
               {
                  myRotationDegrees = POI_ROTATION_STACKED;
                  doIHaveStylesToApply = true;
               }
               else
               {
                  try
                  {
                     myRotationDegrees = Short.valueOf(value);
                     doIHaveStylesToApply = true;
                  }
                  catch (NumberFormatException e)
                  {
                     if (DEBUG)
                        System.err.println(" caught: " + e.getMessage());
                  }
               }
            }
            else if (PROPERTY_COLUMN_WIDTH_IN_CHARS.equals(property))
            {
               try
               {
                  double width = Double.parseDouble(value);
                  myColumnWidth = (int) Math.round(256 * width);
                  doIHaveStylesToApply = true;
               }
               catch (NumberFormatException e)
               {
                  if (DEBUG)
                     System.err.println(" caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_ROW_HEIGHT_IN_POINTS.equals(property))
            {
               try
               {
                  double height = Double.parseDouble(value);
                  myRowHeight = (short) Math.round(20 * height);
                  doIHaveStylesToApply = true;
               }
               catch (NumberFormatException e)
               {
                  if (DEBUG)
                     System.err.println(" caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_BORDER_COLOR.equals(property))
            {
               try
               {
                  myBorderBottomColor = myBorderLeftColor = myBorderRightColor =
                     myBorderTopColor = getColor(workbook, value);
                  if (myBorderBottomColor != null)
                     doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_FONT_CHARSET.equals(property))
            {
               try
               {
                  myFontCharset = FontCharset.valueOf(value);
                  doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_FONT_TYPE_OFFSET.equals(property))
            {
               try
               {
                  myFontTypeOffset = FontTypeOffset.valueOf(value);
                  doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_LOCKED.equals(property))
            {
               amILocked = Boolean.valueOf(value);
               doIHaveStylesToApply = true;
            }
            else if (PROPERTY_HIDDEN.equals(property))
            {
               amIHidden = Boolean.valueOf(value);
               doIHaveStylesToApply = true;
            }
            else if (PROPERTY_BORDER_BOTTOM.equals(property))
            {
               try
               {
                  myBorderBottomType = BorderType.valueOf(value);
                  doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_BORDER_LEFT.equals(property))
            {
               try
               {
                  myBorderLeftType = BorderType.valueOf(value);
                  doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_BORDER_RIGHT.equals(property))
            {
               try
               {
                  myBorderRightType = BorderType.valueOf(value);
                  doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_BORDER_TOP.equals(property))
            {
               try
               {
                  myBorderTopType = BorderType.valueOf(value);
                  doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_BOTTOM_BORDER_COLOR.equals(property))
            {
               try
               {
                  myBorderBottomColor = getColor(workbook, value);
                  if (myBorderBottomColor != null)
                     doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_LEFT_BORDER_COLOR.equals(property))
            {
               try
               {
                  myBorderLeftColor = getColor(workbook, value);
                  if (myBorderLeftColor != null)
                     doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_RIGHT_BORDER_COLOR.equals(property))
            {
               try
               {
                  myBorderRightColor = getColor(workbook, value);
                  if (myBorderRightColor != null)
                     doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
            else if (PROPERTY_TOP_BORDER_COLOR.equals(property))
            {
               try
               {
                  myBorderTopColor = getColor(workbook, value);
                  if (myBorderTopColor != null)
                     doIHaveStylesToApply = true;
               }
               catch (IllegalArgumentException e)
               {
                  if (DEBUG)
                     System.err.println("IllegalArgumentException caught: " + e.getMessage());
               }
            }
         }  // End null/empty check
      }  // End for loop on styles
   }

   /**
    * <p>Override the cells' current styles with any non-null style property
    * values.</p>
    * @return Whether the first <code>Cell</code> in the <code>Block</code>
    *    associated with this <code>Tag</code> was processed.
    */
   public boolean process()
   {
      TagContext context = getContext();
      Sheet sheet = context.getSheet();
      Workbook workbook = sheet.getWorkbook();
      Block block = context.getBlock();

      int left = block.getLeftColNum();
      int right = block.getRightColNum();
      int top = block.getTopRowNum();
      int bottom = block.getBottomRowNum();

      if (doIHaveStylesToApply)
      {
         // Loop through Rows and Cells, and apply the style to each one in
         // turn.
         for (int r = top; r <= bottom; r++)
         {
            Row row = sheet.getRow(r);
            if (row != null)
            {
               for (int c = left; c <= right; c++)
               {
                  Cell cell = row.getCell(c);
                  if (cell != null)
                  {
                     examineAndApplyStyle(workbook, cell);
                  }
               }
            }
         }
      }

      BlockTransformer transformer = new BlockTransformer();
      transformer.transform(context, getWorkbookContext());

      return true;
   }

   /**
    * Examine the given <code>Cell's</code> current <code>CellStyle</code>.  If
    * necessary, replace its <code>CellStyle</code> and/or <code>Font</code,
    * guided by the property values retrieved earlier from the "style"
    * attribute.
    * @param workbook The <code>Workbook</code> that maintains all
    *    <code>CellStyles</code> and <code>Fonts</code>.
    * @param cell The <code>Cell</code> to examine.
    */
   private void examineAndApplyStyle(Workbook workbook, Cell cell)
   {
      CellStyle cs = cell.getCellStyle();
      Font f = workbook.getFontAt(cs.getFontIndex());

      if (DEBUG)
         System.err.println("eAAS: cell at (" + cell.getRowIndex() + ", " + cell.getColumnIndex() + ")");

      short alignment = (myAlignment != null) ? myAlignment.getIndex() : cs.getAlignment();
      short borderBottom = (myBorderBottomType != null) ? myBorderBottomType.getIndex() : cs.getBorderBottom();
      short borderLeft = (myBorderLeftType != null) ? myBorderLeftType.getIndex() : cs.getBorderLeft();
      short borderRight = (myBorderRightType != null) ? myBorderRightType.getIndex() : cs.getBorderRight();
      short borderTop = (myBorderTopType != null) ? myBorderTopType.getIndex() : cs.getBorderTop();
      String dataFormat = (myDataFormat != null) ? myDataFormat : cs.getDataFormatString();
      Color fillBackgroundColor = (myFillBackgroundColor != null) ? myFillBackgroundColor : cs.getFillBackgroundColorColor();
      Color fillForegroundColor = (myFillForegroundColor != null) ? myFillForegroundColor : cs.getFillForegroundColorColor();
      short fillPattern = (myFillPatternType != null) ? myFillPatternType.getIndex() : cs.getFillPattern();
      boolean hidden = (amIHidden != null) ? amIHidden : cs.getHidden();
      short indention = (myIndention != null) ? myIndention : cs.getIndention();
      boolean locked = (amILocked != null) ? amILocked : cs.getLocked();
      short verticalAlignment = (myVerticalAlignment != null) ? myVerticalAlignment.getIndex() : cs.getVerticalAlignment();
      boolean wrapText = (amIWrappingText != null) ? amIWrappingText : cs.getWrapText();
      short fontBoldweight = (myFontBoldweight != null) ? myFontBoldweight.getIndex() : f.getBoldweight();
      int fontCharset = (myFontCharset != null) ? myFontCharset.getIndex() : f.getCharSet();
      short fontHeightInPoints = (myFontHeightInPoints != null) ? myFontHeightInPoints : f.getFontHeightInPoints();
      String fontName = (myFontName != null) ? myFontName : f.getFontName();
      boolean fontItalic = (amIFontItalic != null) ? amIFontItalic : f.getItalic();
      boolean fontStrikeout = (amIFontStrikeout != null) ? amIFontStrikeout : f.getStrikeout();
      short fontTypeOffset = (myFontTypeOffset != null) ? myFontTypeOffset.getIndex() : f.getTypeOffset();
      byte fontUnderline = (myFontUnderline != null) ? myFontUnderline.getIndex() : f.getUnderline();
      // Certain properties need a type of workbook check.
      short hssfBottomBorderColor = -1;
      short hssfLeftBorderColor = -1;
      short hssfRightBorderColor = -1;
      short hssfTopBorderColor = -1;
      short hssfFontColor = -1;
      XSSFColor xssfBottomBorderColor = null;
      XSSFColor xssfLeftBorderColor = null;
      XSSFColor xssfRightBorderColor = null;
      XSSFColor xssfTopBorderColor = null;
      XSSFColor xssfFontColor = null;
      short rotationDegrees;
      if (workbook instanceof HSSFWorkbook)
      {
         hssfBottomBorderColor = (myBorderBottomColor != null) ? ((HSSFColor) myBorderBottomColor).getIndex() : cs.getBottomBorderColor();
         hssfLeftBorderColor = (myBorderLeftColor != null) ? ((HSSFColor) myBorderLeftColor).getIndex() : cs.getLeftBorderColor();
         hssfRightBorderColor = (myBorderRightColor != null) ? ((HSSFColor) myBorderRightColor).getIndex() : cs.getRightBorderColor();
         hssfTopBorderColor = (myBorderTopColor != null) ? ((HSSFColor) myBorderTopColor).getIndex() : cs.getTopBorderColor();
         rotationDegrees = (myRotationDegrees != null) ? myRotationDegrees : cs.getRotation();
         hssfFontColor = (myFontColor != null) ? ((HSSFColor) myFontColor).getIndex() : f.getColor();
      }
      else
      {
         // XSSFWorkbook
         XSSFCellStyle xcs = (XSSFCellStyle) cs;
         xssfBottomBorderColor = (myBorderBottomColor != null) ? ((XSSFColor) myBorderBottomColor) : xcs.getBottomBorderXSSFColor();
         xssfLeftBorderColor = (myBorderLeftColor != null) ? ((XSSFColor) myBorderLeftColor) : xcs.getLeftBorderXSSFColor();
         xssfRightBorderColor = (myBorderRightColor != null) ? ((XSSFColor) myBorderRightColor) : xcs.getRightBorderXSSFColor();
         xssfTopBorderColor = (myBorderTopColor != null) ? ((XSSFColor) myBorderTopColor) : xcs.getTopBorderXSSFColor();
         // XSSF: Negative values don't make as much sense as in HSSF.
         // From 0-90, they coincide.
         // But HSSF -1  => XSSF 91 , HSSF -15 => XSSF 105,
         //     HSSF -90 => XSSF 180.
         rotationDegrees = (myRotationDegrees != null) ? myRotationDegrees : cs.getRotation();
         if (rotationDegrees < 0)
         {
            rotationDegrees = (short) (90 - rotationDegrees);
         }
         // As of Apache POI 3.8, there are Bugs 51236 and 52079 about font
         // color where somehow black and white get switched.  It appears to
         // have something to do with the fact that XSSFColor(byte[]) does
         // NOT call "correctRGB", but XSSFColor.setRgb(byte[]) DOES call
         // it, and so does XSSFColor.getRgb(byte[]).
         // The private method "correctRGB" flips black and white, but no
         // other colors.  However, correctRGB is its own inverse operation,
         // i.e. correctRGB(correctRGB(rgb)) yields the same bytes as rgb.
         // XSSFFont.setColor(XSSFColor) calls "getRGB", but
         // XSSFCellStyle.set[Xx]BorderColor and
         // XSSFCellStyle.setFill[Xx]Color do NOT.  So apply a correction
         // HERE, with the font color coming back from the existing Font.
         // Solution: Re-correct the font color on the way in by calling
         // "getRgb()", which internally calls "correctRGB".
         xssfFontColor = (myFontColor != null) ? ((XSSFColor) myFontColor) : new XSSFColor(((XSSFFont) f).getXSSFColor().getRgb());
         // End of fix for Bugs 51236 and 52079.
      }

      // Process row height/column width separately.
      if (myRowHeight != null)
      {
         cell.getRow().setHeight(myRowHeight);
      }
      if (myColumnWidth != null)
      {
         cell.getSheet().setColumnWidth(cell.getColumnIndex(), myColumnWidth);
      }

      // At this point, we have all of the desired CellStyle and Font
      // characteristics.  Find one if it exists.
      short numCellStyles = workbook.getNumCellStyles();
      short numFonts = workbook.getNumberOfFonts();
      CellStyle foundStyle = null;
      Font foundFont = null;
      boolean fontFoundWithCellStyle = false;
      // Find a matching CellStyle, hopefully with a matching Font.
      for (short i = 0; i < numCellStyles; i++)
      {
         CellStyle candidateStyle = workbook.getCellStyleAt(i);
         if (cellStyleMatches(candidateStyle, alignment, borderBottom, borderLeft,
            borderRight, borderTop, dataFormat, wrapText, fillBackgroundColor, fillForegroundColor,
            fillPattern, verticalAlignment, indention, rotationDegrees, hssfBottomBorderColor,
            hssfLeftBorderColor, hssfRightBorderColor, hssfTopBorderColor, xssfBottomBorderColor,
            xssfLeftBorderColor, xssfRightBorderColor, xssfTopBorderColor, locked, hidden))
         {
            foundStyle = candidateStyle;
            if (DEBUG)
               System.err.println("  Style found.");
            Font candidateFont = workbook.getFontAt(candidateStyle.getFontIndex());
            if (fontMatches(f, fontBoldweight, fontItalic, hssfFontColor, xssfFontColor, fontName,
               fontHeightInPoints, fontUnderline, fontStrikeout, fontCharset, fontTypeOffset))
            {
               foundFont = candidateFont;
               fontFoundWithCellStyle = true;
               if (DEBUG)
                  System.err.println("    Font found with style!");
               break;
            }
         }
      }

      // Find the Font if not already found.
      if (foundFont == null)
      {
         for (short i = 0; i < numFonts; i++)
         {
            Font candidateFont = workbook.getFontAt(i);
            if (fontMatches(f, fontBoldweight, fontItalic, hssfFontColor, xssfFontColor, fontName,
               fontHeightInPoints, fontUnderline, fontStrikeout, fontCharset, fontTypeOffset))
            {
               foundFont = candidateFont;
               if (DEBUG)
                  System.err.println("  Font found separately.");
               break;
            }
         }
      }
      // If Font still not found, then create it.
      if (foundFont == null)
      {
         foundFont = createFont(workbook, fontBoldweight, fontItalic, hssfFontColor, xssfFontColor, fontName,
            fontHeightInPoints, fontUnderline, fontStrikeout, fontCharset, fontTypeOffset);
         if (DEBUG)
            System.err.println("  Font created.");
      }

      // Set the CellStyle with the Font, creating the CellStyle if necessary.
      if (foundStyle != null)
      {
         if (fontFoundWithCellStyle)
         {
            // Both found together.
            cell.setCellStyle(foundStyle);
            if (DEBUG)
               System.err.println("  Setting existing CellStyle/Font!");
         }
         else
         {
            // Found both, but not together.
            // Tried "cloneStyleFrom", but even though the new CellStyle
            // contains the correct Font, Excel didn't display the new Font.
            // Just create a new CellStyle.
            //CellStyle newStyle = workbook.createCellStyle();
            //newStyle.cloneStyleFrom(foundStyle);
            CellStyle newStyle = createCellStyle(workbook, alignment, borderBottom, borderLeft,
               borderRight, borderTop, dataFormat, wrapText, fillBackgroundColor, fillForegroundColor,
               fillPattern, verticalAlignment, indention, rotationDegrees, hssfBottomBorderColor,
               hssfLeftBorderColor, hssfRightBorderColor, hssfTopBorderColor, xssfBottomBorderColor,
               xssfLeftBorderColor, xssfRightBorderColor, xssfTopBorderColor, locked, hidden);

            newStyle.setFont(foundFont);
            cell.setCellStyle(newStyle);
            CellStyle retrievedStyle = cell.getCellStyle();
            if (DEBUG)
            {
               System.err.println("  foundStyle.font: " + workbook.getFontAt(foundStyle.getFontIndex()));
               System.err.println("  foundFont: " + foundFont);
               System.err.println("  newStyle.font: " + workbook.getFontAt(newStyle.getFontIndex()));
               System.err.println("  retrievedStyle.font: " + workbook.getFontAt(retrievedStyle.getFontIndex()));
               System.err.println("  Setting cloned style with Font.");
            }
         }
      }
      else
      {
         // Create the CellStyle, using the existing font.
         foundStyle = createCellStyle(workbook, alignment, borderBottom, borderLeft,
            borderRight, borderTop, dataFormat, wrapText, fillBackgroundColor, fillForegroundColor,
            fillPattern, verticalAlignment, indention, rotationDegrees, hssfBottomBorderColor,
            hssfLeftBorderColor, hssfRightBorderColor, hssfTopBorderColor, xssfBottomBorderColor,
            xssfLeftBorderColor, xssfRightBorderColor, xssfTopBorderColor, locked, hidden);
         foundStyle.setFont(foundFont);
         cell.setCellStyle(foundStyle);
         if (DEBUG)
            System.err.println("  Created and set new style.");
      }
   }

   /**
    * Helper method to determine if a <code>CellStyle</code> matches the given
    * attributes.
    * @param cs A <code>CellStyle</code>.
    * @param alignment A <code>short</code> alignment constant.
    * @param borderBottom A <code>short</code> border type constant.
    * @param borderLeft A <code>short</code> border type constant.
    * @param borderRight A <code>short</code> border type constant.
    * @param borderTop A <code>short</code> border type constant.
    * @param dataFormat A data format string.
    * @param wrapText Whether text is wrapped.
    * @param fillBackgroundColor A background <code>Color</code>.
    * @param fillForegroundColor A foreground <code>Color</code>.
    * @param fillPattern A <code>short</code> pattern constant.
    * @param verticalAlignment A <code>short</code> vertical alignment constant.
    * @param indention A <code>short</code> number of indent characters.
    * @param rotationDegrees A <code>short</code> degrees rotation of text.
    * @param hssfBottomBorderColor A border <code>short</code> index.
    * @param hssfLeftBorderColor A border <code>short</code> index.
    * @param hssfRightBorderColor A border <code>short</code> index.
    * @param hssfTopBorderColor A border <code>short</code> index.
    * @param xssfBottomBorderColor A border <code>XSSFColor</code>.
    * @param xssfLeftBorderColor A border <code>XSSFColor</code>.
    * @param xssfRightBorderColor A border <code>XSSFColor</code>.
    * @param xssfTopBorderColor A border <code>XSSFColor</code>.
    * @param locked Whether the cell is locked.
    * @param hidden Whether the cell is hidden.
    * @return <code>true</code> if the given <code>CellStyle</code> matches all
    *    attributes, <code>false</code> if it doesn't match at least one
    *    attribute.
    */
   private boolean cellStyleMatches(CellStyle cs, short alignment, short borderBottom, short borderLeft,
      short borderRight, short borderTop, String dataFormat, boolean wrapText, Color fillBackgroundColor,
      Color fillForegroundColor, short fillPattern, short verticalAlignment, short indention,
      short rotationDegrees, short hssfBottomBorderColor, short hssfLeftBorderColor, short hssfRightBorderColor,
      short hssfTopBorderColor, XSSFColor xssfBottomBorderColor, XSSFColor xssfLeftBorderColor,
      XSSFColor xssfRightBorderColor, XSSFColor xssfTopBorderColor, boolean locked, boolean hidden)
   {
      if (cs instanceof HSSFCellStyle)
      {
         HSSFCellStyle hcs = (HSSFCellStyle) cs;
         return (hcs.getAlignment() == alignment &&
            hcs.getBorderBottom() == borderBottom &&
            hcs.getBorderLeft() == borderLeft &&
            hcs.getBorderRight() == borderRight &&
            hcs.getBorderTop() == borderTop &&
            hcs.getDataFormatString().equals(dataFormat) &&
            hcs.getWrapText() == wrapText &&
            hcs.getFillBackgroundColor() == ((HSSFColor) fillBackgroundColor).getIndex() &&
            hcs.getFillForegroundColor() == ((HSSFColor) fillForegroundColor).getIndex() &&
            hcs.getFillPattern() == fillPattern &&
            hcs.getVerticalAlignment() == verticalAlignment &&
            hcs.getIndention() == indention &&
            hcs.getRotation() == rotationDegrees &&
            hcs.getBottomBorderColor() == hssfBottomBorderColor &&
            hcs.getLeftBorderColor() == hssfLeftBorderColor &&
            hcs.getRightBorderColor() == hssfRightBorderColor &&
            hcs.getTopBorderColor() == hssfTopBorderColor &&
            hcs.getLocked() == locked &&
            hcs.getHidden() == hidden);
      }
      else
      {
         // XSSF
         XSSFCellStyle xcs = (XSSFCellStyle) cs;
         return (xcs.getAlignment() == alignment &&
            xcs.getBorderBottom() == borderBottom &&
            xcs.getBorderLeft() == borderLeft &&
            xcs.getBorderRight() == borderRight &&
            xcs.getBorderTop() == borderTop &&
            xcs.getDataFormatString().equals(dataFormat) &&
            xcs.getWrapText() == wrapText &&
            ((xcs.getFillBackgroundXSSFColor() == null && fillBackgroundColor == null) ||
             (xcs.getFillBackgroundXSSFColor() != null && xcs.getFillBackgroundXSSFColor().equals(fillBackgroundColor))) &&
            ((xcs.getFillForegroundXSSFColor() == null && fillForegroundColor == null) ||
             (xcs.getFillForegroundXSSFColor() != null && xcs.getFillForegroundXSSFColor().equals(fillForegroundColor))) &&
            xcs.getFillPattern() == fillPattern &&
            xcs.getVerticalAlignment() == verticalAlignment &&
            xcs.getIndention() == indention &&
            xcs.getRotation() == rotationDegrees &&
            ((xcs.getBottomBorderXSSFColor() == null && xssfBottomBorderColor == null) ||
             (xcs.getBottomBorderXSSFColor() != null && xcs.getBottomBorderXSSFColor().equals(xssfBottomBorderColor))) &&
            ((xcs.getLeftBorderXSSFColor() == null && xssfLeftBorderColor == null) ||
             (xcs.getLeftBorderXSSFColor() != null && xcs.getLeftBorderXSSFColor().equals(xssfLeftBorderColor))) &&
            ((xcs.getRightBorderXSSFColor() == null && xssfRightBorderColor == null) ||
             (xcs.getRightBorderXSSFColor() != null && xcs.getRightBorderXSSFColor().equals(xssfRightBorderColor))) &&
            ((xcs.getTopBorderXSSFColor() == null && xssfTopBorderColor == null) ||
             (xcs.getTopBorderXSSFColor() != null && xcs.getTopBorderXSSFColor().equals(xssfTopBorderColor))) &&
            xcs.getLocked() == locked &&
            xcs.getHidden() == hidden);
      }
   }

   /**
    * Helper method to determine if a <code>Font</code> matches the given
    * attributes.
    * @param f A <code>Font</code>.
    * @param fontBoldweight A <code>short</code> boldweight constant.
    * @param fontItalic Whether the text is italic.
    * @param hssfFontColor A color <code>short</code> index.
    * @param xssfFontColor A color <code>XSSFColor</code>.
    * @param fontName A font name.
    * @param fontHeightInPoints A <code>short</code> font height in points.
    * @param fontUnderline A <code>byte</code> underline constant.
    * @param fontStrikeout Whether the font is strikeout.
    * @param fontCharset An <code>int</code> charset constant.
    * @param fontTypeOffset A <code>short</code> type offset constant.
    * @return <code>true</code> if the given <code>Font</code> matches all
    *    attributes, <code>false</code> if it doesn't match at least one
    *    attribute.
    */
   private boolean fontMatches(Font f, short fontBoldweight, boolean fontItalic, short hssfFontColor,
      XSSFColor xssfFontColor, String fontName, short fontHeightInPoints, byte fontUnderline,
      boolean fontStrikeout, int fontCharset, short fontTypeOffset)
   {
      return (f.getBoldweight() == fontBoldweight &&
              f.getItalic() == fontItalic &&
              ((f instanceof HSSFFont && f.getColor() == hssfFontColor) ||
               (f instanceof XSSFFont && ((XSSFFont)f).getXSSFColor().equals(xssfFontColor))) &&
              f.getFontName().equals(fontName) &&
              f.getFontHeightInPoints() == fontHeightInPoints &&
              f.getUnderline() == fontUnderline &&
              f.getStrikeout() == fontStrikeout &&
              f.getCharSet() == fontCharset &&
              f.getTypeOffset() == fontTypeOffset
            );
   }

   /**
    * Creates a new <code>CellStyle</code> for the given <code>Workbook</code>,
    * with the given attributes.
    * @param workbook A <code>Workbook</code>.
    * @param alignment A <code>short</code> alignment constant.
    * @param borderBottom A <code>short</code> border type constant.
    * @param borderLeft A <code>short</code> border type constant.
    * @param borderRight A <code>short</code> border type constant.
    * @param borderTop A <code>short</code> border type constant.
    * @param dataFormat A data format string.
    * @param wrapText Whether text is wrapped.
    * @param fillBackgroundColor A background <code>Color</code>.
    * @param fillForegroundColor A foreground <code>Color</code>.
    * @param fillPattern A <code>short</code> pattern constant.
    * @param verticalAlignment A <code>short</code> vertical alignment constant.
    * @param indention A <code>short</code> number of indent characters.
    * @param rotationDegrees A <code>short</code> degrees rotation of text.
    * @param hssfBottomBorderColor A border <code>short</code> index.
    * @param hssfLeftBorderColor A border <code>short</code> index.
    * @param hssfRightBorderColor A border <code>short</code> index.
    * @param hssfTopBorderColor A border <code>short</code> index.
    * @param xssfBottomBorderColor A border <code>XSSFColor</code>.
    * @param xssfLeftBorderColor A border <code>XSSFColor</code>.
    * @param xssfRightBorderColor A border <code>XSSFColor</code>.
    * @param xssfTopBorderColor A border <code>XSSFColor</code>.
    * @param locked Whether the cell is locked.
    * @param hidden Whether the cell is hidden.
    * @return A new <code>CellStyle</code>.
    */
   private CellStyle createCellStyle(Workbook workbook, short alignment, short borderBottom, short borderLeft,
      short borderRight, short borderTop, String dataFormat, boolean wrapText, Color fillBackgroundColor,
      Color fillForegroundColor, short fillPattern, short verticalAlignment, short indention,
      short rotationDegrees, short hssfBottomBorderColor, short hssfLeftBorderColor, short hssfRightBorderColor,
      short hssfTopBorderColor, XSSFColor xssfBottomBorderColor, XSSFColor xssfLeftBorderColor,
      XSSFColor xssfRightBorderColor, XSSFColor xssfTopBorderColor, boolean locked, boolean hidden)
   {
      CellStyle cs = workbook.createCellStyle();
      cs.setAlignment(alignment);
      cs.setBorderBottom(borderBottom);
      cs.setBorderLeft(borderLeft);
      cs.setBorderRight(borderRight);
      cs.setBorderTop(borderTop);
      cs.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(dataFormat));
      cs.setHidden(hidden);
      cs.setIndention(indention);
      cs.setLocked(locked);
      cs.setRotation(rotationDegrees);
      cs.setVerticalAlignment(verticalAlignment);
      cs.setWrapText(wrapText);
      // Certain properties need a type of workbook check.
      if (workbook instanceof HSSFWorkbook)
      {
         cs.setBottomBorderColor(hssfBottomBorderColor);
         cs.setLeftBorderColor(hssfLeftBorderColor);
         cs.setRightBorderColor(hssfRightBorderColor);
         cs.setTopBorderColor(hssfTopBorderColor);
         // Per POI Javadocs, set foreground color first!
         cs.setFillForegroundColor(((HSSFColor) fillForegroundColor).getIndex());
         cs.setFillBackgroundColor(((HSSFColor) fillBackgroundColor).getIndex());
      }
      else
      {
         // XSSFWorkbook
         XSSFCellStyle xcs = (XSSFCellStyle) cs;
         if (xssfBottomBorderColor != null)
            xcs.setBottomBorderColor(xssfBottomBorderColor);
         if (xssfLeftBorderColor != null)
            xcs.setLeftBorderColor(xssfLeftBorderColor);
         if (xssfRightBorderColor != null)
            xcs.setRightBorderColor(xssfRightBorderColor);
         if (xssfTopBorderColor != null)
            xcs.setTopBorderColor(xssfTopBorderColor);
         // Per POI Javadocs, set foreground color first!
         if (fillForegroundColor != null)
            xcs.setFillForegroundColor((XSSFColor) fillForegroundColor);
         if (fillBackgroundColor != null)
            xcs.setFillBackgroundColor((XSSFColor) fillBackgroundColor);
      }
      cs.setFillPattern(fillPattern);
      return cs;
   }

   /**
    * Creates a new <code>Font</code> for the given <code>Workbook</code>,
    * with the given attributes.
    * @param workbook A <code>Workbook</code>.
    * @param fontBoldweight A <code>short</code> boldweight constant.
    * @param fontItalic Whether the text is italic.
    * @param hssfFontColor A color <code>short</code> index.
    * @param xssfFontColor A color <code>XSSFColor</code>.
    * @param fontName A font name.
    * @param fontHeightInPoints A <code>short</code> font height in points.
    * @param fontUnderline A <code>byte</code> underline constant.
    * @param fontStrikeout Whether the font is strikeout.
    * @param fontCharset An <code>int</code> charset constant.
    * @param fontTypeOffset A <code>short</code> type offset constant.
    * @return A new <code>Font</code>.
    */
   private Font createFont(Workbook workbook, short fontBoldweight, boolean fontItalic, short hssfFontColor,
      XSSFColor xssfFontColor, String fontName, short fontHeightInPoints, byte fontUnderline,
      boolean fontStrikeout, int fontCharset, short fontTypeOffset)
   {
      if (DEBUG)
      {
         System.err.println("createFont: " + fontBoldweight + "," + fontItalic + "," + hssfFontColor + "," +
            ((xssfFontColor == null) ? "null" : xssfFontColor.getCTColor().toString()) + "," + fontName + "," +
            fontHeightInPoints + "," + fontUnderline + "," + fontStrikeout + "," + fontCharset + "," + fontTypeOffset);
      }
      Font f = workbook.createFont();
      f.setBoldweight(fontBoldweight);
      f.setItalic(fontItalic);
      f.setFontName(fontName);
      f.setFontHeightInPoints(fontHeightInPoints);
      f.setUnderline(fontUnderline);
      f.setStrikeout(fontStrikeout);
      f.setCharSet(fontCharset);
      f.setTypeOffset(fontTypeOffset);
      // Certain properties need a type of workbook check.
      if (workbook instanceof HSSFWorkbook)
      {
         f.setColor(hssfFontColor);
      }
      else
      {
         // XSSFWorkbook
         XSSFFont xf = (XSSFFont) f;
         if (xssfFontColor != null)
         {
            // As of Apache POI 3.8, there are Bugs 51236 and 52079 about font
            // color where somehow black and white get switched.  It appears to
            // have something to do with the fact that XSSFColor(byte[]) does
            // NOT call "correctRGB", but XSSFColor.setRgb(byte[]) DOES call
            // it, and so does XSSFColor.getRgb(byte[]).
            // The private method "correctRGB" flips black and white, but no
            // other colors.  However, correctRGB is its own inverse operation,
            // i.e. correctRGB(correctRGB(rgb)) yields the same bytes as rgb.
            // XSSFFont.setColor(XSSFColor) calls "getRGB", but
            // XSSFCellStyle.set[Xx]BorderColor and
            // XSSFCellStyle.setFill[Xx]Color do NOT.  So apply a correction
            // HERE, with the font color.
            // Solution: Re-correct the font color on the way in.
            XSSFColor fixedXssfColor = new XSSFColor(xssfFontColor.getRgb());
            xf.setColor(fixedXssfColor);
            // End of workaround for Bugs 51236 and  52079.
         }
      }

      return f;
   }

   /**
    * Determines the proper POI <code>Color</code>, given a string value that
    * could be a color name, e.g. "aqua", or a hex string, e.g. "#FFCCCC".
    *
    * @param workbook A <code>Workbook</code>, used only to determine whether
    *    to create an <code>HSSFColor</code> or an <code>XSSFColor</code>.
    * @param value The color value, which could be one of the 48 pre-defined
    *    color names, or a hex value of the format "#RRGGBB".
    * @return A <code>Color</code>, or <code>null</code> if an invalid color
    *    name was given.
    */
   private Color getColor(Workbook workbook, String value)
   {
      if (DEBUG)
         System.err.println("getColor: " + value);
      Color color = null;
      if (workbook instanceof HSSFWorkbook)
      {
         // Create an HSSFColor.
         if (value.startsWith("#"))
         {
            ExcelColor best = ExcelColor.AUTOMATIC;
            int minDist = 255 * 3;
            String strRed = value.substring(1, 3);
            String strGreen = value.substring(3, 5);
            String strBlue = value.substring(5, 7);
            int red   = Integer.parseInt(strRed, 16);
            int green = Integer.parseInt(strGreen, 16);
            int blue  = Integer.parseInt(strBlue, 16);
            // Hex value.  Find the closest defined color.
            for (ExcelColor excelColor : ExcelColor.values())
            {
               int dist = excelColor.distance(red, green, blue);
               if (dist < minDist)
               {
                  best = excelColor;
                  minDist = dist;
               }
            }
            color = best.getHssfColor();
            if (DEBUG)
               System.err.println("  Best HSSFColor found: " + color);
         }
         else
         {
            // Treat it as a color name.
            try
            {
               ExcelColor excelColor = ExcelColor.valueOf(value);
               if (excelColor != null)
                  color = excelColor.getHssfColor();
               if (DEBUG)
                  System.err.println("  HSSFColor name matched: " + value);
            }
            catch (IllegalArgumentException e)
            {
               if (DEBUG)
                  System.err.println("  HSSFColor name not matched: " + e.toString());
            }
         }
      }
      else // XSSFWorkbook
      {
         // Create an XSSFColor.
         if (value.startsWith("#") && value.length() == 7)
         {
            // Create the corresponding XSSFColor.
            color = new XSSFColor(new byte[] {
               Integer.valueOf(value.substring(1, 3), 16).byteValue(),
               Integer.valueOf(value.substring(3, 5), 16).byteValue(),
               Integer.valueOf(value.substring(5, 7), 16).byteValue()
            });
            if (DEBUG)
               System.err.println("  XSSFColor created: " + color);
         }
         else
         {
            // Create an XSSFColor from the RGB values of the desired color.
            try
            {
               ExcelColor excelColor = ExcelColor.valueOf(value);
               if (excelColor != null)
               {
                  color = new XSSFColor(new byte[]
                     {(byte) excelColor.getRed(), (byte) excelColor.getGreen(), (byte) excelColor.getBlue()}
                  );
               }
               if (DEBUG)
                  System.err.println("  XSSFColor name matched: " + value);
            }
            catch (IllegalArgumentException e)
            {
               if (DEBUG)
                  System.err.println("  XSSFColor name not matched: " + e.toString());
            }
         }
      }
      return color;
   }
}

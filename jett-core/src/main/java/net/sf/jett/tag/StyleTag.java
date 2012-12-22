package net.sf.jett.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import net.sf.jett.model.CellStyleCache;
import net.sf.jett.model.ExcelColor;
import net.sf.jett.model.FillPattern;
import net.sf.jett.model.FontBoldweight;
import net.sf.jett.model.FontCharset;
import net.sf.jett.model.FontTypeOffset;
import net.sf.jett.model.FontUnderline;
import net.sf.jett.model.VerticalAlignment;
import net.sf.jett.model.WorkbookContext;
import net.sf.jett.model.FontCache;
import net.sf.jett.transform.BlockTransformer;
import net.sf.jett.util.AttributeUtil;
import net.sf.jett.util.SheetUtil;

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
      WorkbookContext wc = getWorkbookContext();
      CellStyleCache csCache = wc.getCellStyleCache();
      FontCache fCache = wc.getFontCache();

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
      Color bottomBorderColor = null;
      Color leftBorderColor = null;
      Color rightBorderColor = null;
      Color topBorderColor = null;
      Color fontColor;
      short rotationDegrees;
      if (workbook instanceof HSSFWorkbook)
      {
         short hssfBottomBorderColor = (myBorderBottomColor != null) ? ((HSSFColor) myBorderBottomColor).getIndex() : cs.getBottomBorderColor();
         short hssfLeftBorderColor = (myBorderLeftColor != null) ? ((HSSFColor) myBorderLeftColor).getIndex() : cs.getLeftBorderColor();
         short hssfRightBorderColor = (myBorderRightColor != null) ? ((HSSFColor) myBorderRightColor).getIndex() : cs.getRightBorderColor();
         short hssfTopBorderColor = (myBorderTopColor != null) ? ((HSSFColor) myBorderTopColor).getIndex() : cs.getTopBorderColor();
         short hssfFontColor = (myFontColor != null) ? ((HSSFColor) myFontColor).getIndex() : f.getColor();
         if (hssfBottomBorderColor != 0)
            bottomBorderColor = ExcelColor.getHssfColorByIndex(hssfBottomBorderColor);
         if (hssfLeftBorderColor != 0)
            leftBorderColor = ExcelColor.getHssfColorByIndex(hssfLeftBorderColor);
         if (hssfRightBorderColor != 0)
            rightBorderColor = ExcelColor.getHssfColorByIndex(hssfRightBorderColor);
         if (hssfTopBorderColor != 0)
            topBorderColor = ExcelColor.getHssfColorByIndex(hssfTopBorderColor);
         fontColor = ExcelColor.getHssfColorByIndex(hssfFontColor);

         rotationDegrees = (myRotationDegrees != null) ? myRotationDegrees : cs.getRotation();
      }
      else
      {
         // XSSFWorkbook
         XSSFCellStyle xcs = (XSSFCellStyle) cs;
         bottomBorderColor = (myBorderBottomColor != null) ? ((XSSFColor) myBorderBottomColor) : xcs.getBottomBorderXSSFColor();
         leftBorderColor = (myBorderLeftColor != null) ? ((XSSFColor) myBorderLeftColor) : xcs.getLeftBorderXSSFColor();
         rightBorderColor = (myBorderRightColor != null) ? ((XSSFColor) myBorderRightColor) : xcs.getRightBorderXSSFColor();
         topBorderColor = (myBorderTopColor != null) ? ((XSSFColor) myBorderTopColor) : xcs.getTopBorderXSSFColor();
         fontColor = (myFontColor != null) ? ((XSSFColor) myFontColor) : ((XSSFFont) f).getXSSFColor();

         // XSSF: Negative rotation values don't make as much sense as in HSSF.
         // From 0-90, they coincide.
         // But HSSF -1  => XSSF 91 , HSSF -15 => XSSF 105,
         //     HSSF -90 => XSSF 180.
         rotationDegrees = (myRotationDegrees != null) ? myRotationDegrees : cs.getRotation();
         if (rotationDegrees < 0)
         {
            rotationDegrees = (short) (90 - rotationDegrees);
         }
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
      // characteristics.  Find a CellStyle if it exists.
      CellStyle foundStyle = csCache.retrieveCellStyle(fontBoldweight, fontItalic, fontColor, fontName,
         fontHeightInPoints, alignment, borderBottom, borderLeft, borderRight, borderTop, dataFormat, fontUnderline,
         fontStrikeout, wrapText, fillBackgroundColor, fillForegroundColor, fillPattern, verticalAlignment, indention,
         rotationDegrees, bottomBorderColor, leftBorderColor, rightBorderColor, topBorderColor, fontCharset,
         fontTypeOffset, locked, hidden);

      // Find the Font if not already found.
      if (foundStyle == null)
      {
         //short numFonts = workbook.getNumberOfFonts();
         //long start = System.nanoTime();
         Font foundFont = fCache.retrieveFont(fontBoldweight, fontItalic, fontColor, fontName,
               fontHeightInPoints, fontUnderline, fontStrikeout, fontCharset, fontTypeOffset);
         //long end = System.nanoTime();
         //System.err.println("Find Font: " + (end - start) + " ns");

         // If Font still not found, then create it.
         if (foundFont == null)
         {
            //start = System.nanoTime();
            foundFont = SheetUtil.createFont(workbook, fontBoldweight, fontItalic, fontColor, fontName,
               fontHeightInPoints, fontUnderline, fontStrikeout, fontCharset, fontTypeOffset);
            //end = System.nanoTime();
            //System.err.println("Create Font: " + (end - start) + " ns");
            fCache.cacheFont(foundFont);
            if (DEBUG)
               System.err.println("  Font created.");
         }

         // Create the new CellStyle.
         //start = System.nanoTime();
         foundStyle = SheetUtil.createCellStyle(workbook, alignment, borderBottom, borderLeft,
            borderRight, borderTop, dataFormat, wrapText, fillBackgroundColor, fillForegroundColor,
            fillPattern, verticalAlignment, indention, rotationDegrees, bottomBorderColor,
            leftBorderColor, rightBorderColor, topBorderColor, locked, hidden);
         foundStyle.setFont(foundFont);
         //end = System.nanoTime();
         //System.err.println("Create CS: " + (end - start) + " ns");

         csCache.cacheCellStyle(foundStyle);
         if (DEBUG)
            System.err.println("  Created new style.");
      }

      cell.setCellStyle(foundStyle);
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

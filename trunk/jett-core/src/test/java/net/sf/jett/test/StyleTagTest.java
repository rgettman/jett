package net.sf.jett.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FontCharset;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jett.model.Alignment;
import net.sf.jett.model.BorderType;
import net.sf.jett.model.ExcelColor;
import net.sf.jett.model.FillPattern;
import net.sf.jett.model.FontBoldweight;
import net.sf.jett.model.FontTypeOffset;
import net.sf.jett.parser.StyleParser;
import net.sf.jett.util.SheetUtil;
import net.sf.jett.transform.ExcelTransformer;

/**
 * This JUnit Test class tests the evaluation of the "style" tag (always
 * with a body).
 *
 * @author Randy Gettman
 * @since 0.4.0
 */
public class StyleTagTest extends TestCase
{
   /**
    * Tests the .xls template spreadsheet.
    * @throws java.io.IOException If an I/O error occurs.
    * @throws org.apache.poi.openxml4j.exceptions.InvalidFormatException If the input spreadsheet is invalid.
    */
   @Test
   public void testXls() throws IOException, InvalidFormatException
   {
      super.testXls();
   }

   /**
    * Tests the .xlsx template spreadsheet.
    * @throws java.io.IOException If an I/O error occurs.
    * @throws org.apache.poi.openxml4j.exceptions.InvalidFormatException If the input spreadsheet is invalid.
    */
   @Test
   public void testXlsx() throws IOException, InvalidFormatException
   {
      super.testXlsx();
   }

   /**
    * Returns the Excel name base for the template and resultant spreadsheets
    * for this test.
    * @return The Excel name base for this test.
    */
   protected String getExcelNameBase()
   {
      return "StyleTag";
   }

   /**
    * Call certain setup-related methods on the <code>ExcelTransformer</code>
    * before template sheet transformation.
    * @param transformer The <code>ExcelTransformer</code> that will transform
    *    the template worksheet(s).
    */
   protected void setupTransformer(ExcelTransformer transformer)
   {
      try
      {
         transformer.addCssFile("templates/StyleTagStyleSheet1.css");
      }
      catch (IOException e)
      {
         fail("IOException caught reading style sheet: " + e.getMessage());
      }
   }

   /**
    * Validate the newly created resultant <code>Workbook</code> with JUnit
    * assertions.
    * @param workbook A <code>Workbook</code>.
    */
   protected void check(Workbook workbook)
   {
      // Alignments
      Sheet alignment = workbook.getSheetAt(0);
      assertEquals(HorizontalAlignment.CENTER.ordinal(), TestUtility.getCellStyle(alignment, 0, 1).getAlignment());
      assertEquals(HorizontalAlignment.CENTER.ordinal(), TestUtility.getCellStyle(alignment, 0, 3).getAlignment());
      assertEquals(HorizontalAlignment.CENTER_SELECTION.ordinal(), TestUtility.getCellStyle(alignment, 1, 1).getAlignment());
      assertEquals(HorizontalAlignment.CENTER_SELECTION.ordinal(), TestUtility.getCellStyle(alignment, 1, 3).getAlignment());
      assertEquals(HorizontalAlignment.DISTRIBUTED.ordinal(), TestUtility.getCellStyle(alignment, 2, 1).getAlignment());
      assertEquals(HorizontalAlignment.DISTRIBUTED.ordinal(), TestUtility.getCellStyle(alignment, 2, 3).getAlignment());
      assertEquals(HorizontalAlignment.FILL.ordinal(), TestUtility.getCellStyle(alignment, 3, 1).getAlignment());
      assertEquals(HorizontalAlignment.FILL.ordinal(), TestUtility.getCellStyle(alignment, 3, 3).getAlignment());
      assertEquals(HorizontalAlignment.GENERAL.ordinal(), TestUtility.getCellStyle(alignment, 4, 1).getAlignment());
      assertEquals(HorizontalAlignment.GENERAL.ordinal(), TestUtility.getCellStyle(alignment, 4, 3).getAlignment());
      assertEquals(HorizontalAlignment.JUSTIFY.ordinal(), TestUtility.getCellStyle(alignment, 5, 1).getAlignment());
      assertEquals(HorizontalAlignment.JUSTIFY.ordinal(), TestUtility.getCellStyle(alignment, 5, 3).getAlignment());
      assertEquals(HorizontalAlignment.LEFT.ordinal(), TestUtility.getCellStyle(alignment, 6, 1).getAlignment());
      assertEquals(HorizontalAlignment.LEFT.ordinal(), TestUtility.getCellStyle(alignment, 6, 3).getAlignment());
      assertEquals(HorizontalAlignment.RIGHT.ordinal(), TestUtility.getCellStyle(alignment, 7, 1).getAlignment());
      assertEquals(HorizontalAlignment.RIGHT.ordinal(), TestUtility.getCellStyle(alignment, 7, 3).getAlignment());

      // Border Types
      Sheet border = workbook.getSheetAt(1);
      assertEquals(BorderStyle.NONE.ordinal(), TestUtility.getCellStyle(border, 1, 1).getBorderBottom());
      assertEquals(BorderStyle.NONE.ordinal(), TestUtility.getCellStyle(border, 1, 1).getBorderLeft());
      assertEquals(BorderStyle.NONE.ordinal(), TestUtility.getCellStyle(border, 1, 1).getBorderRight());
      assertEquals(BorderStyle.NONE.ordinal(), TestUtility.getCellStyle(border, 1, 1).getBorderTop());
      assertEquals(BorderStyle.NONE.ordinal(), TestUtility.getCellStyle(border, 1, 3).getBorderBottom());
      assertEquals(BorderStyle.NONE.ordinal(), TestUtility.getCellStyle(border, 1, 5).getBorderLeft());
      assertEquals(BorderStyle.NONE.ordinal(), TestUtility.getCellStyle(border, 1, 7).getBorderRight());
      assertEquals(BorderStyle.NONE.ordinal(), TestUtility.getCellStyle(border, 1, 9).getBorderTop());

      assertEquals(BorderStyle.THIN.ordinal(), TestUtility.getCellStyle(border, 3, 1).getBorderBottom());
      assertEquals(BorderStyle.THIN.ordinal(), TestUtility.getCellStyle(border, 3, 1).getBorderLeft());
      assertEquals(BorderStyle.THIN.ordinal(), TestUtility.getCellStyle(border, 3, 1).getBorderRight());
      assertEquals(BorderStyle.THIN.ordinal(), TestUtility.getCellStyle(border, 3, 1).getBorderTop());
      assertEquals(BorderStyle.THIN.ordinal(), TestUtility.getCellStyle(border, 3, 3).getBorderBottom());
      assertEquals(BorderStyle.THIN.ordinal(), TestUtility.getCellStyle(border, 3, 5).getBorderLeft());
      assertEquals(BorderStyle.THIN.ordinal(), TestUtility.getCellStyle(border, 3, 7).getBorderRight());
      assertEquals(BorderStyle.THIN.ordinal(), TestUtility.getCellStyle(border, 3, 9).getBorderTop());

      assertEquals(BorderStyle.MEDIUM.ordinal(), TestUtility.getCellStyle(border, 5, 1).getBorderBottom());
      assertEquals(BorderStyle.MEDIUM.ordinal(), TestUtility.getCellStyle(border, 5, 1).getBorderLeft());
      assertEquals(BorderStyle.MEDIUM.ordinal(), TestUtility.getCellStyle(border, 5, 1).getBorderRight());
      assertEquals(BorderStyle.MEDIUM.ordinal(), TestUtility.getCellStyle(border, 5, 1).getBorderTop());
      assertEquals(BorderStyle.MEDIUM.ordinal(), TestUtility.getCellStyle(border, 5, 3).getBorderBottom());
      assertEquals(BorderStyle.MEDIUM.ordinal(), TestUtility.getCellStyle(border, 5, 5).getBorderLeft());
      assertEquals(BorderStyle.MEDIUM.ordinal(), TestUtility.getCellStyle(border, 5, 7).getBorderRight());
      assertEquals(BorderStyle.MEDIUM.ordinal(), TestUtility.getCellStyle(border, 5, 9).getBorderTop());

      assertEquals(BorderStyle.DASHED.ordinal(), TestUtility.getCellStyle(border, 7, 1).getBorderBottom());
      assertEquals(BorderStyle.DASHED.ordinal(), TestUtility.getCellStyle(border, 7, 1).getBorderLeft());
      assertEquals(BorderStyle.DASHED.ordinal(), TestUtility.getCellStyle(border, 7, 1).getBorderRight());
      assertEquals(BorderStyle.DASHED.ordinal(), TestUtility.getCellStyle(border, 7, 1).getBorderTop());
      assertEquals(BorderStyle.DASHED.ordinal(), TestUtility.getCellStyle(border, 7, 3).getBorderBottom());
      assertEquals(BorderStyle.DASHED.ordinal(), TestUtility.getCellStyle(border, 7, 5).getBorderLeft());
      assertEquals(BorderStyle.DASHED.ordinal(), TestUtility.getCellStyle(border, 7, 7).getBorderRight());
      assertEquals(BorderStyle.DASHED.ordinal(), TestUtility.getCellStyle(border, 7, 9).getBorderTop());

      assertEquals(BorderStyle.HAIR.ordinal(), TestUtility.getCellStyle(border, 9, 1).getBorderBottom());
      assertEquals(BorderStyle.HAIR.ordinal(), TestUtility.getCellStyle(border, 9, 1).getBorderLeft());
      assertEquals(BorderStyle.HAIR.ordinal(), TestUtility.getCellStyle(border, 9, 1).getBorderRight());
      assertEquals(BorderStyle.HAIR.ordinal(), TestUtility.getCellStyle(border, 9, 1).getBorderTop());
      assertEquals(BorderStyle.HAIR.ordinal(), TestUtility.getCellStyle(border, 9, 3).getBorderBottom());
      assertEquals(BorderStyle.HAIR.ordinal(), TestUtility.getCellStyle(border, 9, 5).getBorderLeft());
      assertEquals(BorderStyle.HAIR.ordinal(), TestUtility.getCellStyle(border, 9, 7).getBorderRight());
      assertEquals(BorderStyle.HAIR.ordinal(), TestUtility.getCellStyle(border, 9, 9).getBorderTop());

      assertEquals(BorderStyle.THICK.ordinal(), TestUtility.getCellStyle(border, 11, 1).getBorderBottom());
      assertEquals(BorderStyle.THICK.ordinal(), TestUtility.getCellStyle(border, 11, 1).getBorderLeft());
      assertEquals(BorderStyle.THICK.ordinal(), TestUtility.getCellStyle(border, 11, 1).getBorderRight());
      assertEquals(BorderStyle.THICK.ordinal(), TestUtility.getCellStyle(border, 11, 1).getBorderTop());
      assertEquals(BorderStyle.THICK.ordinal(), TestUtility.getCellStyle(border, 11, 3).getBorderBottom());
      assertEquals(BorderStyle.THICK.ordinal(), TestUtility.getCellStyle(border, 11, 5).getBorderLeft());
      assertEquals(BorderStyle.THICK.ordinal(), TestUtility.getCellStyle(border, 11, 7).getBorderRight());
      assertEquals(BorderStyle.THICK.ordinal(), TestUtility.getCellStyle(border, 11, 9).getBorderTop());

      assertEquals(BorderStyle.DOUBLE.ordinal(), TestUtility.getCellStyle(border, 13, 1).getBorderBottom());
      assertEquals(BorderStyle.DOUBLE.ordinal(), TestUtility.getCellStyle(border, 13, 1).getBorderLeft());
      assertEquals(BorderStyle.DOUBLE.ordinal(), TestUtility.getCellStyle(border, 13, 1).getBorderRight());
      assertEquals(BorderStyle.DOUBLE.ordinal(), TestUtility.getCellStyle(border, 13, 1).getBorderTop());
      assertEquals(BorderStyle.DOUBLE.ordinal(), TestUtility.getCellStyle(border, 13, 3).getBorderBottom());
      assertEquals(BorderStyle.DOUBLE.ordinal(), TestUtility.getCellStyle(border, 13, 5).getBorderLeft());
      assertEquals(BorderStyle.DOUBLE.ordinal(), TestUtility.getCellStyle(border, 13, 7).getBorderRight());
      assertEquals(BorderStyle.DOUBLE.ordinal(), TestUtility.getCellStyle(border, 13, 9).getBorderTop());

      assertEquals(BorderStyle.DOTTED.ordinal(), TestUtility.getCellStyle(border, 15, 1).getBorderBottom());
      assertEquals(BorderStyle.DOTTED.ordinal(), TestUtility.getCellStyle(border, 15, 1).getBorderLeft());
      assertEquals(BorderStyle.DOTTED.ordinal(), TestUtility.getCellStyle(border, 15, 1).getBorderRight());
      assertEquals(BorderStyle.DOTTED.ordinal(), TestUtility.getCellStyle(border, 15, 1).getBorderTop());
      assertEquals(BorderStyle.DOTTED.ordinal(), TestUtility.getCellStyle(border, 15, 3).getBorderBottom());
      assertEquals(BorderStyle.DOTTED.ordinal(), TestUtility.getCellStyle(border, 15, 5).getBorderLeft());
      assertEquals(BorderStyle.DOTTED.ordinal(), TestUtility.getCellStyle(border, 15, 7).getBorderRight());
      assertEquals(BorderStyle.DOTTED.ordinal(), TestUtility.getCellStyle(border, 15, 9).getBorderTop());

      assertEquals(BorderStyle.MEDIUM_DASHED.ordinal(), TestUtility.getCellStyle(border, 17, 1).getBorderBottom());
      assertEquals(BorderStyle.MEDIUM_DASHED.ordinal(), TestUtility.getCellStyle(border, 17, 1).getBorderLeft());
      assertEquals(BorderStyle.MEDIUM_DASHED.ordinal(), TestUtility.getCellStyle(border, 17, 1).getBorderRight());
      assertEquals(BorderStyle.MEDIUM_DASHED.ordinal(), TestUtility.getCellStyle(border, 17, 1).getBorderTop());
      assertEquals(BorderStyle.MEDIUM_DASHED.ordinal(), TestUtility.getCellStyle(border, 17, 3).getBorderBottom());
      assertEquals(BorderStyle.MEDIUM_DASHED.ordinal(), TestUtility.getCellStyle(border, 17, 5).getBorderLeft());
      assertEquals(BorderStyle.MEDIUM_DASHED.ordinal(), TestUtility.getCellStyle(border, 17, 7).getBorderRight());
      assertEquals(BorderStyle.MEDIUM_DASHED.ordinal(), TestUtility.getCellStyle(border, 17, 9).getBorderTop());

      assertEquals(BorderStyle.DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 19, 1).getBorderBottom());
      assertEquals(BorderStyle.DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 19, 1).getBorderLeft());
      assertEquals(BorderStyle.DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 19, 1).getBorderRight());
      assertEquals(BorderStyle.DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 19, 1).getBorderTop());
      assertEquals(BorderStyle.DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 19, 3).getBorderBottom());
      assertEquals(BorderStyle.DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 19, 5).getBorderLeft());
      assertEquals(BorderStyle.DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 19, 7).getBorderRight());
      assertEquals(BorderStyle.DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 19, 9).getBorderTop());

      assertEquals(BorderStyle.MEDIUM_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 21, 1).getBorderBottom());
      assertEquals(BorderStyle.MEDIUM_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 21, 1).getBorderLeft());
      assertEquals(BorderStyle.MEDIUM_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 21, 1).getBorderRight());
      assertEquals(BorderStyle.MEDIUM_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 21, 1).getBorderTop());
      assertEquals(BorderStyle.MEDIUM_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 21, 3).getBorderBottom());
      assertEquals(BorderStyle.MEDIUM_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 21, 5).getBorderLeft());
      assertEquals(BorderStyle.MEDIUM_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 21, 7).getBorderRight());
      assertEquals(BorderStyle.MEDIUM_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 21, 9).getBorderTop());

      assertEquals(BorderStyle.DASH_DOT_DOT.ordinal(), TestUtility.getCellStyle(border, 23, 1).getBorderBottom());
      assertEquals(BorderStyle.DASH_DOT_DOT.ordinal(), TestUtility.getCellStyle(border, 23, 1).getBorderLeft());
      assertEquals(BorderStyle.DASH_DOT_DOT.ordinal(), TestUtility.getCellStyle(border, 23, 1).getBorderRight());
      assertEquals(BorderStyle.DASH_DOT_DOT.ordinal(), TestUtility.getCellStyle(border, 23, 1).getBorderTop());
      assertEquals(BorderStyle.DASH_DOT_DOT.ordinal(), TestUtility.getCellStyle(border, 23, 3).getBorderBottom());
      assertEquals(BorderStyle.DASH_DOT_DOT.ordinal(), TestUtility.getCellStyle(border, 23, 5).getBorderLeft());
      assertEquals(BorderStyle.DASH_DOT_DOT.ordinal(), TestUtility.getCellStyle(border, 23, 7).getBorderRight());
      assertEquals(BorderStyle.DASH_DOT_DOT.ordinal(), TestUtility.getCellStyle(border, 23, 9).getBorderTop());

      // Yes, the "C" on the end is actually there in the POI Enum constant.
      assertEquals(BorderStyle.MEDIUM_DASH_DOT_DOTC.ordinal(), TestUtility.getCellStyle(border, 25, 1).getBorderBottom());
      assertEquals(BorderStyle.MEDIUM_DASH_DOT_DOTC.ordinal(), TestUtility.getCellStyle(border, 25, 1).getBorderLeft());
      assertEquals(BorderStyle.MEDIUM_DASH_DOT_DOTC.ordinal(), TestUtility.getCellStyle(border, 25, 1).getBorderRight());
      assertEquals(BorderStyle.MEDIUM_DASH_DOT_DOTC.ordinal(), TestUtility.getCellStyle(border, 25, 1).getBorderTop());
      assertEquals(BorderStyle.MEDIUM_DASH_DOT_DOTC.ordinal(), TestUtility.getCellStyle(border, 25, 3).getBorderBottom());
      assertEquals(BorderStyle.MEDIUM_DASH_DOT_DOTC.ordinal(), TestUtility.getCellStyle(border, 25, 5).getBorderLeft());
      assertEquals(BorderStyle.MEDIUM_DASH_DOT_DOTC.ordinal(), TestUtility.getCellStyle(border, 25, 7).getBorderRight());
      assertEquals(BorderStyle.MEDIUM_DASH_DOT_DOTC.ordinal(), TestUtility.getCellStyle(border, 25, 9).getBorderTop());

      assertEquals(BorderStyle.SLANTED_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 27, 1).getBorderBottom());
      assertEquals(BorderStyle.SLANTED_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 27, 1).getBorderLeft());
      assertEquals(BorderStyle.SLANTED_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 27, 1).getBorderRight());
      assertEquals(BorderStyle.SLANTED_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 27, 1).getBorderTop());
      assertEquals(BorderStyle.SLANTED_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 27, 3).getBorderBottom());
      assertEquals(BorderStyle.SLANTED_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 27, 5).getBorderLeft());
      assertEquals(BorderStyle.SLANTED_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 27, 7).getBorderRight());
      assertEquals(BorderStyle.SLANTED_DASH_DOT.ordinal(), TestUtility.getCellStyle(border, 27, 9).getBorderTop());

      // BorderColors
      Sheet borderColor = workbook.getSheetAt(2);
      List<HSSFColor> hssfColors = Arrays.asList(ExcelColor.AQUA.getHssfColor(), ExcelColor.BLACK.getHssfColor(),
         ExcelColor.AUTOMATIC.getHssfColor(), ExcelColor.RED.getHssfColor(), ExcelColor.BLUE.getHssfColor(),
         ExcelColor.GREEN.getHssfColor(), ExcelColor.GREY50PERCENT.getHssfColor(), ExcelColor.WHITE.getHssfColor(),
         ExcelColor.LIGHTBLUE.getHssfColor(), ExcelColor.PINK.getHssfColor(), ExcelColor.YELLOW.getHssfColor(),
         ExcelColor.GREY50PERCENT.getHssfColor());
      List<XSSFColor> xssfColors = Arrays.asList(ExcelColor.AQUA.getXssfColor(), ExcelColor.BLACK.getXssfColor(),
         ExcelColor.AUTOMATIC.getXssfColor(), ExcelColor.RED.getXssfColor(), ExcelColor.BLUE.getXssfColor(),
         ExcelColor.GREEN.getXssfColor(), ExcelColor.GREY50PERCENT.getXssfColor(), ExcelColor.WHITE.getXssfColor(),
         ExcelColor.LIGHTBLUE.getXssfColor(), new XSSFColor(new byte[] {-52, 0, -1}),
         new XSSFColor(new byte[] {-1, -1, 0}), new XSSFColor(new byte[] {-128, -112, -128}));

      List<Color> colors;
      if (workbook instanceof HSSFWorkbook)
      {
         colors = new ArrayList<Color>(hssfColors);
      }
      else
      {
         // XSSFWorkbook
         colors = new ArrayList<Color>(xssfColors);
      }
      String autoColorString = "000000";
      for (int i = 0; i < hssfColors.size(); i++)
      {
         String colorString = SheetUtil.getColorHexString(colors.get(i));
         int row = 2 * i + 1;

         // Entire border
         assertEquals(colorString, TestUtility.getCellBottomBorderColorString(borderColor, row, 1));
         assertEquals(colorString, TestUtility.getCellLeftBorderColorString(borderColor, row, 1));
         assertEquals(colorString, TestUtility.getCellRightBorderColorString(borderColor, row, 1));
         assertEquals(colorString, TestUtility.getCellTopBorderColorString(borderColor, row, 1));
         // Bottom only
         assertEquals(colorString, TestUtility.getCellBottomBorderColorString(borderColor, row, 3));
         assertEquals(autoColorString, TestUtility.getCellLeftBorderColorString(borderColor, row, 3));
         assertEquals(autoColorString, TestUtility.getCellRightBorderColorString(borderColor, row, 3));
         assertEquals(autoColorString, TestUtility.getCellTopBorderColorString(borderColor, row, 3));
         // Left only
         assertEquals(autoColorString, TestUtility.getCellBottomBorderColorString(borderColor, row, 5));
         assertEquals(colorString, TestUtility.getCellLeftBorderColorString(borderColor, row, 5));
         assertEquals(autoColorString, TestUtility.getCellRightBorderColorString(borderColor, row, 5));
         assertEquals(autoColorString, TestUtility.getCellTopBorderColorString(borderColor, row, 5));
         // Right only
         assertEquals(autoColorString, TestUtility.getCellBottomBorderColorString(borderColor, row, 7));
         assertEquals(autoColorString, TestUtility.getCellLeftBorderColorString(borderColor, row, 7));
         assertEquals(colorString, TestUtility.getCellRightBorderColorString(borderColor, row, 7));
         assertEquals(autoColorString, TestUtility.getCellTopBorderColorString(borderColor, row, 7));
         // Top only
         assertEquals(autoColorString, TestUtility.getCellBottomBorderColorString(borderColor, row, 9));
         assertEquals(autoColorString, TestUtility.getCellLeftBorderColorString(borderColor, row, 9));
         assertEquals(autoColorString, TestUtility.getCellRightBorderColorString(borderColor, row, 9));
         assertEquals(colorString, TestUtility.getCellTopBorderColorString(borderColor, row, 9));
      }

      // DataFormats
      Sheet dataFormat = workbook.getSheetAt(3);
      // For the expected values, DON'T escape the semicolon separator(s) HERE.
      List<String> dataFormats = Arrays.asList(
         "#,###.00", "0.00%", "???.???", "[Red][<=100]General;[Blue][>100]General", "## ???/???",
         "yyyy-mm-dd hh:mm:ss", "mmmm d, yyyy h:mm:ss AM/PM"
      );
      for (int r = 0; r < 6; r++)
      {
         for (int c = 0; c < dataFormats.size(); c++)
         {
            // Excel will store data formats with all-caps text.
            assertTrue(dataFormats.get(c).equalsIgnoreCase(TestUtility.getCellStyle(dataFormat, r, c).getDataFormatString()));
         }
      }

      // Foreground/Background/Pattern
      Sheet backForePattern = workbook.getSheetAt(4);
      List<Color> backgroundColors;
      List<Color> foregroundColors;
      List<HSSFColor> hssfBackgroundColors = Arrays.asList(ExcelColor.RED.getHssfColor(),
         ExcelColor.YELLOW.getHssfColor(), ExcelColor.BRIGHTGREEN.getHssfColor(), ExcelColor.BLACK.getHssfColor());
      List<XSSFColor> xssfBackgroundColors = Arrays.asList(ExcelColor.RED.getXssfColor(),
         ExcelColor.YELLOW.getXssfColor(), ExcelColor.BRIGHTGREEN.getXssfColor(), ExcelColor.BLACK.getXssfColor());
      List<HSSFColor> hssfForegroundColors = Arrays.asList(ExcelColor.TURQUOISE.getHssfColor(),
         ExcelColor.BLUE.getHssfColor(), ExcelColor.PINK.getHssfColor(), ExcelColor.WHITE.getHssfColor());
      List<XSSFColor> xssfForegroundColors = Arrays.asList(ExcelColor.TURQUOISE.getXssfColor(),
         ExcelColor.BLUE.getXssfColor(), ExcelColor.PINK.getXssfColor(), ExcelColor.WHITE.getXssfColor());
      if (workbook instanceof HSSFWorkbook)
      {
         backgroundColors = new ArrayList<Color>(hssfBackgroundColors);
         foregroundColors = new ArrayList<Color>(hssfForegroundColors);
      }
      else
      {
         // XSSFWorkbook
         backgroundColors = new ArrayList<Color>(xssfBackgroundColors);
         foregroundColors = new ArrayList<Color>(xssfForegroundColors);
      }
      List<Short> fillPatterns = Arrays.asList((short) FillPatternType.NO_FILL.ordinal(),
         (short) FillPatternType.SOLID_FOREGROUND.ordinal(), (short) FillPatternType.FINE_DOTS.ordinal(),
         (short) FillPatternType.ALT_BARS.ordinal(), (short) FillPatternType.SPARSE_DOTS.ordinal(),
         (short) FillPatternType.THICK_HORZ_BANDS.ordinal(), (short) FillPatternType.THICK_VERT_BANDS.ordinal(),
         (short) FillPatternType.THICK_BACKWARD_DIAG.ordinal(), (short) FillPatternType.THICK_FORWARD_DIAG.ordinal(),
         (short) FillPatternType.BIG_SPOTS.ordinal(), (short) FillPatternType.BRICKS.ordinal(),
         (short) FillPatternType.THIN_HORZ_BANDS.ordinal(), (short) FillPatternType.THIN_VERT_BANDS.ordinal(),
         (short) FillPatternType.THIN_BACKWARD_DIAG.ordinal(), (short) FillPatternType.THIN_FORWARD_DIAG.ordinal(),
         (short) FillPatternType.SQUARES.ordinal(), (short) FillPatternType.DIAMONDS.ordinal(),
         (short) FillPatternType.LESS_DOTS.ordinal(), (short) FillPatternType.LEAST_DOTS.ordinal()
      );
      for (int i = 0; i < fillPatterns.size(); i++)
      {
         short fillPattern = fillPatterns.get(i);
         int r = 2 * i + 1;
         for (int c = 0; c < backgroundColors.size(); c++)
         {
            String backgroundColor = SheetUtil.getColorHexString(backgroundColors.get(c));
            String foregroundColor = SheetUtil.getColorHexString(foregroundColors.get(c));
            assertEquals(backgroundColor, TestUtility.getCellBackgroundColorString(backForePattern, r, c));
            assertEquals(foregroundColor, TestUtility.getCellForegroundColorString(backForePattern, r, c));
            assertEquals(fillPattern, TestUtility.getCellFillPattern(backForePattern, r, c));
         }
      }

      // Hidden/Locked/WrapText/Indention
      Sheet hideLockWrap = workbook.getSheetAt(5);
      assertFalse(TestUtility.getCellStyle(hideLockWrap, 0, 0).getHidden());
      assertTrue(TestUtility.getCellStyle(hideLockWrap, 1, 0).getHidden());
      assertFalse(TestUtility.getCellStyle(hideLockWrap, 2, 0).getLocked());
      assertTrue(TestUtility.getCellStyle(hideLockWrap, 3, 0).getLocked());
      assertFalse(TestUtility.getCellStyle(hideLockWrap, 4, 0).getWrapText());
      assertTrue(TestUtility.getCellStyle(hideLockWrap, 5, 0).getWrapText());
      assertEquals(0, TestUtility.getCellStyle(hideLockWrap, 6, 0).getIndention());
      assertEquals(1, TestUtility.getCellStyle(hideLockWrap, 7, 0).getIndention());
      assertEquals(3, TestUtility.getCellStyle(hideLockWrap, 8, 0).getIndention());
      assertEquals(10, TestUtility.getCellStyle(hideLockWrap, 9, 0).getIndention());

      // Rotation
      Sheet rotation = workbook.getSheetAt(6);
      assertEquals(0, TestUtility.getCellStyle(rotation, 0, 0).getRotation());
      assertEquals(30, TestUtility.getCellStyle(rotation, 1, 0).getRotation());
      assertEquals(90, TestUtility.getCellStyle(rotation, 2, 0).getRotation());
      // If rotation < 0, XSSF rotation = 90 - HSSF Rotation.
      assertTrue((-15 == TestUtility.getCellStyle(rotation, 3, 0).getRotation()) ||
                 (105 == TestUtility.getCellStyle(rotation, 3, 0).getRotation()));
      assertTrue((-90 == TestUtility.getCellStyle(rotation, 4, 0).getRotation()) ||
                 (180 == TestUtility.getCellStyle(rotation, 4, 0).getRotation()));
      assertEquals(StyleParser.POI_ROTATION_STACKED, TestUtility.getCellStyle(rotation, 5, 0).getRotation());

      // VerticalAlignments
      Sheet vertAlignment = workbook.getSheetAt(7);
      assertEquals(VerticalAlignment.BOTTOM.ordinal(), TestUtility.getCellStyle(vertAlignment, 0, 1).getVerticalAlignment());
      assertEquals(VerticalAlignment.BOTTOM.ordinal(), TestUtility.getCellStyle(vertAlignment, 0, 3).getVerticalAlignment());
      assertEquals(VerticalAlignment.CENTER.ordinal(), TestUtility.getCellStyle(vertAlignment, 1, 1).getVerticalAlignment());
      assertEquals(VerticalAlignment.CENTER.ordinal(), TestUtility.getCellStyle(vertAlignment, 1, 3).getVerticalAlignment());
      assertEquals(VerticalAlignment.DISTRIBUTED.ordinal(), TestUtility.getCellStyle(vertAlignment, 2, 1).getVerticalAlignment());
      assertEquals(VerticalAlignment.DISTRIBUTED.ordinal(), TestUtility.getCellStyle(vertAlignment, 2, 3).getVerticalAlignment());
      assertEquals(VerticalAlignment.JUSTIFY.ordinal(), TestUtility.getCellStyle(vertAlignment, 3, 1).getVerticalAlignment());
      assertEquals(VerticalAlignment.JUSTIFY.ordinal(), TestUtility.getCellStyle(vertAlignment, 3, 3).getVerticalAlignment());
      assertEquals(VerticalAlignment.TOP.ordinal(), TestUtility.getCellStyle(vertAlignment, 4, 1).getVerticalAlignment());
      assertEquals(VerticalAlignment.TOP.ordinal(), TestUtility.getCellStyle(vertAlignment, 4, 3).getVerticalAlignment());

      // Bold/Italic
      Sheet boldItalic = workbook.getSheetAt(8);
      Font f = workbook.getFontAt(TestUtility.getCellStyle(boldItalic, 0, 0).getFontIndex());
      assertEquals(FontBoldweight.NORMAL.getIndex(), f.getBoldweight());
      assertFalse(f.getItalic());
      f = workbook.getFontAt(TestUtility.getCellStyle(boldItalic, 0, 1).getFontIndex());
      assertEquals(FontBoldweight.BOLD.getIndex(), f.getBoldweight());
      assertFalse(f.getItalic());
      f = workbook.getFontAt(TestUtility.getCellStyle(boldItalic, 1, 0).getFontIndex());
      assertEquals(FontBoldweight.NORMAL.getIndex(), f.getBoldweight());
      assertTrue(f.getItalic());
      f = workbook.getFontAt(TestUtility.getCellStyle(boldItalic, 1, 1).getFontIndex());
      assertEquals(FontBoldweight.BOLD.getIndex(), f.getBoldweight());
      assertTrue(f.getItalic());

      // FontNames/HeightInPoints
      Sheet fontNameHeight = workbook.getSheetAt(9);
      List<String> fontNames = Arrays.asList("Arial", "Courier New", "Tahoma", "Times New Roman", "Verdana");
      List<Short> fontSizes = Arrays.asList((short) 6, (short) 8, (short) 10, (short) 11, (short) 12, (short) 20,
         (short) 72);
      for (int r = 0; r < fontNames.size(); r++)
      {
         for (int c = 0; c < fontSizes.size(); c++)
         {
            f = workbook.getFontAt(TestUtility.getCellStyle(fontNameHeight, r, c).getFontIndex());
            // Excel capitalizes all font names when storing them.
            assertTrue(fontNames.get(r).equalsIgnoreCase(f.getFontName()));
            assertEquals(fontSizes.get(c).shortValue(), f.getFontHeightInPoints());
         }
      }

      // FontColor/Charset
      Sheet fontColorCharset = workbook.getSheetAt(10);
      List<Integer> charsets = new ArrayList<Integer>();
      for (FontCharset charset : FontCharset.values())
         charsets.add(charset.getValue());
      for (int r = 0; r < hssfColors.size(); r++)
      {
         String colorString = SheetUtil.getColorHexString(colors.get(r));

         for (int c = 0; c < charsets.size(); c++)
         {
            //System.err.println("Testing FontColorCharset (r,c) (" + r + "," + c + ")");
            f = workbook.getFontAt(TestUtility.getCellStyle(fontColorCharset, r, c).getFontIndex());
            assertEquals(colorString, TestUtility.getFontColorString(workbook, f));
            assertEquals(charsets.get(c).intValue(), f.getCharSet());
         }
      }

      // FontStrikeout/TypeOffset/Underline
      Sheet fontStrikeoutOffsetUnderline = workbook.getSheetAt(11);
      List<Short> typeOffsets = Arrays.asList(Font.SS_NONE, Font.SS_SUB, Font.SS_SUPER);
      List<Byte> underlines = new ArrayList<Byte>();
      for (FontUnderline underline : FontUnderline.values())
         underlines.add(underline.getByteValue());
      for (int r = 0; r < typeOffsets.size(); r++)
      {
         for (int c = 0; c < underlines.size(); c++)
         {
            f = workbook.getFontAt(TestUtility.getCellStyle(fontStrikeoutOffsetUnderline, r, c).getFontIndex());
            assertFalse(f.getStrikeout());
            assertEquals(typeOffsets.get(r).shortValue(), f.getTypeOffset());
            assertEquals(underlines.get(c).byteValue(), f.getUnderline());

            f = workbook.getFontAt(TestUtility.getCellStyle(fontStrikeoutOffsetUnderline, r + typeOffsets.size(), c).getFontIndex());
            assertTrue(f.getStrikeout());
            assertEquals(typeOffsets.get(r).shortValue(), f.getTypeOffset());
            assertEquals(underlines.get(c).byteValue(), f.getUnderline());
         }
      }

      // ColumnWidth/RowHeight
      Sheet widthHeight = workbook.getSheetAt(12);
      List<Integer> widths = Arrays.asList(10, 12, 15, 20, 50);
      List<Integer> heights = new ArrayList<Integer>(widths);
      for (int w = 0; w < widths.size(); w++)
      {
         assertEquals(widths.get(w).intValue(), widthHeight.getColumnWidth(w + 2) / 256);
      }
      for (int h = 0; h < widths.size(); h++)
      {
         assertEquals(heights.get(h).shortValue(), (int) widthHeight.getRow(h + 2).getHeight() / 20);
      }

      // Class only
      Sheet classOnly = workbook.getSheetAt(13);
      CellStyle cs = TestUtility.getCellStyle(classOnly, 1, 1);
      assertEquals(BorderStyle.THIN.ordinal(), cs.getBorderBottom());
      assertEquals(BorderStyle.THIN.ordinal(), cs.getBorderLeft());
      assertEquals(BorderStyle.THIN.ordinal(), cs.getBorderRight());
      assertEquals(BorderStyle.THIN.ordinal(), cs.getBorderTop());
      assertEquals("ff0000", TestUtility.getCellBottomBorderColorString(classOnly, 1, 1));
      assertEquals("ff0000", TestUtility.getCellLeftBorderColorString(classOnly, 1, 1));
      assertEquals("ff0000", TestUtility.getCellRightBorderColorString(classOnly, 1, 1));
      assertEquals("ff0000", TestUtility.getCellTopBorderColorString(classOnly, 1, 1));
      assertEquals(CellStyle.ALIGN_CENTER, cs.getAlignment());
      f = workbook.getFontAt(cs.getFontIndex());
      assertEquals("000000", TestUtility.getFontColorString(workbook, f));
      assertEquals(11, f.getFontHeightInPoints());
      assertEquals(Font.BOLDWEIGHT_NORMAL, f.getBoldweight());

      cs = TestUtility.getCellStyle(classOnly, 3, 1);
      assertEquals(BorderStyle.NONE.ordinal(), cs.getBorderBottom());
      assertEquals(BorderStyle.NONE.ordinal(), cs.getBorderLeft());
      assertEquals(BorderStyle.NONE.ordinal(), cs.getBorderRight());
      assertEquals(BorderStyle.NONE.ordinal(), cs.getBorderTop());
      assertEquals(CellStyle.ALIGN_GENERAL, cs.getAlignment());
      f = workbook.getFontAt(cs.getFontIndex());
      assertEquals("0000ff", TestUtility.getFontColorString(workbook, f));
      assertEquals(24, f.getFontHeightInPoints());
      assertEquals(Font.BOLDWEIGHT_BOLD, f.getBoldweight());

      cs = TestUtility.getCellStyle(classOnly, 5, 1);
      assertEquals(BorderStyle.NONE.ordinal(), cs.getBorderBottom());
      assertEquals(BorderStyle.NONE.ordinal(), cs.getBorderLeft());
      assertEquals(BorderStyle.NONE.ordinal(), cs.getBorderRight());
      assertEquals(BorderStyle.NONE.ordinal(), cs.getBorderTop());
      assertEquals(CellStyle.ALIGN_GENERAL, cs.getAlignment());
      f = workbook.getFontAt(cs.getFontIndex());
      assertEquals("000000", TestUtility.getFontColorString(workbook, f));
      assertEquals(11, f.getFontHeightInPoints());
      assertEquals(Font.BOLDWEIGHT_NORMAL, f.getBoldweight());

      cs = TestUtility.getCellStyle(classOnly, 7, 1);
      f = workbook.getFontAt(cs.getFontIndex());
      assertEquals("008000", TestUtility.getFontColorString(workbook, f));
      assertEquals(Font.BOLDWEIGHT_NORMAL, f.getBoldweight());
      assertTrue(f.getItalic());
      assertEquals(24, f.getFontHeightInPoints());

      cs = TestUtility.getCellStyle(classOnly, 9, 1);
      assertEquals(BorderStyle.THIN.ordinal(), cs.getBorderBottom());
      assertEquals(BorderStyle.THIN.ordinal(), cs.getBorderLeft());
      assertEquals(BorderStyle.THIN.ordinal(), cs.getBorderRight());
      assertEquals(BorderStyle.THIN.ordinal(), cs.getBorderTop());
      assertEquals("ff0000", TestUtility.getCellBottomBorderColorString(classOnly, 1, 1));
      assertEquals("ff0000", TestUtility.getCellLeftBorderColorString(classOnly, 1, 1));
      assertEquals("ff0000", TestUtility.getCellRightBorderColorString(classOnly, 1, 1));
      assertEquals("ff0000", TestUtility.getCellTopBorderColorString(classOnly, 1, 1));
      assertEquals(CellStyle.ALIGN_CENTER, cs.getAlignment());
      f = workbook.getFontAt(cs.getFontIndex());
      assertEquals("0000ff", TestUtility.getFontColorString(workbook, f));
      assertEquals(24, f.getFontHeightInPoints());
      assertEquals(Font.BOLDWEIGHT_BOLD, f.getBoldweight());
   }
   
   /**
    * This test is a single map test.
    * @return <code>false</code>.
    */
   protected boolean isMultipleBeans()
   {
      return false;
   }

   /**
    * For single beans map tests, return the <code>Map</code> of bean names to
    * bean values.
    * @return A <code>Map</code> of bean names to bean values.
    */
   protected Map<String, Object> getBeansMap()
   {
      Map<String, Object> beans = new HashMap<String, Object>();

      List<String> alignments = new ArrayList<String>();
      for (Alignment alignment : Alignment.values())
         alignments.add(alignment.toString());
      beans.put("alignments", alignments);

      List<String> borderTypes = new ArrayList<String>();
      for (BorderType borderType : BorderType.values())
         borderTypes.add(borderType.toString());
      beans.put("borderTypes", borderTypes);

      List<String> colors = Arrays.asList(ExcelColor.AQUA.toString(), ExcelColor.BLACK.toString(),
         ExcelColor.AUTOMATIC.toString(), ExcelColor.RED.toString(), ExcelColor.BLUE.toString(),
         ExcelColor.GREEN.toString(), ExcelColor.GREY50PERCENT.toString(), ExcelColor.WHITE.toString(),
         ExcelColor.LIGHTBLUE.toString(), "#CC00FF", "#FFFF00", "#809080");
      beans.put("colors", colors);

      List<Object> formattedValues = Arrays.<Object>asList(25, 1234567, Math.PI, 8.6, 0.000012,
         Calendar.getInstance()
      );
      // Escape the ";" here, because the StyleTag interprets a ";" as end of property/value.
      // The StyleTag class respects escaped semicolons.
      List<String> dataFormats = Arrays.asList(
         "#,###.00", "0.00%", "???.???", "[Red][<=100]General\\;[Blue][>100]General", "## ???/???",
         "yyyy-mm-dd hh:mm:ss", "mmmm d, yyyy h:mm:ss AM/PM"
      );
      beans.put("formattedValues", formattedValues);
      beans.put("dataFormats", dataFormats);

      List<String> backgroundColors = Arrays.asList(
         ExcelColor.RED.toString(), ExcelColor.YELLOW.toString(), ExcelColor.BRIGHTGREEN.toString(), ExcelColor.BLACK.toString());
      List<String> foregroundColors = Arrays.asList(
         ExcelColor.TURQUOISE.toString(), ExcelColor.BLUE.toString(), ExcelColor.PINK.toString(), ExcelColor.WHITE.toString());
      List<String> fillPatterns = new ArrayList<String>();
      for (FillPattern fillPattern : FillPattern.values())
         fillPatterns.add(fillPattern.toString());
      beans.put("backgroundColors", backgroundColors);
      beans.put("foregroundColors", foregroundColors);
      beans.put("fillPatterns", fillPatterns);

      List<String> booleans = Arrays.asList("false", "true");
      beans.put("booleans", booleans);
      List<Short> indentions = Arrays.asList((short) 0, (short) 1, (short) 3, (short) 10);
      beans.put("indentions", indentions);

      List<Object> rotations = Arrays.<Object>asList((short) 0, (short) 30, (short) 90, (short) -15, (short) -90, StyleParser.ROTATION_STACKED);
      beans.put("rotations", rotations);

      List<String> vertAlignments = new ArrayList<String>();
      for (net.sf.jett.model.VerticalAlignment vertAlignment : net.sf.jett.model.VerticalAlignment.values())
         vertAlignments.add(vertAlignment.toString());
      beans.put("vertAlignments", vertAlignments);

      List<String> bolds = new ArrayList<String>();
      for (FontBoldweight boldweight : FontBoldweight.values())
         bolds.add(boldweight.toString());
      beans.put("bolds", bolds);

      List<String> fontNames = Arrays.asList("Arial", "Courier New", "Tahoma", "Times New Roman", "Verdana");
      List<Short> fontSizes = Arrays.asList((short) 6, (short) 8, (short) 10, (short) 11, (short) 12, (short) 20,
         (short) 72);
      beans.put("fontNames", fontNames);
      beans.put("fontSizes", fontSizes);

      List<String> charsets = new ArrayList<String>();
      for (net.sf.jett.model.FontCharset charset : net.sf.jett.model.FontCharset.values())
         charsets.add(charset.toString());
      beans.put("charsets", charsets);

      List<String> offsets = new ArrayList<String>();
      List<String> underlines = new ArrayList<String>();
      for (FontTypeOffset offset : FontTypeOffset.values())
         offsets.add(offset.toString());
      for (net.sf.jett.model.FontUnderline underline : net.sf.jett.model.FontUnderline.values())
         underlines.add(underline.toString());
      beans.put("offsets", offsets);
      beans.put("underlines", underlines);

      List<Integer> widths = Arrays.asList(10, 12, 15, 20, 50);
      List<Integer> heights = new ArrayList<Integer>(widths);
      beans.put("widths", widths);
      beans.put("heights", heights);
      
      return beans;
   }
}

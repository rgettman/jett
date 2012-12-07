package net.sf.jett.test.model;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import net.sf.jett.event.TagEvent;
import net.sf.jett.event.TagListener;
import net.sf.jett.model.Block;

/**
 * A <code>BoldTagListener</code> is a <code>TagListener</code> that turns all
 * text within the block bold.
 *
 * @author Randy Gettman
 * @since 0.3.0
 */
public class BoldTagListener implements TagListener
{
   /**
    * Turns all cell text bold!
    * @param event The <code>TagEvent</code>.
    */
   public void onTagProcessed(TagEvent event)
   {
      Block block = event.getBlock();
      Sheet sheet = event.getSheet();
      for (int r = block.getTopRowNum(); r <= block.getBottomRowNum(); r++)
      {
         Row row = sheet.getRow(r);
         if (row != null)
         {
            for (int c = block.getLeftColNum(); c <= block.getRightColNum(); c++)
            {
               Cell cell = row.getCell(c);
               if (cell != null)
               {
                  Workbook workbook = sheet.getWorkbook();
                  CellStyle style = cell.getCellStyle();
                  short fontIdx = style.getFontIndex();
                  Font font = workbook.getFontAt(fontIdx);
                  if (font.getBoldweight() != Font.BOLDWEIGHT_BOLD)
                  {
                     Font boldFont = workbook.findFont(Font.BOLDWEIGHT_BOLD, font.getColor(), font.getFontHeight(),
                        font.getFontName(), font.getItalic(), font.getStrikeout(), font.getTypeOffset(),
                        font.getUnderline());
                     CellStyle newStyle = workbook.createCellStyle();
                     newStyle.cloneStyleFrom(style);
                     if (boldFont == null)
                     {
                        boldFont = workbook.createFont();
                        boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
                        boldFont.setColor(font.getColor());
                        boldFont.setFontHeight(font.getFontHeight());
                        boldFont.setFontName(font.getFontName());
                        boldFont.setItalic(font.getItalic());
                        boldFont.setStrikeout(font.getStrikeout());
                        boldFont.setTypeOffset(font.getTypeOffset());
                        boldFont.setUnderline(font.getUnderline());
                     }
                     newStyle.setFont(boldFont);
                     cell.setCellStyle(newStyle);
                  }
               }
            }
         }
      }
   }
}

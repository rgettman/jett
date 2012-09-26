package net.sf.jett.test.model;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import net.sf.jett.event.TagLoopEvent;
import net.sf.jett.event.TagLoopListener;
import net.sf.jett.tag.Block;

/**
 * A <code>BlockShadingLoopListener</code> is a <code>TagLoopListener</code>
 * that shades alternating blocks light gray.
 *
 * @author Randy Gettman
 * @since 0.3.0
 */
public class BlockShadingLoopListener implements TagLoopListener
{
   /**
    * Shade alternating blocks light gray.
    * @param event The <code>TagLoopEvent</code>.
    */
   public void onTagLoopProcessed(TagLoopEvent event)
   {
      Sheet sheet = event.getSheet();
      Workbook workbook = sheet.getWorkbook();
      Block block = event.getBlock();
      int left = block.getLeftColNum();
      int right = block.getRightColNum();
      int top = block.getTopRowNum();
      int bottom = block.getBottomRowNum();
      int index = event.getLoopIndex();

      if (index % 2 == 1)
      {
         for (int r = top; r <= bottom; r++)
         {
            Row row = sheet.getRow(r);
            for (int c = left; c <= right; c++)
            {
               Cell cell = row.getCell(c);
               CellStyle style = cell.getCellStyle();
               CellStyle newStyle = workbook.createCellStyle();
               newStyle.cloneStyleFrom(style);
               newStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
               newStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
               cell.setCellStyle(newStyle);
            }
         }
      }
   }
}

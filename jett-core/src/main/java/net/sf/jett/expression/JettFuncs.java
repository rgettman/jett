package net.sf.jett.expression;

import org.apache.poi.ss.util.CellReference;
/**
 * A <code>JettFuncs</code> object is an object that represents JETT utility
 * functionality in the JEXL world.
 *
 * @author Randy Gettman
 * @since 0.4.0
 */
public class JettFuncs
{
   /**
    * Takes 0-based row and column numbers (e.g. 1, 4), and generates an Excel cell
    * reference (e.g. "D2").
    * @param rowNum The 0-based row number.
    * @param colNum The 0-based column number.
    * @return A string representing an Excel cell reference.
    */
   public static String cellRef(int rowNum, int colNum)
   {
      return CellReference.convertNumToColString(colNum) + (rowNum + 1);
   }

   /**
    * Takes 0-based row and column numbers (e.g. 1, 4) and height and width
    * parameters (e.g. 2, 2), and generates an Excel cell
    * reference (e.g. "D2:E3").
    * @param rowNum The 0-based row number.
    * @param colNum The 0-based column number.
    * @param numRows The number of rows in the reference.
    * @param numCols The number of columns in the reference.
    * @return A string representing an Excel cell reference.
    */
   public static String cellRef(int rowNum, int colNum, int numRows, int numCols)
   {
      return CellReference.convertNumToColString(colNum) + (rowNum + 1) + ":" +
             CellReference.convertNumToColString(colNum + numCols - 1) + (rowNum + numRows);
   }
}

package net.sf.jett.tag;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * A <code>TagContext</code> object represents the context associated with a
 * <code>Tag</code>.
 */
public class TagContext
{
   private Sheet mySheet;
   private Block myBlock;
   private Map<String, Object> myBeans;
   private Map<String, Cell> myProcessedCells;

   /**
    * Construct a <code>TagContext</code>, initializing things to null.
    */
   public TagContext()
   {
      mySheet = null;
      myBlock = null;
      myBeans = null;
   }

   /**
    * Returns the <code>Sheet</code> on which a tag is found.
    * @return A <code>Sheet</code>.
    */
   public Sheet getSheet()
   {
      return mySheet;
   }

   /**
    * Sets the <code>Sheet</code> on which a tag is found.
    * @param sheet A <code>Sheet</code>.
    */
   public void setSheet(Sheet sheet)
   {
      this.mySheet = sheet;
   }

   /**
    * Returns the <code>Block</code> that applies to a tag.
    * @return A <code>Block</code>.
    */
   public Block getBlock()
   {
      return myBlock;
   }

   /**
    * Sets the <code>Block</code> that applies to a tag.
    * @param block A <code>Block</code>.
    */
   public void setBlock(Block block)
   {
      this.myBlock = block;
   }

   /**
    * Returns the <code>Map</code> of beans.
    * @return A <code>Map</code> of bean names and objects.
    */
   public Map<String, Object> getBeans()
   {
      return myBeans;
   }

   /**
    * Sets the <code>Map</code> of beans.
    * @param beans A <code>Map</code> of bean names and objects.
    */
   public void setBeans(Map<String, Object> beans)
   {
      this.myBeans = beans;
   }

   /**
    * Returns the <code>Map</code> of <code>Cells</code> that have already been
    * processed.
    * @return A <code>Map</code> of <code>Cells</code>.
    */
   public Map<String, Cell> getProcessedCellsMap()
   {
      return myProcessedCells;
   }

   /**
    * Sets the <code>Map</code> of <code>Cells</code> that have already been
    * processed.
    * @param processedCells A <code>Map</code> of <code>Cells</code>.
    */
   public void setProcessedCellsMap(Map<String, Cell> processedCells)
   {
      myProcessedCells = processedCells;
   }
}
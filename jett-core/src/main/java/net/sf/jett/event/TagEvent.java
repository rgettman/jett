package net.sf.jett.event;

import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;

import net.sf.jett.model.Block;

/**
 * A <code>TagEvent</code> represents data associated with a "tag processed"
 * event.  It contains a reference to the <code>Block</code> of
 * <code>Cells</code> that was processed and the <code>Map</code> of bean names
 * to values used to process it.
 *
 * @author Randy Gettman
 * @since 0.3.0
 */
public class TagEvent
{
   private Sheet mySheet;
   private Block myBlock;
   private Map<String, Object> myBeans;

   /**
    * Constructs a <code>TagEvent</code> with null references.
    */
   public TagEvent()
   {
      mySheet = null;
      myBlock = null;
      myBeans = null;
   }

   /**
    * Returns the <code>Sheet</code> on which the block of cells was processed.
    * @return The <code>Sheet</code> on which the block of cells was processed.
    */
   public Sheet getSheet()
   {
      return mySheet;
   }

   /**
    * Sets the <code>Sheet</code> on which the block of cells was processed.
    * @param sheet The <code>Sheet</code> on which the block of cells was
    *    processed.
    */
   public void setSheet(Sheet sheet)
   {
      mySheet = sheet;
   }

   /**
    * Returns the <code>Block</code> of cells that was processed.
    * @return The <code>Block</code> of cells that was processed.
    */
   public Block getBlock()
   {
      return myBlock;
   }

   /**
    * Sets the <code>Block</code> of cells that was processed.
    * @param block The <code>Block</code> of cells that was processed.
    */
   public void setBlock(Block block)
   {
      myBlock = block;
   }

   /**
    * Returns the <code>Map</code> of bean names to values used to process the
    * block of cells.
    * @return The <code>Map</code> of bean names to values.
    */
   public Map<String, Object> getBeans()
   {
      return myBeans;
   }

   /**
    * Sets the <code>Map</code> of bean names to values used to process the
    * block of cells.
    * @param beans The <code>Map</code> of bean names to values.
    */
   public void setBeans(Map<String, Object> beans)
   {
      myBeans = beans;
   }
}

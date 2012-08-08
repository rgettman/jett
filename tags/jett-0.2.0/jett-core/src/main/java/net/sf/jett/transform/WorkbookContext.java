package net.sf.jett.transform;

import java.util.List;
import java.util.Map;

import net.sf.jett.event.CellListener;
import net.sf.jett.formula.CellRef;
import net.sf.jett.formula.Formula;
import net.sf.jett.tag.TagLibraryRegistry;

/**
 * A <code>WorkbookContext</code> object holds data relevant to the context of
 * a <code>Workbook</code>.
 */
public class WorkbookContext
{
   private TagLibraryRegistry myRegistry;
   private List<CellListener> myCellListeners;
   private List<String> myFixedSizeCollectionNames;
   private List<String> myNoImplicitProcessingCollectionNames;
   private Map<String, Formula> myFormulaMap;
   private Map<String, List<CellRef>> myCellRefMap;
   private int mySequenceNbr;

   /**
    * Initializes things to null/0.
    */
   public WorkbookContext()
   {
      myRegistry = null;
      myCellListeners = null;
      myFixedSizeCollectionNames = null;
      myNoImplicitProcessingCollectionNames = null;
      myFormulaMap = null;
      myCellRefMap = null;
      mySequenceNbr = 0;
   }

   /**
    * Returns the <code>TagLibraryRegistry</code>.
    * @return The <code>TagLibraryRegistry</code>.
    */
   public TagLibraryRegistry getRegistry()
   {
      return myRegistry;
   }

   /**
    * Sets the <code>TagLibraryRegistry</code>.
    * @param registry The <code>TagLibraryRegistry</code>.
    */
   public void setRegistry(TagLibraryRegistry registry)
   {
      myRegistry = registry;
   }

   /**
    * Returns the <code>CellListeners</code>.
    * @return The <code>CellListeners</code>.
    */
   public List<CellListener> getCellListeners()
   {
      return myCellListeners;
   }

   /**
    * Sets the <code>CellListeners</code>.
    * @param cellListeners The <code>CellListeners</code>.
    */
   public void setCellListeners(List<CellListener> cellListeners)
   {
      myCellListeners = cellListeners;
   }

   /**
    * These named <code>Collections</code> have a known size and do not need to
    * have other <code>Cells</code> shifted out of the way for its contents;
    * space is already allocated.
    * @param collNames A <code>List</code> of <code>Collection</code> names
    *    that don't need other <code>Cells</code> shifted out of the way for
    *    its contents.
    */
   public void setFixedSizeCollectionNames(List<String> collNames)
   {
      myFixedSizeCollectionNames = collNames;
   }

   /**
    * Returns the <code>List</code> of "fixed size" collection names.
    * @return The <code>List</code> of "fixed size" collection names.
    */
   public List<String> getFixedSizedCollectionNames()
   {
      return myFixedSizeCollectionNames;
   }

   /**
    * Turn off implicit collections processing for the given
    * <code>Collections</code> specified by the given collection names.
    * @param collNames The names of the <code>Collections</code> on which NOT
    *    to perform implicit collections processing.
    */
   public void setNoImplicitCollectionProcessingNames(List<String> collNames)
   {
      myNoImplicitProcessingCollectionNames = collNames;
   }

   /**
    * Returns the <code>List</code> of collection names on which NOT to perform
    * implicit collections processing.
    * @return The <code>List</code> of collection names on which NOT to perform
    *    implicit collections processing.
    */
   public List<String> getNoImplicitProcessingCollectionNames()
   {
      return myNoImplicitProcessingCollectionNames;
   }

   /**
    * Returns the formula map, a <code>Map</code> of formula keys to
    * <code>Formulas</code>, with the keys of the format "sheetName!formula".
    * @return A <code>Map</code> of formula keys to <code>Formulas</code>.
    */
   public Map<String, Formula> getFormulaMap()
   {
      return myFormulaMap;
   }

   /**
    * Sets the formula map, a <code>Map</code> of formula keys to
    * <code>Formulas</code>, with the keys of the format "sheetName!formula".
    * @param formulaMap A <code>Map</code> of formula keys to
    *    <code>Formulas</code>.
    */
   public void setFormulaMap(Map<String, Formula> formulaMap)
   {
      myFormulaMap = formulaMap;
   }

   /**
    * Returns the cell reference map, a <code>Map</code> of cell key strings to
    * <code>Lists</code> of <code>CellRefs</code>.  The cell key strings are
    * original cell references, and the <code>Lists</code> contain translated
    * <code>CellRefs</code>, e.g. "Sheet1!C2" => [C2, C3, C4]
    * @return A <code>Map</code> of cell key strings to <code>Lists</code> of
    *    <code>CellRefs</code>.
    */
   public Map<String, List<CellRef>> getCellRefMap()
   {
      return myCellRefMap;
   }

   /**
    * Sets the cell reference map, a <code>Map</code> of cell key strings to
    * <code>Lists</code> of <code>CellRefs</code>.  The cell key strings are
    * original cell references, and the <code>Lists</code> contain translated
    * <code>CellRefs</code>, e.g. "Sheet1!C2" => [C2, C3, C4]
    * @param cellRefMap A <code>Map</code> of cell key strings to
    * <code>Lists</code> of <code>CellRefs</code>.
    */
   public void setCellRefMap(Map<String, List<CellRef>> cellRefMap)
   {
      myCellRefMap = cellRefMap;
   }

   /**
    * Returns the current sequence number.
    * @return The current sequence number.
    */
   public int getSequenceNbr()
   {
      return mySequenceNbr;
   }

   /**
    * Increments the current sequence number.
    */
   public void incrSequenceNbr()
   {
      mySequenceNbr++;
   }
}

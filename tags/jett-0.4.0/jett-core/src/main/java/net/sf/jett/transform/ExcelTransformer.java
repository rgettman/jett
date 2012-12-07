package net.sf.jett.transform;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import net.sf.jett.event.CellListener;
import net.sf.jett.expression.Expression;
import net.sf.jett.expression.ExpressionFactory;
import net.sf.jett.formula.CellRef;
import net.sf.jett.formula.Formula;
import net.sf.jett.tag.JtTagLibrary;
import net.sf.jett.tag.TagLibrary;
import net.sf.jett.tag.TagLibraryRegistry;
import net.sf.jett.util.FormulaUtil;

/**
 * <p>The <code>ExcelTransformer</code> class represents the main JETT API.</p>
 * 
 * <p>An <code>ExcelTransformer</code> knows how to transform Excel template
 * workbooks into fully populated Excel workbooks, using caller-supplied data
 * in the form of <em>beans</em>.  This class is the entry point API for JETT.
 * </p>
 *
 * <p>There are four main methods that accomplish all of the work, all with the
 * overloaded name "transform":</p>
 * <ul>
 * <li><code>public void transform(String inFilename, String outFilename, Map<String, Object> beans)
 *    throws IOException, InvalidFormatException</code>
 * <li><code>public Workbook transform(InputStream is, Map&lt;String, Object&gt; beans)
 *    throws IOException, InvalidFormatException</code>
 * <li><code>public void transform(String inFilename, String outFilename, List<String> templateSheetNamesList,
      List<String> newSheetNamesList, List<Map<String, Object>> beansList)
      throws IOException, InvalidFormatException</code>
 * <li><code>public Workbook transform(InputStream is, List&lt;String&gt; templateSheetNamesList, List&lt;String&gt; newSheetNamesList,
 *    List&lt;Map&lt;tring, Object&gt;&gt; beansList) throws IOException, InvalidFormatException</code>
 * </ul>
 * <p>The first method reads the template spreadsheet from the input filename,
 * applies the bean values across all sheets, and writes the transformed
 * spreadsheet to the output filename.</p>
 * <p>The second method reads the template spreadsheet from the given input
 * stream (usually a file), applies the bean values across all sheets, and
 * returs a <code>Workbook</code> object representing the transformed
 * spreadsheet, which can be written to a file if desired.  The first method
 * calls the second method to do its work.</p>
 * <p>The third method reads the template spreadsheet from the input filename,
 * applies specific bean values to specific sheets, and writes the transformed
 * spreadsheet to the output filename.</p>
 * <p>The fourth method reads the template spreadsheet from the given input
 * stream (usually a file), applies specific bean values to specific sheets,
 * and returs a <code>Workbook</code> object representing the transformed
 * spreadsheet, which can be written to a file if desired.  The third method
 * calls the fourth method to do its work.</p>
 * <p>The <code>ExcelTransformer</code>'s settings can be changed with the
 * other public methods of this class, including recognizing custom tag
 * libraries, adding <code>CellListeners</code>, using fixed size collections,
 * turning off implicit collections processing, passing <code>silent</code> and
 * <code>lenient</code> flags through to the underlying JEXL Engine,
 * passing a cache size to the internal JEXL Engine, and passing namespace
 * objects to register custom functions in the JEXL Engine.</p>
 *
 * @author Randy Gettman
 */
public class ExcelTransformer
{
   private static final boolean DEBUG = false;

   private TagLibraryRegistry myRegistry;
   private List<CellListener> myCellListeners;
   private List<String> myFixedSizeCollectionNames;
   private List<String> myNoImplicitProcessingCollectionNames;

   /**
    * Construct an <code>ExcelTransformer</code>.
    */
   public ExcelTransformer()
   {
      myRegistry = new TagLibraryRegistry();
      registerTagLibrary("jt", JtTagLibrary.getJtTagLibrary());
      myCellListeners = new ArrayList<CellListener>();
      myFixedSizeCollectionNames = new ArrayList<String>();
      myNoImplicitProcessingCollectionNames = new ArrayList<String>();
   }

   /**
    * Registers the given <code>TagLibrary</code> so that this
    * <code>ExcelTransformer</code> can recognize tags from the given
    * namespace.
    * @param namespace The namespace associated with the tag library.
    * @param library The <code>TagLibrary</code>.
    * @throws IllegalArgumentException If the namespace has already been
    *    registered.
    */
   public void registerTagLibrary(String namespace, TagLibrary library)
   {
      myRegistry.registerTagLibrary(namespace, library);
   }

   /**
    * Registers the given <code>CellListener</code>.
    * @param listener A <code>CellListener</code>.
    */
   public void addCellListener(CellListener listener)
   {
      if (listener != null)
         myCellListeners.add(listener);
   }

   /**
    * This particular named <code>Collection</code> has a known size and does
    * not need to have other <code>Cells</code> shifted out of the way for its
    * contents; space is already allocated.
    * @param collName The name of the <code>Collection</code> that doesn't need
    *    other <code>Cells</code> shifted out of the way for its contents.
    */
   public void addFixedSizeCollectionName(String collName)
   {
      if (collName != null)
         myFixedSizeCollectionNames.add(collName);
   }

   /**
    * The caller is stating that it will be explicitly accessing item(s) in the
    * named <code>Collection</code>, so implicit collections processing should
    * NOT be performed on this collection.  Implicit collections processing
    * will still occur on <code>Collections</code> known by other names.
    * @param collName The name of the <code>Collection</code> on which NOT to
    *    perform implicit collections processing.
    */
   public void turnOffImplicitCollectionProcessing(String collName)
   {
      if (collName != null)
         myNoImplicitProcessingCollectionNames.add(collName);
   }

   /**
    * Sets whether the JEXL "lenient" flag is set.
    * @param lenient Whether the JEXL "lenient" flag is set.
    */
   public void setLenient(boolean lenient)
   {
      ExpressionFactory factory = ExpressionFactory.getExpressionFactory();
      factory.setLenient(lenient);
   }

   /**
    * Sets whether the JEXL "silent" flag is set.  Default is
    * <code>false</code>.
    * @param silent Whether the JEXL "silent" flag is set.
    */
   public void setSilent(boolean silent)
   {
      ExpressionFactory factory = ExpressionFactory.getExpressionFactory();
      factory.setSilent(silent);
   }

   /**
    * Creates and uses a JEXL Expression cache of the given size.  The given
    * value is passed through to the JEXL Engine.  The JEXL Engine establishes
    * a parse cache; it's not a result cache.
    * @param size The size of the JEXL Expression cache.
    * @since 0.2.0
    */
   public void setCache(int size)
   {
      ExpressionFactory factory = ExpressionFactory.getExpressionFactory();
      factory.setCache(size);
   }

   /**
    * Registers an object under the given namespace in the internal JEXL
    * Engine.  Each public method in the object's class is exposed as a
    * "function" available in the JEXL Engine.  To use instance methods, pass
    * an instance of the object.  To use class methods, pass a
    * <code>Class</code> object.
    * @param namespace The namespace used to access the functions object.
    * @param funcsObject An object (or a <code>Class</code>) containing the
    *    methods to expose as JEXL Engine functions.
    * @throws IllegalArgumentException If the namespace has already been
    *    registered.
    * @since 0.2.0
    */
   public void registerFuncs(String namespace, Object funcsObject)
   {
      ExpressionFactory factory = ExpressionFactory.getExpressionFactory();
      factory.registerFuncs(namespace, funcsObject);
   }

   /**
    * Transforms the template Excel spreadsheet represented by the given input
    * filename.  Applies the given <code>Map</code> of beans to all sheets.
    * Writes the resultant Excel spreadsheet to the given output filename.
    * @param inFilename The template spreadsheet filename.
    * @param outFilename The resultant spreadsheet filename.
    * @param beans The <code>Map</code> of bean names to bean objects.
    * @throws IOException If there is a problem reading or writing any Excel
    *    spreadsheet.
    * @throws InvalidFormatException If there is a problem creating a
    *    <code>Workbook</code> object.
    * @since 0.2.0
    */
   public void transform(String inFilename, String outFilename, Map<String, Object> beans)
      throws IOException, InvalidFormatException
   {
      FileOutputStream fileOut = null;
      InputStream fileIn = null;
      Workbook workbook;
      try
      {
         fileOut = new FileOutputStream(outFilename);
         fileIn = new BufferedInputStream(new FileInputStream(inFilename));
         workbook = transform(fileIn, beans);
         workbook.write(fileOut);
      }
      finally
      {
         if (fileIn != null)
            try { fileIn.close(); } catch (IOException ignored) {}
         if (fileOut != null)
            try { fileOut.close(); } catch (IOException ignored) {}
      }
   }

   /**
    * Transforms the template Excel spreadsheet represented by the given
    * <code>InputStream</code>.  Applies the given <code>Map</code> of beans
    * to all sheets.
    * @param is The <code>InputStream</code> from the template spreadsheet.
    * @param beans The <code>Map</code> of bean names to bean objects.
    * @return A new <code>Workbook</code> object capable of being written to an
    *    <code>OutputStream</code>.
    * @throws IOException If there is a problem reading the template Excel
    *    spreadsheet.
    * @throws InvalidFormatException If there is a problem creating a
    *    <code>Workbook</code> object.
    */
   public Workbook transform(InputStream is, Map<String, Object> beans)
      throws IOException, InvalidFormatException
   {
      Workbook workbook = WorkbookFactory.create(is);
      // This is done for performance reasons, related to identifying
      // collection names in expression text, which may vary from beans
      // map to beans map.
      Expression.clearExpressionToCollNamesMap();
      SheetTransformer sheetTransformer = new SheetTransformer();
      WorkbookContext context = createContext(workbook, sheetTransformer);
      exposeWorkbook(beans, workbook);
      for (int s = 0; s < workbook.getNumberOfSheets(); s++)
      {
         Sheet sheet = workbook.getSheetAt(s);
         sheetTransformer.transform(sheet, context, beans);
      }
      if (!context.getFormulaMap().isEmpty())
      {
         replaceFormulas(workbook, context, sheetTransformer);
      }
      return workbook;
   }

   /**
    * Transforms the template Excel spreadsheet represented by the given input
    * filename.  If a sheet name is represented <em>n</em> times in the list of
    * template sheet names, then it will cloned to make <em>n</em> total copies
    * and the clones will receive the corresponding sheet name from the list of
    * sheet names.  Each resulting sheet has a corresponding <code>Map</code>
    * of bean names to bean values exposed to it. Writes the resultant Excel
    * spreadsheet to the given output filename.
    * @param inFilename The template spreadsheet filename.
    * @param outFilename The resultant spreadsheet filename.
    * @param templateSheetNamesList A <code>List</code> of template sheet
    *    names, with duplicates indicating to clone sheets.
    * @param newSheetNamesList A <code>List</code> of resulting sheet names
    *    corresponding to the template sheet names list.
    * @param beansList A <code>List</code> of <code>Maps</code> representing
    *    the beans map exposed to each resulting sheet.
    * @throws IOException If there is a problem reading or writing any Excel
    *    spreadsheet.
    * @throws InvalidFormatException If there is a problem creating a
    *    <code>Workbook</code> object.
    * @since 0.2.0
    */
   public void transform(String inFilename, String outFilename, List<String> templateSheetNamesList,
      List<String> newSheetNamesList, List<Map<String, Object>> beansList)
      throws IOException, InvalidFormatException
   {
      FileOutputStream fileOut = null;
      InputStream fileIn = null;
      Workbook workbook;
      try
      {
         fileOut = new FileOutputStream(outFilename);
         fileIn = new BufferedInputStream(new FileInputStream(inFilename));
         workbook = transform(fileIn, templateSheetNamesList, newSheetNamesList, beansList);
         workbook.write(fileOut);
      }
      finally
      {
         if (fileIn != null)
         {
            try { fileIn.close(); } catch (IOException ignored) {}
         }
         if (fileOut != null)
         {
            try { fileOut.close(); } catch (IOException ignored) {}
         }
      }
   }

   /**
    * Transforms the template Excel spreadsheet represented by the given
    * <code>InputStream</code>.  If a sheet name is represented <em>n</em>
    * times in the list of template sheet names, then it will cloned to make
    * <em>n</em> total copies and the clones will receive the corresponding
    * sheet name from the list of sheet names.  Each resulting sheet has a
    * corresponding <code>Map</code> of bean names to bean values exposed to
    * it.
    * @param is The <code>InputStream</code> from the template spreadsheet.
    * @param templateSheetNamesList A <code>List</code> of template sheet
    *    names, with duplicates indicating to clone sheets.
    * @param newSheetNamesList A <code>List</code> of resulting sheet names
    *    corresponding to the template sheet names list.
    * @param beansList A <code>List</code> of <code>Maps</code> representing
    *    the beans map exposed to each resulting sheet.
    * @return A new <code>Workbook</code> object capable of being written to an
    *    <code>OutputStream</code>.
    * @throws IOException If there is a problem reading the template Excel
    *    spreadsheet.
    * @throws InvalidFormatException If there is a problem creating a
    *    <code>Workbook</code> object.
    */
   public Workbook transform(InputStream is, List<String> templateSheetNamesList,
      List<String> newSheetNamesList, List<Map<String, Object>> beansList)
      throws IOException, InvalidFormatException
   {
      Workbook workbook = WorkbookFactory.create(is);
      String prevSheetName = "";
      if (DEBUG)
      {
         System.err.println("templateSheetNamesList.size()=" + templateSheetNamesList.size());
         System.err.println("newSheetNamesList.size()=" + newSheetNamesList.size());
         System.err.println("beansList.size()=" + beansList.size());
      }
      for (int i = 0; i < templateSheetNamesList.size(); i++)
      {
         if (DEBUG)
         {
            for (int j = 0; j < workbook.getNumberOfSheets(); j++)
               System.err.println("  Before: Sheet(" + j + "): \"" + workbook.getSheetAt(j).getSheetName() + "\".");
         }
         String templateSheetName = templateSheetNamesList.get(i);
         String newSheetName = newSheetNamesList.get(i);
         if (prevSheetName.equals(templateSheetName))
         {
            // Clone the previous sheet, name it, and reposition it.
            if (DEBUG)
               System.err.println("Cloning sheet at position " + (i - 1) + ".");
            workbook.cloneSheet(i - 1);
            if (DEBUG)
               System.err.println("Setting sheet name at position " +
                  (workbook.getNumberOfSheets() - 1) + " to \"" + newSheetName + "\".");
            workbook.setSheetName(workbook.getNumberOfSheets() - 1, newSheetName);
            if (DEBUG)
               System.err.println("Moving sheet \"" + newSheetName + "\" to position " + i + ".");
            workbook.setSheetOrder(newSheetName, i);
         }
         else
         {
            // Rename the sheet.
            if (DEBUG)
               System.err.println("Renaming sheet at position " + i + " to \"" + newSheetName + "\".");
            workbook.setSheetName(i, newSheetName);
         }
         prevSheetName = templateSheetName;
         if (DEBUG)
         {
            for (int j = 0; j < workbook.getNumberOfSheets(); j++)
               System.err.println("  After: Sheet(" + j + "): \"" + workbook.getSheetAt(j).getSheetName() + "\".");
         }
      }

      SheetTransformer sheetTransformer = new SheetTransformer();
      WorkbookContext context = createContext(workbook, sheetTransformer);
      if (DEBUG)
         System.err.println("number of Sheets=" + workbook.getNumberOfSheets());
      int numItemsProcessed = 0;
      for (int i = 0; i < workbook.getNumberOfSheets(); i++)
      {
         // Allow extra sheets found to be left alone and untouched.
         if (numItemsProcessed < beansList.size())
         {
            Map<String, Object> beans = beansList.get(i);
            exposeWorkbook(beans, workbook);
            Sheet sheet = workbook.getSheetAt(i);
            // This is done for performance reasons, related to identifying
            // collection names in expression text, which may vary from beans
            // map to beans map.
            Expression.clearExpressionToCollNamesMap();
            sheetTransformer.transform(sheet, context, beans);
         }
         numItemsProcessed++;
      }
      if (!context.getFormulaMap().isEmpty())
      {
         replaceFormulas(workbook, context, sheetTransformer);
      }
      return workbook;
   }

   /**
    * Creates a <code>WorkbookContext</code> for a <code>Workbook</code>.
    * @param workbook The <code>Workbook</code>.
    * @param transformer A <code>SheetTransformer</code>.
    * @return A <code>WorkbookContext</code>.
    */
   private WorkbookContext createContext(Workbook workbook, SheetTransformer transformer)
   {
      WorkbookContext context = new WorkbookContext();
      context.setCellListeners(myCellListeners);
      context.setRegistry(myRegistry);
      context.setFixedSizeCollectionNames(myFixedSizeCollectionNames);
      context.setNoImplicitCollectionProcessingNames(myNoImplicitProcessingCollectionNames);
      Map<String, Formula> formulaMap = createFormulaMap(workbook, transformer);
      context.setFormulaMap(formulaMap);
      Map<String, List<CellRef>> cellRefMap = FormulaUtil.createCellRefMap(formulaMap);
      context.setCellRefMap(cellRefMap);
      if (DEBUG)
      {
         System.err.println("Formula Map:");
         for (String key : formulaMap.keySet())
         {
            System.err.println("  " + key + " => " + formulaMap.get(key));
         }
         System.err.println("Cell Ref Map:");
         for (String key : cellRefMap.keySet())
         {
            List<CellRef> cellRefs = cellRefMap.get(key);
            System.err.print("  " + key + " => [");
            for (CellRef cellRef : cellRefs)
               System.err.print(cellRef.formatAsString() + ",");
            System.err.println("]");
         }
      }
      return context;
   }

   /**
    * Searches for <code>Formulas</code> in the given <code>Workbook</code>.
    * @param workbook The <code>Workbook</code> in which to search.
    * @param transformer A <code>SheetTransformer</code> that searches
    *    individual <code>Sheets</code> within <code>workbook</code>.
    * @return A <code>Map</code> of strings to <code>Formulas</code>.  The keys
    *    are strings of the format "sheetName!formulaText".
    */
   private Map<String, Formula> createFormulaMap(Workbook workbook, SheetTransformer transformer)
   {
      Map<String, Formula> formulaMap = new HashMap<String, Formula>();
      for (int i = 0; i < workbook.getNumberOfSheets(); i++)
      {
         Sheet sheet = workbook.getSheetAt(i);
         transformer.gatherFormulas(sheet, formulaMap);
      }
      return formulaMap;
   }

   /**
    * Replace all <code>Formulas</code> in the <code>Workbook</code> with Excel
    * formulas, e.g. "$[SUM(C2)]" becomes "=SUM(C2:C6)".
    * @param workbook The <code>Workbook</code>.
    * @param context The <code>WorkbookContext</code>.
    * @param transformer A <code>SheetTransformer</code>.
    */
   private void replaceFormulas(Workbook workbook, WorkbookContext context, SheetTransformer transformer)
   {
      Map<String, List<CellRef>> cellRefMap = context.getCellRefMap();
      FormulaUtil.findAndReplaceCellRanges(cellRefMap);
      if (DEBUG)
      {
         Map<String, Formula> formulaMap = context.getFormulaMap();
         System.err.println("Formula Map after transformation:");
         for (String key : formulaMap.keySet())
         {
            System.err.println("  " + key + " => " + formulaMap.get(key));
         }
         System.err.println("CellRefMap after transformation and cell ranges detected and replaced:");
         for (String key : cellRefMap.keySet())
         {
            System.err.print("  " + key + " => [");
            for (CellRef cellRef : cellRefMap.get(key))
               System.err.print(cellRef.formatAsString() + ",");
            System.err.println("]");
         }
      }
      for (int i = 0; i < workbook.getNumberOfSheets(); i++)
      {
         Sheet sheet = workbook.getSheetAt(i);
         transformer.replaceFormulas(sheet, context);
      }
   }

   /**
    * Make the <code>Workbook</code> object available as a bean in the given
    * <code>Map</code> of beans.
    * @param beans The <code>Map</code> of beans.
    * @param workbook The <code>Workbook</code> to expose.
    */
   private void exposeWorkbook(Map<String, Object> beans, Workbook workbook)
   {
      beans.put("workbook", workbook);
   }
}


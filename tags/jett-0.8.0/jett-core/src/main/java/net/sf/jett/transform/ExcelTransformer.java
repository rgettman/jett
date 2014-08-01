package net.sf.jett.transform;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import net.sf.jett.event.CellListener;
import net.sf.jett.event.SheetListener;
import net.sf.jett.expression.Expression;
import net.sf.jett.expression.ExpressionFactory;
import net.sf.jett.formula.CellRef;
import net.sf.jett.formula.Formula;
//import net.sf.jett.lwxssf.LWXSSFWorkbook;
import net.sf.jett.model.CellStyleCache;
import net.sf.jett.model.FontCache;
import net.sf.jett.model.MissingCloneSheetProperties;
import net.sf.jett.model.Style;
import net.sf.jett.model.WorkbookContext;
import net.sf.jett.parser.StyleParser;
import net.sf.jett.tag.JtTagLibrary;
import net.sf.jett.tag.NameTag;
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
 * <p>There are six entry-point methods that accomplish all of the work, all
 * with the overloaded name "transform".  The first 3 apply all bean values to
 * all sheets.  The third method does the work; the preceding 2 each call it
 * to perform the actual transformation.  The last 3 apply specific sets of
 * bean values to specific sheets.  The last method does the work; the
 * preceding 2 each call it to perform the actual transformation.</p>
 * <ul>
 * <li><code>public void transform(String inFilename, String outFilename, Map&lt;String, Object&gt; beans)
 *    throws IOException, InvalidFormatException</code></li>
 * <li><code>public Workbook transform(InputStream is, Map&lt;String, Object&gt; beans)
 *    throws IOException, InvalidFormatException</code></li>
 * <li><code>public void transform(Workbook workbook, Map&lt;String, Object&gt; beans)</code></li>
 * <li><code>public void transform(String inFilename, String outFilename, List&lt;String&gt; templateSheetNamesList,
      List&lt;String&gt; newSheetNamesList, List&lt;Map&lt;String, Object&gt;&gt; beansList)
      throws IOException, InvalidFormatException</code></li>
 * <li><code>public Workbook transform(InputStream is, List&lt;String&gt; templateSheetNamesList, List&lt;String&gt; newSheetNamesList,
 *    List&lt;Map&lt;String, Object&gt;&gt; beansList) throws IOException, InvalidFormatException</code></li>
 * <li><code>public void transform(Workbook workbook, List&lt;String&gt; templateSheetNamesList,
      List&lt;String&gt; newSheetNamesList, List&lt;Map&lt;String, Object&gt;&gt; beansList)</code></li>
 * </ul>
 * <p>The first method reads the template spreadsheet from the input filename,
 * transforms the spreadsheet by calling the third method, and writes the
 * transformed spreadsheet to the output filename.</p>
 * <p>The second method reads the template spreadsheet from the given input
 * stream (usually a file), transforms the spreadsheet by calling the third
 * method, and returs a <code>Workbook</code> object representing the
 * transformed spreadsheet, which can be written to a file if desired.</p>
 * <p>The third method performs the actual transformation on a
 * <code>Workbook</code>, applying bean values to all sheets.</p>
 * <p>The fourth method reads the template spreadsheet from the input filename,
 * transforms the spreadsheet by calling the sixth method, and writes the
 * transformed spreadsheet to the output filename.</p>
 * <p>The fifth method reads the template spreadsheet from the given input
 * stream (usually a file), transforms the spreadsheet by calling the sixth
 * method, and returs a <code>Workbook</code> object representing the
 * transformed spreadsheet, which can be written to a file if desired.</p>
 * <p>The sixth method performs the actual transformation on a
 * <code>Workbook</code>, applying specific bean values to specific sheets.</p>
 * <p>The <code>ExcelTransformer</code>'s settings can be changed with the
 * other public methods of this class, including recognizing custom tag
 * libraries, adding <code>CellListeners</code>, using fixed size collections,
 * turning off implicit collections processing, passing <code>silent</code> and
 * <code>lenient</code> flags through to the underlying JEXL Engine, passing a
 * cache size to the internal JEXL Engine, passing namespace objects to
 * register custom functions in the JEXL Engine, and passing CSS files/text to
 * be recognized by the {@link net.sf.jett.tag.StyleTag} later.</p>
 *
 * @author Randy Gettman
 */
public class ExcelTransformer
{
   private static final boolean DEBUG = false;

   private TagLibraryRegistry myRegistry;
   private List<CellListener> myCellListeners;
   private List<SheetListener> mySheetListeners;
   private List<String> myFixedSizeCollectionNames;
   private List<String> myNoImplicitProcessingCollectionNames;
   private Map<String, Style> myStyleMap;
   private boolean amIEvaluatingFormulas;
   private boolean amIForcingRecalculationOnOpening;
   private boolean amIChangingForcingRecalculation;

   /**
    * Construct an <code>ExcelTransformer</code>.
    */
   public ExcelTransformer()
   {
      myRegistry = new TagLibraryRegistry();
      registerTagLibrary("jt", JtTagLibrary.getJtTagLibrary());
      myCellListeners = new ArrayList<CellListener>();
      mySheetListeners = new ArrayList<SheetListener>();
      myFixedSizeCollectionNames = new ArrayList<String>();
      myNoImplicitProcessingCollectionNames = new ArrayList<String>();
      myStyleMap = new HashMap<String, Style>();
      amIEvaluatingFormulas = false;
      amIForcingRecalculationOnOpening = false;
      amIChangingForcingRecalculation = false;
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
    * Registers the given <code>SheetListener</code>.
    * @param listener A <code>SheetListener</code>.
    * @since 0.8.0
    */
   public void addSheetListener(SheetListener listener)
   {
      if (listener != null)
         mySheetListeners.add(listener);
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
    * <p>Register one or more style definitions, without having to read them
    * from a file.  Style definitions are of the format (whitespace is
    * ignored):</p>
    * <code>[.styleName { [propertyName: value [; propertyName: value]* }]*</code>
    * <p>These style names are recognized by the "class" attribute of the
    * "style" tag.</p>
    * @param cssText A string containing one or more style definitions.
    * @throws net.sf.jett.exception.StyleParseException If there is a problem
    *    parsing the style definition text.
    * @see net.sf.jett.tag.StyleTag
    * @since 0.5.0
    */
   public void addCssText(String cssText)
   {
      StyleParser parser = new StyleParser(cssText);
      parser.parse();
      myStyleMap.putAll(parser.getStyleMap());
   }

   /**
    * <p>Register a file containing CSS-like style definitions.  Style
    * definitions are of the format (whitespace is ignored):</p>
    * <code>[.styleName { [propertyName: value [; propertyName: value]* }]*</code>
    *
    * <p>These style names are recognized by the "class" attribute of the
    * "style" tag.</p>
    * @param filename The name of a file containing CSS-like style definitions.
    * @throws IOException If there is a problem reading the file.
    * @throws net.sf.jett.exception.StyleParseException If there is a problem
    *    parsing the style definition text.
    * @see net.sf.jett.tag.StyleTag
    * @since 0.5.0
    */
   public void addCssFile(String filename) throws IOException
   {
      StringBuilder buf = new StringBuilder();
      BufferedReader reader = null;
      String line;
      try
      {
         reader = new BufferedReader(new FileReader(filename));
         while ((line = reader.readLine()) != null)
         {
            buf.append(line);
            buf.append("\n");
         }
         addCssText(buf.toString());
      }
      finally
      {
         if (reader != null)
         {
            try { reader.close(); }
            catch (IOException ignored) {}
         }
      }
   }

   /**
    * After transformation, this determines whether JETT will evaluate all
    * formulas and store their results in the <code>Workbook</code>.  This
    * defaults to <code>false</code>.  If this is not set, then other tools may
    * or may not evaluate the formulas in the workbook.  If this is set, then
    * the results will be stored, assuming that all formulas evaluated are
    * supported by the underlying Apache POI library.
    * @param evaluate Whether to have JETT evaluate all formulas and store
    *    their resuls.
    * @since 0.8.0
    */
   public void setEvaluateFormulas(boolean evaluate)
   {
      amIEvaluatingFormulas = evaluate;
   }

   /**
    * After transformation, if this was called, then JETT will set whether to
    * force recalculation of formulas when Excel opens this workbook.  If this
    * is not called, then JETT will not change any value that may be present
    * already in the workbook.  This will not control whether JETT will attempt
    * to evaluate all formulas; it will set or clear a flag that controls
    * whether Excel will recalculate all formulas when it opens the workbook.
    * @param forceRecalc The flag for Excel to determine whether to recalculate
    *    all formulas when opening the workbook.
    * @since 0.8.0
    */
   public void setForceRecalculationOnOpening(boolean forceRecalc)
   {
      amIChangingForcingRecalculation = true;
      amIForcingRecalculationOnOpening = forceRecalc;
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
      try
      {
         fileOut = new FileOutputStream(outFilename);
         Workbook workbook = WorkbookFactory.create(new File(inFilename));
         transform(workbook, beans);
         workbook.write(fileOut);
      }
      finally
      {
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
      transform(workbook, beans);
      return workbook;
   }

   /**
    * Transforms the template Excel spreadsheet represented by the given
    * <code>Workbook</code>.  Applies the given <code>Map</code> of beans
    * to all sheets.
    * @param workbook A <code>Workbook</code> object.  Transformation is
    *    performed directly on this object.
    * @param beans The <code>Map</code> of bean names to bean objects.
    * @since 0.6.0
    */
   public void transform(Workbook workbook, Map<String, Object> beans)
   {
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
      postTransformation(workbook, context, sheetTransformer);
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
      transform(workbook, templateSheetNamesList, newSheetNamesList, beansList);
      return workbook;
   }

   /**
    * Transforms the template Excel spreadsheet represented by the given
    * <code>Workbook</code>.  If a sheet name is represented <em>n</em>
    * times in the list of template sheet names, then it will cloned to make
    * <em>n</em> total copies and the clones will receive the corresponding
    * sheet name from the list of sheet names.  Each resulting sheet has a
    * corresponding <code>Map</code> of bean names to bean values exposed to
    * it.
    * @param workbook A <code>Workbook</code> object.  Transformation is
    *    performed directly on this object.
    * @param templateSheetNamesList A <code>List</code> of template sheet
    *    names, with duplicates indicating to clone sheets.
    * @param newSheetNamesList A <code>List</code> of resulting sheet names
    *    corresponding to the template sheet names list.
    * @param beansList A <code>List</code> of <code>Maps</code> representing
    *    the beans map exposed to each resulting sheet.
    * @since 0.6.0
    */
   public void transform(Workbook workbook, List<String> templateSheetNamesList,
      List<String> newSheetNamesList, List<Map<String, Object>> beansList)
   {
      Map<String, Integer> firstReferencedSheets = new HashMap<String, Integer>();
      final List<MissingCloneSheetProperties> missingPropertiesList = new ArrayList<MissingCloneSheetProperties>();
      if (DEBUG)
      {
         System.err.println("templateSheetNamesList.size()=" + templateSheetNamesList.size());
         System.err.println("newSheetNamesList.size()=" + newSheetNamesList.size());
         System.err.println("beansList.size()=" + beansList.size());
      }
      // Note down any sheet properties that are known to be "messed up" when a
      // Sheet is cloned and/or moved.
      for (int i = 0; i < workbook.getNumberOfSheets(); i++)
      {
         missingPropertiesList.add(getMissingCloneSheetProperties(workbook.getSheetAt(i)));
      }

      // Clone and/or move sheets.
      for (int i = 0; i < templateSheetNamesList.size(); i++)
      {
         if (DEBUG)
         {
            for (int j = 0; j < workbook.getNumberOfSheets(); j++)
               System.err.println("  Before: Sheet(" + j + "): \"" + workbook.getSheetAt(j).getSheetName() + "\".");
         }
         String templateSheetName = templateSheetNamesList.get(i);
         String newSheetName = newSheetNamesList.get(i);
         if (firstReferencedSheets.containsKey(templateSheetName))
         {
            int prevIndex = firstReferencedSheets.get(templateSheetName);
            // Clone the previously referenced sheet, name it, and reposition it.
            if (DEBUG)
               System.err.println("Cloning sheet at position " + prevIndex + ".");

            MissingCloneSheetProperties cloned = new MissingCloneSheetProperties(missingPropertiesList.get(prevIndex));

            workbook.cloneSheet(prevIndex);

            if (DEBUG)
               System.err.println("Setting sheet name at position " +
                  (workbook.getNumberOfSheets() - 1) + " to \"" + newSheetName + "\".");

            int clonePos = workbook.getNumberOfSheets() - 1;
            workbook.setSheetName(clonePos, newSheetName);
            cloneNamedRanges(workbook, prevIndex);

            if (DEBUG)
               System.err.println("Moving sheet \"" + newSheetName + "\" to position " + i + ".");

            workbook.setSheetOrder(newSheetName, i);
            updateNamedRangesScope(workbook, clonePos, i);

            missingPropertiesList.add(i, cloned);
         }
         else
         {
            // Find the sheet.
            int index = workbook.getSheetIndex(templateSheetName);
            if (index == -1)
               throw new RuntimeException("Template Sheet \"" + templateSheetName + "\" not found!");

            // Rename the sheet and move it to the current position.
            if (DEBUG)
               System.err.println("Renaming sheet at position " + index + " to \"" + newSheetName + "\".");

            workbook.setSheetName(index, newSheetName);

            if (index != i)
            {
               if (DEBUG)
                  System.err.println("Moving sheet at position " + index + " to " + i + ".");

               MissingCloneSheetProperties move = missingPropertiesList.remove(index);

               workbook.setSheetOrder(newSheetName, i);
               updateNamedRangesScope(workbook, index, i);

               missingPropertiesList.add(i, move);
            }
            firstReferencedSheets.put(templateSheetName, i);
         }
         if (DEBUG)
         {
            for (int j = 0; j < workbook.getNumberOfSheets(); j++)
               System.err.println("  After: Sheet(" + j + "): \"" + workbook.getSheetAt(j).getSheetName() + "\".");
         }
      }

      SheetTransformer sheetTransformer = new SheetTransformer();
      WorkbookContext context = createContext(workbook, sheetTransformer, templateSheetNamesList, newSheetNamesList);
      FormulaUtil.updateSheetNameRefsAfterClone(context);
      if (DEBUG)
         System.err.println("number of Sheets=" + workbook.getNumberOfSheets());

      int numItemsProcessed = 0;
      SheetTransformer.AfterOffSheetProperties missingPropertiesSetter = new SheetTransformer.AfterOffSheetProperties() {
         /**
          * Apply the missing clone sheet properties.
          * @param sheet The given <code>Sheet</code>.
          * @since 0.7.0
          */
         public void applySettings(Sheet sheet)
         {
            replaceMissingCloneSheetProperties(sheet, missingPropertiesList.get(sheet.getWorkbook().getSheetIndex(sheet)));
         }
      };

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
            sheetTransformer.transform(sheet, context, beans, missingPropertiesSetter);
         }
         numItemsProcessed++;
      }
      postTransformation(workbook, context, sheetTransformer);
   }

   /**
    * Clones all named ranges that are scoped to the <code>Sheet</code> at the
    * given index, and scopes the newly cloned named ranges to the last sheet
    * in the workbook, where it is assumed that the cloned sheet still exists.
    * @param workbook A <code>Workbook</code>.
    * @param prevIndex The 0-based sheet index from which to clone named
    *    ranges.
    * @since 0.8.0
    */
   private void cloneNamedRanges(Workbook workbook, int prevIndex)
   {
      int numNamedRanges = workbook.getNumberOfNames();
      int clonedSheetIndex = workbook.getNumberOfSheets() - 1;
      for (int i = 0; i < numNamedRanges; i++)
      {
         Name name = workbook.getNameAt(i);
         // Avoid copying Excel's "built-in" (and hidden) named ranges.
         if (name.getSheetIndex() == prevIndex && !NameTag.EXCEL_BUILT_IN_NAMES.contains(name.getNameName()))
         {
            Name clone = workbook.createName();
            // This will be a sheet-scoped clone of a name that could be workbook-scoped.
            clone.setSheetIndex(clonedSheetIndex);
            clone.setNameName(name.getNameName());
            clone.setComment(name.getComment());
            clone.setFunction(name.isFunctionName());
            clone.setRefersToFormula(name.getRefersToFormula());
         }
      }
   }

   /**
    * The sheet order has changed; a <code>Sheet</code> has been moved from one
    * position to another.  Apache POI doesn't change the scopes of named
    * ranges to match this change.  This accomplishes the task here.
    * @param workbook The <code>Workbook</code> on which a sheet was moved.
    * @param fromIndex The 0-based previous index of the <code>Sheet</code>
    *    that was moved.
    * @param toIndex The 0-based current index of the <code>Sheet</code> that
    *    was moved.
    * @since 0.8.0
    */
   private void updateNamedRangesScope(Workbook workbook, int fromIndex, int toIndex)
   {
      if (fromIndex != toIndex)
      {
         int numNamedRanges = workbook.getNumberOfNames();
         for (int i = 0; i < numNamedRanges; i++)
         {
            Name name = workbook.getNameAt(i);
            int scopeIndex = name.getSheetIndex();
            if (scopeIndex == fromIndex)
            {
               name.setSheetIndex(toIndex);
            }
            else if (fromIndex < scopeIndex && scopeIndex < toIndex)
            {
               name.setSheetIndex(scopeIndex - 1);
            }
            else if (toIndex < scopeIndex && scopeIndex < fromIndex)
            {
               name.setSheetIndex(scopeIndex + 1);
            }
         }
      }
   }

   /**
    * Perform post-transformation processing.  This currently includes
    * replacing all JETT formulas with Excel formulas, recalculating all
    * formulas, and/or marking the workbook to be recalculated when Excel opens
    * it.
    * @param workbook The <code>Workbook</code>.
    * @param context The <code>WorkbookContext</code>.
    * @param sheetTransformer The <code>SheetTransformer</code> used to
    *    transform the sheets.
    * @since 0.8.0
    */
   private void postTransformation(Workbook workbook, WorkbookContext context, SheetTransformer sheetTransformer)
   {
      if (!context.getFormulaMap().isEmpty())
      {
         replaceFormulas(workbook, context, sheetTransformer);
      }
      if (amIEvaluatingFormulas)
      {
         workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
      }
      if (amIChangingForcingRecalculation)
      {
         workbook.setForceFormulaRecalculation(amIForcingRecalculationOnOpening);
      }
   }

   /**
    * Copies the properties that won't be properly copied upon cloning and/or
    * moving the given <code>Sheet</code>.
    * @param sheet The <code>Sheet</code> on which to copy properties.
    * @return A <code>MissingCloneSheetProperties</code>.
    * @since 0.7.0
    */
   private MissingCloneSheetProperties getMissingCloneSheetProperties(Sheet sheet)
   {
      MissingCloneSheetProperties mcsp = new MissingCloneSheetProperties();
      PrintSetup ps = sheet.getPrintSetup();

      mcsp.setRepeatingColumns(sheet.getRepeatingColumns());
      mcsp.setRepeatingRows(sheet.getRepeatingRows());

      mcsp.setCopies(ps.getCopies());
      mcsp.setDraft(ps.getDraft());
      mcsp.setFitHeight(ps.getFitHeight());
      mcsp.setFitWidth(ps.getFitWidth());
      mcsp.setHResolution(ps.getHResolution());
      mcsp.setLandscape(ps.getLandscape());
      mcsp.setNoColor(ps.getNoColor());
      mcsp.setLeftToRight(ps.getLeftToRight());
      mcsp.setNotes(ps.getNotes());
      mcsp.setPageStart(ps.getPageStart());
      mcsp.setPaperSize(ps.getPaperSize());
      mcsp.setScale(ps.getScale());
      mcsp.setUsePage(ps.getUsePage());
      mcsp.setValidSettings(ps.getValidSettings());
      mcsp.setVResolution(ps.getVResolution());

      return mcsp;
   }

   /**
    * Copies the properties that weren't properly copied upon cloning and/or
    * moving the given <code>Sheet</code> back into the sheet.
    * @param sheet The <code>Sheet</code> on which to restore properties.
    * @param mcsp The properties to copy back to the sheet.
    * @since 0.7.0
    */
   private void replaceMissingCloneSheetProperties(Sheet sheet, MissingCloneSheetProperties mcsp)
   {
      PrintSetup ps = sheet.getPrintSetup();

      // Missing properties for any case.
      sheet.setRepeatingColumns(mcsp.getRepeatingColumns());
      sheet.setRepeatingRows(mcsp.getRepeatingRows());

      // Missing properties for XSSF only.
      if (sheet instanceof XSSFSheet)
      {
         ps.setCopies(mcsp.getCopies());
         ps.setDraft(mcsp.isDraft());
         ps.setFitHeight(mcsp.getFitHeight());
         ps.setFitWidth(mcsp.getFitWidth());
         ps.setHResolution(mcsp.getHResolution());
         ps.setLandscape(mcsp.isLandscape());
         ps.setLeftToRight(mcsp.isLeftToRight());
         ps.setNoColor(mcsp.isNoColor());
         ps.setNotes(mcsp.isNotes());
         ps.setPageStart(mcsp.getPageStart());
         ps.setPaperSize(mcsp.getPaperSize());
         ps.setScale(mcsp.getScale());
         ps.setUsePage(mcsp.isUsePage());
         ps.setValidSettings(mcsp.isValidSettings());
         ps.setVResolution(mcsp.getVResolution());
      }
   }

   /**
    * Creates a <code>WorkbookContext</code> for a <code>Workbook</code>.
    * @param workbook The <code>Workbook</code>.
    * @param transformer A <code>SheetTransformer</code>.
    * @return A <code>WorkbookContext</code>.
    */
   public WorkbookContext createContext(Workbook workbook, SheetTransformer transformer)
   {
      return createContext(workbook, transformer, new ArrayList<String>(), new ArrayList<String>());
   }

   /**
    * Creates a <code>WorkbookContext</code> for a <code>Workbook</code>.
    * @param workbook The <code>Workbook</code>.
    * @param transformer A <code>SheetTransformer</code>.
    * @param templateSheetNames A <code>List</code> of template sheet names,
    *    from the <code>transform</code> method.
    * @param sheetNames A <code>List</code> of sheet names, from the
    *    <code>transform</code> method.
    * @return A <code>WorkbookContext</code>.
    * @since 0.8.0
    */
   public WorkbookContext createContext(Workbook workbook, SheetTransformer transformer,
      List<String> templateSheetNames, List<String> sheetNames)
   {
      WorkbookContext context = new WorkbookContext();
      context.setCellListeners(myCellListeners);
      context.setSheetListeners(mySheetListeners);
      context.setRegistry(myRegistry);
      context.setFixedSizeCollectionNames(myFixedSizeCollectionNames);
      context.setNoImplicitCollectionProcessingNames(myNoImplicitProcessingCollectionNames);
      Map<String, Formula> formulaMap = createFormulaMap(workbook, transformer);
      context.setFormulaMap(formulaMap);
      Map<String, List<CellRef>> cellRefMap = FormulaUtil.createCellRefMap(formulaMap);
      context.setCellRefMap(cellRefMap);
      CellStyleCache csCache = new CellStyleCache(workbook);
      context.setCellStyleCache(csCache);
      FontCache fCache = new FontCache(workbook);
      context.setFontCache(fCache);
      context.setStyleMap(myStyleMap);
      context.setTemplateSheetNames(templateSheetNames);
      context.setSheetNames(sheetNames);
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
      Map<String, Formula> formulaMap = context.getFormulaMap();
      Map<String, List<CellRef>> cellRefMap = context.getCellRefMap();
      FormulaUtil.findAndReplaceCellRanges(cellRefMap);
      if (DEBUG)
      {
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
      // Replaced named range formulas that had JETT formulas present in the
      // formula map.
      int numNamedRanges = workbook.getNumberOfNames();
      for (String key : formulaMap.keySet())
      {
         // Look for a "?", which must be present in the keys for all formulas
         // created from a NameTag, but won't be present in the keys for normal
         // JETT formulas, because "?" is an illegal character for an Excel
         // sheet name.
         int questionMark = key.indexOf("?");
         if (questionMark == -1)
            continue;

         int exclamation = key.indexOf("!");
         if (exclamation == -1)
         {
            throw new IllegalStateException("Expected '!' character not found in formula key \"" + key + "\"!");
         }
         // sheetName!namedRangeName?[scope]
         String sheetName = key.substring(0, exclamation);
         String namedRangeName = key.substring(exclamation + 1, questionMark);
         String scopeSheetName = key.substring(questionMark + 1);

         int sheetScopeIndex = -1; // workbook scope
         if (scopeSheetName != null && scopeSheetName.length() > 0)
         {
            sheetScopeIndex = workbook.getSheetIndex(scopeSheetName);
         }

         Name namedRange = null;
         for (int i = 0; i < numNamedRanges; i++)
         {
            Name n = workbook.getNameAt(i);
            if (n.getNameName().equals(namedRangeName) &&
                n.getSheetIndex() == sheetScopeIndex)
            {
               namedRange = n;
               break;
            }
         }

         if (namedRange != null)
         {
            Formula formula = formulaMap.get(key);
            if (formula != null)
            {
               // Replace all original cell references with translated cell references.
               String excelFormula = FormulaUtil.createExcelFormulaString(formula.getFormulaText(), formula, sheetName, context);
               if (DEBUG)
               {
                  System.err.println("  For named range " + namedRangeName +
                          ", scope " + ("".equals(scopeSheetName) ? "workbook" : ("\"" + scopeSheetName + "\"")) +
                          ", mapped to " + formula +
                          ", replacing formula \"" + formula.getFormulaText() + "\" with \"" + excelFormula + "\".");
               }
               namedRange.setRefersToFormula(excelFormula);
            }
         }
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

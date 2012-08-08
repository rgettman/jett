package net.sf.jett.tag;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;

import net.sf.jett.exception.TagParseException;
import net.sf.jett.transform.WorkbookContext;
import net.sf.jett.util.SheetUtil;

/**
 * The abstract class <code>BaseTag</code> provides common functionality to all
 * <code>Tags</code>.
 */
public abstract class BaseTag implements Tag
{
   private Map<String, RichTextString> myAttributes;
   private TagContext myContext;
   private WorkbookContext myWorkbookContext;
   private boolean amIBodiless;

   /**
    * When a <code>Tag</code> is created, the attributes are passed in via a
    * (possibly empty) <code>Map</code> of attribute names and values.
    * @param attributes A <code>Map</code> of attribute names and values.
    */
   public void setAttributes(Map<String, RichTextString> attributes)
   {
      myAttributes = attributes;
   }

   /**
    * Returns the <code>Map</code> of attribute names and attribute values.
    * @return The <code>Map</code> of attribute names and attribute values.
    */
   public Map<String, RichTextString> getAttributes()
   {
      return myAttributes;
   }

   /**
    * Sets the <code>TagContext</code> to which the <code>Tag</code> is
    * associated.
    * @param context A <code>TagContext</code>.
    */
   public void setContext(TagContext context)
   {
      myContext = context;
   }

   /**
    * Returns the <code>WorkbookContext</code> to which the <code>Tag</code> is
    * associated.
    * @return The associated <code>WorkbookContext</code>.
    */
   public WorkbookContext getWorkbookContext()
   {
      return myWorkbookContext;
   }

   /**
    * Sets the <code>WorkbookContext</code> to which the <code>Tag</code> is
    * associated.
    * @param context A <code>WorkbookContext</code>.
    */
   public void setWorkbookContext(WorkbookContext context)
   {
      myWorkbookContext = context;
   }

   /**
    * Returns the <code>TagContext</code> to which the <code>Tag</code> is
    * associated.
    * @return The associated <code>TagContext</code>.
    */
   public TagContext getContext()
   {
      return myContext;
   }

   /**
    * Sets whether this <code>Tag</code> is bodiless.
    * @param bodiless <code>true</code> if this tag does not have a body,
    *    <code>false</code> if this tag does have a body.
    */
   public void setBodiless(boolean bodiless)
   {
      amIBodiless = bodiless;
   }

   /**
    * Returns whether this <code>Tag</code> is bodiless.
    * @return <code>true</code> if this tag does not have a body,
    *    <code>false</code> if this tag does have a body.
    */
   public boolean isBodiless()
   {
      return amIBodiless;
   }

   /**
    * <p>Checks the <code>Tag's</code> attributes to ensure that:</p>
    * <ol>
    * <li>All required attributes are present, and
    * <li>All attributes present are recognized.
    * <li>All attributes are validated through the method
    *    <code>validateAttributes</code> (i.e. that method doesn't throw an
    *    <code>Exception</code>.
    * </ol>
    * <p>A <code>TagParseException</code> is thrown if not all conditions above
    * are not met.</p>
    * <p>This calls <code>validateAttributes</code> if all conditions are met.</p>
    * @throws TagParseException If the above listed conditions are not met.
    * @see #validateAttributes
    */
   public void checkAttributes()
   {
      Map<String, RichTextString> attributes = getAttributes();
      List<String> required = getRequiredAttributes();
      List<String> optional = getOptionalAttributes();
      // Ensure all required attributes are found.
      if (required != null)
      {
         for (String reqName : required)
         {
            if (!attributes.containsKey(reqName))
               throw new TagParseException("Required attribute \"" + reqName +
                  "\" not found for tag \"" + getName() + "\".");
         }
      }
      // Ensure all attributes are in either the required list or in the
      // optional list.
      Set<String> keys = attributes.keySet();
      for (String key : keys)
      {
         if ((required == null || !required.contains(key)) &&
             (optional == null || !optional.contains(key)))
            throw new TagParseException("Unrecognized attribute \"" + key +
               "\" for tag \"" + getName() + "\".");
      }

      // Validate the attributes.
      validateAttributes();
   }

   /**
    * Removes the <code>Block</code> of <code>Cells</code> associated with this
    * <code>Tag</code>.  This can be called by subclasses if it determines that
    * its <code>Block</code> needs to be removed and not processed.
    */
   protected void removeBlock()
   {
      TagContext context = getContext();
      Block block = context.getBlock();
      Sheet sheet = context.getSheet();
      SheetUtil.removeBlock(sheet, block, getWorkbookContext());
   }

   /**
    * Removes the content from the <code>Block</code> of <code>Cells</code>
    * associated with this <code>Tag</code>.  This can be called by subclasses
    * if it determines that its <code>Block</code> needs to have its content
    * removed.
    */
   protected void deleteBlock()
   {
      TagContext context = getContext();
      Block block = context.getBlock();
      Sheet sheet = context.getSheet();
      SheetUtil.deleteBlock(sheet, block, getWorkbookContext());
   }

   /**
    * Clears the content from the <code>Block</code> of <code>Cells</code>
    * associated with this <code>Tag</code>.  This can be called by subclasses
    * if it determines that its <code>Block</code> needs its contents cleared.
    */
   protected void clearBlock()
   {
      TagContext context = getContext();
      Block block = context.getBlock();
      Sheet sheet = context.getSheet();
      SheetUtil.clearBlock(sheet, block, getWorkbookContext());
   }

   /**
    * Returns a <code>List</code> of required attribute names.
    * @return A <code>List</code> of required attribute names.
    */
   protected abstract List<String> getRequiredAttributes();

   /**
    * Returns a <code>List</code> of optional attribute names.
    * @return A <code>List</code> of optional attribute names.
    */
   protected abstract List<String> getOptionalAttributes();

   /**
    * Validates the attributes according to <code>Tag</code>-specific rules.
    * @throws TagParseException If the attribute values are illegal or
    *    unacceptable.
    */
   protected abstract void validateAttributes() throws TagParseException;
}


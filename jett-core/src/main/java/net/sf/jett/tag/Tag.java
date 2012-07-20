package net.sf.jett.tag;

import java.util.Map;

import net.sf.jett.transform.WorkbookContext;

/**
 * <p>A <code>Tag</code> represents an XML tag that can reside in a
 * <code>Cell</code>.  A <code>Tag</code> represents special processing inside
 * its own <code>Block</code> of <code>Cells</code> (the tag "body").  Each
 * <code>Tag</code> is responsible for processing its own <code>Block</code>.</p>
 *
 * <p>Any concrete subclass of <code>Tag</code> must have a public, no-argument
 * constructor.</p>
 */
public interface Tag
{
   /**
    * Returns the name of the <code>Tag</code>.
    * @return The name of the <code>Tag</code>.
    */
   public String getName();

   /**
    * When a <code>Tag</code> is created, the attributes are passed in via a
    * (possibly empty) <code>Map</code> of attribute names and values.
    * @param attributes A <code>Map</code> of attribute names and values.
    */
   public void setAttributes(Map<String, String> attributes);

   /**
    * Sets whether this <code>Tag</code> is bodiless.
    * @param bodiless <code>true</code> if this tag does not have a body,
    *    <code>false</code> if this tag does have a body.
    */
   public void setBodiless(boolean bodiless);

   /**
    * Returns whether this <code>Tag</code> is bodiless.
    * @return <code>true</code> if this tag does not have a body,
    *    <code>false</code> if this tag does have a body.
    */
   public boolean isBodiless();

   /**
    * A <code>Tag</code> can retrieve its attributes by calling this method.
    * @return A <code>Map</code> of attribute names and attribute values.
    */
   public Map<String, String> getAttributes();

   /**
    * Sets the <code>TagContext</code> to which the <code>Tag</code> is
    * associated.
    * @param context A <code>TagContext</code>.
    */
   public void setContext(TagContext context);

   /**
    * Returns the <code>TagContext</code> to which the <code>Tag</code> is
    * associated.
    * @return The associated <code>TagContext</code>.
    */
   public TagContext getContext();

   /**
    * Sets the <code>WorkbookContext</code> to which the <code>Tag</code> is
    * associated.
    * @param context A <code>WorkbookContext</code>.
    */
   public void setWorkbookContext(WorkbookContext context);

   /**
    * Returns the <code>WorkbookContext</code> to which the <code>Tag</code> is
    * associated.
    * @return The associated <code>WorkbookContext</code>.
    */
   public WorkbookContext getWorkbookContext();

   /**
    * Checks the <code>Tag's</code> attributes to ensure that:
    * <ol>
    * <li>All required attributes are present, and
    * <li>All attributes present are recognized.
    * </ol>
    * A <code>TagParseException</code> is thrown if both conditions above are
    * not met.
    * @throws net.sf.jett.exception.TagParseException If the above listed
    *    conditions are not met.
    */
   public void checkAttributes();

   /**
    * Process this <code>Tag</code>.  The logic of the <code>Tag</code> is
    * performed in this method.
    * @return <code>true</code> if the <code>Cell</code> containing this
    *    <code>Tag</code> was transformed, <code>false</code> if it needs to be
    *    transformed again.  This may happen if the <code>Block</code>
    *    associated with the <code>Tag</code> was removed.
    */
   public boolean process();
}


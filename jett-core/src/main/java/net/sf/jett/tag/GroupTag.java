package net.sf.jett.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;

import net.sf.jett.exception.TagParseException;
import net.sf.jett.expression.Expression;
import net.sf.jett.transform.BlockTransformer;
import net.sf.jett.util.SheetUtil;

/**
 * <p>A <code>GroupTag</code> represents a set of rows or a set of columns that
 * needs an Excel "group" associated with it.  Optionally, it may be displayed
 * expanded (default) or collapsed.</p>
 *
 * <p>Attributes:</p>
 * <ul>
 * <li>groupDir (optional): <code>String</code>
 * <li>collapse (optional): <code>boolean</code>
 * </ul>
 *
 * @since 0.2.0
 */
public class GroupTag extends BaseTag
{
   /**
    * Attribute for specifying the direction of the grouping.  This defaults to
    * row grouping.
    * @see #GROUP_DIR_ROWS
    * @see #GROUP_DIR_COLS
    * @see #GROUP_DIR_NONE
    */
   public static final String ATTR_GROUP_DIR = "groupDir";
   /**
    * Attribute for specifying whether the group should be displayed collapsed.
    */
   public static final String ATTR_COLLAPSE = "collapse";

   /**
    * The "group dir" value to specify that columns should be grouped.
    */
   public static final String GROUP_DIR_COLS = "cols";
   /**
    * The "group dir" value to specify that rows should be grouped.
    */
   public static final String GROUP_DIR_ROWS = "rows";
   /**
    * The "group dir" value to specify that neither rows nor columns should be
    * grouped.
    */
   public static final String GROUP_DIR_NONE = "none";

   private static final List<String> OPT_ATTRS =
      new ArrayList<String>(Arrays.asList(ATTR_GROUP_DIR, ATTR_COLLAPSE));

   private Block.Direction myGroupDir;
   private boolean amICollapsed;

   /**
    * Returns this <code>Tag's</code> name.
    * @return This <code>Tag's</code> name.
    */
   public String getName()
   {
      return "group";
   }

   /**
    * Returns a <code>List</code> of required attribute names.
    * @return A <code>List</code> of required attribute names.
    */
   protected List<String> getRequiredAttributes()
   {
      return new ArrayList<String>();
   }

   /**
    * Returns a <code>List</code> of optional attribute names.
    * @return A <code>List</code> of optional attribute names.
    */
   protected List<String> getOptionalAttributes()
   {
      return OPT_ATTRS;
   }

   /**
    * Validates the attributes for this <code>Tag</code>.  This tag must have a
    * body.
    */
   @SuppressWarnings("unchecked")
   public void validateAttributes() throws TagParseException
   {
      if (isBodiless())
         throw new TagParseException("Group tags must have a body.");

      TagContext context = getContext();
      Map<String, Object> beans = context.getBeans();
      Map<String, RichTextString> attributes = getAttributes();

      RichTextString rtsGroupDir = attributes.get(ATTR_GROUP_DIR);
      String attrGroupDir = (rtsGroupDir != null) ? rtsGroupDir.getString() : null;
      if (attrGroupDir != null)
      {
         String groupDir = Expression.evaluateString(attrGroupDir, beans).toString().toLowerCase();
         if (GROUP_DIR_ROWS.equals(groupDir))
            myGroupDir = Block.Direction.VERTICAL;
         else if (GROUP_DIR_COLS.equals(groupDir))
            myGroupDir = Block.Direction.HORIZONTAL;
         else if (GROUP_DIR_NONE.equals(groupDir))
            myGroupDir = Block.Direction.NONE;
         else
            throw new TagParseException("Unknown group direction: " + groupDir +
                  " found at " + attrGroupDir);
      }
      else
      {
         myGroupDir = Block.Direction.VERTICAL;
      }

      RichTextString rtsCollapse = attributes.get(ATTR_COLLAPSE);
      String attrCollapse = (rtsCollapse != null) ? rtsCollapse.getString() : null;
      if (attrCollapse != null)
      {
         Object test = Expression.evaluateString(attrCollapse, beans);
         if (test instanceof Boolean)
            amICollapsed = (Boolean) test;
         else
            amICollapsed = Boolean.parseBoolean(test.toString());
      }
   }

   /**
    * <p>Create an Excel group of rows or columns around the height or the
    * width of the block.</p>
    * @return Whether the first <code>Cell</code> in the <code>Block</code>
    *    associated with this <code>Tag</code> was processed.
    */
   public boolean process()
   {
      TagContext context = getContext();
      Sheet sheet = context.getSheet();
      Block block = context.getBlock();
      int begin, end;

      switch(myGroupDir)
      {
      case VERTICAL:
         begin = block.getTopRowNum();
         end = block.getBottomRowNum();
         SheetUtil.groupRows(sheet, begin, end, amICollapsed);
         break;
      case HORIZONTAL:
         begin = block.getLeftColNum();
         end = block.getRightColNum();
         SheetUtil.groupColumns(sheet, begin, end, amICollapsed);
         break;
      // Do nothing on NONE.
      }

      BlockTransformer transformer = new BlockTransformer();
      transformer.transform(context, getWorkbookContext());

      return true;
   }
}
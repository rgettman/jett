package net.sf.jett.event;

/**
 * A <code>TagListener</code> is an object that has an opportunity to inspect
 * a block of <code>Cells</code> as they're being transformed for a tag, with
 * access to the block of <code>Cells</code> and the current
 * <code>Map</code> of beans.
 *
 * @author Randy Gettman
 * @since 0.3.0
 */
public interface TagListener
{
   /**
    * Called when the block of <code>Cells</code> has been processed.  The
    * given <code>TagEvent</code> contains the following related data: a
    * reference to the <code>Block</code> of <code>Cells</code> that was
    * processed and the <code>Map</code> of bean names to bean values that was
    * used.
    *
    * @param event The <code>TagEvent</code>.
    */
   public void onTagProcessed(TagEvent event);
}

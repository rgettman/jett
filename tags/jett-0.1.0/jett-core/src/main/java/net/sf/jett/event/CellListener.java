package net.sf.jett.event;

/**
 * A <code>CellListener</code> is an object that has an opportunity to inspect
 * a <code>Cell</code> as it's being transformed, with access to the
 * <code>Cell</code>, the current <code>Map</code> of beans, and the old and
 * new values for the <code>Cell</code>.
 */
public interface CellListener
{
   /**
    * Called when a <code>Cell</code> has been processed.  The given
    * <code>CellEvent</code> contains the following related data: a reference
    * to the <code>Cell</code> that was processed, a <code>Map</code> of bean
    * names to bean values that was used, the old value of the
    * <code>Cell</code>, and the new value of the <code>Cell</code> after
    * processing.
    *
    * @param event The <code>CellEvent</code>.
    */
   public void cellProcessed(CellEvent event);
}

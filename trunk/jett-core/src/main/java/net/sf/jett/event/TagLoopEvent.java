package net.sf.jett.event;

/**
 * A <code>TagLoopEvent</code> represents data associated with a "tag loop
 * processed" event.  It contains the same data as a <code>TagEvent</code>,
 * plus the current 0-based looping index.
 *
 * @see TagEvent
 * @author Randy Gettman
 * @since 0.3.0
 */
public class TagLoopEvent extends TagEvent
{
   private int myLoopIndex;

   /**
    * Constructs a <code>TagLoopEvent</code> with a loop index of zero.
    */
   public TagLoopEvent()
   {
      super();
      myLoopIndex = 0;
   }

   /**
    * Returns the current loop index (zero-based).
    * @return The current loop index (zero-based).
    */
   public int getLoopIndex()
   {
      return myLoopIndex;
   }

   /**
    * Sets the current loop index (zero-based).
    * @param loopIndex The current loop index (zero-based).
    */
   public void setLoopIndex(int loopIndex)
   {
      myLoopIndex = loopIndex;
   }
}

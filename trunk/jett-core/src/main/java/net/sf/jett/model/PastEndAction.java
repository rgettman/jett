package net.sf.jett.model;

/**
 * <p>A <code>PastEndAction</code> enumerated value specifies a possible action
 * when dealing with expressions that reference collection items beyond the end
 * of the iteration.  This comes up when a <code>MultiForEachTag</code> is
 * operating on collections of different sizes, and one collection has run out
 * of values before another collection.</p>
 *
 * <p>If such an expression were written in Java code, it would result in an
 * <code>IndexOutOfBoundsException</code>.  Each enumerated value specifies a
 * way of handling this condition.</p>
 *
 * @author Randy Gettman
 */
public enum PastEndAction
{
   /**
    * Specifies that any <code>Cell</code> containing an expression that
    * references a collection item beyond the end of the iteration should
    * result in the entire <code>Cell</code> being blanked out.
    */
   CLEAR_CELL,
   /**
    * Specifies that any <code>Cell</code> containing an expression that
    * references a collection item beyond the end of the iteration should
    * result in the entire <code>Cell</code> being removed, formatting and all.
    */
   REMOVE_CELL
   // There may be other values in the future. e.g. "NULL_EXPR", which would
   // specify that an expression that references a collection item beyond the
   // end of the iteration should result in the expression evaluating to null.
   // This would be different than "CLEAR_CELL" or "REMOVE_CELL" because there
   // may be other expressions in the Cell.
}

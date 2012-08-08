package net.sf.jett.test;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jett.exception.MetadataParseException;
import net.sf.jett.parser.MetadataParser;

/**
 * This JUnit Test class tests the <code>MetadataParser</code>.
 * @since 0.2.0
 */
public class MetadataParserTest
{
   /**
    * Tests a simple metadata string.
    */
   @Test
   public void testSimple()
   {
      String metadata = "extraRows=1;left=2;right=3;copyRight=true;fixed=true;pastEndAction=clear";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();

      assertTrue(parser.isDefiningCols());
      assertEquals(1, parser.getExtraRows());
      assertEquals(2, parser.getColsLeft());
      assertEquals(3, parser.getColsRight());
      assertEquals("clear", parser.getPastEndAction());
      assertTrue(parser.isCopyingRight());
      assertTrue(parser.isFixed());
   }

   /**
    * Tests that the "clear" past end action value is recognized and legal.
    * Also tests defaults for other keys.
    */
   @Test
   public void testPastEndActionClear()
   {
      String metadata = "pastEndAction=clear";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();

      assertFalse(parser.isDefiningCols());
      assertEquals(0, parser.getExtraRows());
      assertEquals(0, parser.getColsLeft());
      assertEquals(0, parser.getColsRight());
      assertEquals("clear", parser.getPastEndAction());
      assertFalse(parser.isCopyingRight());
      assertFalse(parser.isFixed());
   }

   /**
    * Tests that the "remove" past end action value is recognized and legal.
    */
   @Test
   public void testPastEndActionRemove()
   {
      String metadata = "pastEndAction=remove";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();

      assertEquals("remove", parser.getPastEndAction());
   }

   /**
    * If there is a bad past end action value, ensure that a
    * <code>MetadataParseException</code> is thrown.
    */
   @Test(expected = MetadataParseException.class)
   public void testBadPastEndActionValue()
   {
      String metadata = "pastEndAction=badvalue";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();
   }

   /**
    * If there is a non-numeric value for extra rows, ensure that a
    * <code>MetadataParseException</code> is thrown.
    */
   @Test(expected = MetadataParseException.class)
   public void testNonNumericExtraRows()
   {
      String metadata = "extraRows=blah";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();
   }

   /**
    * If there is a negative value for extra rows, ensure that a
    * <code>MetadataParseException</code> is thrown.
    */
   @Test(expected = MetadataParseException.class)
   public void testNegativeExtraRows()
   {
      String metadata = "extraRows=-1";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();
   }

   /**
    * If there is a non-numeric value for left cols, ensure that a
    * <code>MetadataParseException</code> is thrown.
    */
   @Test(expected = MetadataParseException.class)
   public void testNonNumericLeftCols()
   {
      String metadata = "left=blah";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();
   }

   /**
    * If there is a negative value for left cols, ensure that a
    * <code>MetadataParseException</code> is thrown.
    */
   @Test(expected = MetadataParseException.class)
   public void testNegativeLeftCols()
   {
      String metadata = "left=-1";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();
   }

   /**
    * If there is a non-numeric value for right cols, ensure that a
    * <code>MetadataParseException</code> is thrown.
    */
   @Test(expected = MetadataParseException.class)
   public void testNonNumericRightCols()
   {
      String metadata = "right=blah";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();
   }

   /**
    * If there is a negative value for right cols, ensure that a
    * <code>MetadataParseException</code> is thrown.
    */
   @Test(expected = MetadataParseException.class)
   public void testNegativeRightCols()
   {
      String metadata = "right=-1";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();
   }

   /**
    * If there is an unrecognized key, ensure that a
    * <code>MetadataParseException</code> is thrown.
    */
   @Test(expected = MetadataParseException.class)
   public void testUnrecgonizedKey()
   {
      String metadata = "badKey=true";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();
   }

   /**
    * If the key is missing, ensure that a
    * <code>MetadataParseException</code> is thrown.
    */
   @Test(expected = MetadataParseException.class)
   public void testKeyMissing()
   {
      String metadata = "=true";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();
   }

   /**
    * If the key value is missing, ensure that a
    * <code>MetadataParseException</code> is thrown.
    */
   @Test(expected = MetadataParseException.class)
   public void testKeyValueMissing()
   {
      String metadata = "extraRows=";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();
   }

   /**
    * If the equals sign is missing, ensure that a
    * <code>MetadataParseException</code> is thrown.
    */
   @Test(expected = MetadataParseException.class)
   public void testEqualsMissing()
   {
      String metadata = "extraRows";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();
   }

   /**
    * Ensure that the parser recognizes that it's defining columns if only the
    * left columns value is defined.
    */
   @Test
   public void testDefiningColsLeftOnly()
   {
      String metadata = "left=1";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();

      assertTrue(parser.isDefiningCols());
   }

   /**
    * Ensure that the parser recognizes that it's defining columns if only the
    * right columns value is defined.
    */
   @Test
   public void testDefiningColsRightOnly()
   {
      String metadata = "right=1";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();

      assertTrue(parser.isDefiningCols());
   }

   /**
    * If there is a bad past group direction value, ensure that a
    * <code>MetadataParseException</code> is thrown.
    */
   @Test(expected = MetadataParseException.class)
   public void testBadGroupDirValue()
   {
      String metadata = "groupDir=badvalue";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();
   }

   /**
    * Test the "none" group direction value.
    */
   @Test
   public void testGroupDirValueNone()
   {
      String metadata = "groupDir=none";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();

      assertEquals("none", parser.getGroupDir());
   }

   /**
    * Test the "cols" group direction value.
    */
   @Test
   public void testGroupDirValueCols()
   {
      String metadata = "groupDir=cols";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();

      assertEquals("cols", parser.getGroupDir());
   }

   /**
    * Test the "rows" group direction value.
    */
   @Test
   public void testGroupDirValueRows()
   {
      String metadata = "groupDir=rows";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();

      assertEquals("rows", parser.getGroupDir());
   }

   /**
    * Test the "collapse" value without "groupDir".
    */
   @Test
   public void testCollapseNoGroupDir()
   {
      String metadata = "collapse=true";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();

      assertFalse(parser.isCollapsingGroup());
   }

   /**
    * Test the "collapse" value with "groupDir" "none".
    */
   @Test
   public void testCollapseGroupDirNone()
   {
      String metadata = "collapse=true;groupDir=none";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();

      assertFalse(parser.isCollapsingGroup());
   }

   /**
    * Test the "collapse" value with "groupDir" "rows".
    */
   @Test
   public void testCollapseGroupDirRows()
   {
      String metadata = "collapse=true;groupDir=rows";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();

      assertTrue(parser.isCollapsingGroup());
   }

   /**
    * Test the "collapse" value with "groupDir" "cols".
    */
   @Test
   public void testCollapseGroupDirCols()
   {
      String metadata = "collapse=true;groupDir=cols";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();

      assertTrue(parser.isCollapsingGroup());
   }

   /**
    * Test the "copyRight" value without "left" or "right".
    */
   @Test
   public void testCopyRightNoLeftRight()
   {
      String metadata = "copyRight=true";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();

      assertFalse(parser.isCopyingRight());
   }

   /**
    * Test the "copyRight" value with "left".
    */
   @Test
   public void testCopyRightWithLeft()
   {
      String metadata = "copyRight=true;left=1";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();

      assertTrue(parser.isCopyingRight());
   }

   /**
    * Test the "copyRight" value with "right".
    */
   @Test
   public void testCopyRightWithRight()
   {
      String metadata = "copyRight=true;right=1";

      MetadataParser parser = new MetadataParser(metadata);
      parser.parse();

      assertTrue(parser.isCopyingRight());
   }
}

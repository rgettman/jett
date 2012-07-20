package net.sf.jett.tag;

import java.util.HashMap;
import java.util.Map;

/**
 * A <code>JtTagLibrary</code> represents the built-in <code>TagLibrary</code>.
 */
public class JtTagLibrary implements TagLibrary
{
   private static JtTagLibrary theLibrary = new JtTagLibrary();

   private Map<String, Class<? extends Tag>> myTagMap;

   /**
    * Singleton constructor.
    */
   private JtTagLibrary()
   {
      myTagMap = new HashMap<String, Class<? extends Tag>>();
      myTagMap.put("agg"         , AggTag.class);
      myTagMap.put("for"         , ForTag.class);
      myTagMap.put("forEach"     , ForEachTag.class);
      myTagMap.put("if"          , IfTag.class);
      myTagMap.put("multiForEach", MultiForEachTag.class);
      myTagMap.put("null"        , NullTag.class);
      myTagMap.put("span"        , SpanTag.class);
      myTagMap.put("total"       , TotalTag.class);
   }

   /**
    * Returns the singleton instance of a <code>JtTagLibrary</code>.
    * @return The <code>JtTagLibrary</code>.
    */
   public static JtTagLibrary getJtTagLibrary()
   {
      return theLibrary;
   }

   /**
    * Returns the <code>Map</code> of tag names to tag <code>Class</code>
    * objects, e.g. <code>"if" => IfTag.class</code>.
    * @return A <code>Map</code> of tag names to tag <code>Class</code>
    *    objects.
    */
   public Map<String, Class<? extends Tag>> getTagMap()
   {
      return myTagMap;
   }
}


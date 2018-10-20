package org.fulib.yaml;

import java.util.LinkedHashMap;

public class YamlObject
{

   public static final String ID = ".id";
   public static final String TYPE = "type";


   public YamlObject()
   {
      // empty
   }

   public YamlObject(String id, String type)
   {
      map.put(ID, id);
      map.put(TYPE, type);
   }

   private LinkedHashMap<String, Object> map = new LinkedHashMap<>();

   public LinkedHashMap<String, Object> getMap()
   {
      return map;
   }

   public YamlObject put(String tag, Object value)
   {
      map.put(tag, value);
      return this;
   }

   public Object get(String tag)
   {
      return map.get(tag);
   }

   @Override
   public String toString()
   {
      Object id = map.get(ID);

      if (id != null) return id.toString();

      return super.toString();
   }
}

package org.fulib.yaml;

import java.util.ArrayList;
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


   public YamlObject with(String tag, Object value)
   {
      ArrayList<Object> list = (ArrayList<Object>) map.get(tag);

      if (list == null)
      {
         list = new ArrayList<>();
         map.put(tag, list);
      }

      list.add(value);

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

package org.fulib.yaml;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class YamlObject
{
   // =============== Constants ===============

   public static final String ID   = ".id";
   public static final String TYPE = "type";

   // =============== Fields ===============

   private LinkedHashMap<String, Object> map = new LinkedHashMap<>();

   // =============== Constructors ===============

   public YamlObject()
   {
      // empty
   }

   public YamlObject(String id, String type)
   {
      this.map.put(ID, id);
      this.map.put(TYPE, type);
   }

   // =============== Properties ===============

   public LinkedHashMap<String, Object> getMap()
   {
      return this.map;
   }

   // =============== Methods ===============

   public Object get(String tag)
   {
      return this.map.get(tag);
   }

   public YamlObject put(String tag, Object value)
   {
      this.map.put(tag, value);
      return this;
   }

   public YamlObject with(String tag, Object value)
   {
      ArrayList<Object> list = (ArrayList<Object>) this.map.get(tag);

      if (list == null)
      {
         list = new ArrayList<>();
         this.map.put(tag, list);
      }

      list.add(value);

      return this;
   }

   @Override
   public String toString()
   {
      Object id = this.map.get(ID);

      if (id != null)
      {
         return id.toString();
      }

      return super.toString();
   }
}

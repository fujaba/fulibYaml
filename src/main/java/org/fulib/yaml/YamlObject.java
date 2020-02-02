package org.fulib.yaml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

   /**
    * @since 1.2
    */
   public YamlObject(String id)
   {
      this.map.put(ID, id);
   }

   public YamlObject(String id, String type)
   {
      this(id);
      this.map.put(TYPE, type);
   }

   // =============== Properties ===============

   /**
    * @return the properties
    *
    * @deprecated since 1.2; use {@link #getProperties()} instead
    */
   public LinkedHashMap<String, Object> getMap()
   {
      return this.map;
   }

   /**
    * @return the properties
    *
    * @since 1.2
    */
   public Map<String, Object> getProperties()
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

   public YamlObject with(String tag, Object item)
   {
      this.map.compute(tag, (k, oldValue) -> {
         if (oldValue == null) // not yet present
         {
            final List<Object> list = new ArrayList<>();
            list.add(item);
            return list;
         }

         final List<Object> list;
         if (oldValue instanceof List) // old value was a list
         {
            list = (List<Object>) oldValue;
         }
         else // old value was an object
         {
            list = new ArrayList<>();
            list.add(oldValue);
         }
         list.add(item);
         return list;
      });
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

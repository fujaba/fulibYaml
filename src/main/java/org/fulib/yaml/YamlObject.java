package org.fulib.yaml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class YamlObject
{
   // =============== Constants ===============

   public static final String ID = ".id";
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
      this.setId(id);
   }

   public YamlObject(String id, String type)
   {
      this(id);
      this.setType(type);
   }

   // =============== Properties ===============

   /**
    * @return the properties
    *
    * @deprecated since 1.2; use {@link #getProperties()} instead
    */
   @Deprecated
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

   /**
    * @since 1.2
    */
   public String getId()
   {
      final Object id = this.map.get(ID);
      return id != null ? id.toString() : null;
   }

   /**
    * @since 1.2
    */
   public void setId(String id)
   {
      this.map.put(ID, id);
   }

   /**
    * @since 1.2
    */
   public String getType()
   {
      final Object type = this.map.get(TYPE);
      return type != null ? type.toString() : null;
   }

   /**
    * @since 1.2
    */
   public void setType(String type)
   {
      this.map.put(TYPE, type);
   }

   // =============== Methods ===============

   public Object get(String property)
   {
      return this.map.get(property);
   }

   public YamlObject put(String property, Object value)
   {
      this.map.put(property, value);
      return this;
   }

   public YamlObject with(String property, Object item)
   {
      this.map.compute(property, (k, oldValue) -> {
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
      final String id = this.getId();
      return id != null ? id : super.toString();
   }
}

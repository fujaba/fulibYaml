package org.fulib.yaml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A container for arbitrary attributes.
 * Simple attributes can be set with {@link #put(String, Object)} and get with {@link #get(String)}.
 * Collection attributes can be created with {@link #with(String, Object)}.
 */
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
    * @param id
    *    the {@link #setId(String) id}
    *
    * @since 1.2
    */
   public YamlObject(String id)
   {
      this.setId(id);
   }

   /**
    * @param id
    *    the {@link #setId(String) id}
    * @param type
    *    the {@link #setType(String) type}
    */
   public YamlObject(String id, String type)
   {
      this(id);
      this.setType(type);
   }

   // =============== Properties ===============

   /**
    * @return the map of property names to property values
    *
    * @deprecated since 1.2; use {@link #getProperties()} instead
    */
   @Deprecated
   public LinkedHashMap<String, Object> getMap()
   {
      return this.map;
   }

   /**
    * @return the map of property names to property values
    *
    * @since 1.2
    */
   public Map<String, Object> getProperties()
   {
      return this.map;
   }

   /**
    * Convenience getter for {@link #get(String) get}({@link #ID}).
    * In particular, roughly implemented as
    *
    * <pre><code>
    *    final Object id = this.get(ID);
    *    return id != null ? id.toString() : null;
    * </code></pre>
    *
    * @return the ID
    *
    * @since 1.2
    */
   public String getId()
   {
      final Object id = this.map.get(ID);
      return id != null ? id.toString() : null;
   }

   /**
    * Convenience setter for {@link #put(String, Object) put}({@link #ID}, {@code id}).
    *
    * @param id
    *    the ID
    *
    * @since 1.2
    */
   public void setId(String id)
   {
      this.map.put(ID, id);
   }

   /**
    * Convenience getter for {@link #get(String) get}({@link #TYPE}).
    * In particular, roughly implemented as
    *
    * <pre><code>
    *    final Object type = this.get(TYPE);
    *    return type != null ? type.toString() : null;
    * </code></pre>
    *
    * @return the type
    *
    * @since 1.2
    */
   public String getType()
   {
      final Object type = this.map.get(TYPE);
      return type != null ? type.toString() : null;
   }

   /**
    * Convenience setter for {@link #put(String, Object) put}({@link #TYPE}, {@code type}).
    *
    * @param type
    *    the type
    *
    * @since 1.2
    */
   public void setType(String type)
   {
      this.map.put(TYPE, type);
   }

   // =============== Methods ===============

   /**
    * @param property
    *    the property name
    *
    * @return the attribute value for the given property name, or {@code null} if not specified
    */
   public Object get(String property)
   {
      return this.map.get(property);
   }

   /**
    * Sets the attribute value for the given property name.
    *
    * @param property
    *    the property name
    * @param value
    *    the attribute value
    *
    * @return this instance, to allow method chaining
    */
   public YamlObject put(String property, Object value)
   {
      this.map.put(property, value);
      return this;
   }

   /**
    * Adds the item to the collection attribute with the given property name.
    * In particular, the implementation is as follows:
    *
    * <ol>
    *   <li>If the property is not set, set the item as the attribute value as if
    *   {@link #put(String, Object)}({@code property}, {@code item}) was called</li>
    *   <li>If the property is already set and the value is a {@link List}, add the {@code item} to that list</li>
    *   <li>If the property is already set and the value is not a {@link List},
    *   create a new list with the old value and the {@code item} and set that as the new value</li>
    * </ul>
    *
    * @param property
    *    the property name
    * @param item
    *    the item to add or set
    *
    * @return this instance, to allow method chaining
    */
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

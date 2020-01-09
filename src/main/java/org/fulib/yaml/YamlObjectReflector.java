package org.fulib.yaml;

import java.util.Set;

public class YamlObjectReflector extends Reflector
{
   // =============== Fields ===============

   private YamlObject yamlObject;

   // =============== Constructors ===============

   /**
    * Creates a new {@link YamlObjectReflector} instance.
    *
    * @param newObject
    *    the yaml object (must be a {@link YamlObject} instance)
    *
    * @deprecated since 1.2; use {@link #YamlObjectReflector(YamlObject)} instead
    */
   @Deprecated
   public YamlObjectReflector(Object newObject)
   {
      this((YamlObject) newObject);
   }

   /**
    * Creates a new {@link YamlObjectReflector} instance.
    *
    * @param yamlObject
    *    the yaml object
    *
    * @since 1.2
    */
   public YamlObjectReflector(YamlObject yamlObject)
   {
      this.yamlObject = yamlObject;
   }

   // =============== Properties ===============

   @Override
   public String[] getProperties()
   {
      Set<String> stringSet = yamlObject.getMap().keySet();
      return stringSet.toArray(new String[stringSet.size()]);
   }

   // =============== Methods ===============

   @Override
   public Object newInstance()
   {
      return new YamlObject();
   }

   @Override
   public Object getValue(Object object, String attribute)
   {
      return this.yamlObject.get(attribute);
   }

   @Override
   public Object setValue(Object object, String attribute, Object value)
   {
      return this.yamlObject.put(attribute, value);
   }

   @Override
   public void removeObject(Object object)
   {
   }
}

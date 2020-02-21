package org.fulib.yaml;

import java.util.Set;

/**
 * A reflector specialized for {@link YamlObject} instances.
 */
public class YamlObjectReflector extends Reflector
{
   // =============== Fields ===============

   private YamlObject yamlObject;

   // =============== Constructors ===============

   /**
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
   public Set<String> getOwnProperties()
   {
      return this.yamlObject.getProperties().keySet();
   }

   @Override
   public Set<String> getAllProperties()
   {
      return this.getOwnProperties();
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

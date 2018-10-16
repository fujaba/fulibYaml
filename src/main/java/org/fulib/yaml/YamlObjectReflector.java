package org.fulib.yaml;

import java.util.Set;

public class YamlObjectReflector extends Reflector
{
   private YamlObject yamlObject;

   public YamlObjectReflector(Object newObject)
   {
      super();
      yamlObject = (YamlObject) newObject;
   }

   @Override
   public void removeObject(Object object)
   {
      super.removeObject(object);
   }

   @Override
   public String[] getProperties()
   {
      Set<String> stringSet = yamlObject.getMap().keySet();
      return stringSet.toArray(new String[stringSet.size()]);
   }

   @Override
   public Object newInstance()
   {
      return new YamlObject();
   }

   @Override
   public Object getValue(Object object, String attribute)
   {
      return yamlObject.getMap().get(attribute);
   }

   @Override
   public Object setValue(Object object, String attribute, Object value, String type)
   {
      return yamlObject.getMap().put(attribute, value);
   }
}

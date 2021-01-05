package org.fulib.yaml;

import java.util.Arrays;
import java.util.Map;

/**
 * Simple yaml encoding
 *
 * @since 1.5
 */
public class Yaml
{
   private final YamlIdMap idMap;

   public static String encode(Object... objects)
   {
      final String[] packageNames = Arrays
         .stream(objects)
         .map(Object::getClass)
         .map(Class::getPackage)
         .map(Package::getName)
         .toArray(String[]::new);
      return new Yaml(packageNames).idMap.encode(objects);
   }

   public static Yaml forPackage(String... packageNames)
   {
      return new Yaml(packageNames);
   }

   public Yaml(String... packageNames)
   {
      this.idMap = new YamlIdMap(packageNames);
   }

   public Map<String, Object> decode(String yaml)
   {
      this.idMap.decode(yaml);
      return this.idMap.getObjIdMap();
   }

   public Object getObject(String id)
   {
      return this.idMap.getObject(id);
   }
}

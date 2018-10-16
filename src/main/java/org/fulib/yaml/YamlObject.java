package org.fulib.yaml;

import java.util.LinkedHashMap;

public class YamlObject
{
   private LinkedHashMap<String, Object> map = new LinkedHashMap<>();

   public LinkedHashMap<String, Object> getMap()
   {
      return map;
   }

   @Override
   public String toString()
   {
      Object id = map.get(".id");

      if (id != null) return id.toString();

      return super.toString();
   }
}

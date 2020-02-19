package org.fulib.yaml;

import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventYamler
{
   public static final String TIME = "time";
   public static final String SOURCE = "source";
   public static final String SOURCE_TYPE = "sourceType";
   public static final String PROPERTY = "property";
   public static final String OLD_VALUE = "oldValue";
   public static final String OLD_VALUE_TYPE = OLD_VALUE + "Type";
   public static final String NEW_VALUE = "newValue";
   public static final String NEW_VALUE_TYPE = NEW_VALUE + "Type";
   public static final String HISTORY_KEY = "historyKey";

   private YamlIdMap yamlIdMap;

   public EventYamler(String packageName)
   {
      this(new YamlIdMap(packageName));
   }

   /**
    * @since 1.2
    */
   public EventYamler(YamlIdMap idMap)
   {
      this.yamlIdMap = idMap;
   }

   /**
    * @since 1.2
    */
   public YamlIdMap getYamlIdMap()
   {
      return this.yamlIdMap;
   }

   public EventYamler setYamlIdMap(YamlIdMap yamlIdMap)
   {
      this.yamlIdMap = yamlIdMap;

      return this;
   }

   public String encode(PropertyChangeEvent e)
   {
      Object source = e.getSource();
      StringBuilder buf = new StringBuilder("- ");

      long timeMillis = System.currentTimeMillis();
      Date date = new Date(timeMillis);
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
      String timeString = dateFormat.format(date);
      buf.append(TIME + ": ").append(timeString).append("\n");

      String sourceKey = this.yamlIdMap.putObject(source);
      buf.append("  " + SOURCE + ": ").append(sourceKey).append("\n");

      String className = source.getClass().getSimpleName();
      buf.append("  " + SOURCE_TYPE + ": ").append(className).append("\n");

      String prop = e.getPropertyName();
      buf.append("  " + PROPERTY + ": ").append(prop).append("\n");

      String historyKey = sourceKey + "/" + prop;

      Object oldValue = e.getOldValue();
      if (oldValue != null)
      {
         Class<?> valueClass = oldValue.getClass();

         if (valueClass == String.class)
         {
            String encapsulted = YamlGenerator.encapsulate((String) oldValue);
            buf.append("  " + OLD_VALUE + ": ").append(encapsulted).append("\n");
         }
         else if (valueClass.getName().startsWith("java.lang."))
         {
            buf.append("  " + OLD_VALUE + ": ").append(oldValue).append("\n");
         }
         else
         {
            String valueKey = this.yamlIdMap.putObject(oldValue);
            buf.append("  " + OLD_VALUE + ": ").append(valueKey).append("\n");

            historyKey += "/" + valueKey;

            className = oldValue.getClass().getSimpleName();
            buf.append("  " + OLD_VALUE_TYPE + ": ").append(className).append("\n");
         }
      }

      Object newValue = e.getNewValue();
      if (newValue != null)
      {
         Class<?> valueClass = newValue.getClass();

         if (valueClass == String.class)
         {
            String encapsulted = YamlGenerator.encapsulate((String) newValue);
            buf.append("  " + NEW_VALUE + ": ").append(encapsulted).append("\n");
         }
         else if (valueClass.getName().startsWith("java.lang."))
         {
            buf.append("  " + NEW_VALUE + ": ").append(newValue).append("\n");
         }
         else
         {
            String valueKey = this.yamlIdMap.putObject(newValue);
            buf.append("  " + NEW_VALUE + ": ").append(valueKey).append("\n");

            Reflector reflector = this.yamlIdMap.getReflector(className);
            Object attrValue = reflector.getValue(source, prop);
            if (attrValue != null && Collection.class.isAssignableFrom(attrValue.getClass()))
            {
               historyKey += "/" + valueKey;
            }

            className = newValue.getClass().getSimpleName();
            buf.append("  " + NEW_VALUE_TYPE + ": ").append(className).append("\n");
         }
      }

      buf.append("  " + HISTORY_KEY + ": ").append(historyKey).append("\n");
      buf.append("\n");
      return buf.toString();
   }

   public Object decode(Object rootObject, String content)
   {
      Yamler yamler = new Yamler();
      ArrayList<LinkedHashMap<String, String>> list = yamler.decodeList(content);

      String firstKey = null;
      for (LinkedHashMap<String, String> map : list)
      {
         // execute change
         String sourceKey = map.get(DataManager.SOURCE);

         if (firstKey == null)
         {
            firstKey = sourceKey;
            Object oldObject = this.yamlIdMap.getObject(firstKey);
            if (oldObject == null)
            {
               this.yamlIdMap.putNameObject(firstKey, rootObject);
            }
         }

         Object sourceObject = this.yamlIdMap.getObject(sourceKey);
         String className = map.get(DataManager.SOURCE_TYPE);
         Reflector reflector = this.yamlIdMap.getReflector(className);

         if (sourceObject == null)
         {
            sourceObject = reflector.newInstance();
            this.yamlIdMap.putNameObject(sourceKey, sourceObject);
         }

         String property = map.get(DataManager.PROPERTY);
         String newValue = map.get(DataManager.NEW_VALUE);
         String newValueType = map.get(DataManager.NEW_VALUE_TYPE);

         if (newValueType == null)
         {
            reflector.setValue(sourceObject, property, newValue);
         }
         else
         {
            Object newValueObject = this.yamlIdMap.getObject(newValue);
            if (newValueObject == null)
            {
               Reflector newValueReflector = this.yamlIdMap.getReflector(newValueType);
               newValueObject = newValueReflector.newInstance();
               this.yamlIdMap.putNameObject(newValue, newValueObject);
            }

            reflector.setValue(sourceObject, property, newValueObject);
         }
      }

      return this.yamlIdMap.getObject(firstKey);
   }
}

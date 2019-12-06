package org.fulib.yaml;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * @author Adrian Kunz
 *
 * @since 1.2
 */
public class YamlGenerator
{
   /**
    * Encodes the events as a list of YAML objects.
    *
    * @param events
    *    the events
    *
    * @return the encoded YAML object list
    */
   public static String encodeYaml(SortedMap<Long, ? extends Map<String, String>> events)
   {
      StringBuilder buf = new StringBuilder();

      for (Map.Entry<Long, ? extends Map<String, String>> entry : events.entrySet())
      {
         Map<String, String> event = entry.getValue();

         String oneObj = encodeYaml(event);

         buf.append(oneObj);
      }

      return buf.toString();
   }

   /**
    * Encodes the events as a list of YAML objects.
    *
    * @param events
    *    the events
    *
    * @return the encoded YAML object list
    */
   public static String encodeYaml(List<? extends Map<String, String>> events)
   {
      StringBuilder buf = new StringBuilder();

      for (Map<String, String> event : events)
      {
         String oneObj = encodeYaml(event);
         buf.append(oneObj);
      }

      return buf.toString();
   }

   /**
    * Encodes the event as a YAML object.
    *
    * @param event
    *    the event
    *
    * @return the encoded YAML object
    */
   public static String encodeYaml(Map<String, String> event)
   {
      StringBuilder buf = new StringBuilder();

      String prefix = "- ";
      for (Map.Entry<String, String> keyValuePair : event.entrySet())
      {
         buf.append(prefix).append(keyValuePair.getKey()).append(": ")
            .append(Yamler.encapsulate(keyValuePair.getValue())).append("\n");
         prefix = "  ";
      }
      buf.append("\n");

      return buf.toString();
   }
}

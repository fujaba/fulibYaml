package org.fulib.yaml;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
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
      return encodeYaml(events.values());
   }

   /**
    * Encodes the events as a list of YAML objects into the writer.
    *
    * @param events
    *    the events
    * @param writer
    *    the writer
    */
   public static void encodeYaml(SortedMap<Long, ? extends Map<String, String>> events, Writer writer)
      throws IOException
   {
      encodeYaml(events.values(), writer);
   }

   /**
    * Encodes the events as a list of YAML objects.
    *
    * @param events
    *    the events
    *
    * @return the encoded YAML object list
    */
   public static String encodeYaml(Iterable<? extends Map<String, String>> events)
   {
      final StringWriter writer = new StringWriter();
      try
      {
         encodeYaml(events, writer);
      }
      catch (IOException e)
      {
         // cannot happen
      }
      return writer.toString();
   }

   /**
    * Encodes the events as a list of YAML objects into the writer.
    *
    * @param events
    *    the events
    * @param writer
    *    the writer
    */
   public static void encodeYaml(Iterable<? extends Map<String, String>> events, Writer writer) throws IOException
   {
      for (final Map<String, String> event : events)
      {
         encodeYaml(event, writer);
      }
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
      final StringWriter writer = new StringWriter();
      try
      {
         encodeYaml(event, writer);
      }
      catch (IOException e)
      {
         // cannot happen
      }
      return writer.toString();
   }

   /**
    * Encodes the event as a YAML object into the writer.
    *
    * @param event
    *    the event
    * @param writer
    *    the writer
    */
   public static void encodeYaml(Map<String, String> event, Writer writer) throws IOException
   {
      String prefix = "- ";
      for (Map.Entry<String, String> keyValuePair : event.entrySet())
      {
         writer.write(prefix);
         writer.write(keyValuePair.getKey());
         writer.write(": ");
         writer.write(Yamler.encapsulate(keyValuePair.getValue()));
         writer.write('\n');
         prefix = "  ";
      }
      writer.write('\n');
   }
}

package org.fulib.yaml;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

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
   public static String serialize(Iterable<? extends Map<String, String>> events)
   {
      final StringWriter writer = new StringWriter();
      try
      {
         serialize(events, writer);
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
   public static void serialize(Iterable<? extends Map<String, String>> events, Writer writer) throws IOException
   {
      for (final Map<String, String> event : events)
      {
         serialize(event, writer);
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
   public static String serialize(Map<String, String> event)
   {
      final StringWriter writer = new StringWriter();
      try
      {
         serialize(event, writer);
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
   public static void serialize(Map<String, String> event, Writer writer) throws IOException
   {
      String prefix = "- ";
      for (Map.Entry<String, String> keyValuePair : event.entrySet())
      {
         writer.write(prefix);
         writer.write(keyValuePair.getKey());
         writer.write(": ");
         writer.write(YamlGenerator.encapsulate(keyValuePair.getValue()));
         writer.write('\n');
         prefix = "  ";
      }
      writer.write('\n');
   }

   /**
    * Encapsulates a YAML value by enclosing it in quotes ("), if necessary.
    *
    * @param value
    *    the YAML value to encapsulate
    *
    * @return the encapsulated YAML value
    */
   public static String encapsulate(String value)
   {
      if (value.matches("[a-zA-Z0-9_.]+"))
      {
         return value;
      }
      // replace " with \"
      return "\"" + value.replace("\"", "\\\"") + "\"";
   }
}

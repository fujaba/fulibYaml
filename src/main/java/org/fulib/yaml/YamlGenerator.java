package org.fulib.yaml;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Adrian Kunz
 * @since 1.2
 */
public class YamlGenerator
{
   // =============== Constants ===============

   private static final Pattern SIMPLE_VALUE_PATTERN = Pattern.compile("[a-zA-Z0-9_.]+");

   // =============== Static Methods ===============

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
      final StringBuilder builder = new StringBuilder();
      try
      {
         serialize(events, builder);
      }
      catch (IOException e)
      {
         // cannot happen
      }
      return builder.toString();
   }

   /**
    * Encodes the events as a list of YAML objects into the writer.
    *
    * @param events
    *    the events
    * @param writer
    *    the writer
    *
    * @throws IOException
    *    when appending to the writer produces an error
    */
   public static void serialize(Iterable<? extends Map<String, String>> events, Appendable writer) throws IOException
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
      final StringBuilder writer = new StringBuilder();
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
    *
    * @throws IOException
    *    when appending to the writer produces an error
    */
   public static void serialize(Map<String, String> event, Appendable writer) throws IOException
   {
      String prefix = "- ";
      for (Map.Entry<String, String> keyValuePair : event.entrySet())
      {
         writer.append(prefix);
         writer.append(keyValuePair.getKey());
         writer.append(": ");
         encapsulate(keyValuePair.getValue(), writer);
         writer.append('\n');
         prefix = "  ";
      }
      writer.append('\n');
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
      if (SIMPLE_VALUE_PATTERN.matcher(value).matches())
      {
         return value;
      }
      // replace " with \"
      return "\"" + value.replace("\"", "\\\"") + "\"";
   }

   /**
    * Encapsulates a YAML value by enclosing it in quotes ("), if necessary, and appends the result to the writer.
    *
    * @param value
    *    the YAML value to encapsulate
    * @param writer
    *    the writer
    *
    * @throws IOException
    *    when appending to the writer produces an error
    */
   public static void encapsulate(String value, Appendable writer) throws IOException
   {
      if (SIMPLE_VALUE_PATTERN.matcher(value).matches())
      {
         writer.append(value);
      }
      writer.append('"');
      writer.append(value.replace("\"", "\\\""));
      writer.append('"');
   }
}

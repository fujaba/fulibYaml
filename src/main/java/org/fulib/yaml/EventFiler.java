package org.fulib.yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventFiler
{
   // =============== Fields ===============

   private final EventSource eventSource;

   private String historyFileName = null;

   // =============== Constructors ===============

   public EventFiler(EventSource eventSource)
   {
      this.eventSource = eventSource;
   }

   // =============== Properties ===============

   /**
    * @since 1.2
    */
   public EventSource getEventSource()
   {
      return this.eventSource;
   }

   /**
    * @since 1.2
    */
   public String getHistoryFileName()
   {
      return this.historyFileName;
   }

   public EventFiler setHistoryFileName(String historyFileName)
   {
      this.historyFileName = historyFileName;
      return this;
   }

   // =============== Methods ===============

   public String loadHistory()
   {
      final Path historyFile = Paths.get(this.historyFileName);
      if (Files.exists(historyFile))
      {
         return null;
      }
      try
      {
         return new String(Files.readAllBytes(historyFile));
      }
      catch (IOException e)
      {
         return null;
      }
   }

   public boolean storeHistory()
   {
      final Path historyFile = Paths.get(this.historyFileName);

      createDirs(historyFile);

      try (final Writer writer = Files.newBufferedWriter(historyFile, StandardOpenOption.CREATE))
      {
         this.eventSource.encodeYaml(writer);
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.SEVERE, "could not write to historyFile " + historyFile, e);
         return false;
      }

      return true;
   }

   public EventFiler startEventLogging()
   {
      this.eventSource.addEventListener(this::storeEvent);

      return this;
   }

   /**
    * Appends the event to the history file.
    *
    * @param event
    *    the event
    *
    * @deprecated since 1.2; use {@link #storeEvent(Map)}
    */
   @Deprecated
   public void storeEvent(LinkedHashMap<String, String> event)
   {
      this.storeEvent((Map<String, String>) event);
   }

   /**
    * Appends the event to the history file.
    *
    * @param event
    *    the event
    *
    * @since 1.2
    */
   public void storeEvent(Map<String, String> event)
   {
      final Path historyFile = Paths.get(this.historyFileName);

      createDirs(historyFile);

      try (final Writer writer = Files.newBufferedWriter(historyFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND))
      {
         YamlGenerator.serialize(event, writer);
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.SEVERE, "could not write to historyFile " + historyFile, e);
      }
   }

   private static void createDirs(Path historyFile)
   {
      try
      {
         Files.createDirectories(historyFile.getParent());
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.SEVERE, "could create directories for historyFile " + historyFile, e);
      }
   }
}

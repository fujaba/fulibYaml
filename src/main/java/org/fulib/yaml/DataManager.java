package org.fulib.yaml;

import java.beans.PropertyChangeEvent;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataManager
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
   private EventYamler eventYamler;
   private ReflectorMap reflectorMap;
   private File logFilePath = null;
   private File modelFile = null;
   private String logDirName = "tmp";
   private String logFileName;

   public static DataManager get()
   {
      return new DataManager();
   }

   public DataManager attach(Object rootObject, String logDirName)
   {
      getOrCreateLogDir(logDirName);

      this.logFileName = "logFile.yaml";

      String packageName = rootObject.getClass().getPackage().getName();
      yamlIdMap = new YamlIdMap(packageName);
      eventYamler = new EventYamler(packageName).setYamlIdMap(yamlIdMap);
      reflectorMap = new ReflectorMap(packageName);

      loadModel(rootObject);

      loadEvents(rootObject);

      storeModel(rootObject);

      removeLogFile();

      new ModelListener(rootObject, e -> handleEvent(e));

      return this;
   }

   private void removeLogFile()
   {
      try
      {
         File logFile = new File(logDirName + "/" + logFileName);
         if (logFile.exists())
         {
            logFile.delete();
         }
      }
      catch (Exception e)
      {
         Logger.getGlobal().log(Level.SEVERE, " could not remove log file " + this.logFileName);
      }
   }


   private void getOrCreateLogDir(String logDirName)
   {
      this.logDirName = logDirName;

      try
      {
         File logDirFile = new File(logDirName);

         if ( ! logDirFile.exists())
         {
            logDirFile.mkdirs();
         }
      }
      catch (Exception e)
      {
         // maybe we are on an android system
         this.logDirName = "/sdcard/" + this.logDirName;

         try
         {
            File logDirFile = new File(logDirName);

            if ( ! logDirFile.exists())
            {
               logDirFile.mkdirs();
            }
         }
         catch (Exception e2)
         {
            Logger.getGlobal().log(Level.SEVERE, "could not create log directory " + this.logDirName, e2);
         }
      }
   }


   private void handleEvent(PropertyChangeEvent e)
   {
      String buf = eventYamler.encode(e);

      try
      {
         File logDirFile = new File(logDirName);
         try
         {
            boolean mkdirs = logDirFile.mkdirs();
            logFilePath = new File(logDirName +"/" + logFileName);
            if ( ! logFilePath.exists())
            {
               logFilePath.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(logFilePath, true);
            fileWriter.write(buf.toString());
            fileWriter.flush();
            fileWriter.close();
         }
         catch (Exception e2)
         {
            logDirName = "/sdcard/" + logDirName;
            logDirFile = new File(logDirName);
            logDirFile.mkdirs();
            logFilePath = new File(logDirName +"/" + logFileName);
            if ( ! logFilePath.exists())
            {
               logFilePath.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(logFilePath, true);
            fileWriter.write(buf);
            fileWriter.flush();
            fileWriter.close();
         }
      }
      catch (IOException e1)
      {
         e1.printStackTrace();
         Logger.getGlobal().log(Level.SEVERE, "could not write log to " + logFilePath, e);
      }
   }




   private boolean storeModel(Object rootObject)
   {
      try
      {
         modelFile = new File(logDirName + "/model.yaml");

         if ( ! modelFile.exists())
         {
            modelFile.createNewFile();
         }

         String yamlText = yamlIdMap.encode(rootObject);

         FileWriter fileWriter = new FileWriter(logDirName + "/model.yaml");
         fileWriter.write(yamlText);
         fileWriter.flush();
         fileWriter.close();

         return true;
      }
      catch (Exception e)
      {
         Logger.getGlobal().log(Level.SEVERE, "could not store model to file " + this.logDirName + "/model.yaml");
      }

      return false;
   }


   private void loadModel(Object rootObject)
   {
      try
      {
         modelFile = new File(logDirName + "/model.yaml");

         if ( ! modelFile.exists())
         {
           return;
         }

         byte[] bytes = read(modelFile);

         if (bytes == null) return;

         String content = new String(bytes);

         yamlIdMap.decode(content, rootObject);

      }
      catch (Exception e)
      {
         Logger.getGlobal().log(Level.SEVERE, "could not store model to file " + this.logDirName + "/model.yaml");
      }
   }




   private void loadEvents(Object rootObject)
   {

      try
      {
         File logDirFile = new File(logDirName);
         if (logDirFile.exists())
         {
            logFilePath = new File(logDirName + "/" + logFileName);
         }
         else
         {
            logDirFile = new File("/sdcard/" + logDirName);
            logFilePath = new File(logDirName + "/" + logFileName);
         }
      }
      catch (Exception e)
      {
         Logger.getGlobal().log(Level.SEVERE, "could not create log dir " + logDirName);
         return;
      }

      if (logFilePath == null) return;
      if ( ! logFilePath.exists()) return;

      byte[] bytes = new byte[0];
      bytes = read(logFilePath);

      if (bytes == null) return;

      String content = new String(bytes);

      String packageName = rootObject.getClass().getPackage().getName();
      eventYamler.decode(rootObject, content);
   }


   public byte[] read(File file) {
      byte[] buffer = new byte[(int) file.length()];
      InputStream ios = null;
      try
      {
         ios = new FileInputStream(file);
         if (ios.read(buffer) == -1)
         {
            throw new RuntimeException("EOF reached while trying to read the whole file");
         }
      }
      catch (Exception e)
      {
        Logger.getGlobal().log(Level.SEVERE, "failed reading yaml log", e);
      }
      finally
      {
         try
         {
            if (ios != null)
               ios.close();
         }
         catch (IOException e)
         {
         }
      }
      return buffer;
   }


}

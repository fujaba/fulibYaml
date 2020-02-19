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
   private File logFilePath;
   private File modelFile;
   private String logDirName = "tmp";
   private String logFileName;

   /**
    * @deprecated since 1.2; use {@link #DataManager(Object, String)} instead
    */
   @Deprecated
   public DataManager()
   {
   }

   /**
    * @since 1.2
    */
   public DataManager(Object root, String logDirName)
   {
      this.attach(root, logDirName);
   }

   /**
    * @deprecated since 1.2; use {@link #DataManager()} instead
    */
   @Deprecated
   public static DataManager get()
   {
      return new DataManager();
   }

   /**
    * @deprecated since 1.2; use {@link #DataManager(Object, String)} instead
    */
   @Deprecated
   public DataManager attach(Object rootObject, String logDirName)
   {
      this.getOrCreateLogDir(logDirName);

      this.logFileName = "logFile.yaml";

      String packageName = rootObject.getClass().getPackage().getName();
      this.yamlIdMap = new YamlIdMap(packageName);
      this.eventYamler = new EventYamler(packageName).setYamlIdMap(this.yamlIdMap);
      this.reflectorMap = new ReflectorMap(packageName);

      this.loadModel(rootObject);

      this.loadEvents(rootObject);

      this.storeModel(rootObject);

      this.removeLogFile();

      new ModelListener(rootObject, e -> this.handleEvent(e));

      return this;
   }

   private void removeLogFile()
   {
      try
      {
         File logFile = new File(this.logDirName + "/" + this.logFileName);
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

         if (!logDirFile.exists())
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

            if (!logDirFile.exists())
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
      String buf = this.eventYamler.encode(e);

      try
      {
         File logDirFile = new File(this.logDirName);
         try
         {
            boolean mkdirs = logDirFile.mkdirs();
            this.logFilePath = new File(this.logDirName + "/" + this.logFileName);
            if (!this.logFilePath.exists())
            {
               this.logFilePath.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(this.logFilePath, true);
            fileWriter.write(buf);
            fileWriter.flush();
            fileWriter.close();
         }
         catch (Exception e2)
         {
            this.logDirName = "/sdcard/" + this.logDirName;
            logDirFile = new File(this.logDirName);
            logDirFile.mkdirs();
            this.logFilePath = new File(this.logDirName + "/" + this.logFileName);
            if (!this.logFilePath.exists())
            {
               this.logFilePath.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(this.logFilePath, true);
            fileWriter.write(buf);
            fileWriter.flush();
            fileWriter.close();
         }
      }
      catch (IOException e1)
      {
         e1.printStackTrace();
         Logger.getGlobal().log(Level.SEVERE, "could not write log to " + this.logFilePath, e);
      }
   }

   private boolean storeModel(Object rootObject)
   {
      try
      {
         this.modelFile = new File(this.logDirName + "/model.yaml");

         if (!this.modelFile.exists())
         {
            this.modelFile.createNewFile();
         }

         String yamlText = this.yamlIdMap.encode(rootObject);

         FileWriter fileWriter = new FileWriter(this.logDirName + "/model.yaml");
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
         this.modelFile = new File(this.logDirName + "/model.yaml");

         if (!this.modelFile.exists())
         {
            return;
         }

         byte[] bytes = this.read(this.modelFile);

         if (bytes == null)
         {
            return;
         }

         String content = new String(bytes);

         this.yamlIdMap.decode(content, rootObject);
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
         File logDirFile = new File(this.logDirName);
         if (logDirFile.exists())
         {
            this.logFilePath = new File(this.logDirName + "/" + this.logFileName);
         }
         else
         {
            logDirFile = new File("/sdcard/" + this.logDirName);
            this.logFilePath = new File(this.logDirName + "/" + this.logFileName);
         }
      }
      catch (Exception e)
      {
         Logger.getGlobal().log(Level.SEVERE, "could not create log dir " + this.logDirName);
         return;
      }

      if (this.logFilePath == null)
      {
         return;
      }
      if (!this.logFilePath.exists())
      {
         return;
      }

      byte[] bytes = new byte[0];
      bytes = this.read(this.logFilePath);

      if (bytes == null)
      {
         return;
      }

      String content = new String(bytes);

      String packageName = rootObject.getClass().getPackage().getName();
      this.eventYamler.decode(rootObject, content);
   }

   /**
    * @deprecated since 1.2; for internal use only
    */
   @Deprecated
   public byte[] read(File file)
   {
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
            {
               ios.close();
            }
         }
         catch (IOException e)
         {
         }
      }
      return buffer;
   }
}

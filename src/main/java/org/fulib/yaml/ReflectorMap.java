package org.fulib.yaml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReflectorMap
{
   Map<String, Reflector> reflectorMap = new LinkedHashMap<>();

   private ArrayList<String> packageNames;

   private Class eObject = null;

   public ReflectorMap(String packageName)
   {
      ArrayList<String> packageNames = new ArrayList<>();

      packageNames.add(packageName);

      this.packageNames = packageNames;

      try
      {
         eObject = Class.forName("org.eclipse.emf.ecore.EClass");
         Logger.getGlobal().log(Level.INFO, "could load org.eclipse.emf.ecore.EClass");
      }
      catch (ClassNotFoundException e)
      {
      }
   }

   public ReflectorMap(ArrayList<String> packageNames)
   {
      this.packageNames = packageNames;

      try
      {
         eObject = ClassLoader.getSystemClassLoader().loadClass("org.eclipse.emf.ecore.EObject");
         Logger.getGlobal().log(Level.INFO, "could load org.eclipse.emf.ecore.EObject");
      }
      catch (ClassNotFoundException e)
      {
      }
   }


   public Reflector getReflector(Object newObject)
   {
      if (newObject instanceof YamlObject)
      {
         return new YamlObjectReflector(newObject);
      }

      String simpleName = newObject.getClass().getSimpleName();

      return getReflector(simpleName);
   }


   public Reflector getReflector(String clazzName)
   {
      // already known?
      Reflector reflector = reflectorMap.get(clazzName);

      if (reflector != null) return reflector; //====================

      for (String packageName : packageNames)
      {
         String fullClassName = packageName + "." + clazzName;

         try
         {
            Class<?> theClass = Class.forName(fullClassName);

            if (theClass != null)
            {
               reflector = new Reflector().setClassName(fullClassName);
               reflectorMap.put(clazzName, reflector);
               return reflector;
            }
         }
         catch (Exception e)
         {
            if (eObject == null) return null; //=======================

            try
            {
               fullClassName = packageName + ".impl." + clazzName;

               Class<?> theClass = Class.forName(fullClassName);

               if (theClass != null)
               {
                  reflector = new Reflector()
                        .setClassName(fullClassName)
                        .setUseEMF();
                  reflectorMap.put(clazzName, reflector);
                  return reflector;
               }
            }
            catch (Exception e2)
            {
               reflector = null;
            }
         }
      }

      if (reflector == null)
      {
         String packagesString = "";
         for (String name : packageNames)
         {
            packagesString += "   " + name + "\n";
         }


         throw new RuntimeException("ReflectorMap could not find class description for " + clazzName + "\n" +
               "searching in \n" + packagesString +
               "You might add more packages to the construction of the ReflectorMap / YamlIdMap \n" +
               "or you might move the missing class into the common model package.");
      }

      return reflector;
   }

}

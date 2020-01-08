package org.fulib.yaml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReflectorMap
{
   // =============== Fields ===============

   private Map<String, Reflector> reflectorMap = new LinkedHashMap<>();

   private ArrayList<String> packageNames;

   private Class<?> eObject;

   // =============== Constructors ===============

   public ReflectorMap(String packageName)
   {
      ArrayList<String> packageNames = new ArrayList<>();

      packageNames.add(packageName);

      this.packageNames = packageNames;

      try
      {
         this.eObject = Class.forName("org.eclipse.emf.ecore.EClass");
         Logger.getGlobal().log(Level.INFO, "could load org.eclipse.emf.ecore.EClass");
      }
      catch (ClassNotFoundException ignored)
      {
      }
   }

   public ReflectorMap(ArrayList<String> packageNames)
   {
      this.packageNames = packageNames;

      try
      {
         this.eObject = ClassLoader.getSystemClassLoader().loadClass("org.eclipse.emf.ecore.EObject");
         Logger.getGlobal().log(Level.INFO, "could load org.eclipse.emf.ecore.EObject");
      }
      catch (ClassNotFoundException ignored)
      {
      }
   }

   // =============== Methods ===============

   public Reflector getReflector(Object newObject)
   {
      if (newObject instanceof YamlObject)
      {
         return new YamlObjectReflector(newObject);
      }

      String simpleName = newObject.getClass().getSimpleName();
      String fullName = newObject.getClass().getName();
      String packageName = newObject.getClass().getPackage().getName();

      if (this.packageNames.contains(packageName))
      {
         // yes, we should reflect this object
         Reflector reflector = this.reflectorMap.get(simpleName);

         if (reflector == null)
         {
            reflector = new Reflector().setClassName(fullName).setClazz(newObject.getClass());
            this.reflectorMap.put(simpleName, reflector);
         }

         return reflector;
      }

      return this.getReflector(simpleName);
   }

   public Reflector getReflector(String clazzName)
   {
      // already known?
      Reflector reflector = this.reflectorMap.get(clazzName);

      if (reflector != null)
         return reflector; //====================

      for (String packageName : this.packageNames)
      {
         String fullClassName = packageName + "." + clazzName;

         try
         {
            Class<?> theClass = Class.forName(fullClassName);

            if (theClass != null)
            {
               reflector = new Reflector().setClassName(fullClassName);
               this.reflectorMap.put(clazzName, reflector);
               return reflector;
            }
         }
         catch (Exception e)
         {
            if (this.eObject == null)
               continue; //=======================

            try
            {
               fullClassName = packageName + ".impl." + clazzName;

               Class<?> theClass = Class.forName(fullClassName);

               if (theClass != null)
               {
                  reflector = new Reflector().setClassName(fullClassName).setUseEMF();
                  this.reflectorMap.put(clazzName, reflector);
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
         StringBuilder message = new StringBuilder();
         message.append("ReflectorMap could not find class description for ").append(clazzName)
                .append("\nsearching in \n");

         for (String name : this.packageNames)
         {
            message.append("   ").append(name).append("\n");
         }

         message.append("You might add more packages to the construction of the ReflectorMap / YamlIdMap \n"
                        + "or you might move the missing class into the common model package.");

         throw new RuntimeException(message.toString());
      }

      return reflector;
   }
}

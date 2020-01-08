package org.fulib.yaml;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReflectorMap
{
   // =============== Fields ===============

   private Map<String, Reflector> reflectorMap = new LinkedHashMap<>();

   private final Collection<String> packageNames;

   private Class<?> eObject;

   // =============== Constructors ===============

   /**
    * Creates a new {@link ReflectorMap}.
    *
    * @param packageName the package name to search for classes
    */
   public ReflectorMap(String packageName)
   {
      this(Collections.singleton(packageName));
   }

   /**
    * Creates a new {@link ReflectorMap}.
    * 
    * @param packageNames the package names to search for classes
    *                     
    * @since 1.2
    */
   public ReflectorMap(String... packageNames)
   {
      this(Arrays.asList(packageNames));
   }

   /**
    * Creates a new {@link ReflectorMap}.
    *
    * @param packageNames the package names to search for classes
    *
    * @since 1.2
    */
   public ReflectorMap(Collection<String> packageNames)
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

   /**
    * Creates a new {@link ReflectorMap}.
    *
    * @param packageNames the package names to search for classes
    *
    * @deprecated since 1.2; use {@link #ReflectorMap(Collection)} instead
    */
   public ReflectorMap(ArrayList<String> packageNames)
   {
      this((Collection<String>) packageNames);
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

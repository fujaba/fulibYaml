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
    * @param packageName
    *    the package name to search for classes
    */
   public ReflectorMap(String packageName)
   {
      this(Collections.singleton(packageName));
   }

   /**
    * Creates a new {@link ReflectorMap}.
    *
    * @param packageNames
    *    the package names to search for classes
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
    * @param packageNames
    *    the package names to search for classes
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
    * @param packageNames
    *    the package names to search for classes
    *
    * @deprecated since 1.2; use {@link #ReflectorMap(Collection)} instead
    */
   @Deprecated
   public ReflectorMap(ArrayList<String> packageNames)
   {
      this((Collection<String>) packageNames);
   }

   // =============== Methods ===============

   /**
    * @since 1.2
    */
   public Set<Object> discoverObjects(Object root)
   {
      final Set<Object> neighbors = new HashSet<>();
      this.discoverObjects(root, neighbors);
      return neighbors;
   }

   /**
    * @since 1.2
    */
   public Set<Object> discoverObjects(Object... roots)
   {
      final Set<Object> neighbors = new HashSet<>();
      for (final Object root : roots)
      {
         this.discoverObjects(root, neighbors);
      }
      return neighbors;
   }

   /**
    * @since 1.2
    */
   public void discoverObjects(Object root, Set<Object> out)
   {
      if (root == null || !this.canReflect(root) || !out.add(root))
      {
         return;
      }

      final Reflector reflector = this.getReflector(root);

      for (final String property : reflector.getAllProperties())
      {
         final Object value = reflector.getValue(root, property);
         if (value instanceof Collection)
         {
            for (Object item : (Collection<?>) value)
            {
               this.discoverObjects(item, out);
            }
         }
         else
         {
            this.discoverObjects(value, out);
         }
      }
   }

   /**
    * @since 1.2
    */
   public boolean canReflect(Object object)
   {
      return object != null && this.canReflect(object.getClass());
   }

   /**
    * @since 1.2
    */
   public boolean canReflect(Class<?> type)
   {
      return this.canReflect(type.getPackage());
   }

   /**
    * @since 1.2
    */
   public boolean canReflect(Package thePackage)
   {
      return this.canReflect(thePackage.getName());
   }

   /**
    * @since 1.2
    */
   private boolean canReflect(String packageName)
   {
      return this.packageNames.contains(packageName);
   }

   public Reflector getReflector(Object newObject)
   {
      if (newObject instanceof YamlObject)
      {
         return new YamlObjectReflector((YamlObject) newObject);
      }

      return this.getReflector(newObject.getClass());
   }

   /**
    * @since 1.2
    */
   public Reflector getReflector(Class<?> clazz)
   {
      final String packageName = clazz.getPackage().getName();
      final String fullName = clazz.getName();

      if (!this.packageNames.contains(packageName))
      {
         throw this.unknownClassException(fullName);
      }

      // yes, we should reflect this object

      final String simpleName = clazz.getSimpleName();
      return this.reflectorMap.computeIfAbsent(simpleName, k -> new Reflector().setClassName(fullName).setClazz(clazz));
   }

   public Reflector getReflector(String clazzName)
   {
      return this.reflectorMap.computeIfAbsent(clazzName, this::createReflector);
   }

   private Reflector createReflector(String clazzName)
   {
      for (String packageName : this.packageNames)
      {
         String fullClassName = packageName + "." + clazzName;

         try
         {
            Class<?> theClass = Class.forName(fullClassName);
            return new Reflector().setClassName(fullClassName).setClazz(theClass);
         }
         catch (ClassNotFoundException ignored)
         {
         }

         if (this.eObject == null)
         {
            continue;
         }

         try
         {
            final String implClassName = packageName + ".impl." + clazzName;
            final Class<?> implClass = Class.forName(implClassName);
            return new Reflector().setClassName(implClassName).setClazz(implClass).setUseEMF();
         }
         catch (ClassNotFoundException ignored)
         {
         }
      }

      throw this.unknownClassException(clazzName);
   }

   private RuntimeException unknownClassException(String className)
   {
      StringBuilder message = new StringBuilder();
      message.append("ReflectorMap could not find class description for ")
             .append(className)
             .append("\nsearching in \n");

      for (String name : this.packageNames)
      {
         message.append("   ").append(name).append("\n");
      }

      message.append("You might add more packages to the construction of the ReflectorMap / YamlIdMap \n"
                     + "or you might move the missing class into the common model package.");

      return new RuntimeException(message.toString());
   }
}

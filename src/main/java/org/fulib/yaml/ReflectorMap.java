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
    * @param packageName
    *    the package name to search for classes
    */
   public ReflectorMap(String packageName)
   {
      this(Collections.singleton(packageName));
   }

   /**
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
    * Discovers all objects reachable from the {@code root} and within the packages specified in the constructor.
    * Objects that {@linkplain #canReflect(Object) cannot be handled} are neither added to the result nor recursively scanned.
    *
    * @param root
    *    the root object
    *
    * @return the set of all discovered objects
    *
    * @since 1.2
    */
   public Set<Object> discoverObjects(Object root)
   {
      final Set<Object> neighbors = new HashSet<>();
      this.discoverObjects(root, neighbors);
      return neighbors;
   }

   /**
    * Discovers all objects reachable from the {@code roots} and within the packages specified in the constructor.
    * Objects that {@linkplain #canReflect(Object) cannot be handled} are neither added to the result nor recursively scanned.
    *
    * @param roots
    *    the root objects
    *
    * @return the set of all discovered objects
    *
    * @since 1.2
    */
   public Set<Object> discoverObjects(Object... roots)
   {
      final Set<Object> neighbors = new HashSet<>();
      this.discoverObjects(roots, neighbors);
      return neighbors;
   }

   /**
    * Discovers all objects reachable from the {@code roots} and within the packages specified in the constructor.
    * Objects that {@linkplain #canReflect(Object) cannot be handled} are neither added to the result nor recursively scanned.
    *
    * @param roots
    *    the root objects
    *
    * @return the set of all discovered objects
    *
    * @since 1.2
    */
   public Set<Object> discoverObjects(Collection<?> roots)
   {
      final Set<Object> neighbors = new HashSet<>();
      this.discoverObjects(roots, neighbors);
      return neighbors;
   }

   /**
    * Discovers all objects reachable from the {@code root} and within the packages specified in the constructor.
    * Objects that {@linkplain #canReflect(Object) cannot be handled} are neither added to the result nor recursively scanned.
    *
    * @param root
    *    the root object
    * @param out
    *    the set to which results are added
    *
    * @since 1.2
    */
   public void discoverObjects(Object root, Set<Object> out)
   {
      if (root == null)
      {
         return;
      }

      if (root instanceof Collection)
      {
         this.discoverObjects((Collection<?>) root, out);
         return;
      }
      else if (root instanceof Object[])
      {
         this.discoverObjects((Object[]) root);
         return;
      }

      if (!this.canReflect(root) || !out.add(root))
      {
         return;
      }

      final Reflector reflector = this.getReflector(root);

      for (final String property : reflector.getAllProperties())
      {
         final Object value = reflector.getValue(root, property);
         this.discoverObjects(value, out);
      }
   }

   /**
    * Discovers all objects reachable from the {@code roots} and within the packages specified in the constructor.
    * Objects that {@linkplain #canReflect(Object) cannot be handled} are neither added to the result nor recursively scanned.
    *
    * @param roots
    *    the root objects
    * @param out
    *    the set to which results are added
    *
    * @since 1.2
    */
   public void discoverObjects(Object[] roots, Set<Object> out)
   {
      for (final Object root : roots)
      {
         this.discoverObjects(root, out);
      }
   }

   /**
    * Discovers all objects reachable from the {@code roots} and within the packages specified in the constructor.
    * Objects that {@linkplain #canReflect(Object) cannot be handled} are neither added to the result nor recursively scanned.
    *
    * @param roots
    *    the root objects
    * @param out
    *    the set to which results are added
    *
    * @since 1.2
    */
   public void discoverObjects(Collection<?> roots, Set<Object> out)
   {
      for (final Object item : roots)
      {
         this.discoverObjects(item, out);
      }
   }

   /**
    * @return {@code true} if the given object is not {@code null} and can be handled by this reflector map,
    * i.e. whether it's package name is part of the package names specified in the constructor,
    * and {@code false} otherwise.
    *
    * @see #canReflect(Class)
    * @see #canReflect(Package)
    * @see #canReflect(String)
    * @since 1.2
    */
   public boolean canReflect(Object object)
   {
      return object != null && this.canReflect(object.getClass());
   }

   /**
    * @return {@code true} if the given class can be handled by this reflector map,
    * i.e. whether it's package name is part of the package names specified in the constructor,
    * and {@code false} otherwise.
    *
    * @see #canReflect(Package)
    * @see #canReflect(String)
    * @since 1.2
    */
   public boolean canReflect(Class<?> type)
   {
      return this.canReflect(type.getPackage()) || YamlObject.class.isAssignableFrom(type);
   }

   /**
    * @return {@code true} if the given package can be handled by this reflector map,
    * i.e. whether it's name is part of the package names specified in the constructor,
    * and {@code false} otherwise.
    *
    * @see #canReflect(String)
    * @since 1.2
    */
   public boolean canReflect(Package thePackage)
   {
      return thePackage != null && this.canReflect(thePackage.getName());
   }

   /**
    * @return {@code true} if the given package name can be handled by this reflector map,
    * i.e. whether it is part of the package names specified in the constructor,
    * and {@code false} otherwise.
    *
    * @since 1.2
    */
   private boolean canReflect(String packageName)
   {
      return this.packageNames.contains(packageName);
   }

   /**
    * @param object
    *    the object whose class should be reflected
    *
    * @return a {@link Reflector} corresponding to the class of the given {@code object},
    * or a {@link YamlObjectReflector} if the {@code object} is a {@link YamlObject}.
    *
    * @throws NullPointerException
    *    if {@code object == null}
    * @throws RuntimeException
    *    if the given {@code object} {@linkplain #canReflect(Object) cannot be handled} by this reflector
    * @since 1.2
    */
   public Reflector getReflector(Object object)
   {
      if (object instanceof YamlObject)
      {
         return new YamlObjectReflector((YamlObject) object);
      }

      return this.getReflector(object.getClass());
   }

   /**
    * @param clazz
    *    the class to be reflected
    *
    * @return a {@link Reflector} corresponding to the given {@code clazz}
    *
    * @throws RuntimeException
    *    if the given {@code clazz} {@linkplain #canReflect(Class) cannot be handled} by this reflector
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

   /**
    * @param simpleName
    *    the {@linkplain Class#getSimpleName() simple name} of the class to be reflected
    *
    * @return a {@link Reflector} corresponding to the given Class with the given {@code simpleName}
    *
    * @throws RuntimeException
    *    if the no class with the given {@code simpleName} can be found in the packages specified in the constructor
    * @since 1.2
    */
   public Reflector getReflector(String simpleName)
   {
      return this.reflectorMap.computeIfAbsent(simpleName, this::createReflector);
   }

   private Reflector createReflector(String simpleName)
   {
      for (String packageName : this.packageNames)
      {
         String fullClassName = packageName + "." + simpleName;

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
            final String implClassName = packageName + ".impl." + simpleName;
            final Class<?> implClass = Class.forName(implClassName);
            return new Reflector().setClassName(implClassName).setClazz(implClass).setUseEMF();
         }
         catch (ClassNotFoundException ignored)
         {
         }
      }

      throw this.unknownClassException(simpleName);
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

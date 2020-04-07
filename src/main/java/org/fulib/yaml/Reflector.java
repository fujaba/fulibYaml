package org.fulib.yaml;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Reflector
{
   // =============== Constants ===============

   // Sentinels
   private static final Object INCOMPATIBLE = new Object();

   // =============== Fields ===============

   private String className = "";
   private Method emfCreateMethod;
   private Object emfFactory;
   private Class<?> eObjectClass;
   private Class<?> clazz;

   private transient Set<String> ownProperties;
   private transient Set<String> allProperties;

   private transient Map<String, List<Method>> setterCache = new HashMap<>();

   // =============== Properties ===============

   public Class<?> getClazz()
   {
      if (this.clazz == null)
      {
         try
         {
            this.clazz = Class.forName(this.className);
         }
         catch (ClassNotFoundException e)
         {
            e.printStackTrace();
         }
      }
      return this.clazz;
   }

   public Reflector setClazz(Class<?> clazz)
   {
      this.clazz = clazz;
      return this;
   }

   public String getClassName()
   {
      return this.className;
   }

   public Reflector setClassName(String className)
   {
      this.className = className;
      return this;
   }

   /**
    * Signifies to this reflector that the underlying {@linkplain #getClazz() class} uses EMF.
    * As such, semantics of the various methods provided by this class change in accordance to the EMF conventions.
    *
    * @return this instance, to allow method chaining
    */
   public Reflector setUseEMF()
   {
      String packageName = this.className;
      // chop simpleClassName
      int pos = packageName.lastIndexOf('.');
      String simpleClassName = packageName.substring(pos + 1);
      simpleClassName = simpleClassName.substring(0, simpleClassName.length() - "Impl".length());
      packageName = packageName.substring(0, pos);

      // chop .impl
      packageName = packageName.substring(0, packageName.length() - ".impl".length());

      pos = packageName.lastIndexOf('.');
      String lastPart = packageName.substring(pos + 1);
      String simpleFactoryName = StrUtil.cap(lastPart) + "Factory";
      try
      {
         Class<?> factoryClass = Class.forName(packageName + "." + simpleFactoryName);
         Field eInstanceField = factoryClass.getField("eINSTANCE");
         this.emfFactory = eInstanceField.get(null);

         this.emfCreateMethod = this.emfFactory.getClass().getMethod("create" + simpleClassName);

         this.eObjectClass = Class.forName("org.eclipse.emf.ecore.EObject");
      }
      catch (Exception e)
      {
         Logger.getGlobal().log(Level.SEVERE, "could not find EMF Factory createXY method", e);
      }

      return this;
   }

   /**
    * Equivalent to {@link #getOwnProperties()}{@code .toArray(new String[0])}.
    *
    * @return a sorted array of names of properties the underlying {@link #getClazz() clazz} has.
    *
    * @deprecated since 1.2; use {@link #getOwnProperties()} instead
    */
   @Deprecated
   public String[] getProperties()
   {
      return this.getOwnProperties().toArray(new String[0]);
   }

   /**
    * @return a sorted set of names of properties of the underlying {@linkplain #getClazz() class}.
    *
    * @since 1.2
    */
   public Set<String> getOwnProperties()
   {
      if (this.ownProperties != null)
      {
         return this.ownProperties;
      }

      final Set<String> ownProperties = new TreeSet<>();
      addOwnProperties(this.getClazz(), ownProperties);
      return this.ownProperties = Collections.unmodifiableSet(ownProperties);
   }

   /**
    * @return a sorted set of names of properties the underlying {@linkplain #getClazz() class}, it's super class and
    * it's interfaces provide.
    *
    * @since 1.2
    */
   public Set<String> getAllProperties()
   {
      if (this.allProperties != null)
      {
         return this.allProperties;
      }

      final Set<String> ownProperties = new TreeSet<>();
      addAllProperties(this.getClazz(), ownProperties);
      return this.allProperties = Collections.unmodifiableSet(ownProperties);
   }

   private static void addOwnProperties(Class<?> clazz, Set<String> fieldNames)
   {
      final Method[] methods = clazz.getMethods();
      for (Method method : methods)
      {
         String methodName = method.getName();

         // getter, but not getClass(), get() or parameterized
         if (method.getParameterCount() == 0 && methodName.length() > 3 // fast checks
             && methodName.startsWith("get") && !"getClass".equals(methodName))
         {
            final String attributeName = StrUtil.downFirstChar(methodName.substring(3));
            fieldNames.add(attributeName);
         }
      }
   }

   private static void addAllProperties(Class<?> clazz, Set<String> fieldNames)
   {
      addOwnProperties(clazz, fieldNames);

      final Class<?> superClass = clazz.getSuperclass();
      if (superClass != null)
      {
         addAllProperties(superClass, fieldNames);
      }

      for (final Class<?> superInterface : clazz.getInterfaces())
      {
         addAllProperties(superInterface, fieldNames);
      }
   }

   // =============== Methods ===============

   /**
    * Creates a new instance of the underlying {@linkplain #getClazz() class}.
    * If this reflector {@linkplain #setUseEMF() uses EMF}, the EMF create method is called.
    * Otherwise, the default (no-arg) constructor of the underlying class is invoked.
    * Any exception raised by either operation is ignored; in this case {@code null} is returned
    *
    * @return a new instance of the underlying class, or {@code null} if that fails
    */
   public Object newInstance()
   {
      try
      {
         if (this.emfCreateMethod != null)
         {
            return this.emfCreateMethod.invoke(this.emfFactory);
         }

         Class<?> clazz = this.getClazz();
         return clazz.newInstance();
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }

      return null;
   }

   /**
    * Gets the attribute of the object by invoking either {@code object.get<attribute>()}, {@code object.<attribute>()}
    * or {@code object.is<attribute>()} (trying in that order).
    *
    * @param object
    *    the receiver object
    * @param attribute
    *    the attribute name
    *
    * @return the return value of the getter, or {@code null} if no getter was found
    */
   public Object getValue(Object object, String attribute)
   {
      if (object == null)
      {
         return null;
      }

      final String capName = StrUtil.cap(attribute);
      Class<?> clazz = this.getClazz();

      // e.g. foo.getName(); default bean getter naming convention
      try
      {
         return clazz.getMethod("get" + capName).invoke(object);
      }
      catch (Exception ignored)
      {
      }

      // e.g. foo.name(); used by some code styles and Scala
      try
      {
         return clazz.getMethod(attribute).invoke(object);
      }
      catch (Exception ignored)
      {
      }

      // e.g. foo.isValid(); for booleans
      try
      {
         return clazz.getMethod("is" + capName).invoke(object);
      }
      catch (Exception ignored)
      {
      }

      return null;
   }

   /**
    * Sets the attribute of the object to the new value by invoking a fitting {@code set<attribute>(...)} or
    * {@code with<attribute>(...)} method or {@code get<attribute>.add(...)} for EMF to-many associations.
    *
    * @param object
    *    the object to modify
    * @param attribute
    *    the attribute name
    * @param value
    *    the new value
    * @param type
    *    unused
    *
    * @return the return value of the setter, or {@code true} if successful, or {@code null} if unsuccessful
    *
    * @deprecated since 1.2; use {@link #setValue(Object, String, Object)} instead
    */
   @Deprecated
   public Object setValue(Object object, String attribute, Object value, String type)
   {
      return this.setValue(object, attribute, value);
   }

   /**
    * Sets the attribute of the object to the new value by invoking a fitting {@code set<attribute>(...)} or
    * {@code with<attribute>(...)} method or {@code get<attribute>.add(...)} for EMF to-many associations.
    *
    * @param object
    *    the object to modify
    * @param attribute
    *    the attribute name
    * @param value
    *    the new value
    *
    * @return the return value of the setter, or {@code true} if successful, or {@code null} if unsuccessful
    *
    * @since 1.2
    */
   public Object setValue(Object object, String attribute, Object value)
   {
      if (object == null)
      {
         return null;
      }

      for (final Method setter : this.resolveSetters(attribute))
      {
         Class<?> targetType = setter.getParameterTypes()[0];

         if (setter.isVarArgs())
         {
            targetType = targetType.getComponentType();
         }

         Object param = this.coerce(value, targetType);
         if (param == INCOMPATIBLE)
         {
            continue;
         }

         if (setter.isVarArgs())
         {
            final Object array = Array.newInstance(targetType, 1);
            Array.set(array, 0, param);
            param = array;
         }

         try
         {
            return setter.invoke(object, param);
         }
         catch (InvocationTargetException e)
         {
            throw new RuntimeException(e.getTargetException());
         }
         catch (IllegalAccessException e)
         {
            throw handleIllegalAccess(e);
         }
      }

      if (this.emfCreateMethod != null)
      {
         try
         {
            // its o.getAssoc().add(v)
            Method getMethod = this.clazz.getMethod("get" + StrUtil.cap(attribute));
            Object collection = getMethod.invoke(object);
            Method addMethod = collection.getClass().getMethod("add", Object.class);
            return addMethod.invoke(collection, value);
         }
         catch (Exception ignored)
         {
         }
      }

      return null;
   }

   private static AssertionError handleIllegalAccess(IllegalAccessException e)
   {
      throw new AssertionError("this should not occur since we only search for public methods", e);
   }

   private Object coerce(Object value, Class<?> targetType)
   {
      if (value == null)
      {
         return targetType.isPrimitive() ? INCOMPATIBLE : null;
      }
      if (targetType.isInstance(value))
      {
         return value;
      }
      if (value instanceof String)
      {
         return this.coerce((String) value, targetType);
      }

      return INCOMPATIBLE;
   }

   private Object coerce(String value, Class<?> targetType)
   {
      switch (targetType.getName())
      {
      case "int":
      case "java.lang.Integer":
         return Integer.valueOf(value);
      case "long":
      case "java.lang.Long":
         return Long.valueOf(value);
      case "float":
      case "java.lang.Float":
         return Float.valueOf(value);
      case "double":
      case "java.lang.Double":
         return Double.valueOf(value);
      }
      return value;
   }

   private List<Method> resolveSetters(String propertyName)
   {
      return this.setterCache.computeIfAbsent(propertyName, this::loadSetters);
   }

   private List<Method> loadSetters(String propertyName)
   {
      final String uppercasedPropertyName = StrUtil.cap(propertyName);
      final String setterName = "set" + uppercasedPropertyName;
      final String witherName = "with" + uppercasedPropertyName;
      final List<Method> result = new ArrayList<>();

      for (final Method method : this.getClazz().getMethods())
      {
         final String methodName = method.getName();
         if (!setterName.equals(methodName) && !witherName.equals(methodName))
         {
            continue;
         }

         if (method.getParameterCount() != 1)
         {
            continue;
         }

         result.add(method);
      }

      return result;
   }

   /**
    * Removes all associations from the object by calling it's {@code removeYou()} method.
    * If that method is not available, no action is taken.
    * In particular, no exception is thrown in that case.
    *
    * @param object
    *    the object to remove
    */
   public void removeObject(Object object)
   {
      // call removeYou if possible
      try
      {
         Class<?> clazz = this.getClazz();
         Method removeYou = clazz.getMethod("removeYou");
         removeYou.invoke(object);
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }
   }
}

package org.fulib.yaml;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Reflector
{
   private String className = "";
   private Method emfCreateMethod;
   private Object emfFactory;
   private Class<?> eObjectClass;

   public String getClassName()
   {
      return className;
   }

   public Reflector setClassName(String className)
   {
      this.className = className;
      return this;
   }

   public void removeObject(Object object)
   {
      // call removeYou if possible
      try
      {
         Class<?> clazz = Class.forName(className);
         Method removeYou = clazz.getMethod("removeYou");
         removeYou.invoke(object);
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }

   }


   private String[] properties = null;

   public String[] getProperties()
   {
      if (properties != null)
      {
         return properties;
      }

      try
      {
         Class<?> clazz = Class.forName(className);

         Method[] methods = clazz.getMethods();

         LinkedHashSet<String> fieldNames = new LinkedHashSet<String>();
         for (Method method : methods)
         {
            String methodName = method.getName();

            if (methodName.startsWith("get")
                  && ! methodName.equals("getClass"))
            {
               methodName = methodName.substring(3);

               methodName = StrUtil.downFirstChar(methodName);

               if (!"".equals(methodName.trim()))
               {
                  fieldNames.add(methodName);
               }
            }

         }

         properties = fieldNames.toArray(new String[]{});

         Arrays.sort(properties);

         return properties;
      }
      catch (ClassNotFoundException e)
      {
         e.printStackTrace();
      }
      return null;
   }

   public Object newInstance()
   {
      try
      {
         if (emfCreateMethod != null)
         {
            Object emfObject = emfCreateMethod.invoke(emfFactory);
            return emfObject;
         }

         Class<?> clazz = Class.forName(className);
         return clazz.newInstance();
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }

      return null;
   }

   public Object getValue(Object object, String attribute)
   {
      if (object == null)
      {
         return null;
      }

      try
      {
         Class<?> clazz = Class.forName(className);

         Method method = clazz.getMethod("get" + StrUtil.cap(attribute));

         Object invoke = method.invoke(object);

         return invoke;
      }
      catch (Exception e)
      {
         try
         {
            Class<?> clazz = Class.forName(className);

            Method method = clazz.getMethod(attribute);

            Object invoke = method.invoke(object);

            return invoke;
         }
         catch (Exception e2)
         {
            // e.printStackTrace();
         }

      }

      return null;
   }

   public Object setValue(Object object, String attribute, Object value, String type)
   {
      if (object == null)
      {
         return null;
      }

      try
      {
         Class<?> clazz = Class.forName(className);

         Class<?> valueClass = value.getClass();

         if (eObjectClass != null && eObjectClass.isAssignableFrom(valueClass))
         {
            valueClass = valueClass.getInterfaces()[0];
         }

         Method method = clazz.getMethod("set" + StrUtil.cap(attribute), valueClass);

         Object result = method.invoke(object, value);

         return result;
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }

      // maybe a number
      try
      {
         int intValue = Integer.parseInt((String) value);
         Class<?> clazz = Class.forName(className);

         Method method = clazz.getMethod("set" + StrUtil.cap(attribute), int.class);

         method.invoke(object, intValue);

         return true;
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }

      // maybe a huge number
      try {
         long longValue = Long.parseLong((String) value);
         Class<?> clazz = Class.forName(className);

         Method method = clazz.getMethod("set" + StrUtil.cap(attribute), long.class);

         method.invoke(object, longValue);

         return true;
      } catch (Exception e) {
         // e.printStackTrace(); // I don't like this :(
      }

      // maybe a double
      try
      {
         double doubleValue = Double.parseDouble((String) value);
         Class<?> clazz = Class.forName(className);

         Method method = clazz.getMethod("set" + StrUtil.cap(attribute), double.class);

         method.invoke(object, doubleValue);

         return true;
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }

      // maybe a float
      try
      {
         float floatValue = Float.parseFloat((String) value);
         Class<?> clazz = Class.forName(className);

         Method method = clazz.getMethod("set" + StrUtil.cap(attribute), float.class);

         method.invoke(object, floatValue);

         return true;
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }

      // to-many?
      try
      {
         Class<?> clazz = Class.forName(className);

         Method method = clazz.getMethod("with" + StrUtil.cap(attribute), Object[].class);

         method.invoke(object, new Object[]{new Object[]{value}});

         return true;
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }

      try
      {
         if (emfCreateMethod != null)
         {
            Class<?> clazz = Class.forName(className);

            // its o.getAssoc().add(v)
            Method getMethod = clazz.getMethod("get" + StrUtil.cap(attribute));

            Object collection = getMethod.invoke(object);

            Method addMethod = collection.getClass().getMethod("add", Object.class);

            addMethod.invoke(collection, value);
            return true;
         }
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }

      return null;
   }

   public Reflector setUseEMF()
   {
      String packageName = className;
      // chop simpleClassName
      int pos = packageName.lastIndexOf('.');
      String simpleClassName = packageName.substring(pos+1);
      simpleClassName = simpleClassName.substring(0, simpleClassName.length()-"Impl".length());
      packageName = packageName.substring(0, pos);

      // chop .impl
      packageName = packageName.substring(0, packageName.length()-".impl".length());

      pos = packageName.lastIndexOf('.');
      String lastPart = packageName.substring(pos+1);
      String simpleFactoryName = StrUtil.cap(lastPart) + "Factory";
      try
      {
         Class factoryClass = Class.forName(packageName + "." + simpleFactoryName);
         Field eInstanceField = factoryClass.getField("eINSTANCE");
         emfFactory = eInstanceField.get(null);

         emfCreateMethod = emfFactory.getClass().getMethod("create" + simpleClassName);

         eObjectClass = Class.forName("org.eclipse.emf.ecore.EObject");

      }
      catch (Exception e)
      {
         Logger.getGlobal().log(Level.SEVERE, "could not find EMF Factory createXY method", e);
      }

      return this;
   }
}

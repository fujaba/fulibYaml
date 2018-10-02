package org.fulib.yaml;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.*;


public class ModelListener implements PropertyChangeListener
{
   private HashSet<Object> supervisedObjects = new HashSet<Object>();

   private PropertyChangeListener elementListener;

   private Set<Object> componentElements = new LinkedHashSet<Object>();

   private ReflectorMap creatorMap;
   private boolean closed = false;


   public ModelListener(Object root, PropertyChangeListener elementListener)
   {
      this.elementListener = elementListener;
      String packageName = root.getClass().getPackage().getName();
      ArrayList<String> packageNameList = new ArrayList<>();
      packageNameList.add(packageName);
      creatorMap = new ReflectorMap(packageNameList);
      subscribeTo(root);
   }

   public void removeYou()
   {
      this.closed = true;

      for (Object obj : supervisedObjects)
      {
         Class clazz = obj.getClass();
         try
         {
            Method removePropertyChangeListener = clazz.getMethod("removePropertyChangeListener", PropertyChangeListener.class);
            removePropertyChangeListener.invoke(obj, this);
         }
         catch (Exception e)
         {
            // just skip it
         }
      }
   }


   private void subscribeTo(Object newObject)
   {
      if (supervisedObjects.contains(newObject)) return;


      Class clazz = newObject.getClass();
      try
      {
         Method addPropertyChangeListener = clazz.getMethod("addPropertyChangeListener", PropertyChangeListener.class);
         addPropertyChangeListener.invoke(newObject, this);
      }
      catch (Exception e)
      {
         // just skip it
      }
      supervisedObjects.add(newObject);

      // run through elements and fire property changes and subscribe to neighbors
      Reflector reflector = creatorMap.getReflector(newObject);

      for (String prop : reflector.getProperties())
      {
         Object newValue = reflector.getValue(newObject, prop);

         if (newValue instanceof Collection)
         {
            Collection newCollection = (Collection) newValue;

            for (Object obj : newCollection)
            {
               Object newEntity = obj;

               PropertyChangeEvent event = new PropertyChangeEvent(newObject, prop, null, newEntity);

               propertyChange(event);
            }
         }
         else
         {
            PropertyChangeEvent event = new PropertyChangeEvent(newObject, prop, null, newValue);

            propertyChange(event);
         }
      }
   }

   @Override
   public void propertyChange(PropertyChangeEvent evt)
   {
      if (closed) return;

      // just forward
      if (evt.getNewValue() != null)
      {
         Object newValue = evt.getNewValue();

         if ( ! supervisedObjects.contains(newValue))
         {
            subscribeTo(newValue);
         }
      }

      elementListener.propertyChange(evt);
   }

}

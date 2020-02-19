package org.fulib.yaml;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class ModelListener implements PropertyChangeListener
{
   private HashSet<Object> supervisedObjects = new HashSet<>();

   private PropertyChangeListener elementListener;

   private Set<Object> componentElements = new LinkedHashSet<>();

   private ReflectorMap creatorMap;
   private boolean closed = false;

   public ModelListener(Object root, PropertyChangeListener elementListener)
   {
      this.elementListener = elementListener;
      String packageName = root.getClass().getPackage().getName();
      this.creatorMap = new ReflectorMap(packageName);
      this.subscribeTo(root);
   }

   public void removeYou()
   {
      this.closed = true;

      for (Object obj : this.supervisedObjects)
      {
         Class clazz = obj.getClass();
         try
         {
            Method removePropertyChangeListener = clazz.getMethod("removePropertyChangeListener",
                                                                  PropertyChangeListener.class);
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
      if (this.supervisedObjects.contains(newObject))
      {
         return;
      }

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
      this.supervisedObjects.add(newObject);

      // run through elements and fire property changes and subscribe to neighbors
      Reflector reflector = this.creatorMap.getReflector(newObject);

      if (reflector == null)
      {
         return; // don't know structure of newObject, probably a String
      }

      for (String prop : reflector.getOwnProperties())
      {
         Object newValue = reflector.getValue(newObject, prop);

         if (newValue instanceof Collection)
         {
            Collection newCollection = (Collection) newValue;

            for (Object obj : newCollection)
            {
               Object newEntity = obj;

               PropertyChangeEvent event = new PropertyChangeEvent(newObject, prop, null, newEntity);

               this.propertyChange(event);
            }
         }
         else
         {
            PropertyChangeEvent event = new PropertyChangeEvent(newObject, prop, null, newValue);

            this.propertyChange(event);
         }
      }
   }

   @Override
   public void propertyChange(PropertyChangeEvent evt)
   {
      if (this.closed)
      {
         return;
      }

      // just forward
      if (evt.getNewValue() != null)
      {
         Object newValue = evt.getNewValue();

         if (!this.supervisedObjects.contains(newValue))
         {
            this.subscribeTo(newValue);
         }
      }

      this.elementListener.propertyChange(evt);
   }
}

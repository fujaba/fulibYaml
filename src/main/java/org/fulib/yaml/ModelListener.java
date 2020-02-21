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
   private final PropertyChangeListener elementListener;
   private final ReflectorMap creatorMap;

   private final Set<Object> supervisedObjects = new HashSet<>();

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
         this.callChangeListenerMethod(obj, "removePropertyChangeListener");
      }
   }

   private void subscribeTo(Object newObject)
   {
      if (this.supervisedObjects.contains(newObject))
      {
         return;
      }

      this.callChangeListenerMethod(newObject, "addPropertyChangeListener");
      this.supervisedObjects.add(newObject);

      this.fireInitialPropertyChanges(newObject);
   }

   private void fireInitialPropertyChanges(Object newObject)
   {
      if (!this.creatorMap.canReflect(newObject))
      {
         return; // don't know structure of newObject, probably a String
      }

      Reflector reflector = this.creatorMap.getReflector(newObject);

      for (String prop : reflector.getAllProperties())
      {
         Object propertyValue = reflector.getValue(newObject, prop);

         if (propertyValue instanceof Collection)
         {
            for (Object obj : (Collection<?>) propertyValue)
            {
               this.propertyChange(new PropertyChangeEvent(newObject, prop, null, obj));
            }
         }
         else
         {
            this.propertyChange(new PropertyChangeEvent(newObject, prop, null, propertyValue));
         }
      }
   }

   private void callChangeListenerMethod(Object receiver, String methodName)
   {
      Class<?> clazz = receiver.getClass();
      try
      {
         Method addPropertyChangeListener = clazz.getMethod(methodName, PropertyChangeListener.class);
         addPropertyChangeListener.invoke(receiver, this);
      }
      catch (Exception e)
      {
         // just skip it
      }
   }

   @Override
   public void propertyChange(PropertyChangeEvent evt)
   {
      if (this.closed)
      {
         return;
      }

      final Object newValue = evt.getNewValue();
      if (newValue != null)
      {
         this.subscribeTo(newValue);
      }

      this.elementListener.propertyChange(evt);
   }
}

package org.fulib.yaml.testmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Order  
{

   public static final String PROPERTY_id = "id";

   private String id;

   public String getId()
   {
      return id;
   }

   public Order setId(String value)
   {
      if (value == null ? this.id != null : ! value.equals(this.id))
      {
         String oldValue = this.id;
         this.id = value;
         firePropertyChange("id", oldValue, value);
      }
      return this;
   }

   protected PropertyChangeSupport listeners = null;

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (listeners != null)
      {
         listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();

      result.append(" ").append(this.getId());
      result.append(" ").append(this.getDate());
      result.append(" ").append(this.getState());


      return result.substring(1);
   }

   public void removeYou()
   {
      this.setCustomer(null);

      this.withoutPositions(this.getPositions().clone());


   }

   public static final String PROPERTY_date = "date";

   private String date;

   public String getDate()
   {
      return date;
   }

   public Order setDate(String value)
   {
      if (value == null ? this.date != null : ! value.equals(this.date))
      {
         String oldValue = this.date;
         this.date = value;
         firePropertyChange("date", oldValue, value);
      }
      return this;
   }

   public static final String PROPERTY_state = "state";

   private String state;

   public String getState()
   {
      return state;
   }

   public Order setState(String value)
   {
      if (value == null ? this.state != null : ! value.equals(this.state))
      {
         String oldValue = this.state;
         this.state = value;
         firePropertyChange("state", oldValue, value);
      }
      return this;
   }

   public static final java.util.ArrayList<OrderPosition> EMPTY_positions = new java.util.ArrayList<OrderPosition>()
   { @Override public boolean add(OrderPosition value){ throw new UnsupportedOperationException("No direct add! Use xy.withPositions(obj)"); }};

   public static final String PROPERTY_positions = "positions";

   private java.util.ArrayList<OrderPosition> positions = null;

   public java.util.ArrayList<OrderPosition> getPositions()
   {
      if (this.positions == null)
      {
         return EMPTY_positions;
      }

      return this.positions;
   }

   public Order withPositions(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withPositions(i);
            }
         }
         else if (item instanceof OrderPosition)
         {
            if (this.positions == null)
            {
               this.positions = new java.util.ArrayList<OrderPosition>();
            }
            if ( ! this.positions.contains(item))
            {
               this.positions.add((OrderPosition)item);
               ((OrderPosition)item).setOrder(this);
               firePropertyChange("positions", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }

   public Order withoutPositions(Object... value)
   {
      if (this.positions == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutPositions(i);
            }
         }
         else if (item instanceof OrderPosition)
         {
            if (this.positions.contains(item))
            {
               this.positions.remove((OrderPosition)item);
               ((OrderPosition)item).setOrder(null);
               firePropertyChange("positions", item, null);
            }
         }
      }
      return this;
   }

   public static final String PROPERTY_customer = "customer";

   private Customer customer = null;

   public Customer getCustomer()
   {
      return this.customer;
   }

   public Order setCustomer(Customer value)
   {
      if (this.customer != value)
      {
         Customer oldValue = this.customer;
         if (this.customer != null)
         {
            this.customer = null;
            oldValue.withoutOrders(this);
         }
         this.customer = value;
         if (value != null)
         {
            value.withOrders(this);
         }
         firePropertyChange("customer", oldValue, value);
      }
      return this;
   }

}

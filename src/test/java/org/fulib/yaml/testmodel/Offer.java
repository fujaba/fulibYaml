package org.fulib.yaml.testmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Offer  
{

   public static final String PROPERTY_id = "id";

   private String id;

   public String getId()
   {
      return id;
   }

   public Offer setId(String value)
   {
      if (value == null ? this.id != null : ! value.equals(this.id))
      {
         String oldValue = this.id;
         this.id = value;
         firePropertyChange("id", oldValue, value);
      }
      return this;
   }

   public static final String PROPERTY_price = "price";

   private double price;

   public double getPrice()
   {
      return price;
   }

   public Offer setPrice(double value)
   {
      if (value != this.price)
      {
         double oldValue = this.price;
         this.price = value;
         firePropertyChange("price", oldValue, value);
      }
      return this;
   }

   public static final String PROPERTY_startTime = "startTime";

   private String startTime;

   public String getStartTime()
   {
      return startTime;
   }

   public Offer setStartTime(String value)
   {
      if (value == null ? this.startTime != null : ! value.equals(this.startTime))
      {
         String oldValue = this.startTime;
         this.startTime = value;
         firePropertyChange("startTime", oldValue, value);
      }
      return this;
   }

   public static final String PROPERTY_endTime = "endTime";

   private String endTime;

   public String getEndTime()
   {
      return endTime;
   }

   public Offer setEndTime(String value)
   {
      if (value == null ? this.endTime != null : ! value.equals(this.endTime))
      {
         String oldValue = this.endTime;
         this.endTime = value;
         firePropertyChange("endTime", oldValue, value);
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
      result.append(" ").append(this.getStartTime());
      result.append(" ").append(this.getEndTime());


      return result.substring(1);
   }

   public static final String PROPERTY_product = "product";

   private Product product = null;

   public Product getProduct()
   {
      return this.product;
   }

   public Offer setProduct(Product value)
   {
      if (this.product != value)
      {
         Product oldValue = this.product;
         if (this.product != null)
         {
            this.product = null;
            oldValue.withoutOffers(this);
         }
         this.product = value;
         if (value != null)
         {
            value.withOffers(this);
         }
         firePropertyChange("product", oldValue, value);
      }
      return this;
   }

   public void removeYou()
   {
      this.setProduct(null);

      this.withoutOrders(this.getOrders().clone());


   }

   public static final java.util.ArrayList<OrderPosition> EMPTY_orders = new java.util.ArrayList<OrderPosition>()
   { @Override public boolean add(OrderPosition value){ throw new UnsupportedOperationException("No direct add! Use xy.withOrders(obj)"); }};

   public static final String PROPERTY_orders = "orders";

   private java.util.ArrayList<OrderPosition> orders = null;

   public java.util.ArrayList<OrderPosition> getOrders()
   {
      if (this.orders == null)
      {
         return EMPTY_orders;
      }

      return this.orders;
   }

   public Offer withOrders(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withOrders(i);
            }
         }
         else if (item instanceof OrderPosition)
         {
            if (this.orders == null)
            {
               this.orders = new java.util.ArrayList<OrderPosition>();
            }
            if ( ! this.orders.contains(item))
            {
               this.orders.add((OrderPosition)item);
               ((OrderPosition)item).setOffer(this);
               firePropertyChange("orders", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }

   public Offer withoutOrders(Object... value)
   {
      if (this.orders == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutOrders(i);
            }
         }
         else if (item instanceof OrderPosition)
         {
            if (this.orders.contains(item))
            {
               this.orders.remove((OrderPosition)item);
               ((OrderPosition)item).setOffer(null);
               firePropertyChange("orders", item, null);
            }
         }
      }
      return this;
   }

}

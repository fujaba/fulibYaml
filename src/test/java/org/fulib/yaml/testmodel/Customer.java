package org.fulib.yaml.testmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Customer  
{

   public static final String PROPERTY_id = "id";

   private String id;

   public String getId()
   {
      return id;
   }

   public Customer setId(String value)
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
      result.append(" ").append(this.getName());
      result.append(" ").append(this.getAddress());


      return result.substring(1);
   }

   public static final String PROPERTY_name = "name";

   private String name;

   public String getName()
   {
      return name;
   }

   public Customer setName(String value)
   {
      if (value == null ? this.name != null : ! value.equals(this.name))
      {
         String oldValue = this.name;
         this.name = value;
         firePropertyChange("name", oldValue, value);
      }
      return this;
   }

   public static final String PROPERTY_address = "address";

   private String address;

   public String getAddress()
   {
      return address;
   }

   public Customer setAddress(String value)
   {
      if (value == null ? this.address != null : ! value.equals(this.address))
      {
         String oldValue = this.address;
         this.address = value;
         firePropertyChange("address", oldValue, value);
      }
      return this;
   }

   public static final java.util.ArrayList<Product> EMPTY_products = new java.util.ArrayList<Product>()
   { @Override public boolean add(Product value){ throw new UnsupportedOperationException("No direct add! Use xy.withProducts(obj)"); }};

   public static final String PROPERTY_products = "products";

   private java.util.ArrayList<Product> products = null;

   public java.util.ArrayList<Product> getProducts()
   {
      if (this.products == null)
      {
         return EMPTY_products;
      }

      return this.products;
   }

   public Customer withProducts(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withProducts(i);
            }
         }
         else if (item instanceof Product)
         {
            if (this.products == null)
            {
               this.products = new java.util.ArrayList<Product>();
            }
            if ( ! this.products.contains(item))
            {
               this.products.add((Product)item);
               ((Product)item).withCustomers(this);
               firePropertyChange("products", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }

   public Customer withoutProducts(Object... value)
   {
      if (this.products == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutProducts(i);
            }
         }
         else if (item instanceof Product)
         {
            if (this.products.contains(item))
            {
               this.products.remove((Product)item);
               ((Product)item).withoutCustomers(this);
               firePropertyChange("products", item, null);
            }
         }
      }
      return this;
   }

   public void removeYou()
   {
      this.withoutOrders(this.getOrders().clone());


      this.withoutProducts(this.getProducts().clone());


   }

   public static final java.util.ArrayList<Order> EMPTY_orders = new java.util.ArrayList<Order>()
   { @Override public boolean add(Order value){ throw new UnsupportedOperationException("No direct add! Use xy.withOrders(obj)"); }};

   public static final String PROPERTY_orders = "orders";

   private java.util.ArrayList<Order> orders = null;

   public java.util.ArrayList<Order> getOrders()
   {
      if (this.orders == null)
      {
         return EMPTY_orders;
      }

      return this.orders;
   }

   public Customer withOrders(Object... value)
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
         else if (item instanceof Order)
         {
            if (this.orders == null)
            {
               this.orders = new java.util.ArrayList<Order>();
            }
            if ( ! this.orders.contains(item))
            {
               this.orders.add((Order)item);
               ((Order)item).setCustomer(this);
               firePropertyChange("orders", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }

   public Customer withoutOrders(Object... value)
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
         else if (item instanceof Order)
         {
            if (this.orders.contains(item))
            {
               this.orders.remove((Order)item);
               ((Order)item).setCustomer(null);
               firePropertyChange("orders", item, null);
            }
         }
      }
      return this;
   }

}

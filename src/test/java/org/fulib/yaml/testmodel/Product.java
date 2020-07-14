package org.fulib.yaml.testmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Product  
{

   public static final String PROPERTY_description = "description";

   private String description;

   public String getDescription()
   {
      return description;
   }

   public Product setDescription(String value)
   {
      if (value == null ? this.description != null : ! value.equals(this.description))
      {
         String oldValue = this.description;
         this.description = value;
         firePropertyChange("description", oldValue, value);
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
      result.append(" ").append(this.getDescription());


      return result.substring(1);
   }

   public static final String PROPERTY_id = "id";

   private String id;

   public String getId()
   {
      return id;
   }

   public Product setId(String value)
   {
      if (value == null ? this.id != null : ! value.equals(this.id))
      {
         String oldValue = this.id;
         this.id = value;
         firePropertyChange("id", oldValue, value);
      }
      return this;
   }

   public static final java.util.ArrayList<Offer> EMPTY_offers = new java.util.ArrayList<Offer>()
   { @Override public boolean add(Offer value){ throw new UnsupportedOperationException("No direct add! Use xy.withOffers(obj)"); }};

   public static final String PROPERTY_offers = "offers";

   private java.util.ArrayList<Offer> offers = null;

   public java.util.ArrayList<Offer> getOffers()
   {
      if (this.offers == null)
      {
         return EMPTY_offers;
      }

      return this.offers;
   }

   public Product withOffers(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withOffers(i);
            }
         }
         else if (item instanceof Offer)
         {
            if (this.offers == null)
            {
               this.offers = new java.util.ArrayList<Offer>();
            }
            if ( ! this.offers.contains(item))
            {
               this.offers.add((Offer)item);
               ((Offer)item).setProduct(this);
               firePropertyChange("offers", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }

   public Product withoutOffers(Object... value)
   {
      if (this.offers == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutOffers(i);
            }
         }
         else if (item instanceof Offer)
         {
            if (this.offers.contains(item))
            {
               this.offers.remove((Offer)item);
               ((Offer)item).setProduct(null);
               firePropertyChange("offers", item, null);
            }
         }
      }
      return this;
   }

   public void removeYou()
   {
      this.withoutOffers(this.getOffers().clone());


      this.withoutCustomers(this.getCustomers().clone());


   }

   public static final java.util.ArrayList<Customer> EMPTY_customers = new java.util.ArrayList<Customer>()
   { @Override public boolean add(Customer value){ throw new UnsupportedOperationException("No direct add! Use xy.withCustomers(obj)"); }};

   public static final String PROPERTY_customers = "customers";

   private java.util.ArrayList<Customer> customers = null;

   public java.util.ArrayList<Customer> getCustomers()
   {
      if (this.customers == null)
      {
         return EMPTY_customers;
      }

      return this.customers;
   }

   public Product withCustomers(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withCustomers(i);
            }
         }
         else if (item instanceof Customer)
         {
            if (this.customers == null)
            {
               this.customers = new java.util.ArrayList<Customer>();
            }
            if ( ! this.customers.contains(item))
            {
               this.customers.add((Customer)item);
               ((Customer)item).withProducts(this);
               firePropertyChange("customers", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }

   public Product withoutCustomers(Object... value)
   {
      if (this.customers == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutCustomers(i);
            }
         }
         else if (item instanceof Customer)
         {
            if (this.customers.contains(item))
            {
               this.customers.remove((Customer)item);
               ((Customer)item).withoutProducts(this);
               firePropertyChange("customers", item, null);
            }
         }
      }
      return this;
   }

}

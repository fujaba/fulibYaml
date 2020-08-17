package org.fulib.yaml;

import org.fulib.yaml.testmodel.Customer;
import org.fulib.yaml.testmodel.Order;
import org.fulib.yaml.testmodel.OrderPosition;
import org.fulib.yaml.testmodel.Student;
import org.fulib.yaml.testmodel.subpackage.Room;
import org.fulib.yaml.testmodel.subpackage.University;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestReflectorRemoveToManyLink
{
   @Test
   public void testRemoveToManyLink()
   {
      Order order1 = new Order().setId("order1");
      OrderPosition tooExpensive = new OrderPosition().setId("tooExpensive");
      OrderPosition ok = new OrderPosition().setId("ok");
      OrderPosition unwanted = new OrderPosition().setId("unwanted");

      order1.withPositions(tooExpensive, ok);

      Reflector reflector = new Reflector().setClazz(Order.class);

      reflector.setValue(order1, Order.PROPERTY_state, "new");

      assertThat(order1.getState(), is("new"));
      assertThat(order1.getPositions().size(), is(2));

      reflector.removeLink(order1, Order.PROPERTY_positions, tooExpensive);

      assertThat(order1.getPositions().size(), is(1));
      assertThat(order1.getPositions().contains(tooExpensive), is(false));

      Customer alice = new Customer().setName("Alice");
      order1.setCustomer(alice);
      assertThat(alice.getOrders(), hasItem(order1));
      reflector.removeLink(order1, Order.PROPERTY_customer, null);
      assertThat(alice.getOrders(), not(hasItem(order1)));

      Customer bob = new Customer().setName("Bob");
      order1.setCustomer(bob);
      assertThat(bob.getOrders(), hasItem(order1));
      reflector.removeLink(order1, Order.PROPERTY_customer, bob);
      assertThat(bob.getOrders(), not(hasItem(order1)));

   }
}

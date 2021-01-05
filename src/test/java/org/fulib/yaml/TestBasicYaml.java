package org.fulib.yaml;

import org.fulib.yaml.testmodel.*;
import org.fulib.yaml.testmodel.subpackage.Room;
import org.fulib.yaml.testmodel.subpackage.University;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestBasicYaml
{
   @Test
   public void testNewYamlForDataWithAssocs()
   {
      Product tShirt = new Product().setId("tShirt").setDescription("Shirt 42");
      Product hoodie = new Product().setId("hoodie").setDescription("Uni Kassel Hoodie");
      Customer alice = new Customer().setId("alice").setName("Alice A.").setAddress("Wonderland 1");
      Customer bob = new Customer().setId("bob").setName("Bob A.").setAddress("Wonderland 1");

      Offer marchSpecial = new Offer().setId("marchSpecial").setProduct(tShirt).setPrice(19.99)
         .setStartTime("2020.03.01").setEndTime("2020.03.31");
      Order aliceShirt = new Order().setId("aliceShirtOrder").setCustomer(alice).setDate("2020.03.02")
         .setState("just ordered");

      alice.withProducts(tShirt, hoodie);
      bob.withProducts(tShirt);

      String yamlString = Yaml.encode(tShirt);

      System.out.println(yamlString);

      assertThat(yamlString.contains("just ordered"), is(true));
      assertThat(yamlString, containsString("Uni Kassel Hoodie"));

      Map<String, Object> resultMap = Yaml.forPackage(tShirt.getClass().getPackage().getName()).decode(yamlString);
      Customer alice2 = (Customer) resultMap.get("alice");
      Product tShirt2 = (Product) resultMap.get("tShirt");


      assertThat(alice2, is(not(alice)));
      assertThat(alice2.getProducts().size(), is(2));
      assertThat(tShirt2, is(not(tShirt)));
      assertThat(tShirt2.getCustomers().size(), is(2));
      // seems to work.
   }

   @Test
   public void testNewCodeStyle()
   {
      University uni = new University();
      uni.setName("StudyRight");

      Room math = new Room().setId("math");
      Room arts = new Room().setId("arts");
      Room other = new Room().setId("other");
      Room other2 = new Room().setId("other");

      uni.withRooms(math).withRooms(arts).withRooms(other).withRooms(other2);

      Student alice = new Student().setUniversity(uni);
      alice.setFavoriteDay(Day.THURSDAY);
      alice.setType(Student.Type.MASTER);

      String yaml = Yaml.encode(alice);

      Map<String, Object> decodedMap = Yaml
         .forPackage(University.class.getPackage().getName(), Student.class.getPackage().getName())
         .decode(yaml);

      Object decodedStudyRight = decodedMap.get("studyRight");
      assertThat("Decoded map should contain studyRight", decodedStudyRight, notNullValue());
      University dUni = (University) decodedStudyRight;

      Object decodeOther2 = decodedMap.get("other2");
      assertThat(decodeOther2, notNullValue());
      Room dOther2 = (Room) decodeOther2;
      assertThat(dUni.getRooms(), hasItem(dOther2));

      Object decodedAlice = decodedMap.get("s");
      assertThat("Decoded map should contain student s2", decodedAlice, notNullValue());

      Student dAlice = (Student) decodedAlice;
      assertThat(dAlice.getFavoriteDay(), is(Day.THURSDAY));
      assertThat(dAlice.getUniversity(), is(dUni));

   }
}

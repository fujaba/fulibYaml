package org.fulib.yaml;

import org.fulib.yaml.testmodel.Student;
import org.fulib.yaml.testmodel.subpackage.Room;
import org.fulib.yaml.testmodel.subpackage.University;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class ReflectorTest
{
   @Test
   public void removeValue()
   {
      final Reflector studentReflector = new Reflector().setClazz(Student.class);

      final University university = new University();
      final Student student = new Student();
      student.setUniversity(university);

      assertThat(student.getUniversity(), is(university));
      studentReflector.removeValue(student, "university", university);
      assertThat(student.getUniversity(), nullValue());

      final Reflector uniReflector = new Reflector().setClazz(University.class);

      final Room room = new Room();
      university.withRooms(room);

      assertThat(university.getRooms(), hasItem(room));
      uniReflector.removeValue(university, "rooms", room);
      assertThat(university.getRooms(), not(hasItem(room)));

      try
      {
         uniReflector.removeValue(null, "rooms", room);
      }
      catch (Exception e)
      {
         fail("removeValue did not allow null receiver", e);
      }

      try
      {
         uniReflector.removeValue(university, "propertyThatDoesNotExist", room);
      }
      catch (Exception e)
      {
         fail("removeValue did not allow unknown property", e);
      }

      try
      {
         uniReflector.removeValue(university, "rooms", student);
      }
      catch (Exception e)
      {
         fail("removeValue did not allow cast error", e);
      }
   }
}

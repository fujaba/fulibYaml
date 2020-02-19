package org.fulib.yaml;

import org.fulib.yaml.testmodel.Student;
import org.fulib.yaml.testmodel.subpackage.University;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class TestExternalReferences
{
   @Test
   void ensureTestModelIntegrity()
   {
      boolean containsColorSetter = false;
      boolean containsColorGetter = false;

      for (Method method : Student.class.getMethods())
      {
         if ("getUniversity".equals(method.getName()))
         {
            containsColorGetter = true;
         }
         else if ("setUniversity".equals(method.getName()))
         {
            containsColorSetter = true;
         }
      }

      assertThat(containsColorGetter, is(true));
      assertThat(containsColorSetter, is(true));
   }

   @Test
   void testExternalReference()
   {
      final University university = new University();
      final Student student = new Student().setUniversity(university);

      final YamlIdMap studentIdMap = new YamlIdMap(student.getClass().getPackage().getName());
      final String studentYaml = studentIdMap.encode(student);
      assertThat(studentYaml, equalTo("- s: \tStudent\n" + "  university: \t" + university.toString() + "\n\n"));

      final YamlIdMap studentUniIdMap = new YamlIdMap(student.getClass().getPackage().getName(),
                                                      University.class.getPackage().getName());
      final String studentUniYaml = studentUniIdMap.encode(student);
      assertThat(studentUniYaml,
                 equalTo("- s: \tStudent\n" + "  university: \tu\n" + "\n" + "- u: \tUniversity\n" + "\n"));
   }
}

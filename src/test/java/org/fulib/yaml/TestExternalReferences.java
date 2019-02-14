package org.fulib.yaml;

import org.fulib.yaml.testmodel.Student;
import org.fulib.yaml.testmodel.subpackage.University;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestExternalReferences {

    @Test
    void ensureTestModelIntegrity() {

        boolean containsColorSetter = false;
        boolean containsColorGetter = false;

        for (Method method : Student.class.getMethods()) {
            if (method.getName().equals("getUniversity")) {
                containsColorGetter = true;
            } else if (method.getName().equals("setUniversity")) {
                containsColorSetter = true;
            }
        }

        assertThat(containsColorGetter, is(true));
        assertThat(containsColorSetter, is(true));
    }

    @Test
    void testExternalReference() {

        Student student = new Student().setUniversity(new University());

        YamlIdMap yim = new YamlIdMap(student.getClass().getPackage().getName());

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> yim.encode(student));

        assertThat(runtimeException.getMessage(), is(equalTo(
                "ReflectorMap could not find class description for University\n" +
                        "searching in \n" + student.getClass().getPackage().getName() +
                        "You might add more packages to the construction of the ReflectorMap / YamlIdMap \n" +
                        "or you might move the missing class into the common model package.")));
    }
}

package org.fulib.yaml;

import javafx.scene.paint.Color;
import org.fulib.yaml.testmodel.Student;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestExternalReferences {

    @Test
    public void ensureTestModelIntegrity() {

        boolean containsColorSetter = false;
        boolean containsColorGetter = false;

        for (Method method : Student.class.getMethods()) {
            if(method.getName().equals("getColor")) {
                containsColorGetter = true;
            } else if(method.getName().equals("setColor")) {
                containsColorSetter = true;
            }
        }

        assertTrue(containsColorGetter);
        assertTrue(containsColorSetter);
    }

    @Test
    public void testExternalReference() {

        Student student = new Student().setColor(Color.FIREBRICK);

        YamlIdMap yim = new YamlIdMap(student.getClass().getPackage().getName());

        try {
            yim.encode(student);
        } catch(NullPointerException e) {
            fail("NullPointerException trown!");
        }
    }
}

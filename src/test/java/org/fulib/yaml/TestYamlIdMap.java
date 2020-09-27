
package org.fulib.yaml;

import org.fulib.yaml.testmodel.Day;
import org.fulib.yaml.testmodel.Student;
import org.fulib.yaml.testmodel.subpackage.Room;
import org.fulib.yaml.testmodel.subpackage.University;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class TestYamlIdMap
{
   @Test
   void testPlainYaml()
   {
      String yaml = "" +
            "joining: abu \n" +
            "lastChanges: 2018-03-17T14:48:00.000.abu 2018-03-17T14:38:00.000.bob 2018-03-17T14:18:00.000.xia";

      Yamler yamler = new Yamler();

      LinkedHashMap<String, String> map = yamler.decode(yaml);
      assertThat(map.get("joining"), is(equalTo("abu")));
   }

   @Test
   void testYammlerObjectList()
   {
      String yaml = "" +
            "- time: 2018.10.09T12:14:55.007\n" +
            "  source: g1\n" +
            "  sourceType: GroupEvent\n" +
            "  property: name\n" +
            "  newValue: BBQ\n" +
            "- time: 2018.10.09T12:14:55.008\n" +
            "  source: m2\n" +
            "  sourceType: Member\n" +
            "  property: name\n" +
            "  newValue: \"Abu Aba\"\n" +
            "- time: 2018.10.09T12:14:55.009\n" +
            "  source: g1\n" +
            "  sourceType: GroupEvent\n" +
            "  property: members\n" +
            "  newValue: m2\n" +
            "  newValueType: Member\n"
            ;

      Yamler yamler = new Yamler();

      ArrayList<LinkedHashMap<String, String>> list = yamler.decodeList(yaml);
      assertThat(list.size(), equalTo(3));
      LinkedHashMap<String, String> map = list.get(1);
      assertThat(map.get("newValue"), equalTo("Abu Aba"));
   }


   @Test
   void testYamlIdMap()
   {
      String yaml = "" +
            "- sr: .Map\n" +
            "  clazz: Uni\n" +
            "  name: Study Right\n" +
            "  rooms: r1 r2\n" +
            "- r1: .Map\n" +
            "  clazz: Room\n" +
            "  name: wa1337\n" +
            "  uni: sr\n" +
            "- r2: .Map\n" +
            "  clazz: Room\n" +
            "  name: wa4242\n" +
            "  uni: sr\n";

      YamlIdMap idMap = new YamlIdMap("");

      YamlObject yamlObj = (YamlObject) idMap.decode(yaml);
      Map<String, Object> map = yamlObj.getProperties();

      assertThat(map.get("clazz"), equalTo("Uni"));
      ArrayList<Object> rooms = (ArrayList<Object>) map.get("rooms");
      assertThat(rooms.size(), equalTo(2));

      YamlIdMap dumpMap = new YamlIdMap();
      LinkedHashSet<Object> list = dumpMap.collectObjects(yamlObj);

      assertThat(list.size(), equalTo(3));
   }

   @Test
   public void testUserObjectIds()
   {
      University uni = new University();
      uni.setName("studyright");

      Room math = new Room().setId("math");
      Room arts = new Room().setId("arts");
      Room other = new Room().setId("other");
      Room other2 = new Room().setId("other");
      Room empty = new Room().setId("");
      Room empty2 = new Room().setId("");

      uni.withRooms(math);
      uni.withRooms(arts);
      uni.withRooms(other);
      uni.withRooms(other2);
      uni.withRooms(empty);
      uni.withRooms(empty2);

      YamlIdMap idMap = new YamlIdMap(uni.getClass().getPackage().getName());
      idMap.encode(uni);
      assertThat(idMap.getId(math), is("math"));
      assertThat(idMap.getId(other), is("other"));
      assertThat(idMap.getId(other2), is("other1"));
      assertThat(idMap.getId(empty), is("_"));
      assertThat(idMap.getId(empty2), is("_2"));
   }

   @Test
   void testExternalReference()
   {
      final University university = new University();
      final Student student = new Student().setUniversity(university);

      final YamlIdMap studentIdMap = new YamlIdMap(student.getClass().getPackage().getName());
      final String studentYaml = studentIdMap.encode(student);
      assertThat(studentYaml, Matchers.equalTo("- s: \tStudent\n" + "  university: \t" + university.toString() + "\n\n"));

      final YamlIdMap studentUniIdMap = new YamlIdMap(student.getClass().getPackage().getName(),
                                                      University.class.getPackage().getName());
      final String studentUniYaml = studentUniIdMap.encode(student);
      assertThat(studentUniYaml,
                 Matchers.equalTo("- s: \tStudent\n" + "  university: \tu\n" + "\n" + "- u: \tUniversity\n" + "\n"));
   }

   @Test
   public void testEnums()
   {
      final Student student = new Student();
      student.setFavoriteDay(Day.FRIDAY);
      student.setType(Student.Type.BACHELOR);

      final YamlIdMap studentIdMap = new YamlIdMap(student.getClass().getPackage().getName());
      final String studentYaml = studentIdMap.encode(student);
      // language=yaml
      assertThat(studentYaml, Matchers.equalTo(
         "- s: \tStudent\n" + "  favoriteDay: \torg.fulib.yaml.testmodel.Day.FRIDAY\n"
         + "  type: \torg.fulib.yaml.testmodel.Student$Type.BACHELOR\n" + "\n"));

      final YamlIdMap newIdMap = new YamlIdMap(student.getClass().getPackage().getName());
      final Student newStudent = (Student) newIdMap.decode(studentYaml);
      assertThat(newStudent.getFavoriteDay(), sameInstance(Day.FRIDAY));
      assertThat(newStudent.getType(), sameInstance(Student.Type.BACHELOR));
   }

   @Test
   public void enumCollections()
   {
      final Student student = new Student();
      student.withStudyDays(Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);

      final YamlIdMap studentIdMap = new YamlIdMap(student.getClass().getPackage().getName());
      final String studentYaml = studentIdMap.encode(student);
      // language=yaml
      assertThat(studentYaml, Matchers.equalTo("- s: \tStudent\n"
                                               + "  studyDays: \torg.fulib.yaml.testmodel.Day.MONDAY \torg.fulib.yaml.testmodel.Day.WEDNESDAY \torg.fulib.yaml.testmodel.Day.FRIDAY"
                                               + "\n" + "\n"));

      final YamlIdMap newIdMap = new YamlIdMap(student.getClass().getPackage().getName());
      final Student newStudent = (Student) newIdMap.decode(studentYaml);
      assertThat(newStudent.getStudyDays(), hasItems(Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY));
   }

   @Test
   public void stringCollections()
   {
      final Student student = new Student();
      student.withNotes("study", "foo", "bar");

      final YamlIdMap studentIdMap = new YamlIdMap(student.getClass().getPackage().getName());
      final String studentYaml = studentIdMap.encode(student);
      // language=yaml
      assertThat(studentYaml,
                 Matchers.equalTo("- s: \tStudent\n" + "  notes: \tstudy \tfoo \tbar\n" + "\n"));

      final YamlIdMap newIdMap = new YamlIdMap(student.getClass().getPackage().getName());
      final Student newStudent = (Student) newIdMap.decode(studentYaml);
      assertThat(newStudent.getNotes(), hasItems("study", "foo", "bar"));
   }
}

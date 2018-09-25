
package org.fulib.yaml;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TestYamlIdMap
{
   @Test
   public void testPlainYaml()
   {
      String yaml = "" +
            "joining: abu \n" +
            "lastChanges: 2018-03-17T14:48:00.000.abu 2018-03-17T14:38:00.000.bob 2018-03-17T14:18:00.000.xia";

      Yamler yamler = new Yamler();

      LinkedHashMap<String, String> map = yamler.decode(yaml);
      assertThat(map.get("joining"), equalTo("abu"));
   }


   @Test
   public void testYamlIdMap()
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

      LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) idMap.decode(yaml);

      assertThat(map.get("clazz"), equalTo("Uni"));
      ArrayList<Object> rooms = (ArrayList<Object>) map.get("rooms");
      assertThat(rooms.size(), equalTo(2));
   }
}

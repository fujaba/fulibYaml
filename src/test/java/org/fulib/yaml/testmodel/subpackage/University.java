package org.fulib.yaml.testmodel.subpackage;

import java.util.ArrayList;

public class University
{
   private String name;

   public String getName()
   {
      return this.name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   private ArrayList<Room> rooms = new ArrayList<>();

   public ArrayList<Room> getRooms()
   {
      return this.rooms;
   }

   public University withRooms(Room newRoom)
   {
      this.rooms.add(newRoom);
      return this;
   }
}

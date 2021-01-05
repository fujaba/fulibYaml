package org.fulib.yaml.testmodel;

public class Person
{
   private String name;
   private Day favoriteDay;

   public String getName()
   {
      return this.name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public Day getFavoriteDay()
   {
      return this.favoriteDay;
   }

   public void setFavoriteDay(Day favorityDay)
   {
      this.favoriteDay = favorityDay;
   }
}

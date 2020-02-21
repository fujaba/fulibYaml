package org.fulib.yaml.testmodel;

import org.fulib.yaml.testmodel.subpackage.University;

public class Student
{
   private University university;
   private Day favoriteDay;

   public University getUniversity()
   {
      return this.university;
   }

   public Student setUniversity(University university)
   {
      this.university = university;
      return this;
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

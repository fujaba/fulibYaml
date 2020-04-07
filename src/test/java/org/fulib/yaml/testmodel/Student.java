package org.fulib.yaml.testmodel;

import org.fulib.yaml.testmodel.subpackage.University;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class Student
{
   private University university;
   private Day favoriteDay;

   private Set<Day> studyDays = EnumSet.noneOf(Day.class);

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

   public Set<Day> getStudyDays()
   {
      return this.studyDays;
   }

   public void withStudyDays(Day value)
   {
      this.studyDays.add(value);
   }

   public void withStudyDays(Day... value)
   {
      Collections.addAll(this.studyDays, value);
   }

   public void withStudyDays(Collection<Day> value)
   {
      this.studyDays.addAll(value);
   }
}

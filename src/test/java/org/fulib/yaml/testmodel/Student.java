package org.fulib.yaml.testmodel;

import org.fulib.yaml.testmodel.subpackage.University;

import java.util.*;

public class Student
{
   public enum Type
   {
      BACHELOR,
      MASTER,
   }

   private University university;
   private Day favoriteDay;
   private Type type;

   private Set<Day> studyDays = EnumSet.noneOf(Day.class);
   private Set<String> notes = new LinkedHashSet<>();

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

   public Type getType()
   {
      return type;
   }

   public void setType(Type type)
   {
      this.type = type;
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

   public Set<String> getNotes()
   {
      return this.notes;
   }

   public void withNotes(String value)
   {
      this.notes.add(value);
   }

   public void withNotes(String... value)
   {
      Collections.addAll(this.notes, value);
   }

   public void withNotes(Collection<String> value)
   {
      this.notes.addAll(value);
   }
}

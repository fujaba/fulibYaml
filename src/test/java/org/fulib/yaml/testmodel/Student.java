package org.fulib.yaml.testmodel;

import org.fulib.yaml.testmodel.subpackage.University;

public class Student
{
   private University university;

   public University getUniversity()
   {
      return this.university;
   }

   public Student setUniversity(University university)
   {
      this.university = university;
      return this;
   }
}

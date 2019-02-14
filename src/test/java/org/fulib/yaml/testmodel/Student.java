package org.fulib.yaml.testmodel;

import org.fulib.yaml.testmodel.subpackage.University;

public class Student {

    private University university;

    public University getUniversity() {
        return university;
    }

    public Student setUniversity(University university) {
        this.university = university;
        return this;
    }
}

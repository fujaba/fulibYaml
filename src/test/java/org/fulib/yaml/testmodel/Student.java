package org.fulib.yaml.testmodel;

import javafx.scene.paint.Color;

public class Student {

    private Color color;

    public Student setColor(Color color) {
        this.color = color;
        return this;
    }

    public Color getColor() {
        return color;
    }
}

package com.app.mycustomview.bean;

/**
 * Created by DongHao on 2016/10/8.
 * Description:
 */

public class PieData {
    private String name;
    private float values;
    private float percentage;

    private int color=0;
    private float anger=0;

    public PieData(float values, String name) {
        this.values = values;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValues() {
        return values;
    }

    public void setValues(float values) {
        this.values = values;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getAnger() {
        return anger;
    }

    public void setAnger(float anger) {
        this.anger = anger;
    }
}

package com.gnotes.app;

import java.io.Serializable;

public class Item implements Serializable {
    private int id = 1;
    private String name;
    private String category;
    private String extraField;
    private String extraInfo;

    public Item() { }

    public Item(String name, String category) {
        id++;
        this.name = name;
        this.category = category;
    }

    public Item(String name, String category, String extraField, String extraInfo) {
        id++;
        this.name = name;
        this.category = category;
        this.extraField = extraField;
        this.extraInfo = extraInfo;
    }

    private int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExtraField() {
        return extraField;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraField(String extraField) {
        this.extraField = extraField;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

}

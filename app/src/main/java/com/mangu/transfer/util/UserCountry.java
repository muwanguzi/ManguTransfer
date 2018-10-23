package com.mangu.transfer.util;

/**
 * Created by ok on 7/6/2017.
 */

public class UserCountry {


    private String String;
    private String name;
    private String Category;


    public UserCountry(String String, String name, String Category) {
        this.String = String;
        this.name = name;
        this.Category = Category;

    }

    public String getId() {
        return this.String;
    }

    public void setId(String String) {
        this.String = String;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return this.String;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }


}
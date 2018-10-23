package com.mangu.transfer.model;

import java.util.ArrayList;

/**
 * Created by FRANCIS on 01/05/2017.
 */

public class Custom_project_list_model {
    private String title, CharityActivity;
    private String date;
    private int m_organizationId;
    private String primaryPhone;
    private ArrayList<String> genre;

    public Custom_project_list_model() {
    }

    public Custom_project_list_model(String name, String CharityActivity, String date, String primaryPhone, int m_organizationId,
                                     ArrayList<String> genre) {
        this.title = name;
        this.CharityActivity = CharityActivity;
        this.date = date;
        this.primaryPhone = primaryPhone;
        this.m_organizationId = m_organizationId;
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getCharityActivity() {
        return CharityActivity;
    }

    public void setCharityActivity(String CharityActivity) {
        this.CharityActivity = CharityActivity;
    }

    public String getdate() {
        return date;
    }

    public void setdate(String date) {
        this.date = date;
    }

    public int getMorganizationId() {
        return m_organizationId;
    }

    public void setMorganizationId(int m_organizationId) {
        this.m_organizationId = m_organizationId;
    }

    public String getprimaryPhone() {
        return primaryPhone;
    }

    public void setprimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }


    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }

}



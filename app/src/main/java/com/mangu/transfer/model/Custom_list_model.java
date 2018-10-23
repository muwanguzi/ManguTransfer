package com.mangu.transfer.model;

/**
 * Created by FRANCIS on 01/05/2017.
 */

public class Custom_list_model {
    private String m_title;
    private String thumbnailUrl;
    private String m_implementer;
    private String m_description;
    private String m_additional_info;
    private String m_other_details;
    private String m_id;
    private String m_date;

    public Custom_list_model() {
    }

    public Custom_list_model(String m_title, String thumbnailUrl, String m_implementer,
                             String m_description, String m_additional_info, String
                                     m_other_details, String m_id, String m_date) {
        this.m_title = m_title;
        this.thumbnailUrl = thumbnailUrl;
        this.m_implementer = m_implementer;
        this.m_description = m_description;
        this.m_additional_info = m_additional_info;
        this.m_other_details = m_other_details;
        this.m_id = m_id;
        this.m_date = m_date;
    }

    public String getTitle() {
        return m_title;
    }

    public void setTitle(String m_title) {
        this.m_title = m_title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getImplementer() {
        return m_implementer;
    }

    public void setImplementer(String m_implementer) {
        this.m_implementer = m_implementer;
    }

    public String getDescription() {
        return m_description;
    }

    public void setDescription(String m_description) {
        this.m_description = m_description;
    }

    public String getAdditional_info() {
        return m_additional_info;
    }

    public void setAdditional_info(String m_additional_info) {
        this.m_additional_info = m_additional_info;
    }

    public String getDate() {
        return m_date;
    }

    public void setDate(String m_date) {
        this.m_date = m_date;
    }

    public String getOther_details() {
        return m_other_details;
    }

    public void setOther_details(String m_other_details) {
        this.m_other_details = m_other_details;
    }

    public String getM_id() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id = m_id;
    }

    @Override
    public String toString() {
        return this.thumbnailUrl + "." + this.m_title + ". " + this.m_description + ". " + this.m_additional_info + ". " + this.m_id + ". " + this.m_date;
    }

}



package com.mangu.transfer.model;

import java.util.ArrayList;

/**
 * Created by FRANCIS on 04/05/2017.
 */

public class Custom_recipient_cart {
    private String m_contactName, m_contactNumber;
    private String m_formattedAmountCharged;
    private String primaryPhone;
    private ArrayList<String> genre;

    public Custom_recipient_cart() {
    }

    public Custom_recipient_cart(String m_contactName, String m_contactNumber, String m_formattedAmountCharged, String primaryPhone,
                                 ArrayList<String> genre) {
        this.m_contactName = m_contactName;
        this.m_contactNumber = m_contactNumber;
        this.m_formattedAmountCharged = m_formattedAmountCharged;
        this.primaryPhone = primaryPhone;
        this.genre = genre;
    }

    public String getTitle() {
        return m_contactName;
    }

    public void setTitle(String m_contactName) {
        this.m_contactName = m_contactName;
    }

    public String getm_contactNumber() {
        return m_contactNumber;
    }

    public void setm_contactNumber(String m_contactNumber) {
        this.m_contactNumber = m_contactNumber;
    }

    public String getm_formattedAmountCharged() {
        return m_formattedAmountCharged;
    }

    public void setm_formattedAmountCharged(String m_formattedAmountCharged) {
        this.m_formattedAmountCharged = m_formattedAmountCharged;
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



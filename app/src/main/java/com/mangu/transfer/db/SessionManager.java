package com.mangu.transfer.db;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mangu.transfer.ui.LoginActivity;

import java.util.HashMap;

public class SessionManager {
    // User name (make variable public to access from outside)
    public static final String KEY_SENDER_ID = "m_senderId";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    //public static final String KEY_PHONE = "phone";
    public static final String KEY_SESSIONID = "USessionID";
    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String m_senderId, String email) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_SENDER_ID, m_senderId);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // Storing email in pref
        //editor.putString(KEY_PHONE, phone);

        // commit changes
        editor.apply();
    }


    public void storeSessionID(String USessionID) {

        // Storing email in pref
        editor.putString(KEY_SESSIONID, USessionID);

        // commit changes
        editor.apply();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            _context.startActivity(i);
        }

    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_SENDER_ID, pref.getString(KEY_SENDER_ID, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));


        //user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));

        // return user
        return user;
    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getTransId() {
        HashMap<String, String> transId = new HashMap<String, String>();
        // user name
        transId.put(KEY_SESSIONID, pref.getString(KEY_SESSIONID, null));

        // return user
        return transId;
    }

    /**
     * Clear session details
     */
    public void clearTransId() {
        //Clearing all data from Shared Preferences
        editor.remove(KEY_SESSIONID);

    }


    public void clearAllFromSharedPreferences() {
        //Clearing all data from Shared Preferences
        editor.clear();
        editor.apply();
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.apply();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        _context.startActivity(i);

    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
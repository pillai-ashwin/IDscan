package siesgst.tml17.idscan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by leprechaun on 16/1/17.
 */

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "MyPreferences";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "true";

    private static final String EVENT = "event";

    private static final String PHONECONTACT = "phcontact";


    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "username";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    public static final String EVENT_ID = "id";



    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String name, String email,String event,String ph_contact,String id){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        editor.putString(EVENT,event);

        editor.putString(EVENT_ID,id);


        editor.putString(PHONECONTACT,ph_contact);


        // commit changes
        editor.apply();
    }

    /**
     * Check login method will check user login status
     * If false scanner will scan next barcode to check existence in database
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // event head is not logged in so scan barcode to log in event head
            Intent i = new Intent(_context, MainActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(Context context){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Main Activity
        Intent i = new Intent(context, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, true);
    }

    public String getName(){return pref.getString(KEY_NAME," ");}

    public String getEmail(){return pref.getString(KEY_EMAIL," ");}

    public String getEventName(){return pref.getString(EVENT," ");}

    public String getContact(){return pref.getString(PHONECONTACT," ");}

    public String getID(){return pref.getString(EVENT_ID," ");}



}

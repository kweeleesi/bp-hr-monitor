package com.example.hp.heartrytcare.helper;

import java.util.Calendar;

public class Constants {

    public static String FIREBASE_UID;

    public static Calendar CALENDAR = Calendar.getInstance();

    ///////////////////////////////////////////////////////////////////////////
    // BLUETOOTH FIELDS
    ///////////////////////////////////////////////////////////////////////////
    public final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    public final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
}

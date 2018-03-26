package com.example.hp.heartrytcare.helper;

import java.util.Calendar;

public class Constants {

    public static String FIREBASE_UID;
    public static int FIREBASE_USER_TYPE = -1;

    public static Calendar CALENDAR = Calendar.getInstance();

    public static final int TYPE_USER_DOCTOR = 1;
    public static final int TYPE_USER_PATIENT = 0;

    ///////////////////////////////////////////////////////////////////////////
    // BLUETOOTH FIELDS
    ///////////////////////////////////////////////////////////////////////////
    public final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    public final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    ///////////////////////////////////////////////////////////////////////////
    // HIGH BLOOD PRESSURE VALUES
    ///////////////////////////////////////////////////////////////////////////
    public static final int MAX_SYSTOLIC_HBP = 140;
    public static final int MAX_DIASTOLIC_HBP = 90;
}

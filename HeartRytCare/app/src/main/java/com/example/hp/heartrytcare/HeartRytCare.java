package com.example.hp.heartrytcare;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;

import com.example.hp.heartrytcare.db.DaoMaster;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.helper.BluetoothBPHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class HeartRytCare extends Application {

    private FirebaseAuth firebaseAuth;
    public BluetoothBPHelper bluetoothBPHelper;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAuth = FirebaseAuth.getInstance();
        bluetoothBPHelper = BluetoothBPHelper.getInstance();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.hp.heartrytcare.SMS_RECEIVER");

        HeartRytCare.context = getApplicationContext();
    }

    public DaoSession getDaoSession() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, BuildConfig.databaseSchema, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public static Context getAppContext() {
        return HeartRytCare.context;
    }
}

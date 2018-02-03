package com.example.hp.heartrytcare;

import android.app.Application;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.hp.heartrytcare.db.DaoMaster;
import com.example.hp.heartrytcare.db.DaoSession;
import com.example.hp.heartrytcare.helper.SmsBroadcastReceiver;
import com.google.firebase.auth.FirebaseAuth;

public class HeartRytCare extends Application implements SmsBroadcastReceiver.OTPReceivedListener{

    private FirebaseAuth firebaseAuth;
    private SmsBroadcastReceiver smsBroadcastReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAuth = FirebaseAuth.getInstance();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.hp.heartrytcare.SMS_RECEIVER");
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
}

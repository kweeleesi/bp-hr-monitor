package com.example.hp.heartrytcare;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.example.hp.heartrytcare.db.DaoMaster;
import com.example.hp.heartrytcare.db.DaoSession;

public class HeartRytCare extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public DaoSession getDaoSession() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, BuildConfig.databaseSchema, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }
}

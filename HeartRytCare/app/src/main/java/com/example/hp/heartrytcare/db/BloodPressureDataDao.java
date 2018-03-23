package com.example.hp.heartrytcare.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.example.hp.heartrytcare.db.BloodPressureData;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BLOOD_PRESSURE_DATA".
*/
public class BloodPressureDataDao extends AbstractDao<BloodPressureData, Long> {

    public static final String TABLENAME = "BLOOD_PRESSURE_DATA";

    /**
     * Properties of entity BloodPressureData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property Firebase_user_id = new Property(1, String.class, "firebase_user_id", false, "FIREBASE_USER_ID");
        public final static Property Systolic = new Property(2, int.class, "systolic", false, "SYSTOLIC");
        public final static Property Diastolic = new Property(3, int.class, "diastolic", false, "DIASTOLIC");
        public final static Property Date = new Property(4, String.class, "date", false, "DATE");
    };


    public BloodPressureDataDao(DaoConfig config) {
        super(config);
    }
    
    public BloodPressureDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BLOOD_PRESSURE_DATA\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," + // 0: id
                "\"FIREBASE_USER_ID\" TEXT NOT NULL ," + // 1: firebase_user_id
                "\"SYSTOLIC\" INTEGER NOT NULL ," + // 2: systolic
                "\"DIASTOLIC\" INTEGER NOT NULL ," + // 3: diastolic
                "\"DATE\" TEXT NOT NULL );"); // 4: date
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BLOOD_PRESSURE_DATA\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, BloodPressureData entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindString(2, entity.getFirebase_user_id());
        stmt.bindLong(3, entity.getSystolic());
        stmt.bindLong(4, entity.getDiastolic());
        stmt.bindString(5, entity.getDate());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public BloodPressureData readEntity(Cursor cursor, int offset) {
        BloodPressureData entity = new BloodPressureData( //
            cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // firebase_user_id
            cursor.getInt(offset + 2), // systolic
            cursor.getInt(offset + 3), // diastolic
            cursor.getString(offset + 4) // date
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, BloodPressureData entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setFirebase_user_id(cursor.getString(offset + 1));
        entity.setSystolic(cursor.getInt(offset + 2));
        entity.setDiastolic(cursor.getInt(offset + 3));
        entity.setDate(cursor.getString(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(BloodPressureData entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(BloodPressureData entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}

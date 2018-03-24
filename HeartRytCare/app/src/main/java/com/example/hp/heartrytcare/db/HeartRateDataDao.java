package com.example.hp.heartrytcare.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.example.hp.heartrytcare.db.HeartRateData;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "HEART_RATE_DATA".
*/
public class HeartRateDataDao extends AbstractDao<HeartRateData, Long> {

    public static final String TABLENAME = "HEART_RATE_DATA";

    /**
     * Properties of entity HeartRateData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Firebase_user_id = new Property(1, String.class, "firebase_user_id", false, "FIREBASE_USER_ID");
        public final static Property Bpm = new Property(2, int.class, "bpm", false, "BPM");
        public final static Property Date = new Property(3, String.class, "date", false, "DATE");
    };


    public HeartRateDataDao(DaoConfig config) {
        super(config);
    }
    
    public HeartRateDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"HEART_RATE_DATA\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"FIREBASE_USER_ID\" TEXT NOT NULL ," + // 1: firebase_user_id
                "\"BPM\" INTEGER NOT NULL ," + // 2: bpm
                "\"DATE\" TEXT NOT NULL );"); // 3: date
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"HEART_RATE_DATA\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, HeartRateData entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getFirebase_user_id());
        stmt.bindLong(3, entity.getBpm());
        stmt.bindString(4, entity.getDate());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public HeartRateData readEntity(Cursor cursor, int offset) {
        HeartRateData entity = new HeartRateData( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // firebase_user_id
            cursor.getInt(offset + 2), // bpm
            cursor.getString(offset + 3) // date
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, HeartRateData entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFirebase_user_id(cursor.getString(offset + 1));
        entity.setBpm(cursor.getInt(offset + 2));
        entity.setDate(cursor.getString(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(HeartRateData entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(HeartRateData entity) {
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

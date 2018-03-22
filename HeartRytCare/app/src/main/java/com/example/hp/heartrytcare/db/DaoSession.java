package com.example.hp.heartrytcare.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.example.hp.heartrytcare.db.User;
import com.example.hp.heartrytcare.db.Journal;
import com.example.hp.heartrytcare.db.Medication;
import com.example.hp.heartrytcare.db.DoctorPatient;
import com.example.hp.heartrytcare.db.Appointment;

import com.example.hp.heartrytcare.db.UserDao;
import com.example.hp.heartrytcare.db.JournalDao;
import com.example.hp.heartrytcare.db.MedicationDao;
import com.example.hp.heartrytcare.db.DoctorPatientDao;
import com.example.hp.heartrytcare.db.AppointmentDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userDaoConfig;
    private final DaoConfig journalDaoConfig;
    private final DaoConfig medicationDaoConfig;
    private final DaoConfig doctorPatientDaoConfig;
    private final DaoConfig appointmentDaoConfig;

    private final UserDao userDao;
    private final JournalDao journalDao;
    private final MedicationDao medicationDao;
    private final DoctorPatientDao doctorPatientDao;
    private final AppointmentDao appointmentDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        journalDaoConfig = daoConfigMap.get(JournalDao.class).clone();
        journalDaoConfig.initIdentityScope(type);

        medicationDaoConfig = daoConfigMap.get(MedicationDao.class).clone();
        medicationDaoConfig.initIdentityScope(type);

        doctorPatientDaoConfig = daoConfigMap.get(DoctorPatientDao.class).clone();
        doctorPatientDaoConfig.initIdentityScope(type);

        appointmentDaoConfig = daoConfigMap.get(AppointmentDao.class).clone();
        appointmentDaoConfig.initIdentityScope(type);

        userDao = new UserDao(userDaoConfig, this);
        journalDao = new JournalDao(journalDaoConfig, this);
        medicationDao = new MedicationDao(medicationDaoConfig, this);
        doctorPatientDao = new DoctorPatientDao(doctorPatientDaoConfig, this);
        appointmentDao = new AppointmentDao(appointmentDaoConfig, this);

        registerDao(User.class, userDao);
        registerDao(Journal.class, journalDao);
        registerDao(Medication.class, medicationDao);
        registerDao(DoctorPatient.class, doctorPatientDao);
        registerDao(Appointment.class, appointmentDao);
    }
    
    public void clear() {
        userDaoConfig.getIdentityScope().clear();
        journalDaoConfig.getIdentityScope().clear();
        medicationDaoConfig.getIdentityScope().clear();
        doctorPatientDaoConfig.getIdentityScope().clear();
        appointmentDaoConfig.getIdentityScope().clear();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public JournalDao getJournalDao() {
        return journalDao;
    }

    public MedicationDao getMedicationDao() {
        return medicationDao;
    }

    public DoctorPatientDao getDoctorPatientDao() {
        return doctorPatientDao;
    }

    public AppointmentDao getAppointmentDao() {
        return appointmentDao;
    }

}

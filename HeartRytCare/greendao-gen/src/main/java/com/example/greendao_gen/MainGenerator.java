package com.example.greendao_gen;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MainGenerator {

    private static final String PROJECT_DIR = System.getProperty("user.dir");

    public static void main(String[] args) {
        Schema schema = new Schema(1, "com.example.hp.heartrytcare.db");
        schema.enableKeepSectionsByDefault();

        addTables(schema);

        try {
            new DaoGenerator().generateAll(schema, PROJECT_DIR + "\\app\\src\\main\\java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTables(final Schema schema) {
        Entity user = addUser(schema);
        Entity journal = addJournal(schema);
        Entity medication = addMedicationSched(schema);
        Entity doctorPatient = addDoctorPatient(schema);
        Entity appt = addAppointment(schema);
        Entity hr = addHeartRateData(schema);
        Entity bp = addBloodPressureData(schema);
    }

    private static Entity addUser(final Schema schema) {
        Entity user = schema.addEntity("User");
        user.addIdProperty().primaryKey().autoincrement();
        user.addStringProperty("firebase_user_id").notNull();
        user.addIntProperty("user_type").notNull(); // 1 = doctor | 0 = patient
        user.addStringProperty("last_name").notNull();
        user.addStringProperty("first_name").notNull();
        user.addStringProperty("license_number");
        user.addStringProperty("email").notNull();
        user.addStringProperty("contact_number");
        user.addStringProperty("password").notNull();
        user.addIntProperty("age").notNull();
        user.addIntProperty("height").notNull();
        user.addDoubleProperty("weight").notNull();
        user.addIntProperty("gender").notNull();

        return user;
    }

    private static Entity addJournal(final Schema schema) {
        Entity journal = schema.addEntity("Journal");
        journal.addIdProperty().primaryKey().autoincrement();
        journal.addStringProperty("firebase_user_id").notNull();
        journal.addStringProperty("meals_taken");
        journal.addStringProperty("heart_rate");
        journal.addStringProperty("systolic");
        journal.addStringProperty("diastolic");
        journal.addStringProperty("temperature");
        journal.addStringProperty("weight");
        journal.addStringProperty("medicine_name");
        journal.addStringProperty("dosage");
        journal.addStringProperty("pieces");
        journal.addStringProperty("how_often");
        journal.addStringProperty("notes");
        journal.addStringProperty("entry_date").notNull();

        return journal;
    }

    private static Entity addMedicationSched(final Schema schema) {
        Entity medication = schema.addEntity("Medication");
        medication.addIdProperty().primaryKey().autoincrement();
        medication.addStringProperty("firebase_user_id").notNull();
        medication.addStringProperty("nameOfMed");
        medication.addStringProperty("dosage");
        medication.addStringProperty("numberOfMed");
        medication.addStringProperty("method");
        medication.addStringProperty("howOften");
        medication.addStringProperty("time");
        medication.addStringProperty("startDate");
        medication.addStringProperty("duration");
        medication.addBooleanProperty("alert");

        return medication;
    }

    private static Entity addAppointment(final Schema schema) {
        Entity appt = schema.addEntity("Appointment");
        appt.addIdProperty().primaryKey().notNull().autoincrement();
        appt.addStringProperty("firebase_user_id").notNull();
        appt.addStringProperty("header");
        appt.addStringProperty("notes");
        appt.addStringProperty("time");
        appt.addStringProperty("date");

        return appt;
    }

    private static Entity addHeartRateData(final Schema schema) {
        Entity hr = schema.addEntity("HeartRateData");
        hr.addIdProperty().primaryKey().notNull().autoincrement();
        hr.addStringProperty("firebase_user_id").notNull();
        hr.addIntProperty("bpm").notNull();
        hr.addStringProperty("date").notNull();

        return hr;
    }

    private static Entity addBloodPressureData(final Schema schema) {
        Entity bp = schema.addEntity("BloodPressureData");
        bp.addIdProperty().primaryKey().autoincrement().notNull();
        bp.addStringProperty("firebase_user_id").notNull();
        bp.addIntProperty("systolic").notNull();
        bp.addIntProperty("diastolic").notNull();
        bp.addStringProperty("date").notNull();

        return bp;
    }

    private static Entity addDoctorPatient(final Schema schema) {
        Entity doctorPatient = schema.addEntity("DoctorPatient");
        doctorPatient.addIdProperty().primaryKey().autoincrement();
        doctorPatient.addStringProperty("doctor_firebase_uid").notNull();
        doctorPatient.addStringProperty("patient_firebase_uid").notNull();

        return doctorPatient;
    }
}

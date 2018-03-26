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
        Entity journal = addJournal(schema);
        Entity medication = addMedicationSched(schema);
        Entity appt = addAppointment(schema);
        Entity hr = addHeartRateData(schema);
        Entity bp = addBloodPressureData(schema);
        Entity lmt = addLimitValues(schema);
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
        appt.addIdProperty().primaryKey().autoincrement();
        appt.addStringProperty("firebase_user_id").notNull();
        appt.addStringProperty("header");
        appt.addStringProperty("notes");
        appt.addStringProperty("time");
        appt.addStringProperty("date");

        return appt;
    }

    private static Entity addHeartRateData(final Schema schema) {
        Entity hr = schema.addEntity("HeartRateData");
        hr.addIdProperty().primaryKey().autoincrement();
        hr.addStringProperty("firebase_user_id").notNull();
        hr.addIntProperty("bpm").notNull();
        hr.addStringProperty("date").notNull();
        hr.addLongProperty("timestamp").notNull();

        return hr;
    }

    private static Entity addBloodPressureData(final Schema schema) {
        Entity bp = schema.addEntity("BloodPressureData");
        bp.addIdProperty().primaryKey().autoincrement();
        bp.addStringProperty("firebase_user_id").notNull();
        bp.addIntProperty("systolic").notNull();
        bp.addIntProperty("diastolic").notNull();
        bp.addStringProperty("date").notNull();
        bp.addLongProperty("timestamp").notNull();

        return bp;
    }

    private static Entity addLimitValues(final Schema schema) {
        Entity lmt = schema.addEntity("LimitValues");
        lmt.addIdProperty().primaryKey().autoincrement();
        lmt.addStringProperty("firebase_user_id").notNull();
        lmt.addStringProperty("bpLimit");
        lmt.addStringProperty("hrLimit");

        return lmt;
    }
}

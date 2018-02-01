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
        Entity doctorCode = addDoctorCode(schema);
        Entity doctorPatient = addDoctorPatient(schema);
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

    private static Entity addDoctorCode(final Schema schema) {
        Entity doctorCode = schema.addEntity("DoctorCode");
        doctorCode.addIdProperty().primaryKey().autoincrement();
        doctorCode.addStringProperty("firebase_user_id").notNull();
        doctorCode.addIntProperty("doctor_code").notNull();
        doctorCode.addBooleanProperty("verified").notNull();

        return doctorCode;
    }

    private static Entity addDoctorPatient(final Schema schema) {
        Entity doctorPatient = schema.addEntity("DoctorPatient");
        doctorPatient.addIdProperty().primaryKey().autoincrement();
        doctorPatient.addStringProperty("doctor_firebase_uid").notNull();
        doctorPatient.addStringProperty("patient_firebase_uid").notNull();

        return doctorPatient;
    }
}

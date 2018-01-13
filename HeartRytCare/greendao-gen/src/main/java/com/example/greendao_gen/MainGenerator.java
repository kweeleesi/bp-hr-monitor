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

        return user;
    }
}

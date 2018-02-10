package com.example.hp.heartrytcare.db;

public class UserFirebase {

    public String firebase_user_id;
    public int user_type;
    public String first_name;
    public String last_name;
    public String license_number;
    public String contact_number;
    public String email;
    public String password;
    public int age;
    public int height;
    public int weight;
    public int gender;

    public UserFirebase() {}

    public UserFirebase(String firebase_user_id, int user_type, String first_name, String last_name,
                        String license_number, String contact_number, String email, String password,
                        int age, int height, int weight, int gender) {
        this.firebase_user_id = firebase_user_id;
        this.user_type = user_type;
        this.first_name = first_name;
        this.last_name = last_name;
        this.license_number = license_number;
        this.contact_number = contact_number;
        this.email = email;
        this.password = password;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
    }

}
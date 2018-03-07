package com.example.hp.heartrytcare.db;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "JOURNAL".
 */
public class Journal {

    private Long id;
    private String meals_taken;
    private Integer heart_rate;
    private String systolic;
    private String diastolic;
    private Integer temperature;
    private Double weight;
    private String medicine_name;
    private String dosage;
    private String pieces;
    private String how_often;
    private String notes;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Journal() {
    }

    public Journal(Long id) {
        this.id = id;
    }

    public Journal(Long id, String meals_taken, Integer heart_rate, String systolic, String diastolic, Integer temperature, Double weight, String medicine_name, String dosage, String pieces, String how_often, String notes) {
        this.id = id;
        this.meals_taken = meals_taken;
        this.heart_rate = heart_rate;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.temperature = temperature;
        this.weight = weight;
        this.medicine_name = medicine_name;
        this.dosage = dosage;
        this.pieces = pieces;
        this.how_often = how_often;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeals_taken() {
        return meals_taken;
    }

    public void setMeals_taken(String meals_taken) {
        this.meals_taken = meals_taken;
    }

    public Integer getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(Integer heart_rate) {
        this.heart_rate = heart_rate;
    }

    public String getSystolic() {
        return systolic;
    }

    public void setSystolic(String systolic) {
        this.systolic = systolic;
    }

    public String getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(String diastolic) {
        this.diastolic = diastolic;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getPieces() {
        return pieces;
    }

    public void setPieces(String pieces) {
        this.pieces = pieces;
    }

    public String getHow_often() {
        return how_often;
    }

    public void setHow_often(String how_often) {
        this.how_often = how_often;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}

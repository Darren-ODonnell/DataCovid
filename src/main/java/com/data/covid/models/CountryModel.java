package com.data.covid.models;

public class CountryModel {
    private String name;
    private String code;
    private String week;
    private Double occupancy;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getWeek() {
        return week;
    }
    public void setWeek(String week) {
        this.week = week;
    }
    public Double getOccupancy() {
        return occupancy;
    }
    public void setOccupancy(Double occupancy) {
        this.occupancy = occupancy;
    }

    public Country translateModelToCountry() {
        Country country = new Country();

        country.setName(name);
        country.setCode(code);
        country.setOccupancy(occupancy);
        country.setWeek(week);

        return country;
    }

}
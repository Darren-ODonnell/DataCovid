package com.data.covid.models;

public class CovidVariantModel {
    private String name;
    private Integer occurrences;
    private Country countryCode;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getOccurrences() {
        return occurrences;
    }
    public void setOccurrences(Integer occurrences) {
        this.occurrences = occurrences;
    }
    public Country getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(Country countryCode) {
        this.countryCode = countryCode;
    }

    public CovidVariant translateModelToCovidVariant() {
        CovidVariant covidVariant = new CovidVariant();

        covidVariant.setName(name);
        covidVariant.setCountryCode(countryCode);
        covidVariant.setOccurrences(occurrences);

        return covidVariant;
    }
}
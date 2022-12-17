package com.data.covid.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "covid_variants")
public class CovidVariant implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @javax.validation.constraints.Size(max = 45)
    @javax.validation.constraints.NotNull
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "occurrences")
    private Integer occurrences;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code", referencedColumnName = "code")
    private Country countryCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

}
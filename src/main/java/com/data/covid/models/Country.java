package com.data.covid.models;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "country")
public class Country implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @javax.validation.constraints.Size(max = 45)
    @javax.validation.constraints.NotNull
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @javax.validation.constraints.Size(max = 10)
    @Column(name = "code", length = 10)
    private String code;

    @javax.validation.constraints.Size(max = 45)
    @Column(name = "week", length = 45)
    private String week;

    @Column(name = "occupancy")
    private Double occupancy;

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

}
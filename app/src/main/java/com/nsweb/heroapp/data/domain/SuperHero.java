package com.nsweb.heroapp.data.domain;

import com.orm.SugarRecord;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuperHero extends SugarRecord implements Serializable {

    private Long id;
    private String name;
    private String description;
    private String primaryPower;
    private String secondaryPower;
    private String imageUri;

    public SuperHero(String name, String description, String primaryPower, String secondaryPower, String imageUri) {
        this.name = name;
        this.description = description;
        this.primaryPower = primaryPower;
        this.secondaryPower = secondaryPower;
        this.imageUri = imageUri;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

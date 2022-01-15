package com.kaktus.application.data.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;

@Data
public class Projekat {
    @Id
    @GeneratedValue
    private Long id;
    private String naziv;
    private Long sifra_projekta;

    public Projekat(String naziv, Long sifra_projekta) {
        this.id = null;
        this.naziv = naziv;
        this.sifra_projekta = sifra_projekta;
    }

    public Projekat() {

    }

    public Projekat withId(Long id) {
        if (this.id.equals(id)) {
            return this;
        } else {
            Projekat newObject = new Projekat(this.naziv, this.sifra_projekta);
            newObject.id = id;
            return newObject;
        }
    }

}

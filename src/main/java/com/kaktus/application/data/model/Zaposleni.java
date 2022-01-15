package com.kaktus.application.data.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Data
@Node("Zaposleni")
public class Zaposleni {
    @Id
    @GeneratedValue
    private Long id;
    private String ime;
    private String prezime;
    private String datum_rodjenja;
    private String pol;
    private Long jmbg;
    private Long kartica;
    private Long idFirme;

    public Zaposleni(String ime, String prezime, String datum_rodjenja, String pol, Long jmbg, Long kartica, Long idFirme) {
        this.id = null;
        this.ime = ime;
        this.prezime = prezime;
        this.datum_rodjenja = datum_rodjenja;
        this.pol = pol;
        this.jmbg = jmbg;
        this.kartica = kartica;
        this.idFirme = idFirme;
    }

    public Zaposleni() {

    }

    public String filterToString() {
        return ime + " " + prezime + " " + pol + " " + jmbg + " " + kartica;
    }

    public Zaposleni withId(Long id) {
        if (this.id.equals(id)) {
            return this;
        } else {
            Zaposleni newObject = new Zaposleni(this.ime, this.prezime, this.datum_rodjenja, this.pol, this.jmbg, this.kartica, this.idFirme);
            newObject.id = id;
            return newObject;
        }
    }
}

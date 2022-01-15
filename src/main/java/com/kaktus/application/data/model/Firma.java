package com.kaktus.application.data.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Data
@Node("Firma")
public class Firma {
    @Id
    @GeneratedValue
    private Long id;
    private String naziv;
    private Double godisnja_zarada;
    private Long pib;
    private String datum_osnivanja;
    private Long idKancelarije;

    @Relationship(type = "Rade" , direction = Relationship.Direction.OUTGOING)
    private List<Zaposleni> zaposleni;

    public Firma(String naziv, Double godisnja_zarada, Long pib, List<Zaposleni> zaposleni, String datum_osnivanja,Long idKancelarije) {
        this.id = null;
        this.naziv = naziv;
        this.godisnja_zarada = godisnja_zarada;
        this.pib = pib;
        this.zaposleni = zaposleni;
        this.datum_osnivanja = datum_osnivanja;
        this.idKancelarije = idKancelarije;
    }

    public Firma() {

    }
public String filterToString() {
        return naziv + " " + godisnja_zarada + " " + pib;
    }    public Firma withId(Long id) {
        if (this.id.equals(id)) {
            return this;
        } else {
            Firma newObject = new Firma(this.naziv, this.godisnja_zarada, this.pib, this.zaposleni, this.datum_osnivanja, this.idKancelarije);
            newObject.id = id;
            return newObject;
        }
    }

    @Override
    public String toString() {
        return "Firma{" +
                "id=" + id +
                ", naziv='" + naziv + '\'' +
                ", godisnja_zarada=" + godisnja_zarada +
                ", pib=" + pib +
                ", datum_osnivanja='" + datum_osnivanja + '\'' +
                ", zaposleni=" + zaposleni +
                '}';
    }
}

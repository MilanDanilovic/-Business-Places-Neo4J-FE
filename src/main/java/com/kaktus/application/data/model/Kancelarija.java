package com.kaktus.application.data.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Data
@Node("Kancelarija")
public class Kancelarija {
    @Id
    @GeneratedValue
    private Long id;
    private Long broj_kancelarije;
    private Double kvadratura;
    private Long broj_radnika;
    private String status;

    @Relationship(type = "Iznajmljuje" , direction = Relationship.Direction.OUTGOING)
    private Firma firma;

    public Kancelarija() {

    }

    public Kancelarija(Long broj_kancelarije, Double kvadratura, Long broj_radnika, String status) {
        this.id = null;
        this.broj_kancelarije = broj_kancelarije;
        this.kvadratura = kvadratura;
        this.broj_radnika = broj_radnika;
        this.status = status;
    }

    public String filterToString() {
        return broj_kancelarije + " " + kvadratura + " " + broj_radnika + " " + status;
    }

    public Kancelarija withId(Long id) {
        if (this.id.equals(id)) {
            return this;
        } else {
            Kancelarija newObject = new Kancelarija(this.broj_kancelarije, this.kvadratura, this.broj_radnika, this.status);
            newObject.id = id;
            return newObject;
        }
    }
}

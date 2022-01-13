package com.kaktus.application.data.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

@Data
@RelationshipProperties
public class Poseduje {

    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private final PoslovniProstor poslovniProstor;

    @Property
    private String datum_kupovine;

    public Poseduje(PoslovniProstor poslovniProstor, String datum_kupovine) {
        this.id = null;
        this.datum_kupovine = datum_kupovine;
        this.poslovniProstor = poslovniProstor;
    }

    public Poseduje withId(Long id) {
        if (this.id.equals(id)) {
            return this;
        } else {
            Poseduje newObject = new Poseduje(this.poslovniProstor, this.datum_kupovine);
            newObject.id = id;
            return newObject;
        }
    }
}

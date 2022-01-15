package com.kaktus.application.feign_client;

import com.kaktus.application.data.model.Firma;
import com.kaktus.application.data.model.Zaposleni;
import feign.Param;
import feign.RequestLine;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Collection;

public interface FirmaFeignClient extends CommonFeignClient<Firma> {
    @RequestLine("GET getAll")
    Collection<Firma> findAllFirma();

    @RequestLine("PUT updateFirma")
    void updateFirma(@Valid @RequestBody(required = true) Firma firma);

    @RequestLine("DELETE deleteFirma")
    void deleteFirma(@Valid @RequestBody(required = true) Firma firma);

    @RequestLine("POST addFirma?datum_od={datum_od}&datum_do={datum_do}&broj_kancelarije={broj_kancelarije}")
    void addFirma(@Param("datum_od") String datum_od, @Param("datum_do") String datum_do, @Param("broj_kancelarije") Long broj_kancelarije,@Valid @RequestBody(required = true) Firma firma);
}

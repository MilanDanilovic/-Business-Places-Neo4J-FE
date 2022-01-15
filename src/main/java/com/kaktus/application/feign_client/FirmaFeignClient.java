package com.kaktus.application.feign_client;

import com.kaktus.application.data.model.Firma;
import com.kaktus.application.data.model.Zaposleni;
import feign.Param;
import feign.RequestLine;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Collection;

public interface FirmaFeignClient extends CommonFeignClient<Firma> {
    @RequestLine("GET getAll")
    Collection<Firma> findAllFirma();

    @RequestLine("PUT updateFirma")
    void updateFirma(@Valid @RequestBody(required = true) Firma firma);

    @RequestLine("DELETE deleteFirma")
    void deleteFirma(@Valid @RequestBody(required = true) Firma firma);
}

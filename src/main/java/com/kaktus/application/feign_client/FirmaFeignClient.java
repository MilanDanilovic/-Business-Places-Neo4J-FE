package com.kaktus.application.feign_client;

import com.kaktus.application.data.model.Firma;
import feign.Param;
import feign.RequestLine;
import org.springframework.hateoas.CollectionModel;

import java.util.Collection;

public interface FirmaFeignClient extends CommonFeignClient<Firma> {
    @RequestLine("GET getAll")
    Collection<Firma> findAllFirma();
}

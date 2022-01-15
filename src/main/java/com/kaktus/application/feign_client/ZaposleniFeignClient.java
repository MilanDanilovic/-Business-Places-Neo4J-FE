package com.kaktus.application.feign_client;

import com.kaktus.application.data.model.Zaposleni;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Collection;


public interface ZaposleniFeignClient extends CommonFeignClient<Zaposleni>{
    @RequestLine("GET getAll")
    Collection<Zaposleni> findAllZaposleni();

    @RequestLine("PUT updateZaposleni")
    void updateZaposleni(@Valid @RequestBody(required = true) Zaposleni zaposleni);

    @RequestLine("DELETE deleteZaposleni")
    void deleteZaposleni(@Valid @RequestBody(required = true) Zaposleni zaposleni);

    @RequestLine("POST addZaposleni?datum_od={datum_od}&datum_do={datum_do}&pozicija={pozicija}&pib={pib}")
    void addZaposleni(@Param("datum_od") String datum_od, @Param("datum_do") String datum_do, @Param("pozicija") String pozicija, @Param("pib") Long pib, @Valid @RequestBody(required = true) Zaposleni zaposleni);
}

package com.kaktus.application.feign_client;

import com.kaktus.application.data.model.Projekat;
import com.kaktus.application.data.model.Vlasnik;
import com.kaktus.application.data.model.Zaposleni;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Collection;

public interface ProjektiFeignClient extends CommonFeignClient<Projekat>{

    @RequestLine("GET getAll")
    Collection<Projekat> findAllProjekat();

    @RequestLine("PUT updateProjekat")
    void updateProjekat(@Valid @RequestBody(required = true) Projekat projekat);

    @RequestLine("DELETE deleteProjekat")
    void deleteProjekat(@Valid @RequestBody(required = true) Projekat projekat);
}


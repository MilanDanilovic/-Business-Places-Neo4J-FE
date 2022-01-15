package com.kaktus.application.feign_client;

import com.kaktus.application.data.model.Zaposleni;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Collection;

public interface ZaposleniFeignClient extends CommonFeignClient<Zaposleni>{
    @RequestLine("GET getAll")
    Collection<Zaposleni> findAllZaposleni();

    @RequestLine("PUT updateZaposleni")
    void updateZaposleni(@Valid @RequestBody(required = true) Zaposleni zaposleni);
}

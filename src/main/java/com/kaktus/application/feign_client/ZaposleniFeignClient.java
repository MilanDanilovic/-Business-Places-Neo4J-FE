package com.kaktus.application.feign_client;

import com.kaktus.application.data.model.Firma;
import com.kaktus.application.data.model.Zaposleni;
import feign.RequestLine;

import java.util.Collection;

public interface ZaposleniFeignClient extends CommonFeignClient<Zaposleni>{
    @RequestLine("GET getAll")
    Collection<Zaposleni> findAllZaposleni();
}

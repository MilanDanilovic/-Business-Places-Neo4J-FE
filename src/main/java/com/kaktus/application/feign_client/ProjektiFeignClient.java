package com.kaktus.application.feign_client;

import com.kaktus.application.data.model.Projekat;
import com.kaktus.application.data.model.Vlasnik;
import feign.RequestLine;

import java.util.Collection;

public interface ProjektiFeignClient extends CommonFeignClient<Projekat>{
    @RequestLine("GET getAll")
    Collection<Projekat> findAllProjekat();
}

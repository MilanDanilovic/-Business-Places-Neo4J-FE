package com.kaktus.application.feign_client;

import com.kaktus.application.data.model.Kancelarija;
import feign.RequestLine;

import java.util.Collection;

public interface KancelarijaFeignClient extends CommonFeignClient<Kancelarija>{
    @RequestLine("GET getAll")
    Collection<Kancelarija> findAllKancelarija();
}

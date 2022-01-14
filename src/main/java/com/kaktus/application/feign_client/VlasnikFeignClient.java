package com.kaktus.application.feign_client;

import com.kaktus.application.data.model.Vlasnik;
import feign.RequestLine;

import java.util.Collection;

public interface VlasnikFeignClient extends CommonFeignClient<Vlasnik>{
    @RequestLine("GET getAll")
    Collection<Vlasnik> findAllVlasnik();
}

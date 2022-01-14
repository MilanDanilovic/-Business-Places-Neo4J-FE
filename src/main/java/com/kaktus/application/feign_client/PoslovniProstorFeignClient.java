package com.kaktus.application.feign_client;

import com.kaktus.application.data.model.PoslovniProstor;
import feign.RequestLine;

import java.util.Collection;

public interface PoslovniProstorFeignClient extends CommonFeignClient<PoslovniProstor> {
    @RequestLine("GET getAll")
    Collection<PoslovniProstor> findAllPoslovniProstor();
}

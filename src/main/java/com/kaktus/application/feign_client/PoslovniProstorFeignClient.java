package com.kaktus.application.feign_client;

import com.kaktus.application.data.model.PoslovniProstor;
import com.kaktus.application.data.model.Projekat;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Collection;

public interface PoslovniProstorFeignClient extends CommonFeignClient<PoslovniProstor> {
    @RequestLine("GET getAll")
    Collection<PoslovniProstor> findAllPoslovniProstor();

    @RequestLine("DELETE deletePoslovniProstor")
    void deletePoslovniProstor(@Valid @RequestBody(required = true) PoslovniProstor poslovniProstor);

    @RequestLine("PUT updatePoslovniProstor")
    void updatePoslovniProstor(@Valid @RequestBody(required = true) PoslovniProstor poslovniProstor);
}

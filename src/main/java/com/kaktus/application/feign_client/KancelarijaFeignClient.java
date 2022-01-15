package com.kaktus.application.feign_client;

import com.kaktus.application.data.model.Kancelarija;
import com.kaktus.application.data.model.Zaposleni;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Collection;

public interface KancelarijaFeignClient extends CommonFeignClient<Kancelarija>{
    @RequestLine("GET getAll")
    Collection<Kancelarija> findAllKancelarija();

    @RequestLine("PUT updateKancelarija")
    void updateKancelarija(@Valid @RequestBody(required = true) Kancelarija kancelarija);

    @RequestLine("DELETE deleteKancelarija")
    void deleteKancelarija(@Valid @RequestBody(required = true) Kancelarija kancelarija);

    @RequestLine("POST /addKancelarija?adresa={adresa}")
    void addKancelarija(@Param("adresa") String adresa, @RequestBody(required = true) Kancelarija kancelarija);
}

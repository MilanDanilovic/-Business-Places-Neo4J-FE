package com.kaktus.application.feign_client;

import com.kaktus.application.data.model.Vlasnik;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.Collection;

public interface VlasnikFeignClient extends CommonFeignClient<Vlasnik>{
    @RequestLine("GET getAll")
    Collection<Vlasnik> findAllVlasnik();

    @RequestLine("PUT updateVlasnik")
    void updateVlasnik(@Valid @RequestBody(required = true) Vlasnik vlasnik);

    @RequestLine("DELETE deleteVlasnik")
    void deleteVlasnik(@Valid @RequestBody(required = true) Vlasnik vlasnik);
}

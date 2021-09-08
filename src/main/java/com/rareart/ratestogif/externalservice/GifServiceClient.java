package com.rareart.ratestogif.externalservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "gifs", url = "${feign.gifs.url}")
public interface GifServiceClient {
    @RequestMapping(value = "${feign.gifs.route}", params = {"api_key", "tag", "rating"}, method = RequestMethod.GET)
    GifServiceResponseTemplate getGifServiceResponse(@RequestParam("api_key") String api_key,
                                                     @RequestParam("tag") String tag, @RequestParam("rating") String rating);
}


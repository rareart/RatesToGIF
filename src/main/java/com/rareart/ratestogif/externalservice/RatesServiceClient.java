package com.rareart.ratestogif.externalservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "exchangeRates", url = "${feign.rates.url}")
public interface RatesServiceClient {

    @RequestMapping(value = "${feign.rates.startRoute}" + "{date}" + ".json", params = {"app_id"}, method = RequestMethod.GET)
    RateServiceResponseTemplate getRatesServiceResponse(@PathVariable String date,
                                                        @RequestParam("app_id") String app_key);

}

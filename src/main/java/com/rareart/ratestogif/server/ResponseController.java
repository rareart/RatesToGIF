package com.rareart.ratestogif.server;
import com.rareart.ratestogif.externalservice.GifServiceClient;
import com.rareart.ratestogif.externalservice.GifServiceResponseTemplate;
import com.rareart.ratestogif.externalservice.RateServiceResponseTemplate;
import com.rareart.ratestogif.externalservice.RatesServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
@PropertySource("classpath:application.properties")
@RestController
public class ResponseController {

    @Value("${feign.rates.params.apikey}")
    private String rates_api_key;

    @Value("${feign.gifs.params.apikey}")
    private String gifs_api_key;

    @Value("${feign.gifs.params.rating}")
    private String rating;

    @Value("${referenceCurrencyNumericCode}")
    private int referenceNumericCode;

    private final RatesServiceClient ratesServiceClient;
    private final GifServiceClient gifServiceClient;

    @Autowired
    public ResponseController(RatesServiceClient ratesServiceClient, GifServiceClient gifServiceClient) {
        this.ratesServiceClient = ratesServiceClient;
        this.gifServiceClient = gifServiceClient;
    }

    @GetMapping("/getGif")
    public ResponseTemplate getResponse(){
        return new ResponseTemplate(400, "not_available", "null");
    }

    @GetMapping("/getGif/{numericCode}")
    public ResponseTemplate getResponse(@PathVariable Integer numericCode){

        Date dateNow = new Date();
        Date dateYesterday = new Date(System.currentTimeMillis()-24*60*60*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        RateServiceResponseTemplate todayRatesServiceResponse;
        RateServiceResponseTemplate yesterdayRatesServiceResponse;
        try {
            todayRatesServiceResponse = ratesServiceClient.getRatesServiceResponse(sdf.format(dateNow), rates_api_key);
            yesterdayRatesServiceResponse = ratesServiceClient.getRatesServiceResponse(sdf.format(dateYesterday), rates_api_key);
        } catch (Exception e){
            return new ResponseTemplate(502, "error:rates_service_invalid_response", "null");
        }

        double todayCrossRate = todayRatesServiceResponse.getRateToUSDByNumericCode(numericCode)/todayRatesServiceResponse.getRateToUSDByNumericCode(referenceNumericCode);
        double yesterdayCrossRate = yesterdayRatesServiceResponse.getRateToUSDByNumericCode(numericCode)/yesterdayRatesServiceResponse.getRateToUSDByNumericCode(referenceNumericCode);
        if (todayCrossRate == 0D || yesterdayCrossRate == 0D) {
            return new ResponseTemplate(400, "error:wrong_currency_code", "null");
        }

        String gifTag = getTag(todayCrossRate, yesterdayCrossRate);

        GifServiceResponseTemplate gifServiceResponseTemplate;
        try {
            gifServiceResponseTemplate = gifServiceClient.getGifServiceResponse(gifs_api_key, gifTag, rating);
        } catch (Exception e){
            return new ResponseTemplate(502, "error:gif_service_invalid_response", "null");
        }

        return new ResponseTemplate(200, "OK", gifServiceResponseTemplate.getURL());
    }

    private String getTag(double todayCrossRate, double yesterdayCrossRate){
        String gifTag = "error";

        if(todayCrossRate < yesterdayCrossRate) {
            gifTag = "rich";
        }
        if(todayCrossRate > yesterdayCrossRate) {
            gifTag = "broke";
        }
        if(todayCrossRate == yesterdayCrossRate) {
            gifTag = "same";
        }
        return gifTag;
    }
}

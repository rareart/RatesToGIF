package com.rareart.ratestogif.server;

import com.rareart.ratestogif.externalservice.GifServiceClient;
import com.rareart.ratestogif.externalservice.GifServiceResponseTemplate;
import com.rareart.ratestogif.externalservice.RateServiceResponseTemplate;
import com.rareart.ratestogif.externalservice.RatesServiceClient;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class ResponseControllerTest {

    @Autowired
    private ResponseController responseController;

    @MockBean
    private RatesServiceClient ratesServiceClient;

    @MockBean
    private RateServiceResponseTemplate rateServiceResponseTemplate;

    @MockBean
    private GifServiceResponseTemplate gifServiceResponseTemplate;

    @MockBean
    private GifServiceClient gifServiceClient;

    @Test
    void get400Response() {
        ResponseTemplate responseTemplate = responseController.getResponse();
        assertEquals(responseTemplate.getStatus(), 400);
        assertEquals(responseTemplate.getMsg(), "not_available");
        assertEquals(responseTemplate.getImageURL(), "null");
    }


    @Test
    void get200Response() {
        when(rateServiceResponseTemplate.getRateToUSDByNumericCode(ArgumentMatchers.anyInt()))
                .then((Answer<Double>) invocation -> 0D);

        when(ratesServiceClient.getRatesServiceResponse(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .then((Answer<RateServiceResponseTemplate>) invocation -> rateServiceResponseTemplate);

        when(gifServiceClient.getGifServiceResponse(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .then((Answer<GifServiceResponseTemplate>) invocation -> gifServiceResponseTemplate);

        when(gifServiceResponseTemplate.getURL())
                .then((Answer<String>) invocation -> "https://giphy.com/gifs/PCHOfficial-ckkEKMk3qOZJHnBtHD");

        ResponseTemplate responseTemplate = responseController.getResponse(840);

        Mockito.verify(ratesServiceClient, times(2)).getRatesServiceResponse(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.verify(rateServiceResponseTemplate, times(4)).getRateToUSDByNumericCode(ArgumentMatchers.anyInt());
        Mockito.verify(gifServiceClient, times(1)).getGifServiceResponse(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.verify(gifServiceResponseTemplate, times(1)).getURL();
        assertEquals(responseTemplate.getStatus(), 200);
        assertEquals(responseTemplate.getMsg(), "OK");
        assertEquals(responseTemplate.getImageURL(), "https://giphy.com/gifs/PCHOfficial-ckkEKMk3qOZJHnBtHD");
    }
}
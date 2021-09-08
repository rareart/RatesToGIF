package com.rareart.ratestogif.externalservice;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Currency;
import java.util.HashMap;
import java.util.Optional;

public class RateServiceResponseTemplate {

    private final String disclaimer;
    private final String license;
    private final String timestamp;
    private final String base;
    private final HashMap<String, Object> rates;

    @SuppressWarnings("unchecked")
    public RateServiceResponseTemplate(String disclaimer, String license, String timestamp, String base, Object rates) {
        this.disclaimer = disclaimer;
        this.license = license;
        this.timestamp = timestamp;
        this.base = base;
        this.rates = new ObjectMapper().convertValue(rates, HashMap.class);
        this.rates.put("notfound", 0D);
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public String getLicense() {
        return license;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getBase() {
        return base;
    }

    public double getRateToUSDByNumericCode(int numericCode){
        Optional<Currency> optionalCurrency = Currency.getAvailableCurrencies().stream().filter(c -> c.getNumericCode() == numericCode).findAny();
        if (!optionalCurrency.isPresent()){
            return 0D; //error code
        }
        Currency currency = optionalCurrency.get();
        String currencyISOCode = currency.getCurrencyCode();
        Object value;
        if (rates != null){
            value = rates.get(currencyISOCode);
        } else {
            return 0D;
        }
        if (value instanceof Double) {
            return (double) rates.get(currencyISOCode);
        }
        if (value instanceof Integer){
            return (double) (Integer) rates.get(currencyISOCode);
        } else {
            return 0D;
        }

    }
}

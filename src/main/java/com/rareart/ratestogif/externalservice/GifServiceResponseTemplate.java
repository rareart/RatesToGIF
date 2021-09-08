package com.rareart.ratestogif.externalservice;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

public class GifServiceResponseTemplate {
    private final HashMap<String, Object> data;
    private final Object meta;

    @SuppressWarnings("unchecked")
    public GifServiceResponseTemplate(Object data, Object meta) {
        this.data = new ObjectMapper().convertValue(data, HashMap.class);
        this.meta = meta;
    }

    public String getURL(){
        if(data != null){
            return (String) data.get("image_original_url");
        } else {
            return null;
        }
    }

    public Object getMeta() {
        return meta;
    }
}

package com.dy.networkdisk.email.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonTool {
    private final static Gson gson = new GsonBuilder().create();

    public static String toJson(Object object){
        return gson.toJson(object);
    }

    public static  <T> T toObject(String json, Class<T> type){
        return gson.fromJson(json,type);
    }
}

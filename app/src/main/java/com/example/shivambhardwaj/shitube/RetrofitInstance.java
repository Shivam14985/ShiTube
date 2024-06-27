package com.example.shivambhardwaj.shitube;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    public static final String BaseUrl = "https://run.mocky.io/v3/";
    public static Retrofit retrofit;
    public static Retrofit getRetrofit(){

if (retrofit==null){
    retrofit = new Retrofit.Builder()
            .baseUrl(BaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
        return retrofit;
    }
}

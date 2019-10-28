package com.technology.singularium.api;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroFitClient {

    private static Retrofit retroFit = null;

    public static Retrofit getClient(String url){
        if(retroFit == null){
            retroFit = new Retrofit.Builder().baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retroFit;
    }

}

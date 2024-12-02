package com.example.fitfeed.util;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private Retrofit retrofit;

    public RetrofitService() {
        initRetrofit();
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.fitfeed.online:8081")
                .addConverterFactory(GsonConverterFactory.create(GsonHelper.getGson()))
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}

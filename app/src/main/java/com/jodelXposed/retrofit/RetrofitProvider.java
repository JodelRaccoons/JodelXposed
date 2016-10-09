package com.jodelXposed.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Admin on 09.10.2016.
 */

public class RetrofitProvider {

    static Retrofit retrofit;
    static JodelXposedService jodelXposedService;

    public static JodelXposedService getJodelXposedService() {
        return jodelXposedService;
    }

    static {
        retrofit = new Retrofit.Builder()
            .baseUrl("http://api.spectre-app.de")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        jodelXposedService = retrofit.create(JodelXposedService.class);
    }
}

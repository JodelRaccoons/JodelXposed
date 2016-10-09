package com.jodelXposed.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Admin on 09.10.2016.
 */

public interface JodelXposedService {
    @GET("/jx/{version}/hooks.json")
    Call<HooksResponse> getHooks(@Path("version") int version);
}

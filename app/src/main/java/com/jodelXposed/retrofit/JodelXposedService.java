package com.jodelXposed.retrofit;

import com.jodelXposed.models.Hookvalues;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

@SuppressWarnings("WeakerAccess")
public interface JodelXposedService {
    @GET("master/hooks/{version}/hooks.json")
    Call<Hookvalues> getHooks(@Path("version") int version);
}

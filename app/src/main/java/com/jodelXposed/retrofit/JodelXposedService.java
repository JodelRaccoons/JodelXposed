package com.jodelXposed.retrofit;

import com.jodelXposed.models.HookValues;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

@SuppressWarnings("WeakerAccess")
public interface JodelXposedService {
    @GET("master/hooks/{version}/hooks.json")
    Call<HookValues> getHooks(@Path("version") int version);
}

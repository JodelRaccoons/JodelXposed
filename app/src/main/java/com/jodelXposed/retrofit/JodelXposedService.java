package com.jodelXposed.retrofit;

import com.jodelXposed.retrofit.Response.HooksResponse;
import com.jodelXposed.retrofit.Response.VersionResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Admin on 09.10.2016.
 */

@SuppressWarnings("WeakerAccess")
public interface JodelXposedService {
    @GET("/Unbrick/JodelXposed/master/hooks/{version}/hooks.json")
    Call<HooksResponse> getHooks(@Path("version") int version);

    @GET("/Unbrick/JodelXposed/master/hooks/current.json")
    Call<VersionResponse> latestVersion();
}

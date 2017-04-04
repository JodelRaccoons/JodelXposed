package com.jodelXposed.hookupdate

import com.jodelXposed.models.HookValues
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface JodelXposedAPI {
    @GET("master/hooks/{version}/hooks.json")
    fun getHooks(@Path("version") version: Int): Call<HookValues>

    @GET("master/devhooks/{version}/hooks.json")
    fun getDevHooks(@Path("version") version: Int): Call<HookValues>
}

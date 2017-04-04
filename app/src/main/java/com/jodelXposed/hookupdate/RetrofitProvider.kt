package com.jodelXposed.hookupdate

import com.google.gson.GsonBuilder

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    var JXAPI: JodelXposedAPI = Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/krokofant/JodelXposed/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(JodelXposedAPI::class.java)
        private set
}

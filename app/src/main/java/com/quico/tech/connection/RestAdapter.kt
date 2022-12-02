package com.quico.tech.connection

import com.quico.tech.BuildConfig
import com.quico.tech.data.Constant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RestAdapter {
//    fun createAPI(): API {
//        val logging = HttpLoggingInterceptor()
//        logging.setLevel(Level.BODY)
//        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
//        builder.connectTimeout(5, TimeUnit.SECONDS)
//        builder.writeTimeout(10, TimeUnit.SECONDS)
//        builder.readTimeout(30, TimeUnit.SECONDS)
//        if (BuildConfig.DEBUG) {
//            builder.addInterceptor(logging)
//        }
//        builder.cache(null)
//        val okHttpClient: OkHttpClient = builder.build()
//        val retrofit: Retrofit = OkHttpClient.Builder()
//            .baseUrl(Constant.WEB_BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//        return retrofit.create(API::class.java)
//    }
}
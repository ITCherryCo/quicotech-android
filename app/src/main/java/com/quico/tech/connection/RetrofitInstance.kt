package com.quico.tech.connection

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.google.gson.GsonBuilder
import com.quico.tech.BuildConfig

import com.quico.tech.data.Constant.WEB_BASE_URL
import com.quico.tech.data.PrefManager
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.CookieManager
import java.util.concurrent.TimeUnit


object RetrofitInstance {
    lateinit var app: Application
    lateinit var context: Context
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WEB_BASE_URL)
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            //.cookieJar(new JavaNetCookieJar(new CookieManager()))
            .build()
    }
    val api by lazy {

        retrofit.create(API::class.java)
    }

    val builder by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
        //.addInterceptor(interceptor3)
        // .cookieJar(JavaNetCookieJar(CookieHandler.getDefault()))
        // .cookieJar(JavaNetCookieJar(CookieManager()))
        //  .cache(null)

    }


    val interceptor2 = object : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {

           var context = app.applicationContext

            var prefManager = PrefManager(context)
            val request = chain.request()
            val cookieString = prefManager.cookies

            val builder = request.newBuilder()
            if (!cookieString.isNullOrEmpty()) {
                builder.addHeader("Cookie", cookieString)
            }

            val modifiedRequest = builder.build()
            val response = chain.proceed(modifiedRequest)

            // Check for the Set-Cookie header
            val setCookieHeader = response.header("Set-Cookie")
            if (setCookieHeader != null) {
                // Save the new cookies to shared preferences
                if (prefManager.cookies.isNullOrEmpty())
                    prefManager.cookies=setCookieHeader
            }

            return response

        }
    }

    fun createHttpClient(): OkHttpClient {


        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        builder.addInterceptor(interceptor2)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(logging)
        }

        val okHttpClient: OkHttpClient = builder.build()
        return okHttpClient
    }
}



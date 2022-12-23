package com.quico.tech.connection

import com.google.gson.GsonBuilder
import com.quico.tech.BuildConfig
import com.quico.tech.data.Constant.APPLICATION_JSON
import com.quico.tech.data.Constant.CONTENT_TYPE

import com.quico.tech.data.Constant.WEB_BASE_URL
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.util.concurrent.TimeUnit

object RetrofitInstance {
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
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .cache(null)

    }

    var interceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var original: Request = chain.request()
            var request: Request = original.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
               // .header(SESSION_ID_NAME,SESSION_ID_VALUE)
                // .method(original.method(), original.body())
                .build()
            return chain.proceed(request)
        }
    }

    fun createHttpClient(): OkHttpClient {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)


        if (BuildConfig.DEBUG) {
            builder.addInterceptor(logging)
           // builder.addInterceptor(interceptor)
        }

        val okHttpClient: OkHttpClient = builder.build()
        return okHttpClient
    }
}



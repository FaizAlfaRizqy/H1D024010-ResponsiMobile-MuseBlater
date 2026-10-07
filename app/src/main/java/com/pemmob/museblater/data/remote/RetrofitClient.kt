package com.pemmob.museblater.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://api.tenrai.org/v1/"

    /**
     * Interceptor to add custom User-Agent and Accept headers.
     */
    private val headerInterceptor = Interceptor { chain: Interceptor.Chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .header("User-Agent", "Mozilla/5.0 (Linux; Android 12; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36 MuseBlater/1.0")
            .header("Accept", "application/json")
            .method(original.method, original.body)
            .build()
        chain.proceed(request)
    }

    /**
     * Interceptor to handle API rate limits (HTTP 429 Too Many Requests).
     */
    private val rateLimitInterceptor = Interceptor { chain: Interceptor.Chain ->
        val request = chain.request()
        var response = chain.proceed(request)
        var tryCount = 0
        val maxLimitRetries = 3

        while (!response.isSuccessful && response.code == 429 && tryCount < maxLimitRetries) {
            tryCount++
            response.close()
            try {
                Thread.sleep(1000L * tryCount)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
            response = chain.proceed(request)
        }
        response
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(rateLimitInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val jikanApiService: JikanApiService = retrofit.create(JikanApiService::class.java)
}

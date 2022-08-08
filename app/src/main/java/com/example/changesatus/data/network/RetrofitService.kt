package com.example.changesatus.data.network

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    //https://oauth.vk.com/
    @GET("token?grant_type=password&client_id=6146827&client_secret=qVxWRF1CwHERuIrKBnqe&v=5.131&2fa_supported=1")
    suspend fun getToken(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("code") code: String? = null,
        @Query("captcha_sid") captchaSid: String? = null,
        @Query("captcha_key") captchaKey: String? = null
    ): Response<String>

    //https://api.vk.com/
    @GET("method/auth.validatePhone?v=5.95")
    suspend fun validatePhone(
        @Query("sid") sid: String
    ): Response<String>

    //https://api.vk.me/method/
    @GET("account.setPrivacy?v=5.109&key=online")
    suspend fun changeStatus(
        @Query("value") privacyType: String,
        @Query("access_token") token: String
    )

    //https://api.vk.me/method/
    @GET("account.getPrivacySettings?v=5.109&key=online")
    suspend fun getStatus(@Query("access_token") accessToken: String): Response<String>

    companion object {
        fun create(baseUrl: String, userAgent: String? = null): RetrofitService {

            val okHttpClientBuilder = OkHttpClient.Builder()

            if (userAgent != null) {
                okHttpClientBuilder.addInterceptor { chain ->
                    val origin = chain.request()


                    val requestBuilder = origin.newBuilder()
                        .addHeader("User-Agent", userAgent)

                    chain.proceed(requestBuilder.build())
                }
            }

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClientBuilder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(RetrofitService::class.java)
        }
    }
}

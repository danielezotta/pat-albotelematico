package it.danielezotta.albotelematico.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.danielezotta.albotelematico.BuildConfig
import it.danielezotta.albotelematico.data.api.AlboTelematicoApi
import okhttp3.CookieJar
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://www.albotelematico.tn.it/"
    private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 14) AlboTelematicoApp/1.0"

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideCookieManager(): CookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
    }

    @Provides
    @Singleton
    fun provideCookieJar(cookieManager: CookieManager): CookieJar = JavaNetCookieJar(cookieManager)

    @Provides
    @Singleton
    fun provideDefaultHeadersInterceptor(): Interceptor = Interceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .header("Accept", "application/json, text/javascript, */*; q=0.01")
            .header("User-Agent", USER_AGENT)
            .header("Referer", BASE_URL + "bacheca/tutti/tutti")
            .method(original.method, original.body)
            .build()
        chain.proceed(request)
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        cookieJar: CookieJar,
        defaultHeadersInterceptor: Interceptor
    ): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .cookieJar(cookieJar)
            .addInterceptor(defaultHeadersInterceptor)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            httpClient.addInterceptor(loggingInterceptor)
        }

        return httpClient.build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    @Provides
    @Singleton
    fun provideAlboTelematicoApi(retrofit: Retrofit): AlboTelematicoApi =
        retrofit.create(AlboTelematicoApi::class.java)
}

package com.ninele7.tracker.data.remote

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.ninele7.tracker.data.R
import com.ninele7.tracker.domain.habit.RemoteHabitRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteRepositoryModule {
    @Binds
    abstract fun bindRemoteRepository(
        remoteHabitRepositoryImpl: RemoteHabitRepositoryImpl
    ): RemoteHabitRepository

    companion object {
        @Provides
        @Named("token")
        fun token(@ApplicationContext context: Context): String =
            context.getString(R.string.token)

        @Provides
        @Named("authInterceptor")
        fun authInterceptor(@Named("token") token: String): Interceptor {
            return Interceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build()
                chain.proceed(newRequest)
            }
        }

        @Provides
        @Named("loggingInterceptor")
        fun loggingInterceptor(): Interceptor {
            return HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

        @Provides
        fun okHttp(
            @Named("authInterceptor") authInterceptor: Interceptor,
            @Named("loggingInterceptor") loggingInterceptor: Interceptor
        ): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        @Provides
        fun retrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
            .baseUrl("https://droid-test-server.doubletapp.ru/api/")
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                )
            )
            .build()

        @Provides
        fun remoteDataSource(retrofit: Retrofit): RemoteDataSource =
            retrofit.create(RemoteDataSource::class.java)
    }
}
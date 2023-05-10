package com.ninele7.tracker.model

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import okhttp3.Interceptor
import okhttp3.Interceptor.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*


class HabitUID(@Expose val uid: UUID)

data class HabitDTO(
    @Expose val color: Int,
    @Expose val count: Int,
    @Expose val date: Long,
    @Expose val description: String,
    @Expose val doneDates: List<Int>,
    @Expose val frequency: Int,
    @Expose val priority: Int,
    @Expose val title: String,
    @Expose val type: Int,
    @Expose val uid: UUID?
) {
    companion object {
        fun fromStore(habit: Habit): HabitDTO {
            return HabitDTO(
                habit.color,
                habit.timesPerPeriod,
                habit.updated,
                habit.description,
                listOf(),
                habit.period,
                habit.priority.ordinal,
                habit.name,
                habit.type.ordinal,
                habit.uid
            )
        }
    }

    fun toStore(): Habit {
        return Habit(
            updated = date,
            uid = uid ?: UUID.randomUUID(),
            name = title,
            description = description,
            priority = HabitPriority.values()[priority],
            type = HabitType.values()[type],
            period = count,
            timesPerPeriod = frequency,
            color = color
        )
    }
}

interface RemoteDataSource {
    @GET("habit")
    suspend fun getHabits(): List<HabitDTO>

    @PUT("habit")
    suspend fun updateHabit(@Body habit: HabitDTO): HabitUID

    @HTTP(method = "DELETE", path = "habit", hasBody = true)
    suspend fun deleteHabit(@Body uid: HabitUID)
}

object RemoteDataSourceHolder {
    private var INSTANCE: RemoteDataSource? = null

    fun getInstance(token: String): RemoteDataSource {
        return synchronized(this) {
            val instance = INSTANCE
            val curInstance = if (instance != null) instance else {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                val client = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor(Interceptor { chain ->
                        val newRequest: Request = chain.request().newBuilder()
                            .addHeader("Authorization", token)
                            .build()
                        chain.proceed(newRequest)
                    })
                    .build()
                Retrofit.Builder()
                    .baseUrl("https://droid-test-server.doubletapp.ru/api/")
                    .client(client)
                    .addConverterFactory(
                        GsonConverterFactory.create(
                            GsonBuilder()
                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                .excludeFieldsWithoutExposeAnnotation().create()
                        )
                    )
                    .build()
                    .create(RemoteDataSource::class.java)
            }
            INSTANCE = curInstance
            curInstance
        }
    }
}
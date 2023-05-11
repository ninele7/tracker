package com.ninele7.tracker.data

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.ninele7.tracker.domain.habit.*
import okhttp3.Interceptor
import okhttp3.Interceptor.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*
import javax.inject.Inject

class HabitUID(@Expose val uid: UUID)
class HabitDone(@Expose val habitUid: UUID, @Expose val date: Long)

data class HabitRemoteDTO(
    @Expose val color: Int,
    @Expose val count: Int,
    @Expose val date: Long,
    @Expose val description: String,
    @Expose val doneDates: List<Long>,
    @Expose val frequency: Int,
    @Expose val priority: Int,
    @Expose val title: String,
    @Expose val type: Int,
    @Expose val uid: UUID?
) {
    companion object {
        fun fromModel(habit: Habit): HabitRemoteDTO {
            return HabitRemoteDTO(
                habit.color,
                habit.timesPerPeriod,
                habit.updated,
                habit.description,
                habit.completions,
                habit.period,
                habit.priority.ordinal,
                habit.name,
                habit.type.ordinal,
                habit.uid
            )
        }
    }

    fun toModel(): Habit {
        return Habit(
            updated = date,
            uid = uid ?: UUID.randomUUID(),
            name = title,
            description = description,
            priority = HabitPriority.values()[priority],
            type = HabitType.values()[type],
            period = count,
            timesPerPeriod = frequency,
            color = color,
            completions = doneDates
        )
    }
}

interface RemoteDataSource {
    @GET("habit")
    suspend fun getHabits(): List<HabitRemoteDTO>

    @PUT("habit")
    suspend fun updateHabit(@Body habit: HabitRemoteDTO): HabitUID

    @HTTP(method = "DELETE", path = "habit", hasBody = true)
    suspend fun deleteHabit(@Body uid: HabitUID)

    @POST("habit_done")
    suspend fun completeHabit(@Body habitDone: HabitDone)
}

object RemoteDataSourceFactory {
    fun construct(token: Token): RemoteDataSource {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(Interceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Authorization", token.token)
                    .build()
                chain.proceed(newRequest)
            })
            .build()
        return Retrofit.Builder()
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

}


class RemoteHabitRepositoryImpl @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    RemoteHabitRepository {
    override suspend fun updateHabit(habit: Habit): UUID =
        remoteDataSource.updateHabit(HabitRemoteDTO.fromModel(habit)).uid

    override suspend fun addHabit(habit: Habit): UUID =
        remoteDataSource.updateHabit(HabitRemoteDTO.fromModel(habit).copy(uid = null)).uid

    override suspend fun deleteHabit(uid: UUID) = remoteDataSource.deleteHabit(HabitUID(uid))

    override suspend fun getHabits(): List<Habit> =
        remoteDataSource.getHabits().map { it.toModel() }

    override suspend fun completeHabit(uid: UUID, date: Long) {
        remoteDataSource.completeHabit(HabitDone(uid, date))
    }

}
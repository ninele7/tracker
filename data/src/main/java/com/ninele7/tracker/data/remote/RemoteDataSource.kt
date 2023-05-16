package com.ninele7.tracker.data.remote

import okhttp3.Interceptor.*
import retrofit2.http.*
import java.util.*

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



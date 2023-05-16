package com.ninele7.tracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface HabitDao {
    @Query(
        "SELECT * FROM habit " +
                "LEFT JOIN habitCompletion ON habit.uid = habitCompletion.habitUid " +
                "WHERE habitCompletion.date >= :date - habit.period * 24 * 60 * 60 * 1000 or habitCompletion.date is null"
    )
    fun getAll(date: Long): Flow<Map<HabitStoreDTO, List<HabitCompletionStoreDTO>>>

    @Insert
    suspend fun insert(habit: HabitStoreDTO)

    @Insert
    suspend fun insertAll(habits: List<HabitStoreDTO>)

    @Query("DELETE FROM habit WHERE uid = :id")
    suspend fun delete(id: UUID)

    @Update
    suspend fun update(habit: HabitStoreDTO)

    @Query("SELECT * FROM habit WHERE uid = :id")
    suspend fun get(id: UUID): HabitStoreDTO?

    @Query("DELETE FROM habit")
    suspend fun clean()

    @Insert
    suspend fun insert(habitCompletion: HabitCompletionStoreDTO)

    @Query("DELETE FROM habitCompletion")
    suspend fun cleanCompletions()

    @Query("DELETE FROM habitCompletion WHERE habitUid = :id")
    suspend fun clearCompletionsForHabit(id: UUID)

    @Insert
    suspend fun insertAllCompletion(habitCompletions: List<HabitCompletionStoreDTO>)
}
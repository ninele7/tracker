package com.ninele7.tracker.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit")
    fun getAll(): Flow<List<Habit>>

    @Insert
    suspend fun insert(habit: Habit): Long

    @Insert
    suspend fun insertAll(habits: List<Habit>)

    @Query("DELETE FROM habit WHERE uid = :id")
    suspend fun delete(id: UUID)

    @Update
    suspend fun update(habit: Habit)

    @Query("SELECT * FROM habit WHERE uid = :id")
    suspend fun get(id: UUID): Habit?

    @Query("DELETE FROM habit")
    suspend fun clean()
}

@Database(entities = [Habit::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}

package com.ninele7.tracker.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit")
    fun getAll(): LiveData<List<Habit>>

    @Insert
    suspend fun insertAll(vararg habits: Habit)

    @Query("DELETE FROM habit WHERE id = :id")
    suspend fun delete(id: Int)

    @Update
    suspend fun update(habit: Habit)
}

@Database(entities = [Habit::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}

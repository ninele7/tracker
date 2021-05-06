package com.ninele7.tracker.model

import androidx.lifecycle.LiveData
import androidx.room.Room
import com.ninele7.tracker.App
import kotlinx.coroutines.CoroutineScope
import java.lang.NullPointerException

class DataSource(private val db: HabitDao) {
    val habitsList: LiveData<List<Habit>> = db.getAll()

    suspend fun addHabit(habit: Habit) = db.insertAll(habit)

    suspend fun removeHabit(id: Int) = db.delete(id)

    suspend fun updateHabit(habit: Habit) = db.update(habit)

    companion object {
        private var INSTANCE: DataSource? = null

        fun getDataSource(): DataSource {
            return synchronized(DataSource::class) {
                val instance = INSTANCE
                val newInstance = if (instance != null) instance else {
                    val context = App.getContext() ?: throw NullPointerException()
                    val db =
                        Room.databaseBuilder(context, AppDatabase::class.java, "main")
                            .build()
                    DataSource(db.habitDao())
                }
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}
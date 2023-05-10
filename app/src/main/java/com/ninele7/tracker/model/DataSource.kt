package com.ninele7.tracker.model

import androidx.room.Room
import com.ninele7.tracker.App
import kotlinx.coroutines.flow.Flow
import java.lang.NullPointerException
import java.util.UUID

class DataSource(private val db: HabitDao, private val remoteDataSource: RemoteDataSource) {
    val habitsList: Flow<List<Habit>> = db.getAll()

    suspend fun addHabit(habit: Habit) {
        val uid = remoteDataSource.updateHabit(HabitDTO.fromStore(habit).copy(uid = null))
        db.insert(habit.copy(uid = uid.uid))
    }

    suspend fun removeHabit(id: UUID) {
        remoteDataSource.deleteHabit(HabitUID(id))
        db.delete(id)
    }

    suspend fun updateHabit(habit: Habit) {
        remoteDataSource.updateHabit(HabitDTO.fromStore(habit))
        db.update(habit)
    }

    suspend fun forceSync() {
        val habits = remoteDataSource.getHabits()
        val mappedHabits = habits.map { it.toStore() }
        db.clean()
        db.insertAll(mappedHabits)
    }

    suspend fun getHabit(id: UUID): Habit? = db.get(id)

    companion object {
        private var INSTANCE: DataSource? = null

        // TODO add token provider
        private const val token = ""

        fun getDataSource(): DataSource =
            synchronized(DataSource::class) {
                val instance = INSTANCE
                val newInstance = if (instance != null) instance else {
                    val context = App.getContext() ?: throw NullPointerException()
                    val db =
                        Room.databaseBuilder(context, AppDatabase::class.java, "main")
                            .build()
                    DataSource(db.habitDao(), RemoteDataSourceHolder.getInstance(token))
                }
                INSTANCE = newInstance
                newInstance
            }
    }
}
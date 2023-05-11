package com.ninele7.tracker.domain.habit

import kotlinx.coroutines.flow.Flow
import java.util.*

interface LocalHabitRepository {
    fun getAll(date: Long): Flow<List<Habit>>
    suspend fun insert(habit: Habit)
    suspend fun delete(id: UUID)
    suspend fun update(habit: Habit)
    suspend fun clean()
    suspend fun insertAll(habits: List<Habit>)
    suspend fun get(id: UUID): Habit?

    suspend fun insert(id: UUID, date: Long)
}
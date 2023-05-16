package com.ninele7.tracker.domain.habit

import java.util.UUID

interface RemoteHabitRepository {
    suspend fun updateHabit(habit: Habit): UUID
    suspend fun addHabit(habit: Habit): UUID
    suspend fun deleteHabit(uid: UUID)
    suspend fun getHabits(): List<Habit>
    suspend fun completeHabit(uid: UUID, date: Long)
}
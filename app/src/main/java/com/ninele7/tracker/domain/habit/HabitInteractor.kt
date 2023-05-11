package com.ninele7.tracker.domain.habit

import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class HabitInteractor @Inject constructor(
    private val localHabitRepository: LocalHabitRepository,
    private val remoteHabitRepository: RemoteHabitRepository
) {
    fun allLocalHabits(): Flow<List<Habit>> {
        val date = System.currentTimeMillis()
        return localHabitRepository.getAll(date)
    }

    suspend fun addHabit(habit: Habit) {
        val uid = remoteHabitRepository.addHabit(habit)
        localHabitRepository.insert(habit.copy(uid = uid))
    }

    suspend fun removeHabit(id: UUID) {
        remoteHabitRepository.deleteHabit(id)
        localHabitRepository.delete(id)
    }

    suspend fun updateHabit(habit: Habit) {
        remoteHabitRepository.updateHabit(habit)
        localHabitRepository.update(habit)
    }

    suspend fun forceSync() {
        val habits = remoteHabitRepository.getHabits()
        localHabitRepository.clean()
        localHabitRepository.insertAll(habits)
    }

    suspend fun getHabit(id: UUID): Habit? = localHabitRepository.get(id)
    suspend fun completeHabit(id: UUID) {
        val date = System.currentTimeMillis()
        remoteHabitRepository.completeHabit(id, date)
        localHabitRepository.insert(id, date)
    }
}
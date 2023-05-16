package com.ninele7.tracker.data.remote

import com.ninele7.tracker.domain.habit.Habit
import com.ninele7.tracker.domain.habit.RemoteHabitRepository
import java.util.*
import javax.inject.Inject

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
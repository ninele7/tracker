package com.ninele7.tracker.data.local

import com.ninele7.tracker.domain.habit.Habit
import com.ninele7.tracker.domain.habit.LocalHabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject


class LocalHabitRepositoryImpl @Inject constructor(private val dao: HabitDao) :
    LocalHabitRepository {
    override fun getAll(date: Long): Flow<List<Habit>> =
        dao.getAll(date).map { habits ->
            habits.map { pair ->
                pair.key.toModel().copy(completions = pair.value.map { it.date })
            }
        }

    override suspend fun insert(habit: Habit) = dao.insert(HabitStoreDTO.fromModel(habit))

    override suspend fun delete(id: UUID) {
        dao.clearCompletionsForHabit(id)
        dao.delete(id)
    }

    override suspend fun update(habit: Habit) = dao.update(HabitStoreDTO.fromModel(habit))

    override suspend fun clean() {
        dao.clean()
        dao.cleanCompletions()
    }

    override suspend fun insertAll(habits: List<Habit>) {
        dao.insertAll(habits.map { HabitStoreDTO.fromModel(it) })
        dao.insertAllCompletion(habits.flatMap { habit ->
            habit.completions.map { HabitCompletionStoreDTO(habit.uid, it) }
        })
    }

    override suspend fun get(id: UUID): Habit? = dao.get(id)?.toModel()

    override suspend fun insert(id: UUID, date: Long) =
        dao.insert(HabitCompletionStoreDTO(id, date))

}
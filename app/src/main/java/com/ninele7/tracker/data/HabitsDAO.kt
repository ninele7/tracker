package com.ninele7.tracker.data

import androidx.room.*
import com.ninele7.tracker.domain.habit.Habit
import com.ninele7.tracker.domain.habit.HabitPriority
import com.ninele7.tracker.domain.habit.HabitType
import com.ninele7.tracker.domain.habit.LocalHabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

@Entity(tableName = "habit")
data class HabitStoreDTO(
    @PrimaryKey
    var uid: UUID,
    var name: String,
    var description: String,
    var priority: Int,
    var type: Int,
    var period: Int,
    var timesPerPeriod: Int,
    var color: Int,
    val updated: Long,
) {
    fun toModel() =
        Habit(
            uid,
            name,
            description,
            HabitPriority.fromInt(priority),
            HabitType.fromInt(type),
            period,
            timesPerPeriod,
            color,
            updated
        )

    companion object {
        fun fromModel(habit: Habit) =
            HabitStoreDTO(
                habit.uid,
                habit.name,
                habit.description,
                habit.priority.ordinal,
                habit.type.ordinal,
                habit.period,
                habit.timesPerPeriod,
                habit.color,
                habit.updated
            )
    }
}

@Entity(tableName = "habitCompletion", primaryKeys = ["habitUid", "date"])
data class HabitCompletionStoreDTO(
    var habitUid: UUID,
    var date: Long,
)

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

@Database(entities = [HabitStoreDTO::class, HabitCompletionStoreDTO::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}


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
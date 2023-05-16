package com.ninele7.tracker.data.local

import androidx.room.Entity
import java.util.*

@Entity(tableName = "habitCompletion", primaryKeys = ["habitUid", "date"])
data class HabitCompletionStoreDTO(
    var habitUid: UUID,
    var date: Long,
)
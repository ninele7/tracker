package com.ninele7.tracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HabitStoreDTO::class, HabitCompletionStoreDTO::class], version = 1, exportSchema=false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}
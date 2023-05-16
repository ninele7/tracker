package com.ninele7.tracker.data.local

import android.content.Context
import androidx.room.Room
import com.ninele7.tracker.domain.habit.LocalHabitRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalRepositoryModule {
    @Binds
    abstract fun bindLocalRepository(
        localHabitRepositoryImpl: LocalHabitRepositoryImpl
    ): LocalHabitRepository

    companion object {
        @Provides
        fun habitsDAO(@ApplicationContext context: Context): HabitDao =
            Room.databaseBuilder(context, AppDatabase::class.java, "main")
                .build().habitDao()
    }
}
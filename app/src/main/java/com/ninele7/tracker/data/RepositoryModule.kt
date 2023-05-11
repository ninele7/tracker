package com.ninele7.tracker.data

import android.content.Context
import androidx.room.Room
import com.ninele7.tracker.R
import com.ninele7.tracker.domain.habit.LocalHabitRepository
import com.ninele7.tracker.domain.habit.RemoteHabitRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindLocalRepository(
        localHabitRepositoryImpl: LocalHabitRepositoryImpl
    ): LocalHabitRepository

    @Binds
    abstract fun bindRemoteRepository(
        remoteHabitRepositoryImpl: RemoteHabitRepositoryImpl
    ): RemoteHabitRepository

    companion object {
        @Provides
        fun habitsDAO(@ApplicationContext context: Context): HabitDao =
            Room.databaseBuilder(context, AppDatabase::class.java, "main")
                .build().habitDao()

        @Provides
        fun token(@ApplicationContext context: Context): Token =
            Token(context.getString(R.string.token))

        @Provides
        fun remoteDataSource(token: Token): RemoteDataSource =
            RemoteDataSourceFactory.construct(token)
    }
}
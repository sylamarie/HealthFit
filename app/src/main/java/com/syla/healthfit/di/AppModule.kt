package com.syla.healthfit.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.WorkManager
import com.syla.healthfit.data.local.AppDatabase
import com.syla.healthfit.data.local.FoodSeedData
import com.syla.healthfit.data.local.dao.DailyMetricsDao
import com.syla.healthfit.data.local.dao.FoodItemDao
import com.syla.healthfit.data.local.dao.FoodLogDao
import com.syla.healthfit.data.local.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.Lazy
import java.time.Clock
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemDefaultZone()

    @Provides
    @Singleton
    @ApplicationScope
    fun provideAppScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope,
        seedCallback: RoomDatabase.Callback
    ): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "healthfit.db")
            .addCallback(seedCallback)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSeedCallback(
        @ApplicationScope scope: CoroutineScope,
        foodItemDaoLazy: Lazy<FoodItemDao>
    ): RoomDatabase.Callback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            scope.launch {
                val dao = foodItemDaoLazy.get()
                val entities = FoodSeedData.items.map { item ->
                    com.syla.healthfit.data.local.entity.FoodItemEntity(
                        name = item.name,
                        kcalPer100g = item.kcalPer100g,
                        defaultUnit = item.defaultUnit
                    )
                }
                dao.insertAll(entities)
            }
        }
    }

    @Provides
    fun provideUserProfileDao(database: AppDatabase): UserProfileDao = database.userProfileDao()

    @Provides
    fun provideDailyMetricsDao(database: AppDatabase): DailyMetricsDao = database.dailyMetricsDao()

    @Provides
    fun provideFoodItemDao(database: AppDatabase): FoodItemDao = database.foodItemDao()

    @Provides
    fun provideFoodLogDao(database: AppDatabase): FoodLogDao = database.foodLogDao()

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)
}
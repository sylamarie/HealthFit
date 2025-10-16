package com.syla.healthfit.data.repository

import com.syla.healthfit.data.local.dao.UserProfileDao
import com.syla.healthfit.data.local.entity.UserProfileEntity
import com.syla.healthfit.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao
) {
    fun profileStream(): Flow<UserProfile> =
        userProfileDao.observeProfile().map { entity -> entity?.toDomain() ?: UserProfile() }

    suspend fun save(profile: UserProfile) {
        userProfileDao.upsert(profile.toEntity())
    }
}

private fun UserProfileEntity.toDomain(): UserProfile =
    UserProfile(
        id = id,
        age = age,
        sex = sex,
        heightCm = heightCm,
        weightKg = weightKg,
        goalWeightKg = goalWeightKg,
        activityLevel = activityLevel,
        glassSizeMl = glassSizeMl,
        dailyStepGoal = dailyStepGoal,
        dailyWaterGoalMl = dailyWaterGoalMl,
        dailyCalorieTarget = dailyCalorieTarget,
        lastUpdated = lastUpdated
    )

private fun UserProfile.toEntity(): UserProfileEntity =
    UserProfileEntity(
        id = id,
        age = age,
        sex = sex,
        heightCm = heightCm,
        weightKg = weightKg,
        goalWeightKg = goalWeightKg,
        activityLevel = activityLevel,
        glassSizeMl = glassSizeMl,
        dailyStepGoal = dailyStepGoal,
        dailyWaterGoalMl = dailyWaterGoalMl,
        dailyCalorieTarget = dailyCalorieTarget,
        lastUpdated = lastUpdated.takeIf { !it.isAfter(LocalDate.now()) } ?: LocalDate.now()
    )
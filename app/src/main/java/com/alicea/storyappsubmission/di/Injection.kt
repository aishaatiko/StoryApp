package com.alicea.storyappsubmission.di

import android.content.Context
import com.alicea.storyappsubmission.data.StoryRepository
import com.alicea.storyappsubmission.data.local.room.StoryDatabase
import com.alicea.storyappsubmission.network.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(
            database,
            apiService)
    }
}
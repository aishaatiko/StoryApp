package com.alicea.storyappsubmission.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.alicea.storyappsubmission.data.StoryRepository
import com.alicea.storyappsubmission.preference.SessionUserModel
import com.alicea.storyappsubmission.preference.UserPreference

class MapsViewModel(private val pref: UserPreference, private val storyRepository: StoryRepository) : ViewModel() {

    fun getToken(): LiveData<SessionUserModel> {
        return pref.getToken().asLiveData()
    }

    fun getStory(token: String) = storyRepository.getStoryLocation(token)

}
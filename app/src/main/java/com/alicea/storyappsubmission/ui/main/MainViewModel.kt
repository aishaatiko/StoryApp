package com.alicea.storyappsubmission.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.alicea.storyappsubmission.data.StoryRepository
import com.alicea.storyappsubmission.data.local.entity.StoryEntity
import com.alicea.storyappsubmission.preference.SessionUserModel
import com.alicea.storyappsubmission.preference.UserPreference
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreference, private val storyRepository: StoryRepository) : ViewModel() {

    fun getToken(): LiveData<SessionUserModel> {
        return pref.getToken().asLiveData()
    }

    fun story(token: String): LiveData<PagingData<StoryEntity>> {
        return storyRepository.getStory(token).cachedIn(viewModelScope)
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}
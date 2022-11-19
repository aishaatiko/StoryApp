package com.alicea.storyappsubmission.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alicea.storyappsubmission.data.StoryRepository
import com.alicea.storyappsubmission.preference.UserPreference

class LoginViewModel(private val pref: UserPreference, private val storyRepository: StoryRepository) : ViewModel() {
    val loading : MutableLiveData<Boolean> = storyRepository.loadRepo

    fun loginRepo(email: String, password: String) = storyRepository.login(email, password, pref)
}
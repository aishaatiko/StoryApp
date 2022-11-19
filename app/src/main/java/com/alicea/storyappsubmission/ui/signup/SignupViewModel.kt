package com.alicea.storyappsubmission.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alicea.storyappsubmission.data.StoryRepository
import com.alicea.storyappsubmission.preference.UserModel

class SignupViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    val loading : MutableLiveData<Boolean> = storyRepository.loadRepo

    fun registerRepo(user: UserModel) = storyRepository.register(user)
}
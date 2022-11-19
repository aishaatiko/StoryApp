package com.alicea.storyappsubmission.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.alicea.storyappsubmission.data.StoryRepository
import com.alicea.storyappsubmission.preference.SessionUserModel
import com.alicea.storyappsubmission.preference.UserPreference
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(private val pref: UserPreference, private val storyRepository: StoryRepository) : ViewModel() {
    val loading : MutableLiveData<Boolean> = storyRepository.loadRepo

    fun getToken(): LiveData<SessionUserModel> {
        return pref.getToken().asLiveData()
    }

    fun uploadImageRepo(file: MultipartBody.Part, description: RequestBody, token: String) =
        storyRepository.uploadImage(file, description, token)

}
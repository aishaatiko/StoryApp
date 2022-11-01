package com.alicea.storyappsubmission.view.main

import androidx.lifecycle.*
import com.alicea.storyappsubmission.model.SessionUserModel
import com.alicea.storyappsubmission.model.UserPreference
import com.alicea.storyappsubmission.networking.ApiConfig
import com.alicea.storyappsubmission.networking.ListStoryItem
import com.alicea.storyappsubmission.networking.StoryListResponse
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreference) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    private var job: Job? = null
    val loading = MutableLiveData<Boolean>()
    val storyList = MutableLiveData<List<ListStoryItem>>()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun getToken(): LiveData<SessionUserModel> {
        return pref.getToken().asLiveData()
    }

    fun setData(token: String) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val service = ApiConfig.getApiService().getListStories("Bearer $token")

            withContext(Dispatchers.Main) {
                service.enqueue(object : Callback<StoryListResponse> {
                    override fun onResponse(
                        call: Call<StoryListResponse>,
                        response: Response<StoryListResponse>
                    ) {
                        val responseBody = response.body()
                        if (response.isSuccessful ) {
                            if (responseBody != null && !responseBody.error) {
                                loading.value = false

                                storyList.postValue(responseBody.listStory)
                                onError(responseBody.message)
                            }
                        } else {
                            onError("Error : ${response.message()} ")
                        }
                    }

                    override fun onFailure(call: Call<StoryListResponse>, t: Throwable) {
                        onError("onFailure")
                    }
                })

            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }


}
package com.alicea.storyappsubmission.data

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.alicea.storyappsubmission.data.local.entity.StoryEntity
import com.alicea.storyappsubmission.data.local.room.StoryDatabase
import com.alicea.storyappsubmission.preference.UserModel
import com.alicea.storyappsubmission.preference.UserPreference
import com.alicea.storyappsubmission.network.*
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.*

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    val loadRepo = MutableLiveData<Boolean>()

    fun getStory(token: String): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                prefetchDistance = 2*PAGE_SIZE,
                initialLoadSize = 2*PAGE_SIZE
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getStoryLocation(token: String): LiveData<Result<List<StoryEntity>>> = liveData {
        loadRepo.value = true
        try {
            val responseData = apiService.getListStoriesLocation("Bearer $token",  1)
            val responseDataList = responseData.listStory
            val storyList = responseDataList.map { story ->
                StoryEntity(
                    story.id,
                    story.name,
                    story.description,
                    story.photoUrl,
                    story.createdAt,
                    story.lat,
                    story.lon
                )
            }
            emit(Result.Success(storyList))
            storyDatabase.storyDao().deleteAll()
            storyDatabase.storyDao().insertStory(storyList)
        } catch (e: Exception) {
            Log.d("StoryRepository", "getStoryLocation: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        } finally {
            loadRepo.value = false
        }
        val localData: LiveData<Result<List<StoryEntity>>> = storyDatabase.storyDao().getAllStoryLocation().map { Result.Success(it) }
        emitSource(localData)
    }

    companion object {
        const val PAGE_SIZE = 3
    }

    fun login(email: String, password: String, pref: UserPreference? = null) : LiveData<Result<LoginResponse>> = liveData {
        loadRepo.value = true
        try {
            val response = apiService.loginUser(email, password)
            saveToken(response.loginResult.token, pref)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.d("StoryRepository", "login: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        } finally {
            loadRepo.value = false
        }
    }

    fun register(user: UserModel) : LiveData<Result<RegisterResponse>> = liveData {
        loadRepo.value = true
        try {
            val response = apiService.registerUser(user.name, user.email, user.password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.d("StoryRepository", "register: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        } finally {
            loadRepo.value = false
        }
    }

    fun uploadImage(file: MultipartBody.Part, description: RequestBody, token: String) : LiveData<Result<FileUploadResponse>> = liveData  {
        loadRepo.value = true
        try {
            val responseData = apiService.uploadImage(file, description, "Bearer $token")
            emit(Result.Success(responseData))
        } catch (e: Exception) {
            Log.d("StoryRepository", "uploadImage: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        } finally {
            loadRepo.value = false
        }
    }

    private suspend fun saveToken(token: String, pref: UserPreference?){
        pref?.login(token)
    }

}
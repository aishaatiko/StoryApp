package com.alicea.storyappsubmission.data

import com.alicea.storyappsubmission.network.*
import com.alicea.storyappsubmission.utils.DataDummy
import okhttp3.MultipartBody
import okhttp3.RequestBody


class FakeApiService : ApiService {

    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()
    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummyStoryResponse = DataDummy.generateDummyListStoryItem()
    private val dummyStoryLocationResponse = DataDummy.generateDummyListStoryLocationItem()
    private val dummyFileResponse = DataDummy.generateDummyFileUploadResponse()

    override suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): RegisterResponse {
        return dummyRegisterResponse
    }

    override suspend fun loginUser(email: String, password: String): LoginResponse {
        return dummyLoginResponse
    }

    override suspend fun uploadImage(
        file: MultipartBody.Part,
        description: RequestBody,
        authHeader: String
    ): FileUploadResponse {
        return dummyFileResponse
    }

    override suspend fun getListStories(
        authHeader: String,
        page: Int,
        size: Int
    ): StoryListResponse {
        return dummyStoryResponse
    }

    override suspend fun getListStoriesLocation(
        authHeader: String,
        location: Int
    ): StoryLocationListResponse {
        return dummyStoryLocationResponse
    }
}
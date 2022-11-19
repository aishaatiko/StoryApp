package com.alicea.storyappsubmission.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("v1/register")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("v1/login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @Multipart
    @POST("v1/stories")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") authHeader: String
    ): FileUploadResponse


    @GET("v1/stories")
    suspend fun getListStories(
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): StoryListResponse

    @GET("v1/stories")
    suspend fun getListStoriesLocation(
        @Header("Authorization") authHeader: String,
        @Query("location") location: Int
    ): StoryLocationListResponse
}


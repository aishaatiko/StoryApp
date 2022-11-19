package com.alicea.storyappsubmission.utils

import com.alicea.storyappsubmission.data.local.entity.StoryEntity
import com.alicea.storyappsubmission.network.*


object DataDummy {

    fun generateDummyListStoryItem(): StoryListResponse {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "$i",
                "author $i",
                "description $i",
                "https://story-api.dicoding.dev/images/stories/photos-1668006750537_7zx2YBiW.jpg",
                "2022-11-09T15:12:30.540Z",
                "-6.9600243",
                "109.1406238",
            )
            items.add(story)
        }
        return StoryListResponse(false, "Stories fetched successfully", items)
    }

    fun generateDummyListStoryLocationItem(): StoryLocationListResponse {
        val items: MutableList<ListStoryLocationItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryLocationItem(
                "$i",
                "author $i",
                "description $i",
                "https://story-api.dicoding.dev/images/stories/photos-1668006750537_7zx2YBiW.jpg",
                "2022-11-09T15:12:30.540Z",
                "-6.9600243",
                "109.1406238",
            )
            items.add(story)
        }
        return StoryLocationListResponse(false, "Stories fetched successfully", items)
    }

    fun generateDummyStoryEntity(): List<StoryEntity> {
        val storyList: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..10) {
            val story = StoryEntity(
                "$i",
                "name",
                "description $i",
                "https://story-api.dicoding.dev/images/stories/photos-1668006750537_7zx2YBiW.jpg",
                "2022-11-09T15:12:30.540Z",
                "-6.9600243",
                "109.1406238",
            )
            storyList.add(story)
        }
        return storyList
    }

    fun generateDummyLoginResponse(): LoginResponse {
        val itemLogin = LoginResultItem("1", "ais", "token")
        return LoginResponse(false, "success", itemLogin)
    }

    fun generateDummyRegisterResponse(): RegisterResponse {
        return RegisterResponse(false, "User created")
    }

    fun generateDummyFileUploadResponse(): FileUploadResponse {
        return FileUploadResponse(true, "Story created successfully")
    }
}
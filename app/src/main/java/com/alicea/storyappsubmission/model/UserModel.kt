package com.alicea.storyappsubmission.model

data class SessionUserModel(
    val token: String,
    val isLogin: Boolean
)

data class UserModel(
    val name: String,
    val email: String,
    val password: String
)

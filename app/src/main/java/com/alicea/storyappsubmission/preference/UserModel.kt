package com.alicea.storyappsubmission.preference

data class SessionUserModel(
    val token: String,
    val isLogin: Boolean
)

data class UserModel(
    val name: String,
    val email: String,
    val password: String
)

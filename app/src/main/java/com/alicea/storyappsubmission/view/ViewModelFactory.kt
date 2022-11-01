package com.alicea.storyappsubmission.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alicea.storyappsubmission.model.UserPreference
import com.alicea.storyappsubmission.view.add.AddViewModel
import com.alicea.storyappsubmission.view.login.LoginViewModel
import com.alicea.storyappsubmission.view.main.MainViewModel
import com.alicea.storyappsubmission.view.signup.SignupViewModel

class ViewModelFactory(private val pref: UserPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel() as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                AddViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)

        }
    }
}
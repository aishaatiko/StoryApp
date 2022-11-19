package com.alicea.storyappsubmission.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.alicea.storyappsubmission.DataDummy
import com.alicea.storyappsubmission.MainDispatcherRule
import com.alicea.storyappsubmission.data.Result
import com.alicea.storyappsubmission.data.StoryRepository
import com.alicea.storyappsubmission.network.LoginResponse
import com.alicea.storyappsubmission.preference.UserPreference
import com.alicea.storyappsubmission.getOrAwaitValue
import com.alicea.storyappsubmission.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var userPreference: UserPreference

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when User Login should return Response Message`() = runTest {
        val loginViewModel = LoginViewModel(userPreference, storyRepository)
        val dummyData = DataDummy.generateDummyLoginResponse()
        val response = MutableLiveData<Result<LoginResponse>>()
        response.value = Result.Success(dummyData)
        `when`(storyRepository.login("email@gmail.com", "123456", userPreference)).thenReturn(response)
        val expectedResponse = storyRepository.login("email@gmail.com", "123456", userPreference)
        val actualResponse = loginViewModel.loginRepo("email@gmail.com", "123456")

        actualResponse.getOrAwaitValue()
        actualResponse.observeForTesting {
            assertNotNull(actualResponse)
            assertEquals(expectedResponse.value, actualResponse.value)
        }
    }
}
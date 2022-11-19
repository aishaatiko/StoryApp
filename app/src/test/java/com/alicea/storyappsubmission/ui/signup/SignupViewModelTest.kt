package com.alicea.storyappsubmission.ui.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.alicea.storyappsubmission.DataDummy
import com.alicea.storyappsubmission.MainDispatcherRule
import com.alicea.storyappsubmission.data.Result
import com.alicea.storyappsubmission.data.StoryRepository
import com.alicea.storyappsubmission.preference.UserModel
import com.alicea.storyappsubmission.network.RegisterResponse
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
class SignupViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when User Register should return Response Message`() = runTest {
        val signupViewModel = SignupViewModel(storyRepository)
        val dummyData = DataDummy.generateDummyRegisterResponse()
        val response = MutableLiveData<Result<RegisterResponse>>()
        response.value = Result.Success(dummyData)
        val inputUser = UserModel("name", "email@gmail.com","123456")
        `when`(storyRepository.register(inputUser)).thenReturn(response)
        val expectedResponse = storyRepository.register(inputUser)
        val actualResponse = signupViewModel.registerRepo(inputUser)
        actualResponse.getOrAwaitValue()
        actualResponse.observeForTesting {
            assertNotNull(actualResponse)
            assertEquals(expectedResponse.value, actualResponse.value)
        }
    }
}
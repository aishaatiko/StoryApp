package com.alicea.storyappsubmission.ui.add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.alicea.storyappsubmission.DataDummy
import com.alicea.storyappsubmission.MainDispatcherRule
import com.alicea.storyappsubmission.data.Result
import com.alicea.storyappsubmission.data.StoryRepository
import com.alicea.storyappsubmission.network.FileUploadResponse
import com.alicea.storyappsubmission.preference.SessionUserModel
import com.alicea.storyappsubmission.preference.UserPreference
import com.alicea.storyappsubmission.getOrAwaitValue
import com.alicea.storyappsubmission.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var userPreference: UserPreference

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var imageMultipart: MultipartBody.Part

    @Mock
    private lateinit var description: RequestBody

    private lateinit var addViewModel: AddViewModel

    @Before
    fun setUp() {
        addViewModel = AddViewModel(userPreference, storyRepository)
    }

    @Test
    fun `when Upload Image should return Response Message`() = runTest {
        val dummyData = DataDummy.generateDummyFileUploadResponse()
        val response = MutableLiveData<Result<FileUploadResponse>>()
        response.value = Result.Success(dummyData)
        `when`(storyRepository.uploadImage(imageMultipart, description, "token")).thenReturn(response)
        val expectedResponse = storyRepository.uploadImage(imageMultipart, description, "token")
        val actualResponse = addViewModel.uploadImageRepo(imageMultipart, description, "token")

        actualResponse.getOrAwaitValue()
        actualResponse.observeForTesting {
            assertNotNull(actualResponse)
            assertEquals(expectedResponse.value, actualResponse.value)
        }
    }

    @Test
    fun `when Get Token Should return token`() = runTest {
        val expectedToken = SessionUserModel("token", true)
        `when`(userPreference.getToken()).thenReturn(flowOf(expectedToken))
        val actualToken = addViewModel.getToken().getOrAwaitValue()

        assertEquals(expectedToken, actualToken)
    }
}
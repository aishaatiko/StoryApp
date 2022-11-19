package com.alicea.storyappsubmission.data

import android.content.Context
import android.os.Environment
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.alicea.storyappsubmission.data.local.room.StoryDatabase
import com.alicea.storyappsubmission.preference.UserModel
import com.alicea.storyappsubmission.network.ApiService
import com.alicea.storyappsubmission.utils.FileUtil
import com.alicea.storyappsubmission.utils.MainDispatcherRule
import com.alicea.storyappsubmission.utils.getOrAwaitValue
import com.alicea.storyappsubmission.utils.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class StoryRepositoryTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var instrumentationContext: Context

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private var apiService : ApiService = FakeApiService()
    private lateinit var database: StoryDatabase
    private lateinit var storyRepository: StoryRepository
    private lateinit var userModel: UserModel

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StoryDatabase::class.java
        ).build()
        storyRepository = StoryRepository(database, apiService)
        userModel = UserModel("name", "email1@gmail.com","123456")
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun getStoryLocation_Success() = runTest {
        val expectedStory = apiService.getListStoriesLocation("token", 1)
        val actualStory = storyRepository.getStoryLocation("token")

        actualStory.getOrAwaitValue()
        actualStory.observeForTesting {
            assertNotNull(actualStory)
            assertEquals(
                expectedStory.listStory.size,
                (actualStory.value as Result.Success).data.size
            )
        }
    }

    @Test
    fun register_Success() = runTest {
        val expectedResponse = apiService.registerUser(userModel.name, userModel.email, userModel.password)
        val actualResponse = storyRepository.register(userModel)

        actualResponse.getOrAwaitValue()
        actualResponse.observeForever {
            assertNotNull(actualResponse)
            assertTrue(actualResponse.value is Result.Success)
            assertEquals(
                expectedResponse.message,
                (actualResponse.value as Result.Success).data.message
            )
        }
    }

    @Test
    fun login_Success() = runTest {
        val expectedResponse = apiService.loginUser("email@gmail.com", "123456")
        val actualResponse = storyRepository.login("email@gmail.com", "123456", null)

        actualResponse.getOrAwaitValue()
        actualResponse.observeForever {
            assertNotNull(actualResponse)
            assertTrue(actualResponse.value is Result.Success)
            assertEquals(
                expectedResponse.message,
                (actualResponse.value as Result.Success).data.message
            )
        }
    }

    @Test
    fun upload_Success() = runTest {
        val file = createCustomTempFile(instrumentationContext)
        val description = "description".toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
        val expectedResponse = apiService.uploadImage(imageMultipart, description,"token")
        val actualResponse = storyRepository.uploadImage(imageMultipart, description,"token")

        actualResponse.getOrAwaitValue()
        actualResponse.observeForever {
            assertNotNull(actualResponse)
            assertTrue(actualResponse.value is Result.Success)
            assertEquals(
                expectedResponse.message,
                (actualResponse.value as Result.Success).data.message
            )
        }
    }

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(FileUtil.timeStamp, ".jpg", storageDir)
    }
}
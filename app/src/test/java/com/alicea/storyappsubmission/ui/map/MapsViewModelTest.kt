package com.alicea.storyappsubmission.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.alicea.storyappsubmission.DataDummy
import com.alicea.storyappsubmission.MainDispatcherRule
import com.alicea.storyappsubmission.data.Result
import com.alicea.storyappsubmission.data.StoryRepository
import com.alicea.storyappsubmission.data.local.entity.StoryEntity
import com.alicea.storyappsubmission.preference.SessionUserModel
import com.alicea.storyappsubmission.preference.UserPreference
import com.alicea.storyappsubmission.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
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
class MapsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var userPreference: UserPreference

    @Mock
    private lateinit var storyRepository: StoryRepository

    private lateinit var mapsViewModel: MapsViewModel

    @Before
    fun setUp() {
        mapsViewModel = MapsViewModel(userPreference, storyRepository)
    }

    @Test
    fun `when Get Story Should Not Null and Return Success`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryEntity()
        val expectedStory = MutableLiveData<Result<List<StoryEntity>>>()
        expectedStory.value = Result.Success(dummyStory)
        `when`(storyRepository.getStoryLocation("token")).thenReturn(expectedStory)

        val actualStory = mapsViewModel.getStory("token").getOrAwaitValue()

        assertTrue(actualStory is Result.Success)
        assertNotNull(actualStory)
        assertEquals(expectedStory.value, actualStory)
        assertEquals(
            (expectedStory.value as Result.Success<List<StoryEntity>>).data.size,
            (actualStory as Result.Success<List<StoryEntity>>).data.size )
        assertEquals(
            (expectedStory.value as Result.Success<List<StoryEntity>>).data[0],
            actualStory.data[0]
        )
    }

    @Test
    fun `when Get Token Should return token`() = runTest {
        val expectedToken = SessionUserModel("token", true)
        `when`(userPreference.getToken()).thenReturn(flowOf(expectedToken))
        val actualToken = mapsViewModel.getToken().getOrAwaitValue()
        assertEquals(expectedToken, actualToken)
    }
}
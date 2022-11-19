package com.alicea.storyappsubmission.data.local.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.alicea.storyappsubmission.utils.DataDummy
import com.alicea.storyappsubmission.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class StoryDaoTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: StoryDatabase
    private lateinit var dao: StoryDao
    private val sampleStory = DataDummy.generateDummyStoryEntity()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StoryDatabase::class.java
        ).build()
        dao = database.storyDao()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertStory_Success() = runTest {
        dao.insertStory(sampleStory)
        val actualStory = dao.getAllStoryLocation().getOrAwaitValue()
        Assert.assertTrue(actualStory.isNotEmpty())
        Assert.assertEquals(sampleStory.size, actualStory.size)
        Assert.assertEquals(sampleStory[0].description, actualStory[0].description)
    }

    @Test
    fun deleteAll_Success() = runTest {
        dao.insertStory(sampleStory)
        dao.deleteAll()
        val actualStory = dao.getAllStoryLocation().getOrAwaitValue()
        Assert.assertTrue(actualStory.isEmpty())
    }

}
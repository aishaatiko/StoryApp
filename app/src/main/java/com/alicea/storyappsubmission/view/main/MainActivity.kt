package com.alicea.storyappsubmission.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alicea.storyappsubmission.R
import com.alicea.storyappsubmission.adapter.ListStoryAdapter
import com.alicea.storyappsubmission.databinding.ActivityMainBinding
import com.alicea.storyappsubmission.model.StoryModel
import com.alicea.storyappsubmission.model.UserPreference
import com.alicea.storyappsubmission.view.ViewModelFactory
import com.alicea.storyappsubmission.view.add.AddStoryActivity
import com.alicea.storyappsubmission.view.welcome.WelcomeActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel : MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var rvStory: RecyclerView
    private val list = ArrayList<StoryModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
    }

    private fun setupView() {
        rvStory = binding.rvListStory
        rvStory.setHasFixedSize(true)
        supportActionBar?.title = getString(R.string.list_stories)
    }


    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.storyList.observe(this) {
            list.clear()
            for (story in it) {
                list.add(
                    StoryModel(
                        story.id,
                        story.name,
                        story.description,
                        story.photoUrl,
                        story.createdAt
                    )
                )
            }
            rvStory.layoutManager = LinearLayoutManager(this)

            val listStoryAdapter = ListStoryAdapter(list)
            rvStory.adapter = listStoryAdapter
        }

        mainViewModel.loading.observe(this) {
            showLoading(it)
        }

        mainViewModel.errorMessage.observe(this) {
            when (it) {
                "Stories fetched successfully" -> {
                    Toast.makeText(this@MainActivity, getString(R.string.fetched_success), Toast.LENGTH_SHORT).show()
                }
                "onFailure" -> {
                    Toast.makeText(this@MainActivity, getString(R.string.on_failure_message), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this@MainActivity, getString(R.string.not_found), Toast.LENGTH_SHORT).show()
                }
            }
        }
        mainViewModel.getToken().observe(this) { session ->
            if (session.isLogin) {
                mainViewModel.setData(session.token)

            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setupAction() {
        val swipeRefresh = binding.swipeRefresh
        swipeRefresh.setOnRefreshListener {
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
            swipeRefresh.isRefreshing = false
        }
        binding.ivAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setMenu(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun setMenu(itemId: Int) {
        when (itemId) {
            R.id.action_logout -> {
                mainViewModel.logout()
            }

            R.id.action_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
    }


}
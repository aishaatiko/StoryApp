package com.alicea.storyappsubmission.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alicea.storyappsubmission.R
import com.alicea.storyappsubmission.adapter.LoadingStateAdapter
import com.alicea.storyappsubmission.adapter.StoryListAdapter
import com.alicea.storyappsubmission.databinding.ActivityMainBinding
import com.alicea.storyappsubmission.preference.UserPreference
import com.alicea.storyappsubmission.ui.ViewModelFactory
import com.alicea.storyappsubmission.ui.add.AddStoryActivity
import com.alicea.storyappsubmission.ui.map.MapsActivity
import com.alicea.storyappsubmission.ui.welcome.WelcomeActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mainViewModel : MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var rvStory: RecyclerView

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
        rvStory.scrollToPosition(0)
    }


    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[MainViewModel::class.java]

        mainViewModel.getToken().observe(this) { session ->
            if (session.isLogin) {
                getData(session.token)

            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
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
        binding.ivAddStory.setOnClickListener(this)
        binding.ivMapActivity.setOnClickListener(this)
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

    private fun getData(token: String) {
        rvStory.layoutManager = LinearLayoutManager(this)

        val adapter = StoryListAdapter()

        binding.rvListStory.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadingStateAdapter{
                adapter.retry()
            },
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        mainViewModel.story(token).observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.iv_add_story -> startActivity(Intent(this, AddStoryActivity::class.java))
            R.id.iv_map_activity -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
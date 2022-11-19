package com.alicea.storyappsubmission.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alicea.storyappsubmission.R
import com.alicea.storyappsubmission.data.local.entity.StoryEntity
import com.alicea.storyappsubmission.databinding.ActivityDetailStoryBinding
import com.alicea.storyappsubmission.utils.DateFormatter
import com.bumptech.glide.Glide
import java.util.*

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupData()
    }
    private fun setupView() {
        supportActionBar?.title = getString(R.string.detail_story)
    }
    @Suppress("DEPRECATION")
    private fun setupData() {
        val story = intent.getParcelableExtra<StoryEntity>("Story") as StoryEntity
        Glide.with(applicationContext)
            .load(story.photoUrl)
            .into(binding.ivDetailPhoto)
        binding.tvDetailName.text = story.name
        binding.tvDetailDescription.text = story.description
        binding.tvDetailCreated.text = DateFormatter.formatDate(story.createdAt, TimeZone.getDefault().id)
    }

}
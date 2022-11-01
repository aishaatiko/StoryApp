package com.alicea.storyappsubmission.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alicea.storyappsubmission.R
import com.alicea.storyappsubmission.databinding.ActivityDetailStoryBinding
import com.alicea.storyappsubmission.model.StoryModel
import com.bumptech.glide.Glide

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
    private fun setupData() {
        val story = intent.getParcelableExtra<StoryModel>("Story") as StoryModel
        Glide.with(applicationContext)
            .load(story.photo)
            .into(binding.ivDetailPhoto)
        binding.tvDetailName.text = story.userName
        binding.tvDetailDescription.text = story.description
        binding.tvDetailCreated.text = story.createdAt.substringBefore("T")
    }

}
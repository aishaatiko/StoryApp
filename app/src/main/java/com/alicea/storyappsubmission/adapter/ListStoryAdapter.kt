package com.alicea.storyappsubmission.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.alicea.storyappsubmission.databinding.ItemRowStoryBinding
import com.alicea.storyappsubmission.model.StoryModel
import com.alicea.storyappsubmission.view.detail.DetailStoryActivity
import com.bumptech.glide.Glide

class ListStoryAdapter(private val listStory: ArrayList<StoryModel>) : RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {
    inner class ListViewHolder(binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        private var imgPhoto = binding.ivItemPhoto
        private var tvName = binding.tvItemName
        private var imgProfile = binding.ivItemProfile

        fun bind(story: StoryModel) {
            Glide.with(itemView.context)
                .load(story.photo)
                .into(imgPhoto)
            tvName.text = story.userName

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra("Story", story)
                val optionCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(imgPhoto, "photo"),
                    Pair(tvName, "name"),
                    Pair(imgProfile, "profile")
                )
                itemView.context.startActivity(intent, optionCompat.toBundle())
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemBinding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ListViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int = listStory.size
}
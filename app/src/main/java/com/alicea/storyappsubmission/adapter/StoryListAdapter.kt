package com.alicea.storyappsubmission.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alicea.storyappsubmission.R
import com.alicea.storyappsubmission.data.local.entity.StoryEntity
import com.alicea.storyappsubmission.databinding.ItemRowStoryBinding
import com.alicea.storyappsubmission.ui.detail.DetailStoryActivity
import com.alicea.storyappsubmission.utils.DateFormatter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*

class StoryListAdapter :
    PagingDataAdapter<StoryEntity, StoryListAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryEntity) {
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .apply(
                    RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error)
                )
                .into(binding.ivItemPhoto)
            binding.tvItemName.text = story.name
            binding.tvItemTime.text = DateFormatter.formatTime(story.createdAt, TimeZone.getDefault().id)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra("Story", story)
                val optionCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(binding.ivItemPhoto, "photo"),
                    Pair(binding.tvItemName, "name"),
                    Pair(binding.ivItemProfile, "profile")
                )
                itemView.context.startActivity(intent, optionCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
package com.unipi.mobdev.unipiaudiostories.classes

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unipi.mobdev.unipiaudiostories.R

class StoryAdapter(
    private val stories: List<Story>,
    private val onStoryClick: (Story) -> Unit
) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.story_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount() = stories.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storyTitle: TextView = itemView.findViewById(R.id.story_title)
        val storyAuthor: TextView = itemView.findViewById(R.id.story_author)
        val storyImage: ImageView = itemView.findViewById(R.id.story_image_preview)

        fun bind(story: Story) {
            storyTitle.text = story.title
            storyAuthor.text = "by ${story.author}"

            try {
                Glide.with(itemView)
                    .load(story.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(storyImage)
            } catch (e: Exception) {
                Log.e("StoryAdapter", "Error loading image: ${story.imageUrl}")
                e.printStackTrace()
            }


            itemView.setOnClickListener {
                onStoryClick(story)
            }
        }
    }
}
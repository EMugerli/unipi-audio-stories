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

/**
 * Adapter for the RecyclerView in the StatisticsActivity.
 * This adapter displays a list of stories sorted by total views count.
 * The list is displayed using a RecyclerView and StatisticsAdapter.
 */
class StatisticsAdapter (
private val stories: List<Story>,
private val onStoryClick: (Story) -> Unit
) : RecyclerView.Adapter<StatisticsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.statistics_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount() = stories.size

    /**
     * ViewHolder for the StatisticsAdapter.
     * This class holds the views for each item in the RecyclerView.
     * It also binds the data to the views.
     * @param itemView The view for the item in the RecyclerView.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storyTitle: TextView = itemView.findViewById(R.id.story_title)
        val storyAuthor: TextView = itemView.findViewById(R.id.story_author)
        val storyTotalViews: TextView = itemView.findViewById(R.id.story_views)
        val storyImage: ImageView = itemView.findViewById(R.id.story_image_preview)
        val storySortValue: TextView = itemView.findViewById(R.id.story_sort_order)

        fun bind(story: Story) {
            storyTitle.text = story.title
            storyAuthor.text = itemView.context.getString(R.string.adapter_author_placeholder, story.author)
            storyTotalViews.text =
                itemView.context.getString(R.string.adapter_total_views_placeholder, story.totalViews.toString())
            storySortValue.text = story.sortOrder.toString()

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
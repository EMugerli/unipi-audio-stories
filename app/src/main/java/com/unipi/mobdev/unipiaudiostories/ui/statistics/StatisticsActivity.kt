package com.unipi.mobdev.unipiaudiostories.ui.statistics

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.unipi.mobdev.unipiaudiostories.R
import com.unipi.mobdev.unipiaudiostories.classes.LanguageHelper
import com.unipi.mobdev.unipiaudiostories.classes.StatisticsAdapter
import com.unipi.mobdev.unipiaudiostories.classes.Story
import com.unipi.mobdev.unipiaudiostories.classes.StoryAdapter
import com.unipi.mobdev.unipiaudiostories.databinding.ActivityStatisticsBinding
import com.unipi.mobdev.unipiaudiostories.databinding.FragmentStoryListBinding
import com.unipi.mobdev.unipiaudiostories.ui.story_list.StoryListFragmentDirections

/**
 * Activity for displaying statistics about stories.
 * This activity displays a list of stories sorted by total views count.
 * The total views count is stored in SharedPreferences.
 * The stories are fetched from Firestore.
 * The list is displayed using a RecyclerView and StatisticsAdapter.
 */
class StatisticsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StatisticsAdapter
    private val storyList = mutableListOf<Story>()

    private lateinit var progressBar: ProgressBar
    private lateinit var binding: ActivityStatisticsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.applySavedLocale(this)

        // Inflate the layout using View Binding
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize UI elements
        recyclerView = binding.storyList
        progressBar = binding.storyListProgressBar

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StatisticsAdapter(storyList) { story ->
            // Handle item click (if needed, e.g., navigate to detail)
            Log.d("StatisticsActivity", "Clicked story: ${story.title}")
        }
        recyclerView.adapter = adapter

        // Fetch stories from Firestore
        fetchStoriesFromFirebase()
    }

    /**
     * Fetch stories from Firestore.
     * For each story, get the total views count from SharedPreferences.
     * Sort stories by total views count.
     */
    private fun fetchStoriesFromFirebase() {
        val db = Firebase.firestore
        db.collection("stories")
            .get()
            .addOnSuccessListener { snapshot ->
                storyList.clear()
                for (document in snapshot.documents) {
                    val story = document.toObject(Story::class.java)
                    if (story != null) {
                        story.id = document.reference.id
                        // Get view count for each story
                        story.totalViews = getViewCount(story.id)

                        storyList.add(story)
                        Log.d("StoryListFragment", "Fetched story: ${story.title}")
                    }
                }

                // Sort stories by total views count
                storyList.sortByDescending { it.totalViews }

                for (story in storyList) {
                    story.sortOrder = storyList.indexOf(story) + 1
                }

                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                Log.e("StoryListFragment", "Error fetching stories", e)
            }
    }

    /**
     * Get the total views count for a story from SharedPreferences.
     */
    fun getViewCount(storyId: String): Int {
        Log.d("StatisticsAdapter", "Getting view count for story: $storyId")
        val sharedPreferences = getSharedPreferences("StoryStats", Context.MODE_PRIVATE)
        return sharedPreferences.getInt(storyId, 0)
    }

    /**
     * Attach the base context with the saved language code.
     */
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LanguageHelper.setLocale(it, getSavedLanguageCode(it)) })
    }

    /**
     * Get the saved language code from SharedPreferences.
     */
    private fun getSavedLanguageCode(context: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString("language_preference", "en") ?: "en"
    }
}
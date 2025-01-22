package com.unipi.mobdev.unipiaudiostories.ui.story_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.unipi.mobdev.unipiaudiostories.databinding.FragmentStoryListBinding
import com.unipi.mobdev.unipiaudiostories.classes.Story
import com.unipi.mobdev.unipiaudiostories.classes.StoryAdapter

/**
 * A Fragment representing a list of Pings. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link StoryDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */

class StoryListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StoryAdapter
    private val storyList = mutableListOf<Story>()

    private lateinit var progressBar: ProgressBar

    private var _binding: FragmentStoryListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentStoryListBinding.inflate(inflater, container, false)
        recyclerView = binding.storyList
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = StoryAdapter(storyList) { story ->
            val action = StoryListFragmentDirections.showStoryDetail(story.id)
            requireView().findNavController().navigate(action)
        }

        recyclerView.adapter = adapter
        progressBar = binding.storyListProgressBar!!

        return binding.root

    }

    override fun onResume() {
        super.onResume()
        fetchStoriesFromFirebase()
    }

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
                        storyList.add(story)
                        Log.d("StoryListFragment", "Fetched story: ${story.title}")
                    }
                }
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                Log.e("StoryListFragment", "Error fetching stories", e)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
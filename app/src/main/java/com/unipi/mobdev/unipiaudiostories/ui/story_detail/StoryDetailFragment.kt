package com.unipi.mobdev.unipiaudiostories.ui.story_detail

import android.content.Context.MODE_PRIVATE
import android.graphics.Typeface
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.text.clearSpans
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.mobdev.unipiaudiostories.R
import com.unipi.mobdev.unipiaudiostories.classes.Story
import com.unipi.mobdev.unipiaudiostories.databinding.FragmentStoryDetailBinding
import java.util.Locale

/**
 * A fragment representing a single Story detail screen.
 * This fragment is either contained in a [StoryListActivity]
 * in two-pane mode (on tablets) or a [StoryDetailHostActivity]
 * on handsets.
 */
class StoryDetailFragment : Fragment() {

    private var story: Story? = null

    private var storyTitleTextView: TextView? = null
    private var storyAuthorTextView: TextView? = null
    private var storyTextTextView: TextView? = null
    private var storyYearTextView: TextView? = null
    private var storyImageImageView: ImageView? = null
    private var controlsLayout: LinearLayout? = null
    private var storyCard: CardView? = null
    private var storyBanner: LinearLayout? = null
    private var playButton: Button? = null
    private var stopButton: Button? = null

    private var tts: TextToSpeech? = null
    private var isSpeaking = false
    private var currentStory: Story? = null

    private var _binding: FragmentStoryDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentStoryDetailBinding.inflate(inflater, container, false)
        val rootView = binding.root

        storyTitleTextView = binding.storyTitle
        storyAuthorTextView = binding.storyAuthor
        storyTextTextView = binding.storyText
        storyYearTextView = binding.storyYear
        storyImageImageView = binding.storyImage
        controlsLayout = binding.controlsLayout
        storyCard = binding.storyCard
        storyBanner = binding.storyBanner
        playButton = binding.playButton
        stopButton = binding.stopButton

        setupTextToSpeech()
        setupControls()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                //story = PlaceholderContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
                loadStoryDetails(it.getString(ARG_ITEM_ID)!!) { story ->
                    if (story != null) {
                        currentStory = story
                        updateContent(story)
                    } else {
                        Toast.makeText(context, "Failed to load story details.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Update the content of the fragment with the story details.
     */
    private fun updateContent(story: Story) {
        story.let {
            storyTitleTextView?.text = it.title
            storyAuthorTextView?.text = it.author
            storyTextTextView?.text = it.text
            storyYearTextView?.text = it.year.toString()
            try {
                Glide.with(this)
                    .load(it.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(storyImageImageView!!)
            } catch (e: Exception) {
                Log.e("StoryDetailFragment", "Failed to load image: ${e.message}")
            }
        }
    }

    /**
     * Load story details from Firestore.
     * @param storyId The ID of the story to load.
     * @param callback The callback function to return the story.
     */
    private fun loadStoryDetails(storyId: String, callback: (Story?) -> Unit) {
        showLoading(true)
        try {
            val db = FirebaseFirestore.getInstance()
            db.collection("stories").document(storyId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val story = document.toObject(Story::class.java)
                        story?.id = document.id
                        showLoading(false)
                        callback(story) // Return the story through the callback
                    } else {
                        Log.e("StoryDetailFragment", "No such document exists!")
                        showLoading(false)
                        callback(null) // Return null if no document is found
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("StoryDetailFragment", "Failed to load story: ${e.message}")
                    showLoading(false)
                    callback(null) // Return null if there's an error
                }
        } catch (e: Exception) {
            Log.e("Neki", e.message!!)
        }
    }

    /**
     * Setup the TextToSpeech engine.
     * Initialize the TextToSpeech engine and set the language to US English.
     * Keep track of the speaking state to highlight the words being spoken.
     */
    private fun setupTextToSpeech() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US

                // Setup up the progress listener to highlight the words

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {

                    }

                    override fun onDone(utteranceId: String?) {
                        isSpeaking = false
                    }

                    override fun onError(utteranceId: String?) {
                        Log.e("StoryDetailFragment", "TTS Error on utterance: $utteranceId")
                    }

                    override fun onRangeStart(
                        utteranceId: String?,
                        start: Int,
                        end: Int,
                        frame: Int
                    ) {
                        super.onRangeStart(utteranceId, start, end, frame)
                        activity?.runOnUiThread {
                            highlightText(start, end)
                        }
                    }

                })
            } else {
                Log.e("StoryDetailFragment", "TTS Initialization failed!")
            }
        }
    }

    private fun setupControls() {
        binding.playButton?.setOnClickListener {
            currentStory?.let { story ->
                if (!isSpeaking) {
                    incrementStoryViewCount(currentStory!!.id)
                    tts?.speak(story.text, TextToSpeech.QUEUE_FLUSH, null, "StoryTTS")
                    isSpeaking = true
                }
            }
        }

        binding.stopButton?.setOnClickListener {
            if (isSpeaking) {
                val spannable = SpannableString(binding.storyText?.text)
                spannable.clearSpans()

                binding.storyText?.text = spannable

                tts?.stop() // Pause by stopping playback
                isSpeaking = false
            }
        }
    }

    /**
     * Highlight the text being spoken by the TextToSpeech engine.
     */
    private fun highlightText(start: Int, end: Int) {
        val spannable = SpannableString(binding.storyText?.text)
        val purple200 = resources.getColor(R.color.purple_200, null);
        spannable.setSpan(
            ForegroundColorSpan(purple200),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.storyText?.text = spannable

        //
        binding.storyText?.post {
            val layout = binding.storyText?.layout
            if (layout != null) {
                val scrollY = layout.getLineTop(layout.getLineForOffset(start))
                binding.storyTextScrollView?.smoothScrollTo(0, scrollY, 2000)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.storyCard?.visibility = View.GONE
            binding.storyBanner?.visibility = View.GONE
            binding.controlsLayout?.visibility = View.GONE
            binding.storyDetailProgress?.visibility = View.VISIBLE
        } else {
            binding.storyCard?.visibility = View.VISIBLE
            binding.storyBanner?.visibility = View.VISIBLE
            binding.controlsLayout?.visibility = View.VISIBLE
            binding.storyDetailProgress?.visibility = View.GONE
        }

    }

    /**
     * Increment the view count for a story.
     * @param storyId The ID of the story to increment the view count for.
     */
    private fun incrementStoryViewCount(storyId: String) {
        if (this.activity == null) return
        Log.d("StoryDetailFragment", "Incrementing view count for story: $storyId")
        val sharedPreferences = this.requireActivity().getSharedPreferences("StoryStats", MODE_PRIVATE)
        val currentCount = sharedPreferences.getInt(storyId, 0)
        sharedPreferences.edit().putInt(storyId, currentCount + 1).apply()
    }

    companion object {
        const val ARG_ITEM_ID = "story_id"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.shutdown()
        _binding = null
    }
}
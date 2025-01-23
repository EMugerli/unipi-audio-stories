package com.unipi.mobdev.unipiaudiostories.classes

/**
 * Data class representing a story.
 * @property id The unique ID of the story.
 * @property title The title of the story.
 * @property author The author of the story.
 * @property year The year the story was published.
 * @property text The text of the story.
 * @property imageUrl The URL of the image associated with the story.
 * @property totalViews The total number of views the story has received.
 * @property sortOrder The order in which the story should be displayed.
 */
data class Story(
    var id: String = "",
    val title: String = "",
    val author: String = "",
    val year: Int = 0,
    val text: String = "",
    val imageUrl: String = "",
    var totalViews: Int = 0,
    var sortOrder: Int = 0
)

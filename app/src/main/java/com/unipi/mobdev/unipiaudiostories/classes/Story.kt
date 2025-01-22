package com.unipi.mobdev.unipiaudiostories.classes

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

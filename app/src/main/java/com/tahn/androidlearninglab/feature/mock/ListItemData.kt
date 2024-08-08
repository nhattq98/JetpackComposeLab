package com.tahn.androidlearninglab.feature.mock

data class ListItemData(
    val title: String,
    val description: String
)

val mockListData = listOf(
    ListItemData("Item 1", "Description for item 1"),
    ListItemData("Item 2", "Description for item 2"),
    ListItemData("Item 3", "Description for item 3"),
    ListItemData("Item 4", "Description for item 4"),
    ListItemData("Item 5", "Description for item 5"),
    ListItemData("Item 6", "Description for item 6"),
    ListItemData("Item 7", "Description for item 7"),
    ListItemData("Item 8", "Description for item 8"),
    ListItemData("Item 9", "Description for item 9"),
)
package com.example.matchmaker.models

data class User(
    val name: String = "",
    val imageUrl: String = "",
    val uid: String = "",
    val age: Long = 0L,
    val gender: String = "",
    val income: Long = 0L,
    val height: Long = 0L,
    val religion: String = "",
    val contact: String = "",
    val likedBy: ArrayList<String> = ArrayList()
)
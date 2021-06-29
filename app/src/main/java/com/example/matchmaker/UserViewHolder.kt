package com.example.matchmaker

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById(R.id.userName)
    val gender: TextView = itemView.findViewById(R.id.gender)
    val age: TextView = itemView.findViewById(R.id.age)
    val height: TextView = itemView.findViewById(R.id.height)
    val income: TextView = itemView.findViewById(R.id.income)
    val religion: TextView = itemView.findViewById(R.id.religion)
    val contact: TextView = itemView.findViewById(R.id.contact)
    val userImage: ImageView = itemView.findViewById(R.id.userImage)
    val likeCount: TextView = itemView.findViewById(R.id.likeCount)
    val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
}
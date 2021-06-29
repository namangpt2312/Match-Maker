package com.example.matchmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.matchmaker.models.User
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserAdapter(options: FirestoreRecyclerOptions<User>, val listener: IPostAdapter) : FirestoreRecyclerAdapter<User, UserViewHolder>(
    options
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val viewHolder =  UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false))
        viewHolder.likeButton.setOnClickListener{
            listener.onLikeClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
        holder.name.text = model.name
        Glide.with(holder.userImage.context).load(model.imageUrl).centerCrop().into(holder.userImage)
        holder.gender.text = "Gender: ${model.gender}"
        holder.age.text = "Age: ${model.age} years"
        holder.height.text = "Height: ${model.height} cm"
        holder.income.text = "Income: ₹${model.income}/month"
        holder.religion.text = "Religion: ${model.religion}"
        holder.contact.text = "Contact: ${model.contact}"
        holder.likeCount.text = model.likedBy.size.toString()

        val auth = Firebase.auth
        val currentUserId = auth.currentUser!!.uid
        val isLiked = model.likedBy.contains(currentUserId)
        if(isLiked) {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_liked))
        } else {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_unliked))
        }
    }
}

interface IPostAdapter {
    fun onLikeClicked(postId: String)
}
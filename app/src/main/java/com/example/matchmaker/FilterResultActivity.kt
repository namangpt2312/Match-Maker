package com.example.matchmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.matchmaker.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_filter_result.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.recyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

const val GENDER = "gender"
const val RELIGION = "religion"
const val CATEGORY = "category"
const val NUM = "num"
const val TYPE = "type"

class FilterResultActivity : AppCompatActivity(), IPostAdapter {

    private lateinit var adapter: UserAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_result)

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val gender = intent!!.getStringExtra(GENDER)
        val religion = intent!!.getStringExtra(RELIGION)
        val income = intent!!.getStringExtra(NUM)!!.toLong()
        var category = intent!!.getStringExtra(CATEGORY)
        val type = intent!!.getStringExtra(TYPE)

        if(category == "Age")
            category = "age"
        else if(category == "Income")
            category = "income"
        else
            category = "height"

        val userCollection = FirebaseFirestore.getInstance().collection("users")
        var query = userCollection
            .whereGreaterThanOrEqualTo(category!!, income!!.toInt())
            .orderBy(category, Query.Direction.ASCENDING)
            .orderBy("name", Query.Direction.ASCENDING)

        if(type == ">=") {
            if(gender != "Any" && religion != "Any") {
                query = userCollection
                    .whereEqualTo("gender", gender)
                    .whereEqualTo("religion", religion)
                    .whereGreaterThanOrEqualTo(category!!, income!!.toInt())
                    .orderBy(category, Query.Direction.ASCENDING)
                    .orderBy("name", Query.Direction.ASCENDING)
            }
            else if(gender != "Any") {
                query = userCollection
                    .whereEqualTo("gender", gender)
                    .whereGreaterThanOrEqualTo(category!!, income!!.toInt())
                    .orderBy(category, Query.Direction.ASCENDING)
                    .orderBy("name", Query.Direction.ASCENDING)
            }
            else if(religion != "Any") {
                query = userCollection
                    .whereEqualTo("religion", religion)
                    .whereGreaterThanOrEqualTo(category!!, income!!.toInt())
                    .orderBy(category, Query.Direction.ASCENDING)
                    .orderBy("name", Query.Direction.ASCENDING)
            }
        }
        else {
            query = userCollection
                .whereLessThanOrEqualTo(category!!, income!!.toInt())
                .orderBy(category, Query.Direction.DESCENDING)
                .orderBy("name", Query.Direction.ASCENDING)

            if(gender != "Any" && religion != "Any") {
                query = userCollection
                    .whereEqualTo("gender", gender)
                    .whereEqualTo("religion", religion)
                    .whereLessThanOrEqualTo(category!!, income!!.toInt())
                    .orderBy(category, Query.Direction.DESCENDING)
                    .orderBy("name", Query.Direction.ASCENDING)
            }
            else if(gender != "Any") {
                query = userCollection
                    .whereEqualTo("gender", gender)
                    .whereLessThanOrEqualTo(category!!, income!!.toInt())
                    .orderBy(category, Query.Direction.DESCENDING)
                    .orderBy("name", Query.Direction.ASCENDING)
            }
            else if(religion != "Any") {
                query = userCollection
                    .whereEqualTo("religion", religion)
                    .whereLessThanOrEqualTo(category!!, income!!.toInt())
                    .orderBy(category, Query.Direction.DESCENDING)
                    .orderBy("name", Query.Direction.ASCENDING)
            }
        }

        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java).build()

        adapter = UserAdapter(recyclerViewOptions, this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikeClicked(postId: String) {
        GlobalScope.launch {
            val currentUserId = Firebase.auth.currentUser!!.uid
            val post = FirebaseFirestore.getInstance().collection("users").document(postId).get().await().toObject(User::class.java)!!
            val isLiked = post.likedBy.contains(currentUserId)

            if(isLiked) {
                post.likedBy.remove(currentUserId)
            } else {
                post.likedBy.add(currentUserId)
            }

            FirebaseFirestore.getInstance().collection("users").document(postId).set(post)
        }
    }
}
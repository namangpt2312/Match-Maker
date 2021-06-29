package com.example.matchmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.matchmaker.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.activity_filter.recyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FilterActivity : AppCompatActivity(), IPostAdapter {

    private val genders = arrayListOf("Male", "Female", "Others", "Any")
    private val religions = arrayListOf("Hindu", "Muslim", "Sikh", "Christian", "Jain", "Parsi", "Buddhist", "Jewish", "Any")
    private val categories = arrayListOf("Age", "Income", "Height")
    private val queries = arrayListOf(">=", "<=")

    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        setUpSpinner()
        setUpRecyclerView()

        applyBtn.setOnClickListener {
            val gender = genderSpinner.selectedItem.toString()
            val religion = religionSpinner.selectedItem.toString()
            var category = categorySpinner.selectedItem.toString()
            val type = querySpinner.selectedItem.toString()
            val income = incomeEt.text.toString()

//            val intent = Intent(this, FilterResultActivity :: class.java)
//            intent.putExtra(GENDER, gender)
//            intent.putExtra(RELIGION, religion)
//            intent.putExtra(CATEGORY, category)
//            intent.putExtra(NUM, income)
//            intent.putExtra(TYPE, type)
//            startActivity(intent)

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

            adapter.updateOptions(recyclerViewOptions)
        }
    }

    private fun setUpRecyclerView() {
        val userCollection = FirebaseFirestore.getInstance().collection("users")
        val query = userCollection.orderBy("name", Query.Direction.ASCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java).build()

        adapter = UserAdapter(recyclerViewOptions, this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setUpSpinner() {
        val genderAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, genders)
        genderSpinner.adapter = genderAdapter

        val religionAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, religions)
        religionSpinner.adapter = religionAdapter

        val categoryAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, categories)
        categorySpinner.adapter = categoryAdapter

        val queryAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, queries)
        querySpinner.adapter = queryAdapter
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
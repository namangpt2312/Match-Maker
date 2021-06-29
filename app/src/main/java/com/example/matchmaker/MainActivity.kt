package com.example.matchmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.matchmaker.auth.SignUpActivity
import com.example.matchmaker.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity(), IPostAdapter {

    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val userCollection = FirebaseFirestore.getInstance().collection("users")
        val query = userCollection.orderBy("name", Query.Direction.ASCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java).build()

        adapter = UserAdapter(recyclerViewOptions, this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.filter -> {
                val intent = Intent(this, FilterActivity :: class.java)
//                intent.putExtra(NAME, user.name)
//                intent.putExtra(IMAGE, user.imageUrl)
//                intent.putExtra(UID, user.uid)
                startActivity(intent)
            }
            R.id.profile -> {
                startActivity(Intent(this, SignUpActivity :: class.java))
            }
            R.id.signOut -> {
                Firebase.auth.signOut()
                startActivity(
                    Intent(this, SplashActivity:: class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            }
        }

        return super.onOptionsItemSelected(item)
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
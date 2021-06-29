package com.example.matchmaker.auth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.matchmaker.MainActivity
import com.example.matchmaker.R
import com.example.matchmaker.models.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.genderSpinner
import kotlinx.android.synthetic.main.activity_sign_up.incomeEt
import kotlinx.android.synthetic.main.activity_sign_up.religionSpinner

class SignUpActivity : AppCompatActivity() {

    val storage by lazy {
        FirebaseStorage.getInstance()
    }

    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    val database by lazy {
        FirebaseFirestore.getInstance()
    }

    lateinit var downloadUrl : String

    private val genders = arrayListOf("Male", "Female", "Others", "Any")
    private val religions = arrayListOf("Hindu", "Muslim", "Sikh", "Christian", "Jain", "Parsi", "Buddhist", "Jewish", "Any")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setUpSpinner()

        userImgView.setOnClickListener {
            checkPermissionForImage()
        }

        nextBtn.setOnClickListener {
            val name = nameEt.text.toString()
//            val gender = genderEt.text.toString()
            val gender = genderSpinner.selectedItem.toString()
            val religion = religionSpinner.selectedItem.toString()
            val age = ageEt.text.toString().trim()
            val height = heightEt.text.toString().trim()
            val income = incomeEt.text.toString().trim()

            if(name.isEmpty() || gender.isEmpty() || age.isEmpty() || height.isEmpty() || income.isEmpty() || religion.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show()
            }
            else if(!::downloadUrl.isInitialized) {
                Toast.makeText(this, "Profile picture cannot be empty!", Toast.LENGTH_SHORT).show()
            }
            else {
                nextBtn.isEnabled = false
                val user = User(
                    name,
                    downloadUrl,
                    auth.uid!!,
                    age.toLong(),
                    gender,
                    income.toLong(),
                    height.toLong(),
                    religion,
                    auth.currentUser!!.phoneNumber!!
                )
                database.collection("users").document(auth.uid!!).set(user).addOnSuccessListener {
                    val intent = Intent(this, MainActivity:: class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    nextBtn.isEnabled = true
                }
            }
        }
    }

    private fun setUpSpinner() {
        val genderAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, genders)
        genderSpinner.adapter = genderAdapter

        val religionAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, religions)
        religionSpinner.adapter = religionAdapter
    }

    private fun checkPermissionForImage() {
        if((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED))
        {
            val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            val permissionWrite = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            requestPermissions(permission, 1001)
            requestPermissions(permissionWrite, 1002)
        }
        else
        {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == 1000) {
            data?.data?.let {
                userImgView.setImageURI(it)
                uploadImage(it)
            }
        }
    }

    private fun uploadImage(it: Uri) {
        nextBtn.isEnabled = false
        val ref = storage.reference.child("uploads/" + auth.uid.toString())
        val uploadTask = ref.putFile(it)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if(!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }

            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            nextBtn.isEnabled = true

            if(task.isSuccessful) {
                downloadUrl = task.result.toString()
            }
            else {

            }
        }.addOnFailureListener {

        }
    }
}
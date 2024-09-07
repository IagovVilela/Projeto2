package com.example.projeto2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase

class segundomain : AppCompatActivity() {

    private lateinit var selectImageButton: Button
    private lateinit var uploadImageButton: Button
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var logoutBtnButton: Button

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_uploadiamgem)

        // Inicialize o Firebase Storage
        storage = Firebase.storage

        selectImageButton = findViewById(R.id.select_image_button)
        uploadImageButton = findViewById(R.id.upload_image_button)
        imageView = findViewById(R.id.image_view)
        logoutBtnButton = findViewById(R.id.logout_btn)

        selectImageButton.setOnClickListener {
            openFileChooser()
        }

        uploadImageButton.setOnClickListener {
            imageUri?.let { uri ->
                uploadImageToFirebase(uri)
            } ?: run {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura o botão de logout para voltar à activity_main
        logoutBtnButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finaliza a activity atual para que o usuário não possa voltar para ela
        }
    }

    private fun openFileChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${fileUri.lastPathSegment}")

        val uploadTask = imageRef.putFile(fileUri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                Log.d("Firebase", "Image upload successful. Download URL: $uri")
                Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Image upload failed", exception)
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
        }
    }
}

package com.example.projeto2

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.OutputStream

class segundomain : AppCompatActivity() {

    private lateinit var selectImageButton: Button
    private lateinit var uploadImageButton: Button
    private lateinit var logoutBtnButton: Button
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    private lateinit var storage: FirebaseStorage

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val STORAGE_PERMISSION_CODE = 1001
        private const val REQUEST_SAVE_IMAGE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_uploadiamgem)

        // Inicialize o Firebase Storage
        storage = Firebase.storage

        selectImageButton = findViewById(R.id.select_image_button)
        uploadImageButton = findViewById(R.id.upload_image_button)
        logoutBtnButton = findViewById(R.id.logout_btn)
        imageView = findViewById(R.id.image_view)

        // Verificar permissões de armazenamento


        // Configurar o botão para selecionar a imagem
        selectImageButton.setOnClickListener {
            openFileChooser()
        }

        // Configurar o botão para fazer upload da imagem
        uploadImageButton.setOnClickListener {
            imageUri?.let { uri ->
                uploadImageToFirebase(uri)
            } ?: run {
                Toast.makeText(this, "Nenhuma imagem selecionada", Toast.LENGTH_SHORT).show()
            }
        }

        logoutBtnButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finaliza a activity atual para que o usuário não possa voltar para ela
        }

        // Configurar o botão para listar imagens


        // Ativar links clicáveis no TextView

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
        } else if (requestCode == REQUEST_SAVE_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val saveUri = data.data ?: return
            saveImageToUri(saveUri)
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${fileUri.lastPathSegment}")

        val uploadTask = imageRef.putFile(fileUri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                Log.d("Firebase", "Upload bem-sucedido. URL: $uri")
                Toast.makeText(this, "Upload bem-sucedido", Toast.LENGTH_SHORT).show()
                // Após o upload, permitir que o usuário escolha onde salvar a imagem
                downloadImageFromFirebase(uri.toString(), fileUri.lastPathSegment ?: "downloaded_image.jpg")
            }
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Falha no upload", exception)
            Toast.makeText(this, "Falha no upload", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadImageFromFirebase(downloadUrl: String, fileName: String) {
        // Abrir um diálogo para o usuário escolher onde salvar o arquivo
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/jpeg" // Ou o tipo de arquivo da imagem
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        startActivityForResult(intent, REQUEST_SAVE_IMAGE)
    }

    private fun saveImageToUri(uri: Uri) {
        val contentResolver = contentResolver
        val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${imageUri?.lastPathSegment}")

        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            outputStream?.use {
                it.write(bytes)
                it.flush()
                Toast.makeText(this, "Imagem salva com sucesso", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Falha no download", exception)
            Toast.makeText(this, "Falha ao salvar a imagem", Toast.LENGTH_SHORT).show()
        }
    }



    // Função para verificar a permissão de armazenamento
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão concedida", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
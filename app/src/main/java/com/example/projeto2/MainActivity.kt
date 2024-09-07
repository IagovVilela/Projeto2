package com.example.projeto2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {


    //---------------------------Váriaveis Globais
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Inicializa o Firebase
        FirebaseApp.initializeApp(this)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)
        loginButton = findViewById(R.id.loginButton)
        val intent = Intent( this, segundomain::class.java)
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            register(email, password)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            login(email, password)
            startActivity(intent)
        }

    }
    //------------------------------------------------------------Função para registrar novo usuario

    fun register(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro bem-sucedido
                    val user = auth.currentUser
                    Toast.makeText(this, "Registrado com sucesso: ${user?.email}", Toast.LENGTH_SHORT).show()
                } else {
                    // Falha no registro
                    Toast.makeText(this, "Erro ao registrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //----------------------------------------------------------------------------------------------

//--------------------------------------------------Login de usuários existentes

    fun login(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login bem-sucedido
                    val user = auth.currentUser
                    Toast.makeText(this, "Logado com sucesso: ${user?.email}",
                        Toast.LENGTH_SHORT).show()
                } else {
                    // Falha no login
                    Toast.makeText(this, "Erro ao logar: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
//--------------------------------------------------------------------------------------------------

}
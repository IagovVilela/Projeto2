package com.example.projeto2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider

class informacaoMain : AppCompatActivity() {

    private lateinit var emailTextView: TextView
    private lateinit var newEmailEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var statusTextView: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.informacao)

        // Inicialize o Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Encontre as views
        emailTextView = findViewById(R.id.text_view_email)
        newEmailEditText = findViewById(R.id.edit_text_new_email)
        newPasswordEditText = findViewById(R.id.edit_text_new_password)
        updateButton = findViewById(R.id.button_update)
        statusTextView = findViewById(R.id.text_view_status)

        // Definir o e-mail atual no TextView
        val currentUser = auth.currentUser
        currentUser?.let {
            emailTextView.text = "E-mail Atual: ${it.email}"
        }

        // Configurar o botão para atualizar o e-mail e a senha
        updateButton.setOnClickListener {
            val newEmail = newEmailEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()
            updateUserInfo(newEmail, newPassword)
        }
    }

    private fun updateUserInfo(newEmail: String, newPassword: String) {
        val user = auth.currentUser

        if (user != null) {
            // Obter o e-mail e a senha atuais do usuário
            val currentEmail = user.email
            val currentPassword = "senha_atual" // Substitua com a senha atual fornecida pelo usuário, se necessário

            if (currentEmail != null && currentPassword.isNotEmpty()) {
                val credential = EmailAuthProvider.getCredential(currentEmail, currentPassword)

                user.reauthenticate(credential).addOnCompleteListener { reauthenticateTask ->
                    if (reauthenticateTask.isSuccessful) {
                        // Atualizar o e-mail se fornecido
                        if (newEmail.isNotEmpty()) {
                            user.updateEmail(newEmail).addOnCompleteListener { emailUpdateTask ->
                                if (emailUpdateTask.isSuccessful) {
                                    statusTextView.text = "E-mail atualizado com sucesso"
                                    Toast.makeText(this, "E-mail atualizado com sucesso", Toast.LENGTH_SHORT).show()
                                } else {
                                    statusTextView.text = "Falha ao atualizar e-mail: ${emailUpdateTask.exception?.message}"
                                    Toast.makeText(this, "Falha ao atualizar e-mail: ${emailUpdateTask.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }

                        // Atualizar a senha se fornecida
                        if (newPassword.isNotEmpty()) {
                            user.updatePassword(newPassword).addOnCompleteListener { passwordUpdateTask ->
                                if (passwordUpdateTask.isSuccessful) {
                                    statusTextView.text = "Senha atualizada com sucesso"
                                    Toast.makeText(this, "Senha atualizada com sucesso", Toast.LENGTH_SHORT).show()
                                } else {
                                    statusTextView.text = "Falha ao atualizar senha: ${passwordUpdateTask.exception?.message}"
                                    Toast.makeText(this, "Falha ao atualizar senha: ${passwordUpdateTask.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }

                        // Verificar se nenhum dos campos foi preenchido
                        if (newEmail.isEmpty() && newPassword.isEmpty()) {
                            statusTextView.text = "Nenhuma informação para atualizar"
                            Toast.makeText(this, "Nenhuma informação para atualizar", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        statusTextView.text = "Falha na reautenticação: ${reauthenticateTask.exception?.message}"
                        Toast.makeText(this, "Falha na reautenticação: ${reauthenticateTask.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                statusTextView.text = "E-mail atual não encontrado"
                Toast.makeText(this, "E-mail atual não encontrado", Toast.LENGTH_SHORT).show()
            }
        } else {
            statusTextView.text = "Usuário não autenticado"
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
        }
    }
}
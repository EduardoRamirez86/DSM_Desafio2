package com.example.desafio_dsm2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth

        val edtEmailRegister: TextInputEditText = findViewById(R.id.edtEmailRegister)
        val edtPasswordRegister: TextInputEditText = findViewById(R.id.edtPasswordRegister)
        val edtConfirmPassword: TextInputEditText = findViewById(R.id.edtConfirmPassword)
        val btnRegisterFinal: Button = findViewById(R.id.btnRegisterFinal)
        val btnBackToLogin: Button = findViewById(R.id.btnBackToLogin)

        btnRegisterFinal.setOnClickListener {
            val email = edtEmailRegister.text.toString()
            val password = edtPasswordRegister.text.toString()
            val confirmPassword = edtConfirmPassword.text.toString()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lógica de registro con Firebase
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registro exitoso.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMessage = task.exception?.message
                        Toast.makeText(this, "Error en el registro: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        btnBackToLogin.setOnClickListener {
            finish() // Cierra la actividad de registro para volver a la de login
        }
    }
}
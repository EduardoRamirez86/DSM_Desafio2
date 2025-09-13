package com.example.desafio_dsm2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth

        // Verifica si el usuario ha iniciado sesión (no es nulo) y actualiza la UI
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // No hay usuario, ir a la pantalla de login
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        } else {
            // Usuario ya ha iniciado sesión, ir a la pantalla de la base de datos
            val intent = Intent(this, PersonasActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
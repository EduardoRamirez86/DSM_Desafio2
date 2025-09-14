package com.example.desafio_dsm2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        mostrarUsuario()

        val cardEstudiantes = findViewById<CardView>(R.id.cardEstudiantes)
        val cardNotas = findViewById<CardView>(R.id.cardNotas)
        val cardVerNotas = findViewById<CardView>(R.id.cardVerNotas)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        cardEstudiantes.setOnClickListener {
            val intent = Intent(this, EstudiantesActivity::class.java)
            startActivity(intent)
        }

        cardNotas.setOnClickListener {
            val intent = Intent(this, NotasActivity::class.java)
            startActivity(intent)
        }

        cardVerNotas.setOnClickListener {
            val intent = Intent(this, ListaNotasActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun mostrarUsuario() {
        val user = auth.currentUser
        val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)
        if (user != null) {
            tvUserEmail.text = "Usuario: ${user.email}"
        } else {
            tvUserEmail.text = "Usuario: Anónimo"
        }
    }
}
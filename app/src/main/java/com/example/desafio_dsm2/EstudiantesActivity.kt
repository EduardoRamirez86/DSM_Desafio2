package com.example.desafio_dsm2

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.desafio_dsm2.datos.Estudiante
import com.example.desafio_dsm2.datos.EstudianteAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class EstudiantesActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var refEstudiantes: DatabaseReference
    private lateinit var listaEstudiantes: ListView
    private lateinit var fabAgregarEstudiante: FloatingActionButton

    private var estudiantes: MutableList<Estudiante> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estudiantes)

        auth = Firebase.auth
        refEstudiantes = FirebaseDatabase.getInstance().getReference("estudiantes")

        try {
            listaEstudiantes = findViewById(R.id.ListaEstudiantes)
            fabAgregarEstudiante = findViewById(R.id.fab_agregar_estudiante)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: Vistas no encontradas. Verifique su XML.", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            return
        }

        configurarListeners()
        cargarEstudiantes()
    }

    private fun cargarEstudiantes() {
        refEstudiantes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                estudiantes.clear()
                for (dato in dataSnapshot.children) {
                    try {
                        val estudiante: Estudiante? = dato.getValue(Estudiante::class.java)
                        estudiante?.key = dato.key
                        if (estudiante != null) {
                            estudiantes.add(estudiante)
                        }
                    } catch (e: com.google.firebase.database.DatabaseException) {
                        e.printStackTrace()
                    }
                }
                val adapter = EstudianteAdapter(this@EstudiantesActivity, estudiantes)
                listaEstudiantes.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@EstudiantesActivity, "Error al cargar datos.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun configurarListeners() {
        fabAgregarEstudiante.setOnClickListener {
            val intent = Intent(this, AddEstudianteActivity::class.java)
            startActivity(intent)
        }
    }
}
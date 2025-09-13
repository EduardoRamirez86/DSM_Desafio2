package com.example.desafio_dsm2

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.desafio_dsm2.datos.Estudiante
import com.example.desafio_dsm2.datos.Nota
import com.google.firebase.database.FirebaseDatabase

class NotasActivity : AppCompatActivity() {

    private lateinit var spinnerEstudiante: Spinner
    private lateinit var spinnerGrado: Spinner
    private lateinit var spinnerMateria: Spinner
    private lateinit var edtNotaFinal: EditText

    private var estudiantes: MutableList<Estudiante> = mutableListOf()
    private val refGrados = FirebaseDatabase.getInstance().getReference("grados")
    private val refMaterias = FirebaseDatabase.getInstance().getReference("materias")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        spinnerEstudiante = findViewById(R.id.spinnerEstudiante)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        spinnerMateria = findViewById(R.id.spinnerMateria)
        edtNotaFinal = findViewById(R.id.edtNotaFinal)

        cargarEstudiantes()
        cargarGrados()
        cargarMaterias()
    }

    private fun cargarEstudiantes() {
        EstudiantesActivity.refEstudiantes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                estudiantes.clear()
                val nombresEstudiantes = mutableListOf<String>()
                for (dato in dataSnapshot.children) {
                    val estudiante: Estudiante? = dato.getValue(Estudiante::class.java)
                    estudiante?.key = dato.key
                    if (estudiante != null) {
                        estudiantes.add(estudiante)
                        nombresEstudiantes.add(estudiante.nombreCompleto ?: "Sin Nombre")
                    }
                }
                val adapter = ArrayAdapter(this@NotasActivity, android.R.layout.simple_spinner_item, nombresEstudiantes)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerEstudiante.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@NotasActivity, "Error al cargar estudiantes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarGrados() {
        refGrados.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val grados = mutableListOf<String>()
                for (dato in dataSnapshot.children) {
                    grados.add(dato.getValue(String::class.java) ?: "Sin Grado")
                }
                val adapter = ArrayAdapter(this@NotasActivity, android.R.layout.simple_spinner_item, grados)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerGrado.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@NotasActivity, "Error al cargar grados", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarMaterias() {
        refMaterias.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val materias = mutableListOf<String>()
                for (dato in dataSnapshot.children) {
                    materias.add(dato.getValue(String::class.java) ?: "Sin Materia")
                }
                val adapter = ArrayAdapter(this@NotasActivity, android.R.layout.simple_spinner_item, materias)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerMateria.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@NotasActivity, "Error al cargar materias", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun guardarNota(view: View) {
        val estudianteSeleccionado = spinnerEstudiante.selectedItem.toString()
        val gradoSeleccionado = spinnerGrado.selectedItem.toString()
        val materiaSeleccionada = spinnerMateria.selectedItem.toString()
        val notaStr = edtNotaFinal.text.toString()

        if (estudianteSeleccionado.isEmpty() || notaStr.isEmpty() || gradoSeleccionado.isEmpty() || materiaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val notaFinal = notaStr.toDoubleOrNull()
        if (notaFinal == null || notaFinal < 0 || notaFinal > 10) {
            Toast.makeText(this, "La nota debe ser un número entre 0 y 10", Toast.LENGTH_SHORT).show()
            return
        }

        val estudianteKey = estudiantes.find { it.nombreCompleto == estudianteSeleccionado }?.key
        if (estudianteKey != null) {
            val nota = Nota(estudianteKey, gradoSeleccionado, materiaSeleccionada, notaFinal)
            val newKey = EstudiantesActivity.refNotas.push().key
            if (newKey != null) {
                EstudiantesActivity.refNotas.child(newKey).setValue(nota)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Nota guardada con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al guardar la nota", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "No se encontró el estudiante", Toast.LENGTH_SHORT).show()
        }
    }
}
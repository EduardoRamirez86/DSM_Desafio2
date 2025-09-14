package com.example.desafio_dsm2

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.desafio_dsm2.datos.Estudiante
import com.example.desafio_dsm2.datos.Nota
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class NotasActivity : AppCompatActivity() {

    private lateinit var spinnerEstudiante: Spinner
    private lateinit var spinnerGrado: Spinner
    private lateinit var spinnerMateria: Spinner
    private lateinit var edtNotaFinal: TextInputEditText
    private lateinit var btnGuardarNota: Button

    // Listas para los Spinners
    private var listaEstudiantes: MutableList<Estudiante>? = null

    // Variables para el modo de edición
    private var notaKey: String? = null
    private var estudianteKey: String? = null

    // Referencias a la base de datos de Firebase
    private lateinit var refEstudiantes: DatabaseReference
    private lateinit var refNotas: DatabaseReference
    private lateinit var refGrados: DatabaseReference
    private lateinit var refMaterias: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        refEstudiantes = database.getReference("estudiantes")
        refNotas = database.getReference("notas")
        refGrados = database.getReference("grados")
        refMaterias = database.getReference("materias")

        // Obtener los datos del Intent para el modo de edición
        estudianteKey = intent.getStringExtra("estudiante_key")
        notaKey = intent.getStringExtra("nota_key")
        val notaValor = intent.getStringExtra("nota_valor")

        // Agregar para editar: recibir el grado y la materia
        val notaGrado = intent.getStringExtra("nota_grado")
        val notaMateria = intent.getStringExtra("nota_materia")

        inicializarVistas()

        // Si notaKey no es nulo, estamos en modo de edición
        if (notaKey != null) {
            title = "Editar Nota"
            edtNotaFinal.setText(notaValor)
            btnGuardarNota.setText("Actualizar Nota")
        } else {
            title = "Nueva Nota"
            btnGuardarNota.setText("Guardar Nota")
        }

        // Cargar datos en los spinners, incluyendo la selección para el modo de edición
        cargarDatosSpinners(notaGrado, notaMateria)
    }

    private fun inicializarVistas() {
        spinnerEstudiante = findViewById(R.id.spinnerEstudiante)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        spinnerMateria = findViewById(R.id.spinnerMateria)
        edtNotaFinal = findViewById(R.id.edtNotaFinal)
        btnGuardarNota = findViewById(R.id.btnGuardarNota)

        btnGuardarNota.setOnClickListener {
            guardarNota()
        }
    }

    private fun cargarDatosSpinners(selectedGrado: String?, selectedMateria: String?) {
        refEstudiantes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaEstudiantes = ArrayList()
                var selectedIndex = -1

                for (data in snapshot.children) {
                    val estudiante = data.getValue(Estudiante::class.java)
                    estudiante?.let {
                        it.key = data.key
                        listaEstudiantes!!.add(it)
                        // Si estamos en modo de edición, encuentra el índice del estudiante
                        if (estudianteKey != null && it.key == estudianteKey) {
                            selectedIndex = listaEstudiantes!!.size - 1
                        }
                    }
                }
                val adapterEstudiantes = ArrayAdapter(
                    this@NotasActivity,
                    android.R.layout.simple_spinner_item,
                    listaEstudiantes!!.map { it.nombreCompleto.toString() }
                )
                spinnerEstudiante.adapter = adapterEstudiantes
                if (selectedIndex != -1) {
                    spinnerEstudiante.setSelection(selectedIndex)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@NotasActivity, "Error al cargar estudiantes", Toast.LENGTH_SHORT).show()
            }
        })

        refGrados.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val grados = ArrayList<String>()
                for (data in snapshot.children) {
                    grados.add(data.getValue(String::class.java)!!)
                }
                val adapterGrados = ArrayAdapter(this@NotasActivity, android.R.layout.simple_spinner_item, grados)
                spinnerGrado.adapter = adapterGrados

                if (selectedGrado != null) {
                    val index = grados.indexOf(selectedGrado)
                    if (index != -1) {
                        spinnerGrado.setSelection(index)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@NotasActivity, "Error al cargar grados", Toast.LENGTH_SHORT).show()
            }
        })

        refMaterias.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val materias = ArrayList<String>()
                for (data in snapshot.children) {
                    materias.add(data.getValue(String::class.java)!!)
                }
                val adapterMaterias = ArrayAdapter(this@NotasActivity, android.R.layout.simple_spinner_item, materias)
                spinnerMateria.adapter = adapterMaterias

                if (selectedMateria != null) {
                    val index = materias.indexOf(selectedMateria)
                    if (index != -1) {
                        spinnerMateria.setSelection(index)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@NotasActivity, "Error al cargar materias", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarNota() {
        val selectedStudentPosition = spinnerEstudiante.selectedItemPosition
        val estudianteSeleccionado = listaEstudiantes?.get(selectedStudentPosition)
        val grado = spinnerGrado.selectedItem.toString()
        val materia = spinnerMateria.selectedItem.toString()
        val notaFinalStr = edtNotaFinal.text.toString()

        if (estudianteSeleccionado == null || grado.isEmpty() || materia.isEmpty() || notaFinalStr.isEmpty()) {
            Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val notaFinal = notaFinalStr.toDouble()

            if (notaFinal < 0 || notaFinal > 10) {
                Toast.makeText(this, "La nota debe estar entre 0 y 10", Toast.LENGTH_SHORT).show()
                return
            }

            if (notaKey != null) {
                // Modo de Edición: Actualizar la nota
                val notaActualizada = mapOf(
                    "estudianteKey" to estudianteSeleccionado.key,
                    "grado" to grado,
                    "materia" to materia,
                    "notaFinal" to notaFinal
                )
                refNotas.child(notaKey!!).updateChildren(notaActualizada)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Nota actualizada con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar la nota", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Modo de Agregar: Crear una nueva nota
                val nuevaNota = Nota(
                    estudianteKey = estudianteSeleccionado.key,
                    grado = grado,
                    materia = materia,
                    notaFinal = notaFinal
                )
                refNotas.push().setValue(nuevaNota)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Nota guardada con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al guardar la nota", Toast.LENGTH_SHORT).show()
                    }
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "La nota debe ser un número válido.", Toast.LENGTH_SHORT).show()
        }
    }
}
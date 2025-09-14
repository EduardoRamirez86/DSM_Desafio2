package com.example.desafio_dsm2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.desafio_dsm2.datos.Estudiante
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddEstudianteActivity : AppCompatActivity() {

    // Se cambió a TextInputEditText para que coincida con el XML
    private lateinit var etNombre: TextInputEditText
    private lateinit var etEdad: TextInputEditText
    private lateinit var etDireccion: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var btnGuardar: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var refEstudiantes: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_estudiante)

        // Inicializar las vistas con los IDs correctos del XML
        etNombre = findViewById(R.id.edtNombreCompleto)
        etEdad = findViewById(R.id.edtEdad)
        etDireccion = findViewById(R.id.edtDireccion)
        etTelefono = findViewById(R.id.edtTelefono)
        btnGuardar = findViewById(R.id.btnGuardar)

        // Inicializar la referencia a la base de datos
        database = FirebaseDatabase.getInstance()
        refEstudiantes = database.getReference("estudiantes")

        // Configurar el listener para el botón de guardar
        btnGuardar.setOnClickListener {
            guardarEstudiante()
        }
    }

    private fun guardarEstudiante() {
        // 1. Obtener los valores de los campos de texto
        val nombre = etNombre.text.toString().trim()
        val edadStr = etEdad.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()

        // 2. Validar que los campos no estén vacíos
        if (nombre.isEmpty() || edadStr.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val edad = edadStr.toInt()

            // 3. Crear un objeto Estudiante
            val estudiante = Estudiante(nombre, edad, direccion, telefono)

            // 4. Guardar el objeto en la base de datos
            val estudianteKey = refEstudiantes.push().key
            if (estudianteKey != null) {
                refEstudiantes.child(estudianteKey).setValue(estudiante)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Estudiante guardado con éxito.", Toast.LENGTH_SHORT).show()
                        finish() // Cierra la actividad para volver a la lista
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al guardar el estudiante: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "No se pudo generar una clave para el estudiante.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "La edad debe ser un número válido.", Toast.LENGTH_SHORT).show()
        }
    }
}
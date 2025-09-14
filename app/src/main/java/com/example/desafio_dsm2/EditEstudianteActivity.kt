package com.example.desafio_dsm2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditEstudianteActivity : AppCompatActivity() {

    private lateinit var estudianteId: String
    private lateinit var etNombre: TextInputEditText
    private lateinit var etEdad: TextInputEditText
    private lateinit var etDireccion: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var btnGuardarCambios: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var refEstudiantes: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_estudiante)

        // Inicializar las vistas
        etNombre = findViewById(R.id.edtNombre)
        etEdad = findViewById(R.id.edtEdad)
        etDireccion = findViewById(R.id.edtDireccion)
        etTelefono = findViewById(R.id.edtTelefono)
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios)

        // Inicializar la referencia a la base de datos
        database = FirebaseDatabase.getInstance()
        refEstudiantes = database.getReference("estudiantes")

        // Recuperar los datos del Intent
        val extras = intent.extras
        if (extras != null) {
            estudianteId = extras.getString("id") ?: ""
            val nombre = extras.getString("nombre") ?: ""
            val edad = extras.getInt("edad", 0)
            val direccion = extras.getString("direccion") ?: ""
            val telefono = extras.getString("telefono") ?: ""

            // Llenar los campos de texto con los datos recuperados
            etNombre.setText(nombre)
            etEdad.setText(edad.toString())
            etDireccion.setText(direccion)
            etTelefono.setText(telefono)
        }

        // Configurar el listener para el botón de guardar
        btnGuardarCambios.setOnClickListener {
            guardarCambios()
        }
    }

    private fun guardarCambios() {
        val nombre = etNombre.text.toString().trim()
        val edadStr = etEdad.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()

        if (nombre.isEmpty() || edadStr.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val edad = edadStr.toInt()

            // Actualizar solo los campos que necesitas
            val estudianteMap = mapOf(
                "nombreCompleto" to nombre,
                "edad" to edad,
                "direccion" to direccion,
                "telefono" to telefono
            )

            // Usar la clave del estudiante para actualizarlo en la base de datos
            refEstudiantes.child(estudianteId).updateChildren(estudianteMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Cambios guardados con éxito.", Toast.LENGTH_SHORT).show()
                    finish() // Cierra la actividad para volver a la lista
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al guardar los cambios: ${it.message}", Toast.LENGTH_SHORT).show()
                }

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "La edad debe ser un número válido.", Toast.LENGTH_SHORT).show()
        }
    }
}
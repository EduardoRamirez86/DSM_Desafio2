// Archivo: app/src/main/java/com/example/desafio_dsm2/EditEstudianteActivity.kt
package com.example.desafio_dsm2

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditEstudianteActivity : AppCompatActivity() {

    private lateinit var refEstudiantes: DatabaseReference

    private lateinit var edtNombre: TextInputEditText
    private lateinit var edtEdad: TextInputEditText
    private lateinit var edtDireccion: TextInputEditText
    private lateinit var edtTelefono: TextInputEditText
    private lateinit var btnGuardarCambios: Button

    private var estudianteKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_estudiante)

        refEstudiantes = FirebaseDatabase.getInstance().getReference("estudiantes")

        edtNombre = findViewById(R.id.edtNombre)
        edtEdad = findViewById(R.id.edtEdad)
        edtDireccion = findViewById(R.id.edtDireccion)
        edtTelefono = findViewById(R.id.edtTelefono)
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios)

        // Obtener los datos del Intent
        estudianteKey = intent.getStringExtra("estudiante_key")
        edtNombre.setText(intent.getStringExtra("estudiante_nombre"))
        edtEdad.setText(intent.getStringExtra("estudiante_edad"))
        edtDireccion.setText(intent.getStringExtra("estudiante_direccion"))
        edtTelefono.setText(intent.getStringExtra("estudiante_telefono"))

        btnGuardarCambios.setOnClickListener {
            guardarCambios()
        }
    }

    private fun guardarCambios() {
        val nombre = edtNombre.text.toString().trim()
        val edadStr = edtEdad.text.toString().trim()
        val direccion = edtDireccion.text.toString().trim()
        val telefono = edtTelefono.text.toString().trim()

        if (nombre.isEmpty() || edadStr.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        estudianteKey?.let { key ->
            try {
                // Intenta convertir la edad a un número.
                val edad = edadStr.toInt()

                val estudianteActualizado = mapOf(
                    "nombreCompleto" to nombre,
                    "edad" to edad,
                    "direccion" to direccion,
                    "telefono" to telefono
                )

                refEstudiantes.child(key).updateChildren(estudianteActualizado)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Estudiante actualizado con éxito.", Toast.LENGTH_SHORT).show()
                        finish() // Cierra la actividad para volver a la lista
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar estudiante.", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: NumberFormatException) {
                // Si la conversión falla, se muestra un mensaje de error.
                Toast.makeText(this, "La edad debe ser un número válido.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
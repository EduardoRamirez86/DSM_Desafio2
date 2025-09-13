// Archivo: app/src/main/java/com/example/desafio_dsm2/AddEstudianteActivity.kt
package com.example.desafio_dsm2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.desafio_dsm2.datos.Estudiante

class AddEstudianteActivity : AppCompatActivity() {
    private var edtNombreCompleto: EditText? = null
    private var edtEdad: EditText? = null
    private var edtDireccion: EditText? = null
    private var edtTelefono: EditText? = null
    private var key: String = ""
    private var accion: String = ""
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_estudiante)
        inicializar()
    }

    private fun inicializar() {
        database = FirebaseDatabase.getInstance().getReference("estudiantes")
        edtNombreCompleto = findViewById(R.id.edtNombreCompleto)
        edtEdad = findViewById(R.id.edtEdad)
        edtDireccion = findViewById(R.id.edtDireccion)
        edtTelefono = findViewById(R.id.edtTelefono)

        val intent = intent
        accion = intent.getStringExtra("accion").toString()
        if (accion == "e") {
            val estudiante = intent.getSerializableExtra("estudiante") as Estudiante
            edtNombreCompleto?.setText(estudiante.nombreCompleto)
            edtEdad?.setText(estudiante.edad.toString())
            edtDireccion?.setText(estudiante.direccion)
            edtTelefono?.setText(estudiante.telefono)
            key = estudiante.key.toString()
        }
    }

    fun guardar(v: View?) {
        val nombreCompleto = edtNombreCompleto?.text.toString()
        val edadStr = edtEdad?.text.toString()
        val direccion = edtDireccion?.text.toString()
        val telefono = edtTelefono?.text.toString()

        if (nombreCompleto.isEmpty() || edadStr.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val edad = edadStr.toIntOrNull() ?: 0
        val estudiante = Estudiante(nombreCompleto, edad, direccion, telefono)

        if (accion == "a") {
            val newKey = database.push().key
            if (newKey != null) {
                database.child(newKey).setValue(estudiante)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Estudiante guardado con éxito", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al guardar estudiante", Toast.LENGTH_SHORT).show()
                    }
            }
        } else if (accion == "e") {
            if (key.isNotEmpty()) {
                val estudianteValues = mapOf(
                    "nombreCompleto" to estudiante.nombreCompleto,
                    "edad" to estudiante.edad,
                    "direccion" to estudiante.direccion,
                    "telefono" to estudiante.telefono
                )
                database.child(key).updateChildren(estudianteValues)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Estudiante actualizado con éxito", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar estudiante", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        finish()
    }

    fun cancelar(v: View?) {
        finish()
    }
}
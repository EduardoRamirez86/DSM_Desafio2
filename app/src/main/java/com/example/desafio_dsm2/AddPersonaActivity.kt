package com.example.desafio_dsm2

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.desafio_dsm2.datos.Persona
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.desafio_dsm2.R

class AddPersonaActivity : AppCompatActivity() {
    private var edtDUI: EditText? = null
    private var edtNombre: EditText? = null
    private var edtApellido: EditText? = null
    private var edtTelefono: EditText? = null
    private var edtEdad: EditText? = null
    private var edtDireccion: EditText? = null
    private var key: String = ""
    private var accion: String = ""
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_persona)
        inicializar()
    }

    private fun inicializar() {
        edtNombre = findViewById(R.id.edtNombre)
        edtDUI = findViewById(R.id.edtDUI)
        edtApellido = findViewById(R.id.edtApellido)
        edtTelefono = findViewById(R.id.edtTelefono)
        edtEdad = findViewById(R.id.edtEdad)
        edtDireccion = findViewById(R.id.edtDireccion)

        database = FirebaseDatabase.getInstance().getReference("personas")

        val extras = intent.extras
        if (extras != null) {
            accion = extras.getString("accion").toString()
            if (accion == "e") {
                val persona = extras.getSerializable("persona") as Persona
                edtNombre!!.setText(persona.nombre)
                edtDUI!!.setText(persona.dui)
                edtApellido!!.setText(persona.apellido)
                edtTelefono!!.setText(persona.telefono)
                edtEdad!!.setText(persona.edad.toString())
                edtDireccion!!.setText(persona.direccion)
                key = persona.key.toString()
            }
        }
    }

    fun guardar(v: View?) {
        val nombre = edtNombre?.text.toString()
        val dui = edtDUI?.text.toString()
        val apellido = edtApellido?.text.toString()
        val telefono = edtTelefono?.text.toString()
        val edadStr = edtEdad?.text.toString()
        val direccion = edtDireccion?.text.toString()

        if (nombre.isEmpty() || dui.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || edadStr.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val edad = edadStr.toIntOrNull() ?: 0

        val persona = Persona(dui, nombre, apellido, telefono, edad, direccion)

        if (accion == "a") {
            val newKey = database.push().key
            if (newKey != null) {
                database.child(newKey).setValue(persona).addOnSuccessListener {
                    Toast.makeText(this, "Se guardó con éxito", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No se pudo generar una clave", Toast.LENGTH_SHORT).show()
            }
        } else if (accion == "e") {
            if (key.isNotEmpty()) {
                val personaValues = persona.toMap()
                val childUpdates = hashMapOf<String, Any>(
                    key to personaValues
                )
                database.updateChildren(childUpdates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Se actualizó con éxito", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "No se encontró la clave del registro", Toast.LENGTH_SHORT).show()
            }
        }
        finish()
    }

    fun cancelar(v: View?) {
        finish()
    }
}
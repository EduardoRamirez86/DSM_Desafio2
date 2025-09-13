// Archivo: app/src/main/java/com/example/desafio_dsm2/EstudiantesActivity.kt
package com.example.desafio_dsm2

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.desafio_dsm2.datos.Estudiante

class EstudiantesActivity : AppCompatActivity() {

    private val consultaEstudiantes: Query = refEstudiantes.orderByChild("nombreCompleto")
    private var estudiantes: MutableList<Estudiante>? = null
    private var listaEstudiantes: ListView? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estudiantes)
        auth = FirebaseAuth.getInstance()
        inicializar()
    }

    private fun inicializar() {
        val fabAgregarEstudiante: FloatingActionButton = findViewById(R.id.fab_agregar_estudiante)
        val fabAgregarNota: FloatingActionButton = findViewById(R.id.fab_agregar_nota)
        listaEstudiantes = findViewById(R.id.ListaEstudiantes)

        listaEstudiantes!!.setOnItemClickListener { _, _, i, _ ->
            val intent = Intent(this, AddEstudianteActivity::class.java)
            intent.putExtra("accion", "e") // Editar
            val estudiante = estudiantes!![i]
            intent.putExtra("estudiante", estudiante as java.io.Serializable)
            startActivity(intent)
        }

        listaEstudiantes!!.setOnItemLongClickListener { _, _, i, _ ->
            val estudiante = estudiantes!![i]
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Eliminar Estudiante")
            alertDialogBuilder.setMessage("¿Estás seguro de que quieres eliminar a ${estudiante.nombreCompleto}?")
            alertDialogBuilder.setPositiveButton("Sí") { _: DialogInterface, _: Int ->
                if (estudiante.key != null) {
                    refEstudiantes.child(estudiante.key!!).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Estudiante eliminado con éxito", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al eliminar estudiante", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            alertDialogBuilder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            true
        }

        fabAgregarEstudiante.setOnClickListener {
            val intent = Intent(this, AddEstudianteActivity::class.java)
            intent.putExtra("accion", "a") // Agregar
            startActivity(intent)
        }

        fabAgregarNota.setOnClickListener {
            val intent = Intent(this, NotasActivity::class.java)
            startActivity(intent)
        }

        estudiantes = ArrayList()

        consultaEstudiantes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                estudiantes!!.clear()
                for (dato in dataSnapshot.children) {
                    val estudiante: Estudiante? = dato.getValue(Estudiante::class.java)
                    estudiante?.key = dato.key
                    if (estudiante != null) {
                        estudiantes!!.add(estudiante)
                    }
                }
                val adapter = AdaptadorEstudiante(this@EstudiantesActivity, estudiantes as ArrayList<Estudiante>)
                listaEstudiantes!!.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                auth.signOut()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val refEstudiantes: DatabaseReference = database.getReference("estudiantes")
        val refNotas: DatabaseReference = database.getReference("notas")
    }
}
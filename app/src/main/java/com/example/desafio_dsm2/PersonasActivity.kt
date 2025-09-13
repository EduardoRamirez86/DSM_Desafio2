package com.example.desafio_dsm2

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth
import android.view.Menu
import android.view.MenuItem
import com.example.desafio_dsm2.R
import com.example.desafio_dsm2.datos.Persona

class PersonasActivity : AppCompatActivity() {

    private val consultaOrdenada: Query = refPersonas.orderByChild("nombre")
    private var personas: MutableList<Persona>? = null
    private var listaPersonas: ListView? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personas)
        auth = FirebaseAuth.getInstance()
        inicializar()
    }

    private fun inicializar() {
        val fabAgregar: FloatingActionButton = findViewById(R.id.fab_agregar)
        listaPersonas = findViewById(R.id.ListaPersonas)

        listaPersonas!!.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(this, AddPersonaActivity::class.java)
            intent.putExtra("accion", "e") // Editar
            val persona = personas!![i]
            intent.putExtra("persona", persona as java.io.Serializable)
            startActivity(intent)
        }

        listaPersonas!!.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
                val persona = personas!![i]
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Eliminar Persona")
                alertDialogBuilder.setMessage("¿Estás seguro de que quieres eliminar a ${persona.nombre}?")
                alertDialogBuilder.setPositiveButton("Sí") { dialogInterface: DialogInterface, _: Int ->
                    if (persona.key != null) {
                        refPersonas.child(persona.key!!).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Se eliminó con éxito", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
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

        fabAgregar.setOnClickListener {
            val intent = Intent(this, AddPersonaActivity::class.java)
            intent.putExtra("accion", "a") // Agregar
            intent.putExtra("dui", "")
            intent.putExtra("nombre", "")
            intent.putExtra("apellido", "")
            intent.putExtra("telefono", "")
            intent.putExtra("edad", "")
            intent.putExtra("direccion", "")
            startActivity(intent)
        }

        personas = ArrayList()

        consultaOrdenada.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                personas!!.clear()
                for (dato in dataSnapshot.children) {
                    val persona: Persona? = dato.getValue(Persona::class.java)
                    persona?.key = dato.key
                    if (persona != null) {
                        personas!!.add(persona)
                    }
                }
                val adapter = AdaptadorPersona(
                    this@PersonasActivity,
                    personas as ArrayList<Persona>
                )
                listaPersonas!!.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    // Agrega el menú de opciones a la actividad
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Maneja la acción del menú (cerrar sesión)
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
        private val refPersonas: DatabaseReference = database.getReference("personas")
    }
}
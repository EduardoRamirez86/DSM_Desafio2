package com.example.desafio_dsm2

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.desafio_dsm2.datos.Estudiante
import com.example.desafio_dsm2.datos.EstudianteNota
import com.example.desafio_dsm2.datos.Nota
import com.google.firebase.database.*
import java.util.*

class ListaNotasActivity : AppCompatActivity() {

    private lateinit var spinnerGrado: Spinner
    private lateinit var spinnerMateria: Spinner
    private lateinit var listaNotasView: ListView

    private var listaEstudiantes: MutableList<Estudiante>? = null
    private var listaEstudiantesNotas: MutableList<EstudianteNota>? = null

    private lateinit var refEstudiantes: DatabaseReference
    private lateinit var refNotas: DatabaseReference
    private lateinit var refGrados: DatabaseReference
    private lateinit var refMaterias: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_notas)

        val database = FirebaseDatabase.getInstance()
        refEstudiantes = database.getReference("estudiantes")
        refNotas = database.getReference("notas")
        refGrados = database.getReference("grados")
        refMaterias = database.getReference("materias")

        spinnerGrado = findViewById(R.id.spinnerGrado)
        spinnerMateria = findViewById(R.id.spinnerMateria)
        listaNotasView = findViewById(R.id.listaNotas)

        cargarDatosSpinners()
        configurarFiltros()
        configurarListeners()
    }

    private fun cargarDatosSpinners() {
        refGrados.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val grados = ArrayList<String>()
                for (data in snapshot.children) {
                    val grado = data.getValue(String::class.java)
                    grado?.let { grados.add(it) }
                }
                val adapterGrados = ArrayAdapter(this@ListaNotasActivity, android.R.layout.simple_spinner_item, grados)
                spinnerGrado.adapter = adapterGrados
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListaNotasActivity, "Error al cargar grados", Toast.LENGTH_SHORT).show()
            }
        })

        refMaterias.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val materias = ArrayList<String>()
                for (data in snapshot.children) {
                    val materia = data.getValue(String::class.java)
                    materia?.let { materias.add(it) }
                }
                val adapterMaterias = ArrayAdapter(this@ListaNotasActivity, android.R.layout.simple_spinner_item, materias)
                spinnerMateria.adapter = adapterMaterias
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListaNotasActivity, "Error al cargar materias", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun configurarFiltros() {
        refEstudiantes.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaEstudiantes = ArrayList()
                for (data in snapshot.children) {
                    val estudiante = data.getValue(Estudiante::class.java)
                    estudiante?.let {
                        it.key = data.key
                        listaEstudiantes!!.add(it)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListaNotasActivity, "Error al cargar estudiantes", Toast.LENGTH_SHORT).show()
            }
        })

        val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val gradoSeleccionado = spinnerGrado.selectedItem?.toString() ?: ""
                val materiaSeleccionada = spinnerMateria.selectedItem?.toString() ?: ""
                filtrarNotas(gradoSeleccionado, materiaSeleccionada)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerGrado.onItemSelectedListener = onItemSelectedListener
        spinnerMateria.onItemSelectedListener = onItemSelectedListener
    }

    private fun filtrarNotas(grado: String, materia: String) {
        if (grado.isEmpty() || materia.isEmpty()) return

        refNotas.orderByChild("grado").equalTo(grado)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaEstudiantesNotas = ArrayList()
                    for (data in snapshot.children) {
                        val nota = data.getValue(Nota::class.java)
                        if (nota != null && nota.materia == materia) {
                            val estudiante = listaEstudiantes?.firstOrNull { it.key == nota.estudianteKey }
                            if (estudiante != null) {
                                val estudianteNota = EstudianteNota(
                                    nombre = estudiante.nombreCompleto,
                                    notaFinal = nota.notaFinal,
                                    key = data.key,
                                    estudianteKey = estudiante.key
                                )
                                listaEstudiantesNotas!!.add(estudianteNota)
                            }
                        }
                    }
                    val adapter = NotasAdapter(this@ListaNotasActivity, listaEstudiantesNotas!!)
                    listaNotasView.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ListaNotasActivity, "Error al filtrar notas", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun configurarListeners() {
        // Listener para editar nota (clic normal)
        listaNotasView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            val notaSeleccionada = listaEstudiantesNotas!![i]
            val intent = Intent(this, NotasActivity::class.java).apply {
                putExtra("estudiante_key", notaSeleccionada.estudianteKey)
                putExtra("nota_key", notaSeleccionada.key)
                putExtra("nota_valor", notaSeleccionada.notaFinal.toString())
                putExtra("nota_grado", spinnerGrado.selectedItem.toString())
                putExtra("nota_materia", spinnerMateria.selectedItem.toString())
            }
            startActivity(intent)
        }

        listaNotasView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, i, _ ->
            val notaSeleccionada = listaEstudiantesNotas!![i]
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Eliminar Nota")
            builder.setMessage("¿Estás seguro de que quieres eliminar la nota de ${notaSeleccionada.nombre}?")
            builder.setPositiveButton("Sí") { dialog, which ->
                notaSeleccionada.key?.let { key ->
                    refNotas.child(key).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Nota eliminada con éxito", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al eliminar la nota", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            builder.setNegativeButton("No", null)
            builder.show()
            true
        }
    }
}
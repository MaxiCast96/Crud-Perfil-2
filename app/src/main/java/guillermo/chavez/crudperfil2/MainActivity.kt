package guillermo.chavez.crudperfil2

import Modelo.ListaTickets
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import RecyclerViewHelpers.TicketAdapter
import RecyclerViewHelpers.TicketAdapterListener
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClassConexion
import java.sql.SQLException

class MainActivity : AppCompatActivity(), TicketAdapterListener {

    private lateinit var ticketAdapter: TicketAdapter
    private val ADD_TICKET_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnNuevo = findViewById<ExtendedFloatingActionButton>(R.id.btn_Nuevo)
        val rcvDatos = findViewById<RecyclerView>(R.id.rcvDatos)

        btnNuevo.setOnClickListener {
            val intent = Intent(this, activity_addTicket::class.java)
            startActivityForResult(intent, ADD_TICKET_REQUEST_CODE)
        }

        rcvDatos.layoutManager = LinearLayoutManager(this)
        actualizarRecyclerView()
    }

    private fun obtenerDatos(): List<ListaTickets> {
        try {
            val objConexion = ClassConexion().cadenaConexion()
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM tickets") ?: return emptyList()

            val listadoTickets = mutableListOf<ListaTickets>()

            while (resultSet.next()) {
                val id = resultSet.getInt("id_ticket")
                val titulo = resultSet.getString("titulo")
                val descripcion = resultSet.getString("descripcion")
                val responsable = resultSet.getString("responsable")
                val emailAutor = resultSet.getString("email_autor")
                val telefonoAutor = resultSet.getString("telefono_autor")
                val ubicacion = resultSet.getString("ubicacion")
                val estado = resultSet.getString("estado")
                val ticket = ListaTickets(id, titulo, descripcion, responsable, emailAutor, telefonoAutor, ubicacion, estado)
                listadoTickets.add(ticket)
            }
            resultSet.close()
            statement?.close()
            objConexion?.close()

            return listadoTickets
        } catch (e: SQLException) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(this@MainActivity, "Error de conexión a la base de datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
            return             emptyList()
        }
    }

    private fun actualizarRecyclerView() {
        CoroutineScope(Dispatchers.IO).launch {
            val ejecutarFuncion = obtenerDatos()

            withContext(Dispatchers.Main) {
                ticketAdapter = TicketAdapter(ejecutarFuncion, this@MainActivity)
                findViewById<RecyclerView>(R.id.rcvDatos).adapter = ticketAdapter

                if (ejecutarFuncion.isEmpty()) {
                    findViewById<TextView>(R.id.textView3).visibility = View.VISIBLE
                } else {
                    findViewById<TextView>(R.id.textView3).visibility = View.GONE
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_TICKET_REQUEST_CODE && resultCode == RESULT_OK) {
            actualizarRecyclerView()
        }
    }

    override fun onTicketsEmpty() {
        findViewById<TextView>(R.id.textView3).visibility = View.VISIBLE
    }
}

package guillermo.chavez.crudperfil2

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClassConexion
import java.sql.SQLException

class activity_ticketinfo : AppCompatActivity() {
    override fun onBackPressed() {
        super.onBackPressed()
    }

    private lateinit var txtNumeroTicket: TextView
    private lateinit var txtTituloTicket: TextView
    private lateinit var txtDescripcionTicket: TextView
    private lateinit var txtResponsableTicket: TextView
    private lateinit var txtEmailAutor: TextView
    private lateinit var txtTelefonoAutor: TextView
    private lateinit var txtUbicacion: TextView
    private lateinit var txtEstadoTicket: TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticketinfo)

        val img_GoBack2 = findViewById<ImageView>(R.id.img_GoBack2)

        txtNumeroTicket = findViewById(R.id.textView4)
        txtTituloTicket = findViewById(R.id.textView5)
        txtDescripcionTicket = findViewById(R.id.textView6)
        txtResponsableTicket = findViewById(R.id.textView7)
        txtEmailAutor = findViewById(R.id.textView8)
        txtTelefonoAutor = findViewById(R.id.textView9)
        txtUbicacion = findViewById(R.id.textView11)
        txtEstadoTicket = findViewById(R.id.textView12)

        val ticketId = intent.getIntExtra("TICKET_ID", -1)
        if (ticketId == -1) {
            finish()
        } else {
            obtenerInformacionTicket(ticketId)
        }
        img_GoBack2.setOnClickListener(){
            onBackPressed()
        }
    }

    private fun obtenerInformacionTicket(ticketId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val objConexion = ClassConexion().cadenaConexion()
                val statement = objConexion?.createStatement()
                val query = "SELECT * FROM tickets WHERE id_ticket = $ticketId"
                val resultSet = statement?.executeQuery(query)

                if (resultSet?.next() == true) {
                    val titulo = resultSet.getString("titulo")
                    val descripcion = resultSet.getString("descripcion")
                    val responsable = resultSet.getString("responsable")
                    val emailAutor = resultSet.getString("email_autor")
                    val telefonoAutor = resultSet.getString("telefono_autor")
                    val ubicacion = resultSet.getString("ubicacion")
                    val estado = resultSet.getString("estado")


                    withContext(Dispatchers.Main) {
                        txtNumeroTicket.text = "Ticket Nº $ticketId"
                        txtTituloTicket.text = "Título: $titulo"
                        txtDescripcionTicket.text = "Descripción del ticket: \n$descripcion"
                        txtResponsableTicket.text = "Autor: $responsable"
                        txtEmailAutor.text = "E-mail: $emailAutor"
                        txtTelefonoAutor.text = "Teléfono del Autor: $telefonoAutor"
                        txtUbicacion.text = "Ubicación: $ubicacion"
                        txtEstadoTicket.text = "Estado del ticket: $estado"
                    }
                }

                resultSet?.close()
                statement?.close()
                objConexion?.close()

            } catch (e: SQLException) {
                e.printStackTrace()
                runOnUiThread {
                    finish()
                }
            }
        }
    }
}

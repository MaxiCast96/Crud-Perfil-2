package guillermo.chavez.crudperfil2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClassConexion

class activity_addTicket : AppCompatActivity() {
    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_ticket)

        val txtTitulo = findViewById<TextInputLayout>(R.id.txtTitulo)
        val txtNombre = findViewById<TextInputLayout>(R.id.txtNombre)
        val txtCorreo = findViewById<TextInputLayout>(R.id.txtCorreo)
        val txtTelefono = findViewById<EditText>(R.id.txtTelefono)
        val txtUbicacion = findViewById<EditText>(R.id.txtUbicacion)
        val txtDescripcion = findViewById<TextInputLayout>(R.id.txtDescripcion)
        val btnAgregar = findViewById<Button>(R.id.btnAgregarTicket)
        val img_GoBack = findViewById<ImageView>(R.id.img_GoBack)

        img_GoBack.setOnClickListener(){
            onBackPressed()
        }

        btnAgregar.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val titulo = txtTitulo.editText?.text.toString()
                val nombre = txtNombre.editText?.text.toString()
                val correo = txtCorreo.editText?.text.toString()
                val telefono = txtTelefono.text.toString()
                val ubicacion = txtUbicacion.text.toString()
                val descripcion = txtDescripcion.editText?.text.toString()

                if (titulo.isNotEmpty() && nombre.isNotEmpty() && correo.isNotEmpty() && descripcion.isNotEmpty()) {
                    val objConexion = ClassConexion().cadenaConexion()

                    if (objConexion != null) {
                        try {
                            val addTicket = objConexion.prepareStatement("INSERT INTO tickets (titulo, descripcion, responsable, email_autor, telefono_autor, ubicacion, estado) VALUES (?, ?, ?, ?, ?, ?, 'Activo')")
                            addTicket.setString(1, titulo)
                            addTicket.setString(2, descripcion)
                            addTicket.setString(3, nombre)
                            addTicket.setString(4, correo)
                            addTicket.setString(5, telefono)
                            addTicket.setString(6, ubicacion)

                            val result = addTicket.executeUpdate()

                            withContext(Dispatchers.Main) {
                                if (result > 0) {
                                    Toast.makeText(this@activity_addTicket, "Ticket agregado exitosamente", Toast.LENGTH_SHORT).show()
                                    setResult(RESULT_OK)  // Indicar que la operación fue exitosa
                                    finish()  // Regresar a la actividad anterior
                                } else {
                                    Toast.makeText(this@activity_addTicket, "No se pudo agregar el ticket", Toast.LENGTH_SHORT).show()
                                }
                            }

                            addTicket.close()
                            objConexion.close()
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@activity_addTicket, "Error al agregar el ticket: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@activity_addTicket, "Error de conexión a la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@activity_addTicket, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

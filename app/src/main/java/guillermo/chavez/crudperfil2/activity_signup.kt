package guillermo.chavez.crudperfil2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class activity_signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnIrAlLogin = findViewById<Button>(R.id.btn_IrAlLogin)
        val btnCrearCuenta = findViewById<Button>(R.id.btnCrearCuenta)

        auth = FirebaseAuth.getInstance()

        btnIrAlLogin.setOnClickListener {
            val intent = Intent(this, activity_login::class.java)
            startActivity(intent)
            finish()
        }

        btnCrearCuenta.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val txtCorreo = findViewById<TextInputLayout>(R.id.txtCorreo)
        val txtContraseña = findViewById<TextInputLayout>(R.id.txtContraseña)
        val txtConfirmarContraseña = findViewById<TextInputLayout>(R.id.txtConfirmarContraseña)
        val txtNombre = findViewById<TextInputLayout>(R.id.txtNombre)

        val email = txtCorreo.editText?.text.toString().trim()
        val password = txtContraseña.editText?.text.toString().trim()
        val confirmPassword = txtConfirmarContraseña.editText?.text.toString().trim()
        val nombre = txtNombre.editText?.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Register", "createUserWithEmail:success")
                    Toast.makeText(baseContext, "Registro exitoso.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, activity_login::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Log.w("Register", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Registro fallido: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

    }
}

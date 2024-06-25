package RecyclerViewHelpers

import Modelo.ListaTickets
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import guillermo.chavez.crudperfil2.R
import guillermo.chavez.crudperfil2.activity_ticketinfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClassConexion

class TicketAdapter(
    private var Datos: List<ListaTickets>,
    private val listener: TicketAdapterListener
) : RecyclerView.Adapter<ViewHolder>() {

    fun actualizarRecyclerView(nuevaLista: List<ListaTickets>) {
        Datos = nuevaLista
        notifyDataSetChanged()
    }

    fun eliminarRegistro(idTicket: Int, posicion: Int) {
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClassConexion().cadenaConexion()

            val deleteTicket = objConexion?.prepareStatement("DELETE FROM tickets WHERE id_ticket = ?")
            deleteTicket?.setInt(1, idTicket)
            deleteTicket?.executeUpdate()

            val commit = objConexion?.prepareStatement("COMMIT")
            commit?.executeUpdate()
            objConexion?.close()
        }

        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()

        if (Datos.isEmpty()) {
            listener.onTicketsEmpty()
        }
    }

    fun actualizarListadoDespuesDeEditar(id_ticket: Int, nuevoTitulo: String) {
        val identificador = Datos.indexOfFirst { it.id == id_ticket }
        Datos[identificador].titulo = nuevoTitulo
        notifyItemChanged(identificador)
    }

    fun editarTicket(idTicket: Int, nuevoTitulo: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClassConexion().cadenaConexion()
            val updateTicket = objConexion?.prepareStatement("UPDATE tickets SET titulo = ? WHERE id_ticket = ?")
            updateTicket?.setString(1, nuevoTitulo)
            updateTicket?.setInt(2, idTicket)
            updateTicket?.executeUpdate()

            val commit = objConexion?.prepareStatement("COMMIT")
            commit?.executeUpdate()
        }
    }

    private fun actualizarEstadoTicket(idTicket: Int, nuevoEstado: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClassConexion().cadenaConexion()
            val updateTicket = objConexion?.prepareStatement("UPDATE tickets SET estado = ? WHERE id_ticket = ?")
            updateTicket?.setString(1, nuevoEstado)
            updateTicket?.setInt(2, idTicket)
            updateTicket?.executeUpdate()

            val commit = objConexion?.prepareStatement("COMMIT")
            commit?.executeUpdate()
            objConexion?.close()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = Datos[position]
        holder.textView.text = ticket.titulo

        holder.imgActivo.setImageResource(
            if (ticket.estado == "Activo") R.drawable.ic_activo else R.drawable.ic_finalizado
        )

        holder.imgActivo.setOnClickListener {
            val nuevoEstado = if (ticket.estado == "Activo") "Finalizado" else "Activo"
            actualizarEstadoTicket(ticket.id, nuevoEstado)
            ticket.estado = nuevoEstado
            notifyItemChanged(position)

            val mensaje = if (nuevoEstado == "Activo") "Activo" else "Finalizado"
            Toast.makeText(holder.itemView.context, mensaje, Toast.LENGTH_SHORT).show()
        }

        holder.imgBorrar.setOnClickListener {
            val context = holder.textView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Estás seguro que deseas eliminar?")

            builder.setPositiveButton("Sí") { dialog, _ ->
                eliminarRegistro(ticket.id, position)
            }

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        holder.imgEditar.setOnClickListener {
            val context = holder.textView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Editar titulo del Ticket")
            val input = EditText(context)
            input.setText(ticket.titulo)
            builder.setView(input)

            builder.setPositiveButton("Guardar") { dialog, _ ->
                val nuevoTitulo = input.text.toString()
                editarTicket(ticket.id, nuevoTitulo)
                actualizarListadoDespuesDeEditar(ticket.id, nuevoTitulo)
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        holder.imgInfo.setOnClickListener {
            val context = holder.textView.context
            val intent = Intent(context, activity_ticketinfo::class.java)
            intent.putExtra("TICKET_ID", ticket.id)
            context.startActivity(intent)
        }
    }

}




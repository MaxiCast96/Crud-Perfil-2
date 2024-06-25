package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import guillermo.chavez.crudperfil2.R

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val textView: TextView = view.findViewById(R.id.txtTickets)
        val imgEditar: ImageView = view.findViewById(R.id.imgEditar)
        val imgBorrar: ImageView = view.findViewById(R.id.imgBorrar)
        val imgInfo: ImageView = view.findViewById(R.id.imgInfo)
        val imgActivo: ImageView = view.findViewById(R.id.imgActivo)

    }

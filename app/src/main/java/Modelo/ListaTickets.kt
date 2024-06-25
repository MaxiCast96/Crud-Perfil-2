package Modelo

data class ListaTickets (
    val id: Int,
    var titulo: String,
    val descripcion: String,
    val responsable: String,
    val emailAutor: String,
    val telefonoAutor: String,
    val ubicacion: String,
    var estado: String
)
package fr.cedric.garcia.nantes.parking.exception

data class OpenDataNantesException(val status: Int, override val message: String) : Exception(message)

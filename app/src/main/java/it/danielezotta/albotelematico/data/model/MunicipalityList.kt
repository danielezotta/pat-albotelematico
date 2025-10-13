package it.danielezotta.albotelematico.data.model

/**
 * Container for the list of municipalities returned by the ente filter endpoint.
 */
data class MunicipalityList(
    val territory: String,
    val municipalities: List<Municipality>
)

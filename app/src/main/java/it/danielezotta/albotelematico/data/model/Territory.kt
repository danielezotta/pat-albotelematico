package it.danielezotta.albotelematico.data.model

/**
 * Represents a geographical territory grouping multiple municipalities listed on Albo Telematico.
 */
data class Territory(
    val slug: String,
    val name: String,
    val municipalitiesCount: Int? = null
)

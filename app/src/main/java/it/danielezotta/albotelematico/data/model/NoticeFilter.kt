package it.danielezotta.albotelematico.data.model

/**
 * Identifies the remote dataset to query (published vs archived).
 */
enum class NoticeDataSource(
    val tipo: Int,
    val chiaveHtaccess: String,
    val chiaveHtaccess2: String
) {
    Published(tipo = 1, chiaveHtaccess = "bacheca", chiaveHtaccess2 = "atto-pubb"),
    Archived(tipo = 2, chiaveHtaccess = "archivio", chiaveHtaccess2 = "atto-arch")
}

/**
 * Filters that can be applied when requesting notices from the remote API.
 */
data class NoticeFilter(
    val tipologia: String = "tutti",
    val territory: String = "",
    val territoryName: String = "",
    val ente: String = "",
    val enteName: String = "",
    val filtroEnte: String = "tutti",
    val organo: String = "tutti",
    val search: String = "",
    val dateFrom: String = "",
    val dateTo: String = "",
    val dataSource: NoticeDataSource = NoticeDataSource.Published
)

package it.danielezotta.albotelematico.data.model

/**
 * Wrapper for paged notice results returned by the remote DataTables endpoint.
 */
data class NoticePage(
    val notices: List<Notice>,
    val total: Int
)

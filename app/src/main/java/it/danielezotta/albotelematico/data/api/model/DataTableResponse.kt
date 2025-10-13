package it.danielezotta.albotelematico.data.api.model

import com.google.gson.annotations.SerializedName

/**
 * Generic DataTables server-side response wrapper returned by Albo Telematico endpoints.
 */
data class DataTableResponse(
    @SerializedName("aaData")
    val rows: List<List<String>> = emptyList(),
    @SerializedName("iTotalRecords")
    val totalRecords: Int = 0,
    @SerializedName("iTotalDisplayRecords")
    val totalDisplayRecords: Int = 0,
    @SerializedName("sEcho")
    val echo: String? = null
)

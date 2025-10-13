package it.danielezotta.albotelematico.data.api

import it.danielezotta.albotelematico.data.api.model.DataTableResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Retrofit API interface for Albo Telematico
 */
interface AlboTelematicoApi {

    @GET("bacheca/tutti/tutti")
    suspend fun warmUp(): ResponseBody

    @Headers("X-Requested-With: XMLHttpRequest")
    @GET("_site/_ajax/getTableAtti_v2.php")
    suspend fun getAttiPage(
        @Query("t") tipo: Int = 1,
        @Query("ente") ente: String = "",
        @Query("ta") tipologia: String = "tutti",
        @Query("fente") filtroEnte: String = "tutti",
        @Query("org") organo: String = "tutti",
        @Query("chiave_htaccess") chiaveHtaccess: String = "bacheca",
        @Query("chiave_htaccess2") chiaveHtaccess2: String = "atto-pubb",
        @Query("data_from") dataFrom: String = "",
        @Query("data_to") dataTo: String = "",
        @Query("sEcho") echo: Int,
        @Query("iColumns") columns: Int = 3,
        @Query("sColumns") columnsList: String = ",,,",
        @Query("iDisplayStart") start: Int,
        @Query("iDisplayLength") length: Int,
        @Query("mDataProp_0") mDataProp0: Int = 0,
        @Query("mDataProp_1") mDataProp1: Int = 1,
        @Query("mDataProp_2") mDataProp2: Int = 2,
        @Query("sSearch") search: String = "",
        @Query("bRegex") regex: Boolean = false,
        @Query("bSearchable_0") searchable0: Boolean = true,
        @Query("bSearchable_1") searchable1: Boolean = true,
        @Query("bSearchable_2") searchable2: Boolean = true,
        @Query("bSortable_0") sortable0: Boolean = false,
        @Query("bSortable_1") sortable1: Boolean = false,
        @Query("bSortable_2") sortable2: Boolean = false,
        @Query("sSearch_0") search0: String = "",
        @Query("sSearch_1") search1: String = "",
        @Query("sSearch_2") search2: String = "",
        @Query("bRegex_0") regex0: Boolean = false,
        @Query("bRegex_1") regex1: Boolean = false,
        @Query("bRegex_2") regex2: Boolean = false,
        @Query("iSortingCols") sortingCols: Int = 0,
        @Query("_") cacheBuster: Long
    ): DataTableResponse
}

package it.danielezotta.albotelematico.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import it.danielezotta.albotelematico.data.model.NoticeDataSource
import it.danielezotta.albotelematico.data.model.NoticeFilter
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class FilterPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object Keys {
        val Tipologia = stringPreferencesKey("filter_tipologia")
        val Territory = stringPreferencesKey("filter_territory")
        val TerritoryName = stringPreferencesKey("filter_territory_name")
        val Ente = stringPreferencesKey("filter_ente")
        val EnteName = stringPreferencesKey("filter_ente_name")
        val FiltroEnte = stringPreferencesKey("filter_filtro_ente")
        val Organo = stringPreferencesKey("filter_organo")
        val Search = stringPreferencesKey("filter_search")
        val DateFrom = stringPreferencesKey("filter_date_from")
        val DateTo = stringPreferencesKey("filter_date_to")
        val DataSource = stringPreferencesKey("filter_data_source")
        val SearchQuery = stringPreferencesKey("filter_search_query")
    }

    private val defaultFilter = NoticeFilter()

    suspend fun readFilter(): NoticeFilter = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val dataSourceName = prefs[Keys.DataSource]
            val dataSource = dataSourceName?.let { name ->
                runCatching { NoticeDataSource.valueOf(name) }.getOrDefault(defaultFilter.dataSource)
            } ?: defaultFilter.dataSource
            NoticeFilter(
                tipologia = prefs[Keys.Tipologia] ?: defaultFilter.tipologia,
                territory = prefs[Keys.Territory] ?: defaultFilter.territory,
                territoryName = prefs[Keys.TerritoryName] ?: defaultFilter.territoryName,
                ente = prefs[Keys.Ente] ?: defaultFilter.ente,
                enteName = prefs[Keys.EnteName] ?: defaultFilter.enteName,
                filtroEnte = prefs[Keys.FiltroEnte] ?: defaultFilter.filtroEnte,
                organo = prefs[Keys.Organo] ?: defaultFilter.organo,
                search = prefs[Keys.Search] ?: defaultFilter.search,
                dateFrom = prefs[Keys.DateFrom] ?: defaultFilter.dateFrom,
                dateTo = prefs[Keys.DateTo] ?: defaultFilter.dateTo,
                dataSource = dataSource
            )
        }
        .first()

    suspend fun saveFilter(filter: NoticeFilter) {
        dataStore.edit { prefs ->
            prefs[Keys.Tipologia] = filter.tipologia
            prefs[Keys.Territory] = filter.territory
            prefs[Keys.TerritoryName] = filter.territoryName
            prefs[Keys.Ente] = filter.ente
            prefs[Keys.EnteName] = filter.enteName
            prefs[Keys.FiltroEnte] = filter.filtroEnte
            prefs[Keys.Organo] = filter.organo
            prefs[Keys.Search] = filter.search
            prefs[Keys.DateFrom] = filter.dateFrom
            prefs[Keys.DateTo] = filter.dateTo
            prefs[Keys.DataSource] = filter.dataSource.name
        }
    }

    suspend fun readSearchQuery(): String = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs -> prefs[Keys.SearchQuery] ?: "" }
        .first()

    suspend fun saveSearchQuery(query: String) {
        dataStore.edit { prefs ->
            if (query.isBlank()) {
                prefs.remove(Keys.SearchQuery)
            } else {
                prefs[Keys.SearchQuery] = query
            }
        }
    }
}

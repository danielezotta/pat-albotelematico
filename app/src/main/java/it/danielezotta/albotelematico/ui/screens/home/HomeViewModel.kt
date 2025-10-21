package it.danielezotta.albotelematico.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.danielezotta.albotelematico.data.local.FilterPreferencesDataSource
import it.danielezotta.albotelematico.data.model.Municipality
import it.danielezotta.albotelematico.data.model.Notice
import it.danielezotta.albotelematico.data.model.NoticeFilter
import it.danielezotta.albotelematico.data.model.Territory
import it.danielezotta.albotelematico.data.repository.NoticeRepository
import it.danielezotta.albotelematico.data.repository.TerritoryRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val notices: List<Notice>, val hasMore: Boolean) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NoticeRepository,
    private val territoryRepository: TerritoryRepository,
    private val filterPreferences: FilterPreferencesDataSource
) : ViewModel() {

    var uiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    var territories: List<Territory> by mutableStateOf(emptyList())
        private set

    var isTerritoriesLoading: Boolean by mutableStateOf(false)
        private set

    var territoryError: String? by mutableStateOf(null)
        private set

    var municipalities: List<Municipality> by mutableStateOf(emptyList())
        private set

    var isMunicipalitiesLoading: Boolean by mutableStateOf(false)
        private set

    var municipalityError: String? by mutableStateOf(null)
        private set

    private var currentPage = 1
    private val pageSize = 20
    private var isFetching = false
    private var totalCount = 0
    private val loadedNotices = mutableListOf<Notice>()
    private var currentFilterState: NoticeFilter by mutableStateOf(NoticeFilter())
    var searchQuery: String by mutableStateOf("")
        private set
    private val municipalityCache = mutableMapOf<String, List<Municipality>>()
    private var lastRequestedTerritory: String? = null

    init {
        viewModelScope.launch {
            ensureTerritoriesLoaded(force = true)

            val savedFilter = filterPreferences.readFilter()
            val savedSearchQuery = filterPreferences.readSearchQuery()

            currentFilterState = savedFilter
            searchQuery = savedSearchQuery

            currentPage = 1
            totalCount = 0
            loadedNotices.clear()

            if (savedFilter.territory.isNotBlank()) {
                loadMunicipalities(savedFilter.territory)
            } else {
                municipalities = emptyList()
            }

            loadNotices(reset = true)
        }
    }

    fun ensureTerritoriesLoaded(force: Boolean = false) {
        if (!force && territories.isNotEmpty()) return
        fetchTerritories()
    }

    private fun fetchTerritories() {
        viewModelScope.launch {
            isTerritoriesLoading = true
            territoryError = null
            territoryRepository.getTerritories()
                .onSuccess { loaded ->
                    territories = loaded
                }
                .onFailure { exception ->
                    territoryError = exception.message ?: "Impossibile caricare i territori"
                }
            isTerritoriesLoading = false
        }
    }

    fun loadMunicipalities(territorySlug: String, force: Boolean = false) {
        if (territorySlug.isBlank()) {
            municipalities = emptyList()
            municipalityError = null
            lastRequestedTerritory = null
            return
        }

        if (!force) {
            municipalityCache[territorySlug]?.let { cached ->
                municipalities = cached
                municipalityError = null
                lastRequestedTerritory = territorySlug
                return
            }
            if (territorySlug == lastRequestedTerritory && isMunicipalitiesLoading) {
                return
            }
        }

        viewModelScope.launch {
            isMunicipalitiesLoading = true
            municipalityError = null
            lastRequestedTerritory = territorySlug
            territoryRepository.getMunicipalities(territorySlug)
                .onSuccess { loaded ->
                    municipalityCache[territorySlug] = loaded
                    municipalities = loaded
                }
                .onFailure { exception ->
                    municipalityError = exception.message ?: "Impossibile caricare i comuni"
                }
            isMunicipalitiesLoading = false
        }
    }

    fun loadNotices(reset: Boolean = false) {
        if (isFetching) return
        if (!reset && totalCount > 0 && loadedNotices.size >= totalCount) return
        viewModelScope.launch {
            isFetching = true
            if (reset) {
                uiState = HomeUiState.Loading
            }

            try {
                // Create a filter that includes both the current filter and search query
                val filterWithSearch = currentFilterState.copy(search = searchQuery)
                val result = repository.getNotices(currentPage, pageSize, filterWithSearch)
                result.onSuccess { page ->
                    if (reset) {
                        loadedNotices.clear()
                        totalCount = page.total
                    }
                    loadedNotices.addAll(page.notices)
                    totalCount = page.total
                    currentPage++
                    uiState = HomeUiState.Success(
                        notices = loadedNotices.toList(),
                        hasMore = loadedNotices.size < totalCount
                    )
                }.onFailure { exception ->
                    uiState = HomeUiState.Error(exception.message ?: "Unknown error occurred")
                }
            } catch (e: Exception) {
                uiState = HomeUiState.Error(e.message ?: "Failed to load notices")
            } finally {
                isFetching = false
            }
        }
    }
    
    fun refresh() {
        currentPage = 1
        totalCount = 0
        loadedNotices.clear()
        loadNotices(reset = true)
    }

    fun applyFilter(filter: NoticeFilter) {
        currentFilterState = filter
        currentPage = 1
        totalCount = 0
        loadedNotices.clear()
        if (filter.territory.isNotBlank()) {
            loadMunicipalities(filter.territory)
        } else {
            municipalities = emptyList()
        }
        viewModelScope.launch {
            filterPreferences.saveFilter(filter)
        }
        loadNotices(reset = true)
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        currentPage = 1
        totalCount = 0
        loadedNotices.clear()
        viewModelScope.launch {
            filterPreferences.saveSearchQuery(query)
        }
        loadNotices(reset = true)
    }

    fun loadMore() {
        loadNotices(reset = false)
    }

    fun currentFilter(): NoticeFilter = currentFilterState
    
    fun currentSearchQuery(): String = searchQuery
}

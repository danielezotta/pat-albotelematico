package it.danielezotta.albotelematico.ui.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.danielezotta.albotelematico.data.model.Notice
import it.danielezotta.albotelematico.data.model.NoticeFilter
import it.danielezotta.albotelematico.data.repository.NoticeRepository
import javax.inject.Inject
import kotlinx.coroutines.launch

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val query: String, val results: List<Notice>) : SearchUiState()
    data class Error(val query: String, val message: String) : SearchUiState()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: NoticeRepository
) : ViewModel() {

    var uiState: SearchUiState by mutableStateOf<SearchUiState>(SearchUiState.Idle)
        private set

    private var lastQuery: String = ""
    private val pageSize = 20

    fun onQueryCleared() {
        lastQuery = ""
        uiState = SearchUiState.Idle
    }

    fun search(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            onQueryCleared()
            return
        }
        lastQuery = trimmed
        uiState = SearchUiState.Loading
        viewModelScope.launch {
            repository.getNotices(
                page = 1,
                pageSize = pageSize,
                filter = NoticeFilter(search = trimmed)
            ).onSuccess { page ->
                uiState = SearchUiState.Success(trimmed, page.notices)
            }.onFailure { throwable ->
                uiState = SearchUiState.Error(trimmed, throwable.message ?: "Impossibile completare la ricerca")
            }
        }
    }

    fun retry() {
        if (lastQuery.isNotEmpty()) {
            search(lastQuery)
        }
    }
}

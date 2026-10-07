package com.pemmob.museblater.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.museblater.data.model.AnimeUiModel
import com.pemmob.museblater.data.model.Genre
import com.pemmob.museblater.data.remote.RetrofitClient
import com.pemmob.museblater.data.repository.AnimeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

data class HomeUiState(
    val searchQuery: String = "",
    val selectedGenreId: Int? = null,
    val animeList: List<AnimeUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val genres: List<Genre> = emptyList(),
    val isGenresLoading: Boolean = false
) {
    val isEmpty: Boolean get() = !isLoading && errorMessage == null && animeList.isEmpty()
}

class HomeViewModel : ViewModel() {

    private val repository = AnimeRepository(RetrofitClient.jikanApiService)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            loadAnime(query = "", genreId = null)
            // Stagger loading genres to avoid hitting Jikan API rate limit (3 req/sec)
            delay(600L)
            loadGenres()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            loadAnime(query = query.trim(), genreId = _uiState.value.selectedGenreId)
        }
    }

    fun onGenreSelected(genreId: Int?) {
        if (_uiState.value.selectedGenreId == genreId) return
        _uiState.update { it.copy(selectedGenreId = genreId) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            loadAnime(query = _uiState.value.searchQuery.trim(), genreId = genreId)
        }
    }

    fun retry() {
        val state = _uiState.value
        loadAnime(query = state.searchQuery.trim(), genreId = state.selectedGenreId)
    }

    private fun loadAnime(query: String, genreId: Int?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.searchAnime(query = query, genreId = genreId)
            result.fold(
                onSuccess = { list ->
                    _uiState.update {
                        it.copy(isLoading = false, animeList = list, errorMessage = null)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = friendlyError(error)
                        )
                    }
                }
            )
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenresLoading = true) }
            val result = repository.getAnimeGenres()
            result.fold(
                onSuccess = { genres ->
                    val filtered = genres
                        .filter { it.type == "anime" || it.type.isEmpty() }
                        .take(20)
                    _uiState.update { it.copy(genres = filtered, isGenresLoading = false) }
                },
                onFailure = {
                    _uiState.update { it.copy(isGenresLoading = false) }
                }
            )
        }
    }

    private fun friendlyError(e: Throwable): String {
        val msg = e.message ?: ""
        return when {
            e is UnknownHostException ||
                    msg.contains("Unable to resolve host", ignoreCase = true) ||
                    msg.contains("No address", ignoreCase = true) ->
                "No internet connection. Please check your network."

            e is ConnectException ||
                    msg.contains("Failed to connect", ignoreCase = true) ->
                "Unable to connect to server. Please check your internet connection."

            e is SocketTimeoutException ||
                    msg.contains("timeout", ignoreCase = true) ->
                "Request timed out. Please try again."

            e is HttpException -> when (e.code()) {
                429 -> "Too many requests to Jikan API. Please wait a moment and tap Retry."
                422 -> "Search term must be at least 3 characters."
                404 -> "Anime data not found."
                else -> "Server error (${e.code()}). Please try again."
            }

            e is IOException ->
                "Network error occurred. Please check your connection and retry."

            else -> "Something went wrong. Please try again."
        }
    }
}

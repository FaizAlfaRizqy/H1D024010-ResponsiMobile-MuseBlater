package com.pemmob.museblater.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.museblater.data.model.AnimeDetailUiModel
import com.pemmob.museblater.data.remote.RetrofitClient
import com.pemmob.museblater.data.repository.AnimeRepository
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

data class DetailUiState(
    val malId: Int = 0,
    val detail: AnimeDetailUiModel? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DetailViewModel : ViewModel() {

    private val repository = AnimeRepository(RetrofitClient.jikanApiService)

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadDetail(malId: Int) {
        if (_uiState.value.malId == malId && _uiState.value.detail != null) return
        _uiState.update { it.copy(malId = malId, isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = repository.getAnimeDetail(malId)
            result.fold(
                onSuccess = { detail ->
                    _uiState.update { it.copy(isLoading = false, detail = detail) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = friendlyError(error))
                    }
                }
            )
        }
    }

    fun retry() {
        val malId = _uiState.value.malId
        if (malId != 0) {
            _uiState.update { it.copy(detail = null) }
            loadDetail(malId)
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
                404 -> "Anime details not found."
                else -> "Server error (${e.code()}). Please try again."
            }

            e is IOException ->
                "Network error occurred. Please check your connection and retry."

            else -> "Could not load anime details. Please try again."
        }
    }
}

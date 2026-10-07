package com.pemmob.museblater.data.repository

import com.pemmob.museblater.data.model.AnimeDetailUiModel
import com.pemmob.museblater.data.model.AnimeUiModel
import com.pemmob.museblater.data.model.Genre
import com.pemmob.museblater.data.model.toDetailUiModel
import com.pemmob.museblater.data.model.toUiModel
import com.pemmob.museblater.data.remote.JikanApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnimeRepository(
    private val apiService: JikanApiService
) {

    /**
     * Search anime by query with optional genre filter using Tenrai API.
     */
    suspend fun searchAnime(
        query: String,
        genreId: Int? = null
    ): Result<List<AnimeUiModel>> = withContext(Dispatchers.IO) {
        runCatching {
            val cleanQuery = query.trim()
            val genreParam = genreId?.toString()

            val response = when {
                cleanQuery.length >= 3 -> {
                    apiService.searchAnime(query = cleanQuery, genreIds = genreParam)
                }
                genreParam != null -> {
                    apiService.getAnimeByGenre(genreIds = genreParam)
                }
                else -> {
                    apiService.getTopAnime()
                }
            }
            response.data.map { it.toUiModel() }
        }
    }

    /**
     * Fetch full anime detail for a given mal_id using Tenrai API.
     */
    suspend fun getAnimeDetail(malId: Int): Result<AnimeDetailUiModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = apiService.getAnimeDetail(malId)
                response.data?.toDetailUiModel()
                    ?: throw IllegalStateException("Anime detail data is null for malId=$malId")
            }
        }

    /**
     * Fetch available anime genres from Tenrai API.
     */
    suspend fun getAnimeGenres(): Result<List<Genre>> = withContext(Dispatchers.IO) {
        runCatching {
            apiService.getAnimeGenres().data
        }
    }
}

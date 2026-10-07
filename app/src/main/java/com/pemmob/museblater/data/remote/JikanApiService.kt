package com.pemmob.museblater.data.remote

import com.pemmob.museblater.data.model.AnimeDetailResponse
import com.pemmob.museblater.data.model.AnimeSearchResponse
import com.pemmob.museblater.data.model.GenreListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JikanApiService {

    /**
     * Search anime by title query with optional genre filter.
     * GET /anime?q={query}&genres={genreId}&limit=25&sfw=true
     */
    @GET("anime")
    suspend fun searchAnime(
        @Query("q") query: String? = null,
        @Query("genres") genreIds: String? = null,
        @Query("limit") limit: Int = 25,
        @Query("sfw") sfw: Boolean = true
    ): AnimeSearchResponse

    /**
     * Fetch anime by genre only (for initial / empty query state).
     * GET /anime?genres={genreId}&limit=25&sfw=true&order_by=score&sort=desc
     */
    @GET("anime")
    suspend fun getAnimeByGenre(
        @Query("genres") genreIds: String,
        @Query("limit") limit: Int = 25,
        @Query("sfw") sfw: Boolean = true,
        @Query("order_by") orderBy: String = "score",
        @Query("sort") sort: String = "desc"
    ): AnimeSearchResponse

    /**
     * Get top anime (used as default Home state).
     * GET /top/anime?limit=25
     */
    @GET("top/anime")
    suspend fun getTopAnime(
        @Query("limit") limit: Int = 25,
        @Query("filter") filter: String? = null
    ): AnimeSearchResponse

    /**
     * Get full anime detail by mal_id.
     * GET /anime/{id}/full
     */
    @GET("anime/{id}/full")
    suspend fun getAnimeDetail(
        @Path("id") malId: Int
    ): AnimeDetailResponse

    /**
     * Get list of available anime genres.
     * GET /genres/anime
     */
    @GET("genres/anime")
    suspend fun getAnimeGenres(): GenreListResponse
}

package com.pemmob.museblater.data.model

import com.google.gson.annotations.SerializedName

// ── Search API response models ──────────────────────────────────────────────

data class AnimeSearchResponse(
    @SerializedName("data") val data: List<AnimeItem> = emptyList(),
    @SerializedName("pagination") val pagination: Pagination? = null
)

data class Pagination(
    @SerializedName("has_next_page") val hasNextPage: Boolean = false,
    @SerializedName("items") val items: PaginationItems? = null
)

data class PaginationItems(
    @SerializedName("count") val count: Int = 0,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("per_page") val perPage: Int = 0
)

data class AnimeItem(
    @SerializedName("mal_id") val malId: Int = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("title_english") val titleEnglish: String? = null,
    @SerializedName("images") val images: AnimeImages? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("score") val score: Double? = null,
    @SerializedName("episodes") val episodes: Int? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("synopsis") val synopsis: String? = null,
    @SerializedName("genres") val genres: List<Genre> = emptyList(),
    @SerializedName("year") val year: Int? = null,
    @SerializedName("season") val season: String? = null,
    @SerializedName("studios") val studios: List<Studio> = emptyList(),
    @SerializedName("rank") val rank: Int? = null,
    @SerializedName("popularity") val popularity: Int? = null,
    @SerializedName("members") val members: Int? = null,
    @SerializedName("duration") val duration: String? = null,
    @SerializedName("url") val url: String? = null
)

data class AnimeImages(
    @SerializedName("jpg") val jpg: ImageUrls? = null,
    @SerializedName("webp") val webp: ImageUrls? = null
)

data class ImageUrls(
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("small_image_url") val smallImageUrl: String? = null,
    @SerializedName("large_image_url") val largeImageUrl: String? = null
)

data class Genre(
    @SerializedName("mal_id") val malId: Int = 0,
    @SerializedName("type") val type: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("url") val url: String = ""
)

data class Studio(
    @SerializedName("mal_id") val malId: Int = 0,
    @SerializedName("name") val name: String = ""
)

// ── Detail API response models ───────────────────────────────────────────────

data class AnimeDetailResponse(
    @SerializedName("data") val data: AnimeItem? = null
)

// ── Genre list API response models ──────────────────────────────────────────

data class GenreListResponse(
    @SerializedName("data") val data: List<Genre> = emptyList()
)

// ── Domain / UI models ───────────────────────────────────────────────────────

data class AnimeUiModel(
    val malId: Int,
    val title: String,
    val imageUrl: String,
    val type: String,
    val score: String,
    val genres: List<String>
)

data class AnimeDetailUiModel(
    val malId: Int,
    val title: String,
    val imageUrl: String,
    val genres: List<String>,
    val rating: String,
    val episodes: String,
    val status: String,
    val synopsis: String,
    val type: String,
    val duration: String,
    val year: String,
    val season: String,
    val studios: String,
    val rank: String,
    val popularity: String,
    val members: String,
    val url: String
)

// ── Mapper helpers ───────────────────────────────────────────────────────────

fun AnimeItem.toUiModel(): AnimeUiModel = AnimeUiModel(
    malId = malId,
    title = titleEnglish?.takeIf { it.isNotBlank() } ?: title,
    imageUrl = images?.jpg?.largeImageUrl
        ?: images?.jpg?.imageUrl
        ?: images?.webp?.largeImageUrl
        ?: images?.webp?.imageUrl
        ?: "",
    type = type ?: "Unknown",
    score = score?.let { String.format("%.1f", it) } ?: "N/A",
    genres = genres.map { it.name }
)

fun AnimeItem.toDetailUiModel(): AnimeDetailUiModel = AnimeDetailUiModel(
    malId = malId,
    title = titleEnglish?.takeIf { it.isNotBlank() } ?: title,
    imageUrl = images?.jpg?.largeImageUrl
        ?: images?.jpg?.imageUrl
        ?: images?.webp?.largeImageUrl
        ?: images?.webp?.imageUrl
        ?: "",
    genres = genres.map { it.name },
    rating = score?.let { String.format("%.2f", it) } ?: "Not rated",
    episodes = episodes?.toString() ?: "Unknown",
    status = status ?: "Unknown",
    synopsis = synopsis?.takeIf { it.isNotBlank() } ?: "No synopsis available.",
    type = type ?: "Unknown",
    duration = duration ?: "Unknown",
    year = year?.toString() ?: "Unknown",
    season = season?.replaceFirstChar { it.uppercaseChar() } ?: "Unknown",
    studios = studios.joinToString(", ") { it.name }.takeIf { it.isNotBlank() } ?: "Unknown",
    rank = rank?.let { "#$it" } ?: "N/A",
    popularity = popularity?.let { "#$it" } ?: "N/A",
    members = members?.let {
        when {
            it >= 1_000_000 -> String.format("%.1fM", it / 1_000_000.0)
            it >= 1_000 -> String.format("%dk", it / 1_000)
            else -> it.toString()
        }
    } ?: "N/A",
    url = url ?: ""
)

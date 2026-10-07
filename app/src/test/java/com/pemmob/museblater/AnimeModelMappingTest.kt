package com.pemmob.museblater

import com.pemmob.museblater.data.model.AnimeImages
import com.pemmob.museblater.data.model.AnimeItem
import com.pemmob.museblater.data.model.Genre
import com.pemmob.museblater.data.model.ImageUrls
import com.pemmob.museblater.data.model.Studio
import com.pemmob.museblater.data.model.toDetailUiModel
import com.pemmob.museblater.data.model.toUiModel
import org.junit.Assert.assertEquals
import org.junit.Test

class AnimeModelMappingTest {

    @Test
    fun toUiModel_prefersEnglishTitleWhenAvailable() {
        val item = AnimeItem(
            malId = 52991,
            title = "Sousou no Frieren",
            titleEnglish = "Frieren: Beyond Journey's End",
            score = 9.14
        )

        val uiModel = item.toUiModel()

        assertEquals("Frieren: Beyond Journey's End", uiModel.title)
        assertEquals(52991, uiModel.malId)
    }

    @Test
    fun toUiModel_fallsBackToDefaultTitleWhenEnglishTitleIsNull() {
        val item = AnimeItem(
            malId = 1,
            title = "Cowboy Bebop",
            titleEnglish = null,
            score = 8.75
        )

        val uiModel = item.toUiModel()

        assertEquals("Cowboy Bebop", uiModel.title)
    }

    @Test
    fun toUiModel_formatsScoreCorrectly() {
        val scoredItem = AnimeItem(malId = 1, title = "Test", score = 8.765)
        val unscoredItem = AnimeItem(malId = 2, title = "Test", score = null)

        assertEquals("8.8", scoredItem.toUiModel().score)
        assertEquals("N/A", unscoredItem.toUiModel().score)
    }

    @Test
    fun toDetailUiModel_formatsAllDetailsCorrectly() {
        val item = AnimeItem(
            malId = 52991,
            title = "Sousou no Frieren",
            titleEnglish = "Frieren: Beyond Journey's End",
            images = AnimeImages(
                jpg = ImageUrls(
                    largeImageUrl = "https://cdn.myanimelist.net/large.jpg",
                    imageUrl = "https://cdn.myanimelist.net/normal.jpg"
                )
            ),
            type = "TV",
            score = 9.14,
            episodes = 28,
            status = "Finished Airing",
            synopsis = "A journey after the demon king's defeat.",
            genres = listOf(Genre(malId = 1, name = "Adventure"), Genre(malId = 2, name = "Fantasy")),
            year = 2023,
            season = "fall",
            studios = listOf(Studio(malId = 11, name = "Madhouse")),
            rank = 1,
            popularity = 45,
            members = 850000,
            duration = "24 min per ep",
            url = "https://myanimelist.net/anime/52991/Sousou_no_Frieren"
        )

        val detail = item.toDetailUiModel()

        assertEquals("Frieren: Beyond Journey's End", detail.title)
        assertEquals("https://cdn.myanimelist.net/large.jpg", detail.imageUrl)
        assertEquals("9.14", detail.rating)
        assertEquals("28", detail.episodes)
        assertEquals("Finished Airing", detail.status)
        assertEquals("A journey after the demon king's defeat.", detail.synopsis)
        assertEquals("TV", detail.type)
        assertEquals("24 min per ep", detail.duration)
        assertEquals("2023", detail.year)
        assertEquals("Fall", detail.season)
        assertEquals("Madhouse", detail.studios)
        assertEquals("#1", detail.rank)
        assertEquals("#45", detail.popularity)
        assertEquals("850k", detail.members)
        assertEquals("https://myanimelist.net/anime/52991/Sousou_no_Frieren", detail.url)
        assertEquals(listOf("Adventure", "Fantasy"), detail.genres)
    }

    @Test
    fun toDetailUiModel_formatsMembersInMillions() {
        val millionItem = AnimeItem(malId = 1, title = "Test", members = 2100000)
        assertEquals("2.1M", millionItem.toDetailUiModel().members)
    }

    @Test
    fun toDetailUiModel_handlesNullsGracefully() {
        val emptyItem = AnimeItem(malId = 999, title = "Empty Anime")
        val detail = emptyItem.toDetailUiModel()

        assertEquals("Empty Anime", detail.title)
        assertEquals("", detail.imageUrl)
        assertEquals("Not rated", detail.rating)
        assertEquals("Unknown", detail.episodes)
        assertEquals("Unknown", detail.status)
        assertEquals("No synopsis available.", detail.synopsis)
        assertEquals("Unknown", detail.type)
        assertEquals("Unknown", detail.duration)
        assertEquals("Unknown", detail.year)
        assertEquals("Unknown", detail.season)
        assertEquals("Unknown", detail.studios)
        assertEquals("N/A", detail.rank)
        assertEquals("N/A", detail.popularity)
        assertEquals("N/A", detail.members)
        assertEquals("", detail.url)
        assertEquals(emptyList<String>(), detail.genres)
    }
}

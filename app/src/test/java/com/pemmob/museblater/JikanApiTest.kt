package com.pemmob.museblater

import com.pemmob.museblater.data.model.AnimeItem
import com.pemmob.museblater.data.model.toUiModel
import org.junit.Assert.assertEquals
import org.junit.Test

class AnimeMappingTest {

    @Test
    fun testAnimeItemToUiModel() {
        val item = AnimeItem(
            malId = 1,
            title = "Test Anime",
            score = 8.5
        )
        val uiModel = item.toUiModel()
        assertEquals(1, uiModel.malId)
        assertEquals("Test Anime", uiModel.title)
        assertEquals("8.5", uiModel.score)
    }
}

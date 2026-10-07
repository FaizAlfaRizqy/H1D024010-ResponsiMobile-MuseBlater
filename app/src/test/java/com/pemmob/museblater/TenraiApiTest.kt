package com.pemmob.museblater

import com.pemmob.museblater.data.remote.RetrofitClient
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TenraiApiTest {

    @Test
    fun testTenraiApiGetTopAnime() = runBlocking {
        val response = RetrofitClient.jikanApiService.getTopAnime()
        assertNotNull(response.data)
        assertTrue(response.data.isNotEmpty())
    }

    @Test
    fun testTenraiApiGetAnimeDetail() = runBlocking {
        val response = RetrofitClient.jikanApiService.getAnimeDetail(1)
        assertNotNull(response.data)
    }
}

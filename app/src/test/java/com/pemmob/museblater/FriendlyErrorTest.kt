package com.pemmob.museblater

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class FriendlyErrorTest {

    @Test
    fun testNetworkExceptionMapping() {
        val hostEx = UnknownHostException("Unable to resolve host \"api.jikan.moe\"")
        val timeoutEx = SocketTimeoutException("timeout")

        assertEquals("No internet connection. Please check your network.", mapError(hostEx))
        assertEquals("Request timed out. Please try again.", mapError(timeoutEx))
    }

    private fun mapError(e: Throwable): String {
        val msg = e.message ?: ""
        return when {
            e is UnknownHostException ||
                    msg.contains("Unable to resolve host", ignoreCase = true) ->
                "No internet connection. Please check your network."

            e is ConnectException ||
                    msg.contains("Failed to connect", ignoreCase = true) ->
                "Unable to connect to server. Please check your internet connection."

            e is SocketTimeoutException ||
                    msg.contains("timeout", ignoreCase = true) ->
                "Request timed out. Please try again."

            e is IOException ->
                "Network error occurred. Please check your connection and retry."

            else -> "Something went wrong. Please try again."
        }
    }
}

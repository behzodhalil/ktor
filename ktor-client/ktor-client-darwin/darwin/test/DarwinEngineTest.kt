import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.engine.darwin.internal.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlin.test.*

/*
 * Copyright 2014-2022 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

class DarwinEngineTest {

    @Test
    fun testRequestInRunBlocking() = runBlocking {
        val client = HttpClient(Darwin)

        try {
            withTimeout(1000) {
                val response = client.get("http://127.0.0.1:8080")
                assertEquals("Hello, world!", response.bodyAsText())
            }
        } finally {
            client.close()
        }
    }

    @Test
    fun testPathWithCyrillic() = runBlocking {
    }

    @Test
    fun testQueryWithCyrillic() = runBlocking {
        val client = HttpClient(Darwin)

        try {
            withTimeout(1000) {
                val response = client.get("http://127.0.0.1:8080/echo_query?привет")
                assertEquals("привет=[]", response.bodyAsText())
            }
        } finally {
            client.close()
        }
    }

    @Test
    fun testNSUrlSanitize() {
        assertEquals(
            "http://127.0.0.1/echo_query?%D0%BF%D1%80%D0%B8%D0%B2%D0%B5%D1%82",
            stringToNSUrlString("http://127.0.0.1/echo_query?привет")
        )

        assertEquals(
            "http://%D0%BF%D1%80%D0%B8%D0%B2%D0%B5%D1%82.%D0%BF%D1%80%D0%B8%D0%B2%D0%B5%D1%82/",
            stringToNSUrlString("http://привет.привет/")
        )
    }

    private fun stringToNSUrlString(value: String): String {
        return Url(value).toNSUrl().absoluteString!!
    }
}
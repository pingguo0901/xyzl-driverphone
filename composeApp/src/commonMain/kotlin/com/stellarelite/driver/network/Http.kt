package com.stellarelite.driver.network

data class HttpResponse(val status: Int, val body: String)

expect suspend fun httpRequest(
    url: String,
    method: String = "GET",
    headers: Map<String, String> = emptyMap(),
    body: String? = null
): HttpResponse

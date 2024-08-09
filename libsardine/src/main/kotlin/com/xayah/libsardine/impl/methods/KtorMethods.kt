package com.xayah.libsardine.impl.methods

import com.xayah.libsardine.model.Propfind
import com.xayah.libsardine.util.KotlinSardineUtil
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.io.IOException

class KtorMethods(private val client: HttpClient) {
    companion object {
        private const val METHOD_PROPFIND = "PROPFIND"
    }

    private fun getDepthString(depth: Int) = if (depth < 0) "infinity" else depth.toString()

    private fun HttpRequestBuilder.stringRequest(depth: Int, body: String, method: String) = run {
        headers {
            append(HttpHeaders.Depth, getDepthString(depth))
        }
        contentType(ContentType.Text.Plain)
        setBody(body)
        this.method = HttpMethod.parse(method)
    }

    @Throws(IOException::class)
    suspend fun propfind(url: String, depth: Int, body: Propfind) = client.request(url) {
        stringRequest(depth = depth, body = KotlinSardineUtil.toXml(body), method = METHOD_PROPFIND)
    }.also { res ->
        if (res.status.value < HttpStatusCode.OK.value || res.status.value >= HttpStatusCode.MultipleChoices.value) {
            throw IOException("Unexpected response $url (${res.status})")
        }
    }
}
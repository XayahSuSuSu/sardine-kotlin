package com.xayah.libsardine.impl

import com.xayah.libsardine.DavResource
import com.xayah.libsardine.KtorSardine
import com.xayah.libsardine.impl.methods.KtorHandler
import com.xayah.libsardine.impl.methods.KtorMethods
import com.xayah.libsardine.model.Allprop
import com.xayah.libsardine.model.Creationdate
import com.xayah.libsardine.model.Displayname
import com.xayah.libsardine.model.Getcontentlength
import com.xayah.libsardine.model.Getcontenttype
import com.xayah.libsardine.model.Getetag
import com.xayah.libsardine.model.Getlastmodified
import com.xayah.libsardine.model.Lockdiscovery
import com.xayah.libsardine.model.Prop
import com.xayah.libsardine.model.Propfind
import com.xayah.libsardine.model.Resourcetype
import com.xayah.libsardine.util.KotlinSardineUtil
import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.auth.providers.bearer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.adaptivity.xmlutil.XmlUtilInternal
import java.io.IOException
import javax.xml.namespace.QName

@OptIn(XmlUtilInternal::class)
class KtorSardineImpl : KtorSardine {
    private var client: HttpClient
    private var methods: KtorMethods

    private suspend fun <T> withIOContext(block: suspend CoroutineScope.() -> T): T =
        withContext(Dispatchers.IO, block)

    constructor() {
        this.client = HttpClient(CIO)
        this.methods = KtorMethods(this.client)
    }

    constructor(client: HttpClient) {
        this.client = client
        this.methods = KtorMethods(this.client)
    }

    constructor(accessToken: String) {
        this.client = HttpClient(CIO) {
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(accessToken, "")
                    }
                }
            }
        }
        this.methods = KtorMethods(this.client)
    }

    constructor(username: String, password: String) {
        this.client = HttpClient(CIO) {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(username = username, password = password)
                    }
                }
            }
        }
        this.methods = KtorMethods(this.client)
    }

    constructor(username: String, password: String, proxyConfig: ProxyConfig) {
        this.client = HttpClient(CIO) {
            engine {
                proxy = proxyConfig
            }

            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(username = username, password = password)
                    }
                }
            }
        }
        this.methods = KtorMethods(this.client)
    }

    @Throws(IOException::class)
    override suspend fun list(url: String): List<DavResource> {
        return this.list(url, 1)
    }

    override suspend fun list(url: String, depth: Int): List<DavResource> {
        return list(url, depth, true)
    }

    private fun addCustomProperties(prop: Prop, props: Set<QName>) {
        val any = prop.any.toMutableList()
        for (entry in props) {
            val element = KotlinSardineUtil.createElement(entry)
            any.add(element)
        }
        prop.any = any
    }

    override suspend fun list(url: String, depth: Int, props: Set<QName>): List<DavResource> {
        val body = Propfind()
        val prop = Prop()
        prop.getcontentlength = Getcontentlength()
        prop.getlastmodified = Getlastmodified()
        prop.creationdate = Creationdate()
        prop.displayname = Displayname()
        prop.getcontenttype = Getcontenttype()
        prop.resourcetype = Resourcetype()
        prop.getetag = Getetag()
        prop.lockdiscovery = Lockdiscovery()
        addCustomProperties(prop, props)
        body.prop = prop
        return propfind(url, depth, body)
    }

    override suspend fun list(url: String, depth: Int, allProp: Boolean): List<DavResource> {
        if (allProp) {
            val body = Propfind()
            body.allprop = Allprop()
            return propfind(url, depth, body)
        } else {
            return list(url, depth, emptySet())
        }
    }

    override suspend fun propfind(url: String, depth: Int, props: Set<QName>): List<DavResource> {
        val body = Propfind()
        val prop = Prop()
        addCustomProperties(prop, props)
        body.prop = prop
        return propfind(url, depth, body)
    }

    suspend fun propfind(url: String, depth: Int, body: Propfind): List<DavResource> = withIOContext {
            KtorHandler.responses(KtorHandler.multiStatus(methods.propfind(url, depth, body)))
        }
}
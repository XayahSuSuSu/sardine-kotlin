package com.xayah.libsardine

import com.xayah.libsardine.impl.KtorSardineImpl
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Local server is powered by alist.
 * @see <a href="https://alist.nn.ci/">alist</a>
 */
class KtorSardineTest {
    @Test
    fun test_list() {
        runBlocking {
            val client = KtorSardineImpl("admin", "admin")
            client.list("http://127.0.0.1:5244/dav").forEach {
                println(it.path)
            }
        }
    }
}
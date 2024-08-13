package com.xayah.libsardine

import com.xayah.libsardine.impl.KtorSardineImpl
import io.ktor.client.statement.bodyAsChannel
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.util.Timer
import java.util.TimerTask

/**
 * Local server is powered by alist.
 * @see <a href="https://alist.nn.ci/">alist</a>
 */
class KtorSardineTest {
    private val server = "http://127.0.0.1:5244/dav"
    private val username = "admin"
    private val password = "admin"
    private var client: KtorSardineImpl = KtorSardineImpl(username, password, 0)

    @Test
    fun test_list() {
        runBlocking {
            client.list(server).forEach {
                println(it.path)
            }
        }
    }

    @Test
    fun test_get() {
        runBlocking {
            var progress = 0F
            val timer = Timer()
            val task = object : TimerTask() {
                override fun run() {
                    println(progress)
                }
            }
            timer.schedule(task, 0, 1000)
            client.get(
                url = "$server/local/test.txt",
                listener = { bytesSentTotal, contentLength ->
                    progress = bytesSentTotal.toFloat() / contentLength
                },
                block = { res ->
                    res.bodyAsChannel().copyAndClose(File("test.txt").writeChannel())
                }
            )
        }
    }

    @Test
    fun test_put() {
        runBlocking {
            var copied = 0L
            var length = 0L
            val timer = Timer()
            val task = object : TimerTask() {
                override fun run() {
                    println("$copied/$length")
                }
            }
            timer.schedule(task, 0, 1000)
            client.put(
                url = "$server/local/large.zip",
                FileInputStream(File("large.zip")),
                listener = { bytesSentTotal, contentLength ->
                    copied = bytesSentTotal
                    length = contentLength
                },
                block = {}
            )
        }
    }

    @Test
    fun test_delete() {
        runBlocking {
            client.delete(url = "$server/local/large.zip")
        }
    }

    @Test
    fun test_put_stream() {
        runBlocking {
            client.put(
                url = "$server/local/stream.txt",
                onWriting = { channel ->
                    var count = 0
                    while (count != 10) {
                        channel.writeByte('K'.code.toByte())
                        delay(1000)
                        count++
                    }
                },
                block = {}
            )
        }
    }

    @Test
    fun test_createDirectory() {
        runBlocking {
            client.createDirectory(url = "$server/local/testDir")
        }
    }

    @Test
    fun test_copy() {
        runBlocking {
            client.copy(sourceUrl = "$server/local/stream.txt", destinationUrl = "$server/local/testDir/stream.txt")
        }
    }

    @Test
    fun test_move() {
        runBlocking {
            client.move(sourceUrl = "$server/local/stream.txt", destinationUrl = "$server/local/testDir/move_stream.txt")
        }
    }
}
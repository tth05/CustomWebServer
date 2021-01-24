package com.github.tth05.webserver.http

import com.github.tth05.webserver.extensions.writeCRLF
import com.github.tth05.webserver.extensions.writeString
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.IllegalArgumentException
import java.util.zip.GZIPOutputStream

class THttpRequest private constructor(
    val path: String,
    val method: THttpMethod,
    val header: Map<String, String>,
    val body: ByteArray
) {

    companion object {
        fun fromStream(stream: InputStream): THttpRequest {
            val reader = BufferedReader(InputStreamReader(stream))
            val firstLine = reader.readLine()?.split(' ') ?: throw HttpRequestReadException()

            val method = try {
                THttpMethod.valueOf(firstLine[0])
            } catch (e: IllegalArgumentException) {
                throw InvalidHttpMethodException()
            }

            val path = firstLine[1]

            val header = mutableMapOf<String, String>()
            var line = reader.readLine()
            while (line.isNotBlank()) {
                header[line.substring(0, line.indexOf(':'))] = line.substring(line.indexOf(':') + 2)
                line = reader.readLine()
            }

            val out = ByteArrayOutputStream()
            while (reader.ready()) {
                out.write(reader.read())
            }

            return THttpRequest(path, method, header, out.toByteArray())
        }
    }
}

class THttpResponse(private val compressed: Boolean) {

    var statusCode = THttpStatusCode.OK
    private val header = mutableMapOf<String, String>()
    val body = ByteArrayOutputStream()

    fun header(p: Pair<String, String>) {
        header += p
    }

    fun writeResource(path: String) {
        val input = this.javaClass.classLoader.getResourceAsStream(path)
            ?: throw IllegalArgumentException("Resource not found $path")

        body.write(input.readBytes())
        input.close()
    }

    fun toByteArray(): ByteArray {
        var bodyArray = body.toByteArray()
        if (compressed && bodyArray.isNotEmpty()) {
            body.reset()
            val gzip = GZIPOutputStream(body, bodyArray.size / 2)
            gzip.write(bodyArray)
            gzip.close()
            bodyArray = body.toByteArray()
        }

        body.reset()
        return body.apply {
            writeString("HTTP/1.1 ")
            writeString("${statusCode.code}")
            writeString(" ${statusCode.stringCode}")
            writeCRLF()
            if (compressed && bodyArray.isNotEmpty())
                header += "Content-Encoding" to "gzip"

            if (bodyArray.isNotEmpty())
                header += "Content-Length" to bodyArray.size.toString()

            header.forEach { (k, v) ->
                writeString("$k: $v")
                writeCRLF()
            }
            writeCRLF()

            if (bodyArray.isNotEmpty())
                write(bodyArray)
        }.toByteArray()
    }
}

enum class THttpStatusCode(val code: Int, val stringCode: String) {
    //2**
    OK(200, "OK"),

    //4**
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found")
}

enum class THttpMethod {
    GET
}

inline fun createResponse(compressed: Boolean, block: THttpResponse.() -> Unit): THttpResponse {
    val response = THttpResponse(compressed)
    block(response)
    return response
}

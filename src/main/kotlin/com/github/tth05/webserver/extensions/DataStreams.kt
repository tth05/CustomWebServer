package com.github.tth05.webserver.extensions

import com.github.tth05.webserver.http.THttpResponse
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

fun InputStream.toByteArray(): ByteArray {
    val buffer = ByteArray(8192)
    val out = ByteArrayOutputStream()
    var r: Int
    while (this.read(buffer, 0, buffer.size).also { r = it } != -1) {
        out.write(buffer, 0, r)
    }

    return out.toByteArray()
}

fun OutputStream.write(response: THttpResponse) {
    write(response.toByteArray())
}

fun ByteArrayOutputStream.writeCRLF() {
    write(13)
    write(10)
}

fun ByteArrayOutputStream.writeString(s: String) {
    write(s.toByteArray())
}

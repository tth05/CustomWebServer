package com.github.tth05.webserver.extensions

import java.io.ByteArrayOutputStream
import java.io.InputStream

fun InputStream.toByteArray(): ByteArray {
    val buffer = ByteArray(8192)
    val out = ByteArrayOutputStream()
    var r: Int
    while (this.read(buffer, 0, buffer.size).also { r = it } != -1) {
        out.write(buffer, 0, r)
    }

    return out.toByteArray()
}

fun ByteArrayOutputStream.writeCRLF() {
    write(13)
    write(10)
}

fun ByteArrayOutputStream.writeString(s: String) {
    write(s.toByteArray())
}

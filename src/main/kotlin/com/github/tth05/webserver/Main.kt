import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer

fun main() {
    val socket = ServerSocket(6701)
    val s = socket.accept()

    var inStream = s.getInputStream()

    while (true) {
        val builder = StringBuilder()
        while (true) {
            val read = inStream.read()
            if (read == 10)
                break
            builder.append(read.toChar())
        }

        println(builder.toString())

        if(inStream.available() < 1) {
            val list = mutableListOf<Byte>()
            list.addAll("HTTP/1.1 200 OK".toByteArray().toList())
            list.add(13)
            list.add(10)
            list.addAll("Content-Type: text/html".toByteArray().toList())
            list.add(13)
            list.add(10)
            list.add(13)
            list.add(10)
            list.addAll("Hallo".toByteArray().toList())
            list.add(13)
            list.add(10)
            s.getOutputStream().write(list.toByteArray())
        }
    }
}
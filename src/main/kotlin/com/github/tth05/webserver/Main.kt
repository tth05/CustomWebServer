import com.github.tth05.webserver.http.THttpRequest
import com.github.tth05.webserver.http.THttpResponse
import com.github.tth05.webserver.http.THttpStatusCode
import com.github.tth05.webserver.http.createResponse
import java.net.ServerSocket

fun main(args: Array<String>) {
    val socket = ServerSocket(8081)

    while (true) {
        val s = socket.accept()
        val inStream = s.getInputStream()

        val request = THttpRequest.fromStream(inStream)
        var response: THttpResponse

        if (request.path == "/") {
            response = createResponse(true) {
                header("Content-Type" to "text/html")
                writeResource("index.html")
            }
        } else if (request.path == "/favicon.ico") {
            response = createResponse(true) {
                header("Content-Type" to "image/png")
                writeResource("favicon.png")
            }
        } else {
            response = createResponse(true) {
//                    header("Content-Type" to "image/png")
                try {
                    writeResource(request.path.substring(1))
                } catch (e: IllegalArgumentException) {
                    statusCode = THttpStatusCode.NOT_FOUND
                }
            }
        }

        s.getOutputStream().write(response.toByteArray())
        s.close()
    }
}

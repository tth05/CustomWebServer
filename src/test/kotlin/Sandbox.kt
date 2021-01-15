import com.github.tth05.webserver.extensions.writeString
import com.github.tth05.webserver.http.createResponse
import com.github.tth05.webserver.server.TWebServer
import com.github.tth05.webserver.server.createDefaultRouter

fun main() {
    TWebServer(8081, createDefaultRouter {
        addRoute("/test") {
            createResponse(false) {
                body.writeString("Hello, world!")
            }
        }
    })
}

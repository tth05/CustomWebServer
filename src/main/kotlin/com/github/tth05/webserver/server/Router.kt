package com.github.tth05.webserver.server

import com.github.tth05.webserver.http.THttpRequest
import com.github.tth05.webserver.http.THttpResponse
import com.github.tth05.webserver.http.THttpStatusCode
import com.github.tth05.webserver.http.createResponse
import java.lang.IllegalArgumentException
import java.util.*
import java.util.regex.Pattern

typealias RequestHandler = (THttpRequest) -> THttpResponse

class TRouter {

    private val routes = LinkedList<Pair<Regex, RequestHandler>>()

    fun addRoute(pattern: String, handler: RequestHandler) {
        if (pattern.isBlank())
            throw IllegalArgumentException("Invalid route matcher")

        routes += Regex("^$pattern$") to handler
    }

    fun getRequestHandler(route: String): RequestHandler? {
        return routes.firstOrNull { it.first.matches(route) }?.second
    }
}

inline fun createDefaultRouter(block: TRouter.() -> Unit): TRouter {
    val router = TRouter()

    block.invoke(router)

    router.addRoute("/") {
        createResponse(true) {
            header("Content-Type" to "text/html")
            writeResource("index.html")
        }
    }
    router.addRoute("/favicon.ico") {
        createResponse(true) {
            header("Content-Type" to "image/png")
            writeResource("favicon.png")
        }
    }
    router.addRoute(".*") {
        createResponse(true) {
            try {
                writeResource(it.path.substring(1))
            } catch (e: IllegalArgumentException) {
                statusCode = THttpStatusCode.NOT_FOUND
            }
        }
    }

    return router
}

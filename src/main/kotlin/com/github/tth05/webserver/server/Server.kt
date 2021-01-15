package com.github.tth05.webserver.server

import com.github.tth05.webserver.http.THttpRequest
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

/**
 * @constructor Creates and starts a new server
 * @param port The port to bind this server to
 */
class TWebServer(port: Int, private val router: TRouter) {

    private val executorService = Executors.newFixedThreadPool(10)
    private val socket: ServerSocket = ServerSocket(port)

    init {
        while (true) {
            val socket = socket.accept()
            executorService.execute {
                handleRequest(socket)
            }
        }
    }

    private fun handleRequest(socket: Socket) {
        val inStream = socket.getInputStream()

        val request = THttpRequest.fromStream(inStream)

        val response = router.getRequestHandler(request.path)?.invoke(request)
            ?: throw IllegalStateException("No request handler found for ${request.path}")

        socket.getOutputStream().write(response.toByteArray())
        socket.close()
    }
}

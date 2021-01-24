package com.github.tth05.webserver.server

import com.github.tth05.webserver.extensions.write
import com.github.tth05.webserver.http.*
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

        val request: THttpRequest
        try {
            request = THttpRequest.fromStream(inStream)
        } catch (e: Exception) {
            when (e) {
                is InvalidHttpMethodException, is HttpRequestReadException -> {
                    socket.getOutputStream().write(createResponse(false) {
                        statusCode = THttpStatusCode.BAD_REQUEST
                    })
                    socket.close()
                    return
                }
                else -> throw e
            }
        }

        val response = router.getRequestHandler(request.path)?.invoke(request)
            ?: throw IllegalStateException("No request handler found for ${request.path}")

        socket.getOutputStream().write(response)
        socket.close()
    }
}

package com.example.vert_x_poc

import io.vertx.ext.web.Router
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainVerticle : CoroutineVerticle() {

  override suspend fun start(/*startPromise: Promise<Void>?*/) {
    // Create a Router
    val router = Router.router(vertx)
    // Mount the handler for all incoming requests at every path and HTTP method

    router.route().handler { context ->
      // Get the address of the request
      //GlobalScope.launch(vertx.dispatcher()) {
      launch(context.vertx().dispatcher()) {
        val address = context.request().connection().remoteAddress().toString()
        // Get the query parameter "name"
        val queryParams = context.queryParams()
        val name = queryParams.get("name") ?: "unknown"
        // Write a json response
        val currentThread = Thread.currentThread()

        val threadIdentification =
          "Thread Identification b [ThreadId: ${currentThread.id}, ThreadName: ${currentThread.name}"
        //awaitEvent<Long> {
        delay(10000)
        context.json(

          json {
            obj(
              "name" to name,
              "address" to address,
              "message" to "Hello $name connected from $address",
              "thread" to threadIdentification
            )
          }
        )

      }

    }

    // Create the HTTP server
    vertx.createHttpServer()
      // Handle every request using the router
      .requestHandler(router)
      // Start listening
      .listen(8888)
      // Print the port
      .onSuccess { server ->
        println("HTTP server started on port " + server.actualPort())
      }
  }
}

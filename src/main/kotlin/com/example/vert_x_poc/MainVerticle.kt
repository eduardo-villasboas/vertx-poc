package com.example.vert_x_poc

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.rxjava3.core.AbstractVerticle
import io.vertx.rxjava3.ext.web.Router
import java.util.concurrent.Executors
import kotlin.random.Random

class MainVerticle : AbstractVerticle() {

  override fun rxStart(): Completable {
    // Create a Router
    val router = Router.router(vertx)
    // Mount the handler for all incoming requests at every path and HTTP method

    router.route().handler { context ->
      // Get the address of the request
      //GlobalScope.launch(vertx.dispatcher()) {
      val address = context.request().connection().remoteAddress().toString()
      // Get the query parameter "name"
      val queryParams = context.queryParams()
      val name = queryParams.get("name") ?: "unknown"
      // Write a json response

      context.vertx().rxExecuteBlocking<Any> {

        val currentThread = Thread.currentThread()
        val threadIdentification =
          "Thread Identification [ThreadId: ${currentThread.id}, ThreadName: ${currentThread.name}"

        val random = Random(System.currentTimeMillis())
        val limit = 10000000000
        var printCount = 0
        for (i in 1..limit) {
          if (i % (limit / 10L) == 0L) {
            println("index=${++printCount} on thread $threadIdentification => ${random.nextInt(100)}")
          }
        }
        it.complete(json {
          obj(
            "name" to name,
            "address" to address,
            "message" to "Hello $name connected from $address",
            "thread" to threadIdentification
          )
        })
        //   }
      }.subscribeOn(Schedulers.computation())
        .subscribe {
          println(it)
          context.json(it)
        }

    }

    val server = vertx.createHttpServer()
    // Create the HTTP server
    return server
      // Handle every request using the router
      .requestHandler(router)
      // Start listening
      .rxListen(8888)
      // Print the port
      .ignoreElement()
  }
}

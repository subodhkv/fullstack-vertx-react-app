package com.curd.vertx_app;

import com.curd.vertx_app.controller.ProductController;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() {
    JsonObject mongoConfig = new JsonObject()
      .put("connection_string", "mongodb://localhost:27017")
      .put("db_name", "test");

    ProductController productController = new ProductController(vertx, mongoConfig);
    Router router = productController.getRouter();

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080, result -> {
        if (result.succeeded()) {
          System.out.println("HTTP server started on port 8080");
        } else {
          System.err.println("Failed to start HTTP server");
        }
      });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}

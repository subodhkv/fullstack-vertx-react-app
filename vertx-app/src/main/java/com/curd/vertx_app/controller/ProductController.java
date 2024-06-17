package com.curd.vertx_app.controller;
import com.curd.vertx_app.model.Product;
import com.curd.vertx_app.service.ProductService;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
public class ProductController {

  private final ProductService productService;

  public ProductController(Vertx vertx, JsonObject mongoConfig) {
    this.productService = new ProductService(vertx, mongoConfig);
  }

  public Router getRouter() {
    Router router = Router.router(Vertx.vertx());
    CorsHandler corsHandler = CorsHandler.create("http://localhost:3000")
      .allowedMethod(HttpMethod.GET)
      .allowedMethod(HttpMethod.POST)
      .allowedMethod(HttpMethod.PUT)
      .allowedMethod(HttpMethod.DELETE)
      .allowedHeader("Content-Type");
    router.route().handler(corsHandler);
    router.route().handler(BodyHandler.create());

    router.get("/api/products").handler(this::getAllProductsHandler);
    router.post("/api/products").handler(this::addProductHandler);
    router.get("/api/products/:id").handler(this::findByIdHandler);
    router.delete("/api/products/:id").handler(this::deleteByIdHandler);
    router.put("/api/products/:id").handler(this::updateProductHandler);

    return router;
  }


  private void getAllProductsHandler(RoutingContext context) {
    String pageParam = context.request().getParam("pageNumber");
    String pageSizeParam = context.request().getParam("pageSize");
    String searchTerm = context.request().getParam("searchTerm");
    String sortBy = context.request().getParam("sortBy");
    String sortOrder = context.request().getParam("sortOrder");
    int pageNumber = pageParam != null ? Integer.parseInt(pageParam) : 1; // Default page number to 1 if not provided
    int defaultPageSize = 10;
    int pageSize = pageSizeParam != null ? Integer.parseInt(pageSizeParam) : defaultPageSize;
    pageSize = Math.min(Math.max(pageSize, 1), 100);

    productService.getAllProducts(pageNumber, pageSize, searchTerm, sortBy, sortOrder, result -> {
      if (result.succeeded()) {
        context.response()
          .setStatusCode(200)
          .putHeader("content-type", "application/json")
          .end(result.result().encodePrettily());
      } else {
        context.fail(result.cause());
      }
    });
  }





  private void addProductHandler(RoutingContext context) {
    JsonObject bodyJson = context.getBodyAsJson();
    if (bodyJson == null) {
      context.fail(400); // Bad Request
      return;
    }
    Product product = new Product(bodyJson);
    productService.addProduct(product, result -> {
      if (result.succeeded()) {
        context.response()
          .setStatusCode(201)
          .end("Product added successfully");
      } else {
        context.fail(result.cause());
      }
    });
  }

  private void findByIdHandler(RoutingContext context) {
    String productId = context.request().getParam("id");

    productService.findById(productId, result -> {
      if (result.succeeded()) {
        Product product = result.result();
        context.response()
          .putHeader("content-type", "application/json")
          .end(Json.encodePrettily(product));
      } else {
        context.fail(404); // Product not found
      }
    });
  }

  private void deleteByIdHandler(RoutingContext context) {
    String productId = context.request().getParam("id");

    productService.deleteById(productId, result -> {
      if (result.succeeded()) {
        context.response()
          .setStatusCode(204) // No content
          .end();
      } else {
        context.fail(500); // Internal server error
      }
    });
  }
  private void updateProductHandler(RoutingContext context) {
    String productId = context.request().getParam("id");
    JsonObject bodyJson = context.getBodyAsJson();
    if (bodyJson == null) {
      context.fail(400); // Bad Request
      return;
    }
    Product product = new Product(bodyJson);
    productService.updateProduct(productId, product, result -> {
      if (result.succeeded()) {
        context.response()
          .setStatusCode(204) // No content
          .end();
      } else {
        context.fail(result.cause());
      }
    });
  }
}

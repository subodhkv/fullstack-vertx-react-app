package com.curd.vertx_app.service;

import com.curd.vertx_app.model.Product;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import java.util.List;
import java.util.regex.Pattern;

public class ProductService {

  private final MongoClient mongoClient;

  public ProductService(Vertx vertx, JsonObject mongoConfig) {
    this.mongoClient = MongoClient.createShared(vertx, mongoConfig);
  }

 /* public void getAllProducts(Handler<AsyncResult<List<Product>>> resultHandler) {
    JsonObject query = new JsonObject();
    mongoClient.find("products", query, findResult -> {
      if (findResult.succeeded()) {
        List<JsonObject> jsonProducts = findResult.result();
        List<Product> products = jsonProducts.stream()
          .map(Product::new)
          .toList();
        resultHandler.handle(io.vertx.core.Future.succeededFuture(products));
      } else {
        resultHandler.handle(io.vertx.core.Future.failedFuture(findResult.cause()));
      }
    });
  }*/
  // ProductService.java

  public void getAllProducts(int pageNumber, int pageSize, String searchTerm, String sortBy, String sortOrder, Handler<AsyncResult<JsonObject>> resultHandler) {
    int skip = (pageNumber - 1) * pageSize;
    JsonObject query = new JsonObject();

    if (searchTerm != null && !searchTerm.isEmpty()) {
      query.put("$or", new JsonArray()
        .add(new JsonObject().put("name", new JsonObject().put("$regex", searchTerm).put("$options", "i")))
        .add(new JsonObject().put("description", new JsonObject().put("$regex", searchTerm).put("$options", "i")))
        .add(new JsonObject().put("price", new JsonObject().put("$regex", searchTerm).put("$options", "i"))));
    }

    JsonObject sort = new JsonObject().put(sortBy, sortOrder.equals("asc") ? 1 : -1);

    mongoClient.count("products", query, countResult -> {
      if (countResult.succeeded()) {
        long totalCount = countResult.result();
        mongoClient.findWithOptions("products", query, new FindOptions().setLimit(pageSize).setSkip(skip).setSort(sort), findResult -> {
          if (findResult.succeeded()) {
            List<JsonObject> jsonProducts = findResult.result();
            JsonObject response = new JsonObject()
              .put("products", jsonProducts)
              .put("totalCount", totalCount);
            resultHandler.handle(Future.succeededFuture(response));
          } else {
            resultHandler.handle(Future.failedFuture(findResult.cause()));
          }
        });
      } else {
        resultHandler.handle(Future.failedFuture(countResult.cause()));
      }
    });
  }







  public void addProduct(Product product, Handler<AsyncResult<String>> resultHandler) {
    JsonObject jsonProduct = product.toJson();
    mongoClient.insert("products", jsonProduct, ar -> {
      if (ar.succeeded()) {
        String generatedId = ar.result();
        resultHandler.handle(io.vertx.core.Future.succeededFuture(generatedId));
      } else {
        resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
      }
    });
  }

  public void findById(String productId, Handler<AsyncResult<Product>> resultHandler) {
    JsonObject query = new JsonObject().put("_id", productId);
    mongoClient.findOne("products", query, new JsonObject(), ar -> {
      if (ar.succeeded()) {
        JsonObject json = ar.result();
        if (json != null) {
          Product product = new Product(json);
          resultHandler.handle(io.vertx.core.Future.succeededFuture(product));
        } else {
          resultHandler.handle(io.vertx.core.Future.failedFuture("Product not found"));
        }
      } else {
        resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
      }
    });
  }

  public void deleteById(String productId, Handler<AsyncResult<Void>> resultHandler) {
    JsonObject query = new JsonObject().put("_id", productId);
    mongoClient.removeDocument("products", query, ar -> {
      if (ar.succeeded()) {
        resultHandler.handle(io.vertx.core.Future.succeededFuture());
      } else {
        resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
      }
    });
  }
  public void updateProduct(String productId, Product product, Handler<AsyncResult<Void>> resultHandler) {
    JsonObject query = new JsonObject().put("_id", productId);
    JsonObject update = new JsonObject().put("$set", product.toJson());
    mongoClient.updateCollection("products", query, update, ar -> {
      if (ar.succeeded()) {
        resultHandler.handle(io.vertx.core.Future.succeededFuture());
      } else {
        resultHandler.handle(io.vertx.core.Future.failedFuture(ar.cause()));
      }
    });
  }
}

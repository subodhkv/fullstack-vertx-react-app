package com.curd.vertx_app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
public class Product {

  @JsonProperty("_id")
  private String id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  @JsonProperty("price")
  private String price;

  public Product(JsonObject json) {
    this.id = json.getString("_id");
    this.name = json.getString("name");
    this.description = json.getString("description");
    this.price = json.getString("price");
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("name", this.name);
    json.put("description", this.description);
    json.put("price", this.price);
    return json;
  }

  public ObjectId getIdAsObjectId() {
    return new ObjectId(this.id); // Convert String _id to ObjectId
  }
}

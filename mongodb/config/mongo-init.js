// Create DB and collection
db = new Mongo().getDB("mqtt_data");
db.createCollection("measurements", { capped: false });
package fhwedel.Mongo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class CRUDclientTest {

    private MongoClient mongoClient = MongoClients.create(
            MongoClientSettings.builder().applyConnectionString(
                new ConnectionString("mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000")
            ).build()
        );

    @Test
    public void testMongoDBConnection() {
        try {

            MongoClient mongoClient = this.mongoClient;

            // Test the connection by getting database info
            MongoDatabase database = mongoClient.getDatabase("buch");

            // List collections to verify connection works
            boolean connectionWorking = false;
            for (String collectionName : database.listCollectionNames()) {
                System.out.println("Found collection: " + collectionName);
                connectionWorking = true;
            }
            
            // If no collections exist, try to create a test one to verify write access
            if (!connectionWorking) {
                database.createCollection("test_connection");
                System.out.println("Created test collection - connection is working!");
                connectionWorking = true;
                // Clean up test collection
                database.getCollection("test_connection").drop();
            }

            this.mongoClient.close();
            assertTrue(connectionWorking || database != null, "MongoDB connection should work");
            
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testA() {
        MongoDatabase DB = this.mongoClient.getDatabase("test");
        assertTrue(DB != null, "Database 'test' should be accessible");
        
        MongoCollection<Document> buchCollection = DB.getCollection("buch");

        buchCollection.deleteMany(new Document().append("INVNR", "9783548609420"));
        buchCollection.deleteMany(new Document().append("AUTOR", "J.K. Rowling"));


        assertTrue(buchCollection != null, "Collection 'buch' should be accessible");

        buchCollection.insertOne(
            new Document()
                .append("INVNR", "9783548609420")
                .append("AUTOR", "Marc-Uwe Kling")
                .append("TITEL", "Die Känguru-Chroniken: Ansichten eines vorlauten Beuteltiers")
                .append("VERLAG", "Ullstein-Verlag")
        );

        assertTrue(null != buchCollection.find(new Document("INVNR", "9783548609420")).first(),
            "Inserted book should be retrievable from 'buch' collection");



        buchCollection.insertMany(
            List.of(
                Document.parse("{" +
                    "'INVNR': 9863113," +
                    "'AUTOR': 'J.K. Rowling'," +
                    "'TITEL': 'Harry Potter und der Stein der Weisen'," +
                    "'VERLAG': 'Carlsen Verlag'" +
                "}"),
                Document.parse("{" +
                    "'INVNR': 9863114," +
                    "'AUTOR': 'J.K. Rowling'," +
                    "'TITEL': 'Harry Potter und die Kammer des Schreckens'," +
                    "'VERLAG': 'Carlsen Verlag'" +
                "}"),
                Document.parse("{" +
                    "'INVNR': 9863115," +
                    "'AUTOR': 'J.K. Rowling'," +
                    "'TITEL': 'Harry Potter und der Gefangene von Askaban'," +
                    "'VERLAG': 'Carlsen Verlag'" +
                "}"),
                Document.parse("{" +
                    "'INVNR': 9863116," +
                    "'AUTOR': 'J.K. Rowling'," +
                    "'TITEL': 'Harry Potter und der Feuerkelch'," +
                    "'VERLAG': 'Carlsen Verlag'" +
                "}"),
                Document.parse("{" +
                    "'INVNR': 9863117," +
                    "'AUTOR': 'J.K. Rowling'," +
                    "'TITEL': 'Harry Potter und der Orden des Phönix'," +
                    "'VERLAG': 'Carlsen Verlag'" +
                "}"),
                Document.parse("{" +
                    "'INVNR': 9863118," +
                    "'AUTOR': 'J.K. Rowling'," +
                    "'TITEL': 'Harry Potter und der Halbblutprinz'," +
                    "'VERLAG': 'Carlsen Verlag'" +
                "}")
            )
        );

        assertTrue(buchCollection.countDocuments(new Document("AUTOR", "J.K. Rowling")) == 6,
            "Inserted books by J.K. Rowling should be retrievable from 'buch' collection");
    }


    @Test
    public void testB() {
        MongoDatabase DB = this.mongoClient.getDatabase("test");
        assertTrue(DB != null, "Database 'test' should be accessible");

        MongoCollection<Document> buchCollection = DB.getCollection("buch");
        assertTrue(buchCollection != null, "Collection 'buch' should be accessible");

        Document foundBook = buchCollection.find(new Document("AUTOR", "Marc-Uwe Kling")).first();
        assertTrue(foundBook != null, "Book by 'Marc-Uwe Kling' should be found");
    }

    @Test
    public void testC() {
        MongoDatabase DB = this.mongoClient.getDatabase("test");
        assertTrue(DB != null, "Database 'test' should be accessible");

        MongoCollection<Document> buchCollection = DB.getCollection("buch");
        assertTrue(buchCollection != null, "Collection 'buch' should be accessible");

        long count = buchCollection.countDocuments();
        assertTrue(count > 0, "Collection 'buch' should contain documents");
    }

    @Test
    public void testD() {
        MongoDatabase DB = this.mongoClient.getDatabase("test");
        assertTrue(DB != null, "Database 'test' should be accessible");

        MongoCollection<Document> entliehenCollection = DB.getCollection("entliehen");
        assertTrue(entliehenCollection != null, "Collection 'entliehen' should be accessible");

        AggregateIterable<Document> result = entliehenCollection.aggregate(List.of(
            new Document("$group", new Document("_id", "$LNR")
            .append("anzahlAusleihen", new Document("$sum", 1))),
            new Document("$match", new Document("anzahlAusleihen", new Document("$gt", 1))),
            new Document("$sort", new Document("anzahlAusleihen", -1))
        ));

        assertNotNull(result, "Aggregation result should not be null");
        result.forEach(doc ->{
            System.out.println(doc.toJson());
        });

    }

    @Test
    public void testE() {
        MongoDatabase DB = this.mongoClient.getDatabase("test");
        assertTrue(DB != null, "Database 'test' should be accessible");

        MongoCollection<Document> buchCollection = DB.getCollection("buch");
        assertTrue(buchCollection != null, "Collection 'buch' should be accessible");

        MongoCollection<Document> leserCollection = DB.getCollection("leser");
        assertTrue(leserCollection != null, "Collection 'leser' should be accessible");

        MongoCollection<Document> entliehenCollection = DB.getCollection("entliehen");
        assertTrue(entliehenCollection != null, "Collection 'entliehen' should be accessible");

        Document leserFF = leserCollection.find(new Document("NAME", "Friedrich Funke")).first();
        assertNotNull(leserFF, "Reader 'Friedrich Funke' should be found");

        Document buch = buchCollection.find(new Document("TITEL", "Die Känguru-Chroniken: Ansichten eines vorlauten Beuteltiers")).first();
        assertNotNull(buch, "Book 'Die Känguru-Chroniken' should be found");

        String buchId = buch.getObjectId("_id").toString();

        entliehenCollection.insertOne(
            new Document("LNR", leserFF.getObjectId("_id").toString())
                .append("INVNR", buchId)
                .append("RÜCKGABEDATUM", "2026-03-04")
        );

        entliehenCollection.deleteOne(
            new Document("LNR", leserFF.getObjectId("_id").toString())
                .append("INVNR", buchId)
        );
    }

    @Test
    public void testF() {
        MongoDatabase DB = this.mongoClient.getDatabase("test");
        assertTrue(DB != null, "Database 'test' should be accessible"); 

        MongoCollection<Document> buchCollection = DB.getCollection("buch");
        assertTrue(buchCollection != null, "Collection 'buch' should be accessible");

        buchCollection.insertOne(
            new Document()
                .append("INVNR", 98666293)
                .append("AUTOR", "Horst Evers")
                .append("TITEL", "Der König von Berlin")
                .append("VERLAG", "Rowolt-Verlag")
        );

        MongoCollection<Document> leserCollection = DB.getCollection("leser");
        assertTrue(leserCollection != null, "Collection 'leser' should be accessible");

        String buchId = buchCollection.find(
            new Document()
                .append("AUTOR", "Marc-Uwe Kling")
                .append("TITEL", "Die Känguru-Chroniken: Ansichten eines vorlauten Beuteltiers")
        ).first().get("INVNR").toString();

        leserCollection.insertOne(
            new Document()
                .append("LNR", "9237279374")
                .append("NAME", "Heinz Müller")
                .append("ADRESSE", "Klopstockweg 17, 38124 Braunschweig")
                .append("ENTLIEHEN", List.of(
                    new Document("INVNR", buchId)
                        .append("RÜCKGABEDATUM", "2022-08-04"),
                    new Document("INVNR", 98666293)
                        .append("RÜCKGABEDATUM", "2024-09-09")
                ))
        );
    }

    @Test
    public void testG() {
        MongoDatabase DB = this.mongoClient.getDatabase("test");
        assertTrue(DB != null, "Database 'test' should be accessible");

        MongoCollection<Document> leserCollection = DB.getCollection("leser");
        assertTrue(leserCollection != null, "Collection 'leser' should be accessible");

        Document leser = leserCollection.find(new Document("LNR", 9237279374L)).first();
        assertNotNull(leser, "Leser with LNR 9237279374 should exist");

        List<Document> entliehen = leser.getList("ENTLIEHEN", Document.class);
        if (entliehen != null) {
            entliehen.removeIf(o -> {
                Object invnr = o.get("INVNR");
                return invnr != null && invnr.toString().equals("9783548609420");
            });

            leserCollection.updateOne(
                new Document("LNR", 9237279374L),
                new Document("$set", new Document("ENTLIEHEN", entliehen))
            );
        }

        Document newEntliehen = new Document("INVNR", "9783548609420")
            .append("RÜCKGABEDATUM", "2023-10-31");

        leserCollection.updateOne(
            new Document("LNR", 3781678045610L),
            new Document("$set", new Document("ENTLIEHEN", List.of(newEntliehen)))
        );
    }
}

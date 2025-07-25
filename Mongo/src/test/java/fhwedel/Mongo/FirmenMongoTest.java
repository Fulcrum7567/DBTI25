package fhwedel.Mongo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoCollection;

public class FirmenMongoTest {
    

    @Test
    public void testA() {
        FirmenMongo firmenMongo = new FirmenMongo();
        firmenMongo.resetFirma();
        firmenMongo.createFirma();
        firmenMongo.connect();
        firmenMongo.importGehalt();
        firmenMongo.importKind();
        firmenMongo.importMaschine();
        firmenMongo.importPersonal();

        MongoCollection<Document> personalCollection = firmenMongo.mongoClient.getDatabase("Firma").getCollection("personal");

        Document newPerson = new Document("name", "Krause")
                .append("vorname", "Henrik")
                .append("krankenkasse", "tkk")
                .append("abteilung", "Produktion")
                .append("gehalt", "it1");

        personalCollection.insertOne(newPerson);

        assertEquals("Henrik", personalCollection.find(new Document("name", "Krause").append("vorname", "Henrik")).first().getString("vorname"));

    }

    @Test
    public void testB() {
        FirmenMongo firmenMongo = new FirmenMongo();
        firmenMongo.resetFirma();
        firmenMongo.createFirma();
        firmenMongo.connect();
        firmenMongo.importGehalt();
        firmenMongo.importKind();
        firmenMongo.importMaschine();
        firmenMongo.importPersonal();

        MongoCollection<Document> personalCollection = firmenMongo.mongoClient.getDatabase("Firma").getCollection("personal");

        personalCollection.find().forEach(doc -> {
            System.out.println(doc.toJson());
        });

    }

    @Test
    public void testC() {
        FirmenMongo firmenMongo = new FirmenMongo();
        firmenMongo.resetFirma();
        firmenMongo.createFirma();
        firmenMongo.connect();
        firmenMongo.importGehalt();
        firmenMongo.importKind();
        firmenMongo.importMaschine();
        firmenMongo.importPersonal();

        MongoCollection<Document> gehaltCollection = firmenMongo.mongoClient.getDatabase("Firma").getCollection("gehalt");

        Document it1Doc = gehaltCollection.find(new Document("stufe", "it1")).first();
        if (it1Doc != null) {
            int currentGehalt = it1Doc.getInteger("betrag");
            int newGehalt = (int) (currentGehalt * 1.10);
            gehaltCollection.updateOne(
            new Document("stufe", "it1"),
            new Document("$set", new Document("betrag", newGehalt))
            );
            assertEquals((int)(currentGehalt * 1.10), gehaltCollection.find(new Document("stufe", "it1")).first().getInteger("betrag"));

        }

    }

    @Test
    public void testD() {
        FirmenMongo firmenMongo = new FirmenMongo();
        firmenMongo.resetFirma();
        firmenMongo.createFirma();
        firmenMongo.connect();
        firmenMongo.importGehalt();
        firmenMongo.importKind();
        firmenMongo.importMaschine();
        firmenMongo.importPersonal();

        MongoCollection<Document> personalCollection = firmenMongo.mongoClient.getDatabase("Firma").getCollection("personal");

        personalCollection.deleteOne(new Document("name", "Tietze").append("vorname", "Lutz"));
        assertEquals(null, personalCollection.find(new Document("name", "Tietze").append("vorname", "Lutz")).first());
    }

    @Test
    public void testE() {
        FirmenMongo firmenMongo = new FirmenMongo();
        firmenMongo.resetFirma();
        firmenMongo.createFirma();
        firmenMongo.connect();
        firmenMongo.importGehalt();
        firmenMongo.importKind();
        firmenMongo.importMaschine();
        firmenMongo.importPersonal();


        MongoCollection<Document> personalCollection = firmenMongo.mongoClient.getDatabase("Firma").getCollection("personal");

        System.out.println("Personal in der Abteilung Verkauf:");
        personalCollection.find().forEach(doc -> {
            if (doc.getString("abteilung").equals("Verkauf")) {
                System.out.println(doc.toJson());
            }
        });

    }
}

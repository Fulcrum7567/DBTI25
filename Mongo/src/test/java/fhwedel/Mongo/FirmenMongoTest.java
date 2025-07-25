package fhwedel.Mongo;

import org.bson.Document;
import org.junit.jupiter.api.Test;

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

        firmenMongo.mongoClient.getDatabase("firma").getCollection("personal").insertOne(
            new Document("name", "Krause")
                .append("vorname", "Henrik")
                .append("krankenkasse", "tkk")
                .append("abteilung", "Produktion")
                .append("gehalt", "it1")
        );
        
    }
}

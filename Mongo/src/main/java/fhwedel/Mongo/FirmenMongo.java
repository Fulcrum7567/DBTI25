package fhwedel.Mongo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class FirmenMongo {

    Connection oldFirma;
    
    public void connect() {
        try {
            this.oldFirma = DriverManager.getConnection("jdbc:mariadb://localhost:3306/firma", "root", "password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FirmenMongo firmenMongo = new FirmenMongo();
        firmenMongo.resetFirma();
        firmenMongo.createFirma();
        
        firmenMongo.listFirma();
        firmenMongo.connect();
        firmenMongo.importGehalt();
        firmenMongo.listGehalt();
        firmenMongo.importKind();
        firmenMongo.listKind();
        firmenMongo.importMaschine();
        firmenMongo.listMaschine();
        
        firmenMongo.importPersonal();
        firmenMongo.listPersonal();
    }


    private MongoClient mongoClient = MongoClients.create(
            MongoClientSettings.builder().applyConnectionString(
                new ConnectionString("mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000")
            ).build()
        );


    public void resetFirma() {
        MongoClient client = this.mongoClient;

        MongoDatabase firma = client.getDatabase("Firma");
        if (firma != null) {
            System.out.println("Firma database is ready.");
        } else {
            System.out.println("Failed to connect to Firma database.");
        }

        // Drop existing collections if they exist
        firma.getCollection("gehalt").drop();
        System.out.println("Collection 'gehalt' dropped from Firma database.");
        firma.getCollection("kind").drop();
        System.out.println("Collection 'kind' dropped from Firma database.");
        firma.getCollection("maschine").drop();
        System.out.println("Collection 'maschine' dropped from Firma database.");
        firma.getCollection("personal").drop();
        System.out.println("Collection 'personal' dropped from Firma database.");
    }

    public void createFirma() {
        MongoClient client = this.mongoClient;


        MongoDatabase firma = client.getDatabase("Firma");
        if (firma != null) {
            System.out.println("Firma database is ready.");
        } else {
            System.out.println("Failed to connect to Firma database.");
        }

        firma.createCollection("gehalt");
        System.out.println("Collection 'gehalt' created in Firma database.");
        firma.createCollection("kind");
        System.out.println("Collection 'kind' created in Firma database.");
        firma.createCollection("maschine");
        System.out.println("Collection 'maschine' created in Firma database.");
        firma.createCollection("personal");
        System.out.println("Collection 'personal' created in Firma database.");


    }

    public void listFirma() {
        MongoClient client = this.mongoClient;

        MongoDatabase firma = client.getDatabase("Firma");
        if (firma != null) {
            System.out.println("Listing Firma database collections:");
            for (String name : firma.listCollectionNames()) {
                System.out.println(name);
            }
        } else {
            System.out.println("Failed to connect to Firma database.");
        }
    }

    public void importGehalt() {
        MongoDatabase newFirma = this.mongoClient.getDatabase("Firma");

        // Import gehalt
        ResultSet gehaltResultSet;
        try {
            gehaltResultSet = oldFirma.createStatement().executeQuery("SELECT * FROM gehalt");
        
            while (gehaltResultSet.next()) {
                Document gehaltDoc = new Document("stufe", gehaltResultSet.getString("geh_stufe"))
                    .append("betrag", gehaltResultSet.getInt("betrag"));

                System.out.println("Importing gehalt: " + gehaltDoc.toJson());
                newFirma.getCollection("gehalt").insertOne(gehaltDoc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listGehalt() {
        MongoDatabase newFirma = this.mongoClient.getDatabase("Firma");

        System.out.println("Listing gehalt collection:");
        for (Document doc : newFirma.getCollection("gehalt").find()) {
            System.out.println(doc.toJson());
        }
    }

    public void importKind() {
        MongoDatabase newFirma = this.mongoClient.getDatabase("Firma");

        // Import kind
        ResultSet kindResultSet;
        try {
            kindResultSet = oldFirma.createStatement().executeQuery("SELECT * FROM kind");
        
            while (kindResultSet.next()) {
                Document kindDoc = new Document("name", kindResultSet.getString("k_name"))
                    .append("vorname", kindResultSet.getString("k_vorname"))
                    .append("geburtsdatum", kindResultSet.getString("k_geb"))
                    .append("pnr", kindResultSet.getString("pnr"));

                System.out.println("Importing kind: " + kindDoc.toJson());
                newFirma.getCollection("kind").insertOne(kindDoc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listKind() {
        MongoDatabase newFirma = this.mongoClient.getDatabase("Firma");

        System.out.println("Listing kind collection:");
        for (Document doc : newFirma.getCollection("kind").find()) {
            System.out.println(doc.toJson());
        }
    }

    public void importMaschine() {
        MongoDatabase newFirma = this.mongoClient.getDatabase("Firma");

        // Import maschine
        ResultSet maschineResultSet;
        try {
            maschineResultSet = oldFirma.createStatement().executeQuery("SELECT * FROM maschine");
        
            while (maschineResultSet.next()) {
                Document maschineDoc = new Document("mnr", maschineResultSet.getInt("mnr"))
                    .append("name", maschineResultSet.getString("name"))
                    .append("pnr", maschineResultSet.getInt("pnr"))
                    .append("anschaffung", maschineResultSet.getString("ansch_datum"))
                    .append("neuwert", maschineResultSet.getInt("neuwert"))
                    .append("zeitwert", maschineResultSet.getInt("zeitwert"));

                System.out.println("Importing maschine: " + maschineDoc.toJson());
                newFirma.getCollection("maschine").insertOne(maschineDoc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listMaschine() {
        MongoDatabase newFirma = this.mongoClient.getDatabase("Firma");

        System.out.println("Listing maschine collection:");
        for (Document doc : newFirma.getCollection("maschine").find()) {
            System.out.println(doc.toJson());
        }
    }

    public void importPersonal() {
        MongoDatabase newFirma = this.mongoClient.getDatabase("Firma");
        // Import personal
        ResultSet personalResultSet;
        try {
            personalResultSet = oldFirma.createStatement().executeQuery("SELECT * FROM personal;");
        
            while (personalResultSet.next()) {
                ResultSet abteilungResultSet = oldFirma.createStatement().executeQuery("SELECT * FROM abteilung WHERE abt_nr = \"" + personalResultSet.getString("abt_nr") + "\";");
                ResultSet praemieResultSet = oldFirma.createStatement().executeQuery("SELECT * FROM praemie WHERE pnr = " + personalResultSet.getInt("pnr") + ";");

                List<Integer> praemieList = new ArrayList<>();
                while (praemieResultSet.next()) {
                    praemieList.add(praemieResultSet.getInt("p_betrag"));
                }


                Document personalDoc = new Document("pnr", personalResultSet.getInt("pnr"))
                    .append("name", personalResultSet.getString("name"))
                    .append("vorname", personalResultSet.getString("vorname"))
                    .append("krankenkasse", personalResultSet.getString("krankenkasse"))
                    .append("abteilung", abteilungResultSet.next() ? abteilungResultSet.getString("name") : null)
                    .append("praemien", praemieList);

                System.out.println("Importing personal: " + personalDoc.toJson());
                newFirma.getCollection("personal").insertOne(personalDoc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listPersonal() {
        MongoDatabase newFirma = this.mongoClient.getDatabase("Firma");

        System.out.println("Listing personal collection:");
        for (Document doc : newFirma.getCollection("personal").find()) {
            System.out.println(doc.toJson());
        }
    }

}

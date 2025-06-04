package fhwedel.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JDBC {

    private Connection connection;

    public static void main(String[] args) throws SQLException {
        JDBC obj = new JDBC();
        obj.connect();
        System.out.println(obj.getConnection());

        //obj.createPersonal();

        obj.readPersonal();

        //obj.updatePersonal();

        //obj.deletePersonal();

        obj.readPersonal();

        obj.getVerkaufsPersonal();
    }








    public Connection getConnection() {
        return this.connection;
    }

    public void connect() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/firma", "root", "password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printResultSetTable(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        // Store column names and their max widths
        List<String> columnNames = new ArrayList<>();
        int[] columnWidths = new int[columnCount];

        for (int i = 1; i <= columnCount; i++) {
            String name = meta.getColumnName(i);
            columnNames.add(name);
            columnWidths[i - 1] = name.length();
        }

        // Store rows while also calculating max width for each column
        List<String[]> rows = new ArrayList<>();
        while (rs.next()) {
            String[] row = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                String value = rs.getString(i);
                value = value == null ? "NULL" : value;
                row[i - 1] = value;
                columnWidths[i - 1] = Math.max(columnWidths[i - 1], value.length());
            }
            rows.add(row);
        }

        // Print header
        for (int i = 0; i < columnCount; i++) {
            System.out.printf("%-" + (columnWidths[i] + 2) + "s", columnNames.get(i));
        }
        System.out.println();

        // Print separator
        for (int i = 0; i < columnCount; i++) {
            System.out.print("-".repeat(columnWidths[i]) + "  ");
        }
        System.out.println();

        // Print rows
        for (String[] row : rows) {
            for (int i = 0; i < columnCount; i++) {
                System.out.printf("%-" + (columnWidths[i] + 2) + "s", row[i]);
            }
            System.out.println();
        }
    }

    

    public void createPersonal() {
        try {
            ResultSet rs = this.connection.createStatement().executeQuery("INSERT INTO personal VALUES (417, 'Krause', 'Henrik', 'it1', 'd13', 'tkk');");
            printResultSetTable(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readPersonal() {
        if (this.connection == null) {
            System.out.println("Not connected!");
            return;
        }
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM personal");
            printResultSetTable(rs);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        

    }

    public void updatePersonal() throws SQLException {
        Integer betrag = null; 
        ResultSet rs = this.connection.createStatement().executeQuery("SELECT betrag FROM gehalt WHERE geh_stufe = 'it1';");
        if (rs.next()) {
            betrag = rs.getInt("betrag");
        }

        if (betrag != null) {
            PreparedStatement ps = this.connection.prepareStatement("UPDATE gehalt SET betrag = ? WHERE geh_stufe = 'it1';");
            ps.setInt(1, (int)(betrag*1.1));
            ps.executeUpdate();
        }
    }

    public void deletePersonal() throws SQLException {
        ResultSet rs = this.connection.createStatement().executeQuery("DELETE FROM personal WHERE vorname='Lutz' AND name = 'Tietze'");
    }

    public void getVerkaufsPersonal() throws SQLException {
        ResultSet rs = this.connection.createStatement().executeQuery(
        "SELECT * FROM personal AS p JOIN abteilung AS a ON a.abt_nr = p.abt_nr WHERE a.abt_nr = 'd15'"
        );
        printResultSetTable(rs);
    }

    
}
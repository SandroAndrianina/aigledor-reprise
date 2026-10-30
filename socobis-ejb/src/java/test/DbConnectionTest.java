package test;

import utilitaire.UtilDB;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbConnectionTest {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            System.out.println("Testing connection to Oracle...");
            UtilDB utilDB = new UtilDB();
            conn = utilDB.GetConn();
            if (conn != null) {
                System.out.println("Connected successfully!");
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT 'Oracle OK' AS MSG, SYSDATE FROM DUAL");
                if (rs.next()) {
                    System.out.println("Result: " + rs.getString("MSG") + ", Sysdate: " + rs.getTimestamp(2));
                }
                rs.close();
                st.close();
            } else {
                System.err.println("Connection returned null!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception e) {}
            }
        }
    }
}

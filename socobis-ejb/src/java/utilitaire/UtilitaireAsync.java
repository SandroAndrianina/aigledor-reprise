package utilitaire;

import java.sql.Connection;
import java.sql.SQLException;

public class UtilitaireAsync {

    public static boolean isOracle(Connection c) throws SQLException {
        return c.getMetaData()
                .getDatabaseProductName()
                .equalsIgnoreCase("Oracle");
    }

    public static boolean isPostgres(Connection c) throws SQLException {
        return c.getMetaData()
                .getDatabaseProductName()
                .equalsIgnoreCase("PostgreSQL");
    }
}

package org.example.provider.db;

import org.example.provider.constants.DbConstants;

import java.sql.*;

public class DatabaseHelper {
    private static final String DB_FILE_PATH = "";
    public static final String DATABASE_NAME = "/iq2remote.db";
    private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_NAME;
    private Connection connection = null;
    private static DatabaseHelper databaseHelper = null;
    private DatabaseHelper(String databaseName, int version) {
        connection = connectDb();
        if (connection == null) {
            System.out.println("Not connected to db");
            return;
        }
//        try {
//            onOpen(connection);
//            int currentVersion = getDatabaseVersion(connection);
//            if (currentVersion == 0) {
//                onCreate(connection);
//                setDatabaseVersion(connection, DbUpgradeManager.DATABASE_VERSION);
//            } else if (currentVersion < DbUpgradeManager.DATABASE_VERSION) {
//                onUpgrade(connection, currentVersion, DbUpgradeManager.DATABASE_VERSION);
//                setDatabaseVersion(connection, DbUpgradeManager.DATABASE_VERSION);
//            } else if (currentVersion > DbUpgradeManager.DATABASE_VERSION) {
//                onDowngrade(connection, currentVersion, DbUpgradeManager.DATABASE_VERSION);
//                setDatabaseVersion(connection, DbUpgradeManager.DATABASE_VERSION);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        System.out.println("Connected to SQLite database.");
    }
    private void onCreate(Connection connection) throws SQLException {
        //create all tables here
        beginTransaction(connection);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(DbConstants.TestTable.CREATE_TBL);
            endTransaction(connection);
        }catch (SQLException e) {
            rollbackTransaction(connection);
            System.out.println("Transaction rolled back due to an error.");
            e.printStackTrace();
        }
    }

    private void onOpen(Connection connection) throws SQLException {
        System.out.println("Database opened.");
        String createVersionTableSQL = "CREATE TABLE IF NOT EXISTS " + DbUpgradeManager.VERSION_TABLE + " (" +
                "version INTEGER NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createVersionTableSQL);
        }
    }

    private int getDatabaseVersion(Connection connection) throws SQLException {
        String query = "SELECT version FROM " + DbUpgradeManager.VERSION_TABLE + " LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("version");
            }
        }
        return 0;
    }

    private void setDatabaseVersion(Connection connection, int version) throws SQLException {
        String deleteOldVersionSQL = "DELETE FROM " + DbUpgradeManager.VERSION_TABLE;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(deleteOldVersionSQL);
            String insertNewVersionSQL = "INSERT INTO " + DbUpgradeManager.VERSION_TABLE + " (version) VALUES (" + version + ")";
            stmt.execute(insertNewVersionSQL);
        }
    }

    private void onUpgrade(Connection connection, int oldVersion, int newVersion) throws SQLException {
        if (oldVersion < 2) {
            String addColumnSQL = "ALTER TABLE test ADD COLUMN age INTEGER";
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(addColumnSQL);
                System.out.println("Database upgraded to version 2.");
            }
        }
    }

    private void onDowngrade(Connection connection, int oldVersion, int newVersion) throws SQLException {
        if (oldVersion > 2) {
            // Handle downgrade logic
            System.out.println("Database downgraded from version " + oldVersion + " to " + newVersion);
        }
    }

    public Connection getConnection() throws SQLException {
        return connection;
    }

    public void beginTransaction(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
    }

    public void endTransaction(Connection connection) throws SQLException {
        connection.commit();
        connection.setAutoCommit(true);
    }

    public void rollbackTransaction(Connection connection) throws SQLException {
        connection.rollback();
        connection.setAutoCommit(true);
    }

    public static DatabaseHelper getInstance(){
        if(databaseHelper == null) {
            databaseHelper = new DatabaseHelper(DATABASE_NAME, DbUpgradeManager.DATABASE_VERSION);
        }
        return databaseHelper;
    }

    private static Connection connectDb() {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}



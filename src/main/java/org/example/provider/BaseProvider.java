package org.example.provider;

import org.example.provider.db.DatabaseHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseProvider {
    protected DatabaseHelper instance;
    public BaseProvider() {
        instance = DatabaseHelper.getInstance();
    }



    //protected DatabaseHelper instance;

    private String tableName = getTableName();


    abstract protected String getTableName();

    abstract protected long incrementTcc();

    abstract public long getTcc();

//    public BaseProvider1() {
//       // instance = DatabaseHelper.getInstance();
//    }

    public static ResultSet insert(PreparedStatement statement){
        System.out.println("ResultSet "+statement);
        //Uri returnUri = null;
        ResultSet rs = null;
        try {
            int result = statement.executeUpdate();
            // Establish a connection to the database
            if (result > 0) {
                // Retrieve the generated keys
                rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    long id = rs.getLong(1);
                    System.out.println("Insertion succeeded, row ID: " + id);
                }
            } else {
                System.out.println("Insertion failed.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return rs;
    }

    public static boolean update(PreparedStatement statement) {
        System.out.println("PreparedStatement: " + statement);
        boolean success = false;
        try {
            int result = statement.executeUpdate();

            if (result > 0) {
                System.out.println("Update succeeded. Rows affected: " + result);
                success = true;
            } else {
                System.out.println("Update failed. No rows affected.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return success;
    }
}

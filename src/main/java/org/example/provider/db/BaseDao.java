package org.example.provider.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

public class BaseDao {
    public static void create(){

    }

    public static void read(String[] projection, String selection, String[] selectionArgs, String sortOrder){
    }

    public static void update(){

    }

    public static void delete(){

    }


    public static ResultSet queryDB(String sql, Map<Integer, String> queryParams, boolean noresultSet) {
        ResultSet resultSet = null;
        PreparedStatement pstmt = null;
        try{
            pstmt = DatabaseHelper.getInstance().getConnection().prepareStatement(sql);
            if(queryParams != null) {
                for (Map.Entry<Integer, String> entry : queryParams.entrySet()) {
                    pstmt.setString(entry.getKey(), entry.getValue());
                }
            }
            if(noresultSet){
                pstmt.executeUpdate();
            }else {
                resultSet = pstmt.executeQuery();
            }
        } catch (Exception e) {
            System.err.println("SQLite JDBC Driver not found.");
            e.printStackTrace();
        }
        return resultSet;

    }
}

package org.example.provider.constants;

public class DbConstants {


    public interface TestTable{
        public static final String TABLE_NAME = "test";
        public static final String CREATE_TBL =  "CREATE TABLE IF NOT EXISTS test (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL)";
    }
}

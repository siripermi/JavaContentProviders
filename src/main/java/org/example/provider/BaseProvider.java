package org.example.provider;

import org.example.provider.db.DatabaseHelper;

public class BaseProvider {
    protected DatabaseHelper instance;
    public BaseProvider() {
        instance = DatabaseHelper.getInstance();
    }
}

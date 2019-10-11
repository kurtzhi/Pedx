package com.kurtzhi.pedx.sql;

class SqlServerDatabaseHelper implements DatabaseHelper {
    @Override
    public String create(String db) {
        return "CREATE DATABASE " + db + "; \n";
    }

    @Override
    public String use(String db) {
        return "USE " + db + "; \n";
    }
}

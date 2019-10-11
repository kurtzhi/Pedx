package com.kurtzhi.pedx.sql;

class MysqlDatabaseHelper implements DatabaseHelper {
    @Override
    public String create(String db) {
        return "CREATE DATABASE "
                + db
                + " DEFAULT CHARACTER SET = utf8 COLLATE = utf8_unicode_ci; \n";
    }

    @Override
    public String use(String db) {
        return "USE " + db + "; \n";
    }
}

package com.kurtzhi.pedx.sql;

/*
 * DB2® database manager 
 */
class Db2DatabaseHelper implements DatabaseHelper {
    @Override
    public String create(String db) {
        return "CREATE DATABASE " + db
                + " USING CODESET UTF-8 TERRITORY US; \n";
    }

    @Override
    public String use(String db) {
        return ">clpplus\nSQL> CONNECT db2admin/your_password@your_host:50000/"
                + db + "; \n";
        // return "CONNECT TO " + db + " USER db2admin using your_password;\n";
    }
}

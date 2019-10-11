package com.kurtzhi.pedx.sql;

class OracleDatabaseHelper implements DatabaseHelper {
    @Override
    public String create(String db) {
        String comments = "/* Creating database using Oracle Database Configuration Assistant. */\n";
        return comments
                + "CREATE DATABASE "
                + db
                + "\n USER SYS IDENTIFIED BY your_password\n USER SYSTEM IDENTIFIED BY your_password\n CHARACTER SET AL32UTF8\n NATIONAL CHARACTER SET AL16UTF16\n SET TIME_ZONE = '+00:00'; \n";
    }

    @Override
    public String use(String db) {
        return ">sqlplus sys/your_password@your_host:your_port/" + db
                + ".your_domain as sysdba" + "; \n";
    }
}

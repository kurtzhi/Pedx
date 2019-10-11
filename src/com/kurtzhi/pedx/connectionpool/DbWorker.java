package com.kurtzhi.pedx.connectionpool;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.kurtzhi.pedx.Pedx;
import com.kurtzhi.pedx.misc.W3cDate;

/*
 * SQL Server 2012: set[N]?String, get[N]?String
 * Oracle 11g: get[N]?String
 * DB2 V10: [{set}|{get}]{1}String
 * MySQL: [{set}|{get}]{1}[N]?String
 */

public class DbWorker {
    private Connection _conn;
    private String _connUrl;
    private W3cDate _initDate;
    private int _maxlifetime;
    private PreparedStatement _pstmt;

    DbWorker(int lifetime, String connUrl) {
        this._maxlifetime = lifetime;
        this._connUrl = connUrl;
    }

    boolean closeConnection() {
        try {
            this._conn.close();
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            this._conn = null;
            return false;
        }
        this._conn = null;
        return true;
    }
    
    boolean rollback() {
        try {
            this._conn.rollback();
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            return false;
        }
        
        return true;
    }
    
    boolean commit() {
        try {
            this._conn.commit();
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            return false;
        }
        
        return true;
    }

    private boolean closeStatement() {
        try {
            this._pstmt.close();
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            this._pstmt = null;
            return false;
        }

        this._pstmt = null;
        return true;
    }

    ResultSet executeQuery() {
        if (this._pstmt == null) {
            return null;
        }

        ResultSet rs = null;
        try {
            rs = this._pstmt.executeQuery();
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            this.closeStatement();
            return null;
        }

        return rs;
    }

    boolean executeUpdate() {
        if (this._pstmt == null) {
            return false;
        }
        int rowCount = 0;
        try {
            rowCount = this._pstmt.executeUpdate();
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            return false;
        }
        this.closeStatement();

        return rowCount == 1;
    }

    boolean freeQueryResultSet() {
        if (this._pstmt == null) {
            return false;
        }

        return this.closeStatement();
    }

    private boolean isConnectionExpired() {
        return (new W3cDate().getUnixTimestamp() - this._initDate
                .getUnixTimestamp()) >= _maxlifetime;
    }

    boolean newConnection() {
        if (this._conn != null) {
            return false;
        }

        if (this._connUrl == null || this._connUrl.isEmpty()) {
            Pedx.logger.fatal("Error value(s) for connection pool properties");
            return false;
        }

        try {
            String db = Pedx.getDatabaseName();
            if (db.equals("db2")) {
                Class.forName("com.ibm.db2.jcc.DB2Driver");
            } else if (db.equals("mysql")) {
                Class.forName("com.mysql.jdbc.Driver");
            } else if (db.equals("oracle")) {
                Class.forName("oracle.jdbc.OracleDriver");
            } else if (db.equals("sqlserver")) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } else {
                return false;
            }
            if (db.equals("mysql")) {
                Properties props = new Properties();
                props.put("useUnicode", "yes");
                props.put("characterEncoding", "UTF-8");
                this._conn = DriverManager.getConnection(this._connUrl, props);
            } else {
                this._conn = DriverManager.getConnection(this._connUrl);
            }
            this._conn.setAutoCommit(false);
        } catch (SQLException e) {
            this._conn = null;
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            return false;
        } catch (ClassNotFoundException e) {
            this._conn = null;
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            return false;
        }
        this._initDate = new W3cDate();
        return true;
    }

    boolean prepareStatement(String sql) {
        if (this._pstmt != null) {
            return false;
        }

        /*
         * if (Pedx.logger.isDebugEnabled()) { Pedx.logger.debug(sql); }
         */

        try {
            this._pstmt = this._conn.prepareStatement(sql);
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            this._pstmt = null;
            return false;
        }

        return true;
    }

    private boolean refreshConnection() {
        if (closeConnection()) {
            return newConnection();
        }

        return false;
    }

    boolean refreshConnectionIfExpired() {
        if (isConnectionExpired()) {
            return refreshConnection();
        }

        return true;
    }

    boolean setLong(int i, long l) {
        if (this._pstmt == null) {
            return false;
        }
        try {
            this._pstmt.setLong(i, l);
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }
        return true;
    }

    boolean setBigDecimal(int i, String s) {
        if (this._pstmt == null) {
            return false;
        }
        try {
            this._pstmt.setBigDecimal(i, new BigDecimal(s));
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }
        return true;
    }

    boolean setBytes(int i, byte[] bs) {
        if (this._pstmt == null) {
            return false;
        }
        try {
            this._pstmt.setBytes(i, bs);
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }
        return true;
    }

    boolean setBlob(int i, byte[] bs) {
        if (this._pstmt == null) {
            return false;
        }
        try {
            this._pstmt.setBlob(i, new ByteArrayInputStream(bs), bs.length);
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }
        return true;
    }

    boolean setString(int i, String s) {
        if (this._pstmt == null) {
            return false;
        }
        try {
            this._pstmt.setString(i, s);
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }
        return true;
    }

    boolean setClob(int i, String s) {
        if (this._pstmt == null) {
            return false;
        }
        try {
            this._pstmt.setClob(i, new StringReader(s));
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }
        return true;
    }

    boolean setNString(int i, String s) {
        if (this._pstmt == null) {
            return false;
        }
        try {
            this._pstmt.setNString(i, s);
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }
        return true;
    }

    boolean setNClob(int i, String s) {
        if (this._pstmt == null) {
            return false;
        }
        try {
            this._pstmt.setNClob(i, new StringReader(s));
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }
        return true;
    }
}

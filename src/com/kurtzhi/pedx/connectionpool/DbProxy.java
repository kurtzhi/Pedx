package com.kurtzhi.pedx.connectionpool;

import java.sql.ResultSet;

public class DbProxy {
    protected DbWorker _worker;

    DbProxy(DbWorker worker) {
        this._worker = worker;
    }

    public ResultSet executeQuery() {
        return _worker.executeQuery();
    }

    public boolean rollback() {
        return _worker.rollback();
    }

    public boolean commit() {
        return _worker.commit();
    }

    public boolean executeUpdate() {
        return _worker.executeUpdate();
    }

    public boolean prepareStatement(String sql) {
        return _worker.prepareStatement(sql);
    }

    public boolean setBytes(int i, byte[] bs) {
        return _worker.setBytes(i, bs);
    }

    public boolean setBlob(int i, byte[] bs) {
        return _worker.setBlob(i, bs);
    }

    public boolean setString(int i, String s) {
        return _worker.setString(i, s);
    }

    public boolean setClob(int i, String s) {
        return _worker.setClob(i, s);
    }

    public boolean setNString(int i, String s) {
        return _worker.setNString(i, s);
    }

    public boolean setNClob(int i, String s) {
        return _worker.setNClob(i, s);
    }

    public boolean setLong(int i, long l) {
        return _worker.setLong(i, l);
    }

    public boolean setBigDecimal(int i, String s) {
        return _worker.setBigDecimal(i, s);
    }

    @Override
    public String toString() {
        return this._worker.toString();
    }
}

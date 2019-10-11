package com.kurtzhi.pedx;

import com.kurtzhi.pedx.connectionpool.ConnectionPool;
import com.kurtzhi.pedx.connectionpool.DbProxy;
import com.kurtzhi.pedx.sql.DatabaseHelper;

public class Transaction {
    ConnectionPool pool;
    DbProxy proxy = null;
    DatabaseHelper helper = null;
    
    public Transaction() {
        helper = Pedx.getDatabaseHelper();
        pool = ConnectionPool.getConnectionPool();
        if (pool != null) {
            proxy = pool.request();
        }
    }
    
    public boolean rollback() {
        boolean ret = false;
        if (proxy != null) {
            ret = proxy.rollback();
        }
        pool.recycle(proxy);
        proxy = null;
        pool = null;
        
        return ret;
    }
    
    public boolean commit() {
        boolean ret = false;
        if (proxy != null) {
            ret = proxy.commit();
        }
        pool.recycle(proxy);
        proxy = null;
        pool = null;
        
        return ret;
    }
}

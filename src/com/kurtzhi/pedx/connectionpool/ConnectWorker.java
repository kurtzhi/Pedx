package com.kurtzhi.pedx.connectionpool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.kurtzhi.pedx.Pedx;

public class ConnectWorker extends Thread {
    private Method _connHatcher;
    private ConnectionPool _pool;
    private int _quota;

    ConnectWorker(ConnectionPool pool, int quota) {
        this._pool = pool;
        this._quota = quota;
        try {
            this._connHatcher = ConnectionPool.class.getDeclaredMethod("hatch");
        } catch (NoSuchMethodException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        } catch (SecurityException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }
        this._connHatcher.setAccessible(true);
    }

    @Override
    public void run() {
        int i = 0;
        while (i < this._quota) {
            try {
                this._connHatcher.invoke(this._pool);
            } catch (IllegalAccessException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            } catch (IllegalArgumentException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            } catch (InvocationTargetException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            }
            i++;
        }

        _pool._isConnecting.set(false);
    }
}
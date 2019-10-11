package com.kurtzhi.pedx.connectionpool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.kurtzhi.pedx.Pedx;

public class CleaningWorker extends Thread {
    private Method _connCleaner;
    private ConnectionPool _pool;
    private int _quota;

    CleaningWorker(ConnectionPool pool, int quota) {
        this._pool = pool;
        this._quota = quota;
        try {
            this._connCleaner = ConnectionPool.class.getDeclaredMethod("clean");
        } catch (NoSuchMethodException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        } catch (SecurityException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }
        this._connCleaner.setAccessible(true);
    }

    @Override
    public void run() {
        int i = 0;
        while (i < this._quota) {
            try {
                this._connCleaner.invoke(this._pool);
            } catch (IllegalAccessException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            } catch (IllegalArgumentException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            } catch (InvocationTargetException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            }
            i++;
        }

        _pool._isCleaning.set(false);
    }
}
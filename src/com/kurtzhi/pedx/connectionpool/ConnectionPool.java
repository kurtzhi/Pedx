package com.kurtzhi.pedx.connectionpool;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.kurtzhi.pedx.Pedx;

public class ConnectionPool {
    private static ConnectionPool pool = new ConnectionPool();

    public static ConnectionPool getConnectionPool() {
        return pool;
    }

    private ConcurrentLinkedQueue<DbWorker> _busyWorkers;
    private int _capacity;
    private String _connUrl;
    private ConcurrentLinkedQueue<DbWorker> _idleWorkers;
    private AtomicInteger _idleWorkersCount = new AtomicInteger(0);
    private int _increment;
    private int _initCapacity;
    protected AtomicBoolean _isCleaning = new AtomicBoolean(false);
    protected AtomicBoolean _isConnecting = new AtomicBoolean(false);
    private int _lifetime;
    private int _maxIdle;
    private int _minIdle;
    private Field _ProxyWorkerfield;

    private AtomicInteger _workersCount = new AtomicInteger(0);

    private ConnectionPool() {
        this._idleWorkers = new ConcurrentLinkedQueue<DbWorker>();
        this._busyWorkers = new ConcurrentLinkedQueue<DbWorker>();
        ConnectionPoolConfig config = Pedx.getConnectionPoolConfig();
        if (config == null) {
            return;
        }

        this._initCapacity = config.getInitialCapacity();
        this._capacity = config.getCapacity();
        this._increment = config.getIncrement();
        this._minIdle = config.getMinIdle();
        this._maxIdle = config.getMaxIdle();
        this._lifetime = config.getLifetime();
        this._connUrl = config.getConnectionUrl();
        this.initConnectionPool();

        try {
            this._ProxyWorkerfield = DbProxy.class.getDeclaredField("_worker");
            this._ProxyWorkerfield.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        } catch (SecurityException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }

    }

    protected synchronized void clean() {
        DbWorker worker;
        if ((worker = this._idleWorkers.poll()) != null) {
            this._idleWorkersCount.getAndDecrement();
            this._workersCount.getAndDecrement();
            worker.closeConnection();
            notify();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        DbWorker worker;
        while ((worker = this._idleWorkers.poll()) != null) {
            worker.closeConnection();
        }
        while ((worker = this._busyWorkers.poll()) != null) {
            worker.closeConnection();
        }
        super.finalize();
    }

    protected synchronized void hatch() {
        this.hatchWorker();
        notify();
    }

    private void hatchWorker() {
        DbWorker worker = new DbWorker(this._lifetime, this._connUrl);
        if (worker.newConnection()) {
            this._idleWorkers.add(worker);
            this._workersCount.getAndIncrement();
            this._idleWorkersCount.getAndIncrement();
        }
    }

    private void initConnectionPool() {
        for (int i = 0; i < this._initCapacity; i++) {
            this.hatchWorker();
        }
    }

    public synchronized void recycle(DbProxy proxy) {
        DbWorker worker = null;
        try {
            worker = (DbWorker) this._ProxyWorkerfield.get(proxy);
            this._ProxyWorkerfield.set(proxy, null);
        } catch (IllegalArgumentException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        } catch (IllegalAccessException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }

        if (worker != null) {
            worker.freeQueryResultSet();
            worker.refreshConnectionIfExpired();
            this._busyWorkers.remove(worker);
            this._idleWorkers.add(worker);
            this._idleWorkersCount.getAndIncrement();
            notify();
        }
    }

    public synchronized DbProxy request() {
        while (this._idleWorkersCount.get() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            }
        }

        DbWorker worker = this._idleWorkers.poll();
        this._busyWorkers.add(worker);
        this._idleWorkersCount.getAndDecrement();
        worker.refreshConnectionIfExpired();
        notify();

        if (this._idleWorkersCount.get() < this._minIdle
                && !_isConnecting.get()) {
            _isConnecting.set(true);
            int gap = this._capacity - this._workersCount.get();
            int quota = gap < this._increment ? gap : this._increment;
            new ConnectWorker(this, quota).start();
        } else if (this._idleWorkersCount.get() > this._maxIdle
                && !_isCleaning.get()) {
            _isCleaning.set(true);
            int quota = this._idleWorkersCount.get() - this._maxIdle;
            new CleaningWorker(this, quota).start();
        }

        return new DbProxy(worker);
    }
}
package com.kurtzhi.pedx.connectionpool;

public interface ConnectionPoolConfig {
    int getCapacity();

    String getConnectionUrl();

    int getIncrement();

    int getInitialCapacity();

    int getLifetime();

    int getMaxIdle();

    int getMinIdle();
}

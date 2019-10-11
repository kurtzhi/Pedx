package com.kurtzhi.pedx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.kurtzhi.pedx.connectionpool.ConnectionPoolConfig;
import com.kurtzhi.pedx.sql.DatabaseHelper;
import com.kurtzhi.pedx.sql.TableHelper;

public class Pedx {
    private static Pedx instance = new Pedx();
    public static Logger logger;

    protected static Object getBatchQueryHelper() {
        return instance.batchQueryHelper;
    }

    public static ConnectionPoolConfig getConnectionPoolConfig() {
        return instance.config;
    }

    public static DatabaseHelper getDatabaseHelper() {
        return instance.databaseHelper;
    }

    public static TableHelper getTableHelper() {
        return instance.tableHelper;
    }

    public static String getDatabaseName() {
        return instance.databaseName;
    }

    protected static boolean isProperlyInitialized() {
        return instance.initializeStatus;
    }

    private String databaseName;

    private Object batchQueryHelper;

    private ConnectionPoolConfig config;

    private DatabaseHelper databaseHelper;

    private Object fieldHelper;

    private boolean initializeStatus;

    private TableHelper tableHelper;

    private Pedx() {
        FileInputStream is = null;
        try {
            URL url = Thread.currentThread().getContextClassLoader()
                    .getResource("pedx_log.properties");
            String file = null;
            if (url != null) {
                file = url.getPath();
            } else {
                file = "pedx_log.properties";
            }
            is = new FileInputStream(file);
            PropertyConfigurator.configure(is);
        } catch (FileNotFoundException e) {
            System.err
                    .println("[Pedx] Error: Could not find file pedx_log.properties");
            return;
        }
        logger = Logger.getLogger("Stoarge");
        configure();
    }

    private void configure() {
        config = genConnectionPoolConfigInstance();
        String connUrl = config.getConnectionUrl();
        if (connUrl != null && !connUrl.isEmpty()) {
            if (connUrl.indexOf("db2") != -1) {
                databaseName = "db2";
            } else if (connUrl.indexOf("mysql") != -1) {
                databaseName = "mysql";
            } else if (connUrl.indexOf("oracle") != -1) {
                databaseName = "oracle";
            } else if (connUrl.indexOf("sqlserver") != -1) {
                databaseName = "sqlserver";
            }
            if (databaseName != null && !databaseName.isEmpty()) {
                initializeStatus = invokeHelperMaterializationHook(connUrl)
                        && passValuesInDark();
            }
        }
    }

    private ConnectionPoolConfig genConnectionPoolConfigInstance() {
        Properties props = new Properties();
        FileInputStream is = null;
        try {
            URL url = Thread.currentThread().getContextClassLoader()
                    .getResource("pedx.properties");
            String file = null;
            if (url != null) {
                file = url.getPath();
            } else {
                file = "pedx.properties";
            }
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error("Could not find file pedx.properties");
        }

        if (null == is) {
            return null;
        }

        try {
            props.load(is);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        final int initialCapacity = Integer.parseInt(props
                .getProperty("connectionPool.initialCapacity"));
        final int capacity = Integer.parseInt(props
                .getProperty("connectionPool.capacity"));
        final int increment = Integer.parseInt(props
                .getProperty("connectionPool.increment"));
        final int minIdle = Integer.parseInt(props
                .getProperty("connectionPool.minIdle"));
        final int maxIdle = Integer.parseInt(props
                .getProperty("connectionPool.maxIdle"));
        final int lifetime = Integer.parseInt(props
                .getProperty("connectionPool.lifetime"));
        final String connectionUrl = props
                .getProperty("connectionPool.connectionUrl");

        if (!(initialCapacity >= 0 && minIdle >= 0 && increment > 0
                && lifetime > 120 && capacity >= initialCapacity
                && increment <= capacity - initialCapacity - minIdle
                && minIdle <= increment && maxIdle > increment && (connectionUrl == null || !connectionUrl
                .isEmpty()))) {
            logger.fatal("Error value(s) for connection pool properties");
            return null;
        }

        return new ConnectionPoolConfig() {
            @Override
            public int getCapacity() {
                return capacity;
            }

            @Override
            public String getConnectionUrl() {
                return connectionUrl;
            }

            @Override
            public int getIncrement() {
                return increment;
            }

            @Override
            public int getInitialCapacity() {
                return initialCapacity;
            }

            @Override
            public int getLifetime() {
                return lifetime;
            }

            @Override
            public int getMaxIdle() {
                return maxIdle;
            }

            @Override
            public int getMinIdle() {
                return minIdle;
            }
        };
    }

    private boolean invokeHelperMaterializationHook(String connUrl) {
        String cmd = null;
        if (connUrl.indexOf("db2") != -1) {
            cmd = "db2";
        } else if (connUrl.indexOf("mysql") != -1) {
            cmd = "mysql";
        } else if (connUrl.indexOf("oracle") != -1) {
            cmd = "oracle";
        } else if (connUrl.indexOf("sqlserver") != -1) {
            cmd = "sqlserver";
        }

        try {
            Method m;
            m = TableHelper.class.getDeclaredMethod(
                    "hookHelperMaterialization", String.class);
            m.setAccessible(true);
            @SuppressWarnings("unchecked")
            ArrayList<Object> helpers = (ArrayList<Object>) m.invoke(null, cmd);
            databaseHelper = (DatabaseHelper) helpers.get(0);
            fieldHelper = helpers.get(1);
            batchQueryHelper = helpers.get(2);
            tableHelper = (TableHelper) helpers.get(3);
        } catch (NoSuchMethodException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (IllegalAccessException e) {
            return false;
        } catch (InvocationTargetException e) {
            return false;
        }

        return true;
    }

    private boolean passValuesInDark() {
        Field f;
        try {
            f = TableHelper.class.getDeclaredField("_fieldHelper");
            f.setAccessible(true);
            f.set(this.tableHelper, this.fieldHelper);
        } catch (NoSuchFieldException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (IllegalAccessException e) {
            return false;
        }
        return true;
    }
}

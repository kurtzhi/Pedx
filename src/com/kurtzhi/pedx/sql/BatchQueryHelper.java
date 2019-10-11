package com.kurtzhi.pedx.sql;

abstract class BatchQueryHelper {
    abstract String obtainQueryString(int[] batchInfo, String[] queryInfo);
}

package com.kurtzhi.pedx.sql;

class MysqlBatchQueryHelper extends BatchQueryHelper {
    @Override
    String obtainQueryString(int[] batchInfo, String[] queryInfo) {
        int offset = 0;
        if (batchInfo[0] > 0) {
            if (batchInfo[0] * batchInfo[1] < batchInfo[2]) {
                offset = batchInfo[0] * batchInfo[1];
            } else {
                return "";
            }
        }
        int capacity = batchInfo[1];

        String queryStatement;
        queryStatement = "SELECT " + queryInfo[0];
        queryStatement += " FROM " + queryInfo[1];
        if (queryInfo[2] != null && !queryInfo[2].isEmpty()) {
            queryStatement += " WHERE " + queryInfo[2];
        }
        if (queryInfo[3] != null && !queryInfo[3].isEmpty()) {
            queryStatement += " ORDER BY " + queryInfo[3];
        }
        queryStatement += " LIMIT " + offset + ", " + capacity;

        return queryStatement;
    }

}

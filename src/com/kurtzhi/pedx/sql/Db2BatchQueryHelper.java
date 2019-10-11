package com.kurtzhi.pedx.sql;

/*
 * $ db2set DB2_COMPATIBILITY_VECTOR=ORA
 * $ db2stop
 * $ db2start
 */
class Db2BatchQueryHelper extends BatchQueryHelper {
    @Override
    String obtainQueryString(int[] batchInfo, String[] queryInfo) {
        int base = 0;
        if (batchInfo[0] >= 0) {
            if (batchInfo[0] * batchInfo[1] < batchInfo[2]) {
                base = batchInfo[0] * batchInfo[1] + 1;
            } else {
                return "";
            }
        } else {
            return "";
        }
        int top = base + batchInfo[1] - 1;

        String queryStatement = "SELECT " + queryInfo[0];
        queryStatement += " FROM " + queryInfo[1];
        queryStatement += " WHERE ";
        if (queryInfo[2] != null && !queryInfo[2].isEmpty()) {
            queryStatement += queryInfo[2] + " AND ";
        }
        queryStatement += "ROWNUM BETWEEN " + base + " AND " + top;
        if (queryInfo[3] != null && !queryInfo[3].isEmpty()) {
            queryStatement += " ORDER BY " + queryInfo[3];
        }

        return queryStatement;
    }
}

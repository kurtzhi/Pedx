package com.kurtzhi.pedx.sql;

class OracleBatchQueryHelper extends BatchQueryHelper {
    @Override
    String obtainQueryString(int[] batchInfo, String[] queryInfo) {
        int offset = 0;
        if (batchInfo[0] >= 0) {
            if (batchInfo[0] * batchInfo[1] < batchInfo[2]) {
                offset = batchInfo[0] * batchInfo[1];
            } else {
                return "";
            }
        } else {
            return "";
        }
        int top = offset + batchInfo[1];

        String queryStatement;
        queryStatement = "SELECT * FROM (";
        if (queryInfo[3] == null || queryInfo[3].isEmpty()) {
            queryStatement += " SELECT " + queryInfo[0] + ", ROWNUM RN_0_0_";
            queryStatement += " FROM " + queryInfo[1];
            if (queryInfo[2] != null && !queryInfo[2].isEmpty()) {
                queryStatement += " WHERE " + queryInfo[2] + "";
            }
        } else {
            String orderBy = " ORDER BY " + queryInfo[3];
            queryStatement += " SELECT " + queryInfo[0] + ",";
            queryStatement += " ROW_NUMBER() OVER (" + orderBy + " ) RN_0_0_";
            queryStatement += " FROM " + queryInfo[1];
            if (queryInfo[2] != null && !queryInfo[2].isEmpty()) {
                queryStatement += " WHERE " + queryInfo[2] + "";
            }
            queryStatement += orderBy;
        }
        queryStatement += ") WHERE RN_0_0_>" + offset + " and RN_0_0_<=" + top;

        return queryStatement;
    }
}

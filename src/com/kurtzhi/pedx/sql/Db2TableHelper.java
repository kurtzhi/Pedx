package com.kurtzhi.pedx.sql;

import com.kurtzhi.pedx.datatype.PdxDate;
import com.kurtzhi.pedx.datatype.PdxDateTime;

class Db2TableHelper extends TableHelper {
    @Override
    public String createColumns() {
        String sql = "";

        for (int i = 0; i < cnt; i++) {
            sql += i == 0 ? clauseStart : clauseBreak;
            sql += FieldHelper.translateFieldName(fields[i])
                    + " "
                    + _fieldHelper.embodyDataType(fields[i],
                            fieldsCapacity.get(i));
            if (fieldsNotNull.contains(fields[i])) {
                sql += " NOT NULL";
            }
            if (fields[i].getType() == PdxDate.class) {
                sql += " WITH DEFAULT 20000000000000";
            } else if (fields[i].getType() == PdxDateTime.class) {
                sql += " WITH DEFAULT 20000000000000000000";
            }
        }
        return sql;
    }

    @Override
    public String createEnd(Class<?> dboClass) {
        return "\n) CCSID UNICODE; ";
    }
}

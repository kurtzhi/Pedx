package com.kurtzhi.pedx.sql;

import com.kurtzhi.pedx.datatype.PdxDate;
import com.kurtzhi.pedx.datatype.PdxDateTime;

class OracleTableHelper extends TableHelper {

    @Override
    public String createColumns() {
        String sql = "";
        String fieldname;
        for (int i = 0; i < cnt; i++) {
            fieldname = FieldHelper.translateFieldName(fields[i]);
            sql += i == 0 ? clauseStart : clauseBreak;
            sql += fieldname
                    + " "
                    + _fieldHelper.embodyDataType(fields[i],
                            fieldsCapacity.get(i));

            if (fields[i].getType() == PdxDate.class) {
                sql += " DEFAULT 20000000000000";
            } else if (fields[i].getType() == PdxDateTime.class) {
                sql += " DEFAULT 20000000000000000000";
            }

            String keyId = fieldname.replace(tableName.toLowerCase(), "") + "_"
                    + tableName;
            keyId = keyId.length() > 27 ? keyId.substring(0, 27) : keyId;
            if (fieldsNotNull.contains(fields[i])) {
                sql += " CONSTRAINT nn_" + keyId + " NOT NULL";
            }
        }
        return sql;
    }

    @Override
    public String createEnd(Class<?> dboClass) {
        return "\n);";
    }
}

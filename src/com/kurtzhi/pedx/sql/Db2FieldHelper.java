package com.kurtzhi.pedx.sql;

import java.lang.reflect.Field;

import com.kurtzhi.pedx.FieldConstraint;
import com.kurtzhi.pedx.ForeignOnDeleteAction;
import com.kurtzhi.pedx.Pedx;
import com.kurtzhi.pedx.datatype.PdxBinary;
import com.kurtzhi.pedx.datatype.PdxByte;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxClob;
import com.kurtzhi.pedx.datatype.PdxDate;
import com.kurtzhi.pedx.datatype.PdxDateTime;
import com.kurtzhi.pedx.datatype.PdxFloat;
import com.kurtzhi.pedx.datatype.PdxGUID;
import com.kurtzhi.pedx.datatype.PdxInteger;
import com.kurtzhi.pedx.misc.BinaryDataType;
import com.kurtzhi.pedx.misc.ElementDataType;

class Db2FieldHelper extends FieldHelper {

    @Override
    public String embodyCommonKey(String table, FieldConstraint type,
            Field field) {
        String col = translateFieldName(field);
        String keyId = table + "_" + col;
        keyId = keyId.length() > 125 ? keyId.substring(0, 125) : keyId;
        switch (type) {
        case SIMPLEKEY:
            return "CONSTRAINT pk_" + keyId + " PRIMARY KEY (" + col + ")";

        case UNIQUE:
            return "CONSTRAINT uq_" + keyId + " UNIQUE (" + col + ")";

        case INDEX:
            return "CREATE INDEX ix_" + keyId + " ON " + table + " (" + col
                    + ");";
        default:
            break;
        }

        return null;
    }

    @Override
    public String embodyCompositeKey(String table, Field[] fields) {
        String cols = "";
        for (Field field : fields) {
            cols += translateFieldName(field) + ", ";
        }
        cols = cols.substring(0, cols.length() - 2);
        String keyId = table + "_" + cols.replaceAll(", ", "_");
        keyId = keyId.length() > 125 ? keyId.substring(0, 125) : keyId;
        return "CONSTRAINT pk_" + keyId + " PRIMARY KEY (" + cols + ")";
    }

    @Override
    public String embodyDataType(Field field, int size) {

        Class<?> dataType = field.getType();
        boolean isDatatypeSupportted = true;

        if (dataType.getSuperclass() == ElementDataType.class) {
            if (dataType == PdxGUID.class) {
                return "CHARACTER(36)";
            } else if (dataType == PdxByte.class) {
                return "SMALLINT";
            } else if (dataType == PdxInteger.class) {
                return "BIGINT";
            } else if (dataType == PdxFloat.class) {
                return "DOUBLE"; // DOUBLE = FLOAT for db2
            } else if (dataType == PdxDate.class) {
                return "BIGINT";
            } else if (dataType == PdxDateTime.class) {
                return "DECIMAL(20, 0)"; // 20 Long
            } else if (dataType == PdxCharacter.class) {
                return "GRAPHIC(" + size + ")";
            } else if (dataType == PdxClob.class) {
                return "DBCLOB(" + size + ")";
            } else {
                isDatatypeSupportted = false;
            }
        } else if (dataType.getSuperclass() == BinaryDataType.class) {
            if (dataType == PdxBinary.class) {
                return "CHARACTER(" + size + ") FOR BIT DATA";
            } else {
                return "BLOB(" + size + ")";
            }
        }

        if (!isDatatypeSupportted) {
            Pedx.logger.error("Field data type \'" + dataType.getSimpleName()
                    + "\' not supported");
        }

        return "";
    }

    @Override
    public String embodyForeignKey(String table, Field field, Object[] info) {
        String col = translateFieldName(field);
        String keyId = table + "_" + col;
        keyId = keyId.length() > 125 ? keyId.substring(0, 125) : keyId;
        String ret = "CONSTRAINT fk_" + keyId + " FOREIGN KEY (" + col + ")";
        ret += " REFERENCES " + ((Class<?>) (info[1])).getSimpleName();
        ret += " (" + info[2] + ")";
        ret += " ON DELETE "
                + transForeignAction((ForeignOnDeleteAction) info[3]);
        ret += " ON UPDATE NO ACTION";
        ret += " ENFORCED";

        return ret;
    }
}

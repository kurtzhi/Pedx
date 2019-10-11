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

class OracleFieldHelper extends FieldHelper {

    @Override
    public String embodyCommonKey(String table, FieldConstraint type,
            Field field) {
        String col = translateFieldName(field);
        String keyId = col.replace(table.toLowerCase(), "") + "_" + table;
        keyId = keyId.length() > 27 ? keyId.substring(0, 27) : keyId;
        switch (type) {
        case SIMPLEKEY:
            return "CONSTRAINT pk_" + keyId + " PRIMARY KEY (" + col + ")";

        case UNIQUE:
            return "CONSTRAINT uk_" + keyId + " UNIQUE  (" + col + ")";

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
        String keyId = cols.replaceAll(", ", "_").replace(table.toLowerCase(),
                "")
                + "_" + table;
        keyId = keyId.length() > 27 ? keyId.substring(0, 27) : keyId;
        return "CONSTRAINT pk_" + keyId + " PRIMARY KEY (" + cols + ")";
    }

    @Override
    public String embodyDataType(Field field, int size) {

        Class<?> dataType = field.getType();
        boolean isDatatypeSupportted = true;

        if (dataType.getSuperclass() == ElementDataType.class) {
            if (dataType == PdxGUID.class) {
                return "CHAR(36 BYTE)";
            } else if (dataType == PdxByte.class) {
                return "NUMBER(3)";
            } else if (dataType == PdxInteger.class) {
                return "NUMBER(19)";
            } else if (dataType == PdxFloat.class) {
                return "BINARY_DOUBLE";
            } else if (dataType == PdxDate.class) {
                return "NUMBER(14)";
            } else if (dataType == PdxDateTime.class) {
                return "NUMBER(20)";
            } else if (dataType == PdxCharacter.class) {
                return "NCHAR(" + size + ")";
            } else if (dataType == PdxClob.class) {
                return "NCLOB";
            } else {
                isDatatypeSupportted = false;
            }
        } else if (dataType.getSuperclass() == BinaryDataType.class) {
            if (dataType == PdxBinary.class) {
                return "RAW(" + size + ")";
            } else {
                return "BLOB";
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
        String keyId = col.replace(table.toLowerCase(), "") + "_" + table;
        keyId = keyId.length() > 27 ? keyId.substring(0, 27) : keyId;
        String ret = "CONSTRAINT fk_" + keyId + " FOREIGN KEY (" + col + ")";
        ret += " REFERENCES " + ((Class<?>) (info[1])).getSimpleName();
        ret += " (" + info[2] + ")";
        if ((ForeignOnDeleteAction) info[3] != ForeignOnDeleteAction.NOACTION) {
            ret += " ON DELETE "
                    + transForeignAction((ForeignOnDeleteAction) info[3]);
        }
        ret += " ENABLE VALIDATE";

        return ret;
    }
}

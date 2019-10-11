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

class MysqlFieldHelper extends FieldHelper {

    @Override
    public String embodyCommonKey(String table, FieldConstraint type,
            Field field) {
        String col = translateFieldName(field);
        String identifier = table + "_" + col;
        switch (type) {
        case SIMPLEKEY:
            return "PRIMARY KEY (" + col + ")";

        case UNIQUE:
            return "UNIQUE KEY (" + col + ")";

        case INDEX:
            return "CREATE INDEX idx_" + identifier + " ON " + table + " ("
                    + col + ");";
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
        return "PRIMARY KEY (" + cols + ")";
    }

    @Override
    public String embodyDataType(Field field, int size) {

        Class<?> dataType = field.getType();
        boolean isDatatypeSupportted = true;

        if (dataType.getSuperclass() == ElementDataType.class) {
            size *= 3; // To support UTF-8
            if (dataType == PdxGUID.class) {
                return "CHAR(36) CHARACTER SET ascii COLLATE ascii_general_ci";
            } else if (dataType == PdxByte.class) {
                return "TINYINT UNSIGNED";
            } else if (dataType == PdxInteger.class) {
                return "BIGINT";
            } else if (dataType == PdxFloat.class) {
                return "DOUBLE";
            } else if (dataType == PdxDate.class) {
                return "BIGINT UNSIGNED";
            } else if (dataType == PdxDateTime.class) {
                return "DECIMAL(20,0)";
            } else if (dataType == PdxCharacter.class) {
                return "CHAR(" + size
                        + ") CHARACTER SET utf8 COLLATE utf8_unicode_ci";
            } else if (dataType == PdxClob.class) {
                if (size <= 65535) {
                    return "TEXT(" + size
                            + ") CHARACTER SET utf8 COLLATE utf8_unicode_ci";
                } else if (size <= 16777215) {
                    return "MEDIUMTEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci";
                } else {
                    return "LONGTEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci";
                }
            } else {
                isDatatypeSupportted = false;
            }
        } else if (dataType.getSuperclass() == BinaryDataType.class) {
            if (dataType == PdxBinary.class) {
                return "BINARY(" + size + ")";
            } else {
                if (size <= 65535) {
                    return "BLOB(" + size + ")";
                } else if (size <= 16777215) {
                    return "MEDIUMBLOB";
                } else {
                    return "LONGBLOB";
                }
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
        String ret = "FOREIGN KEY (" + col + ")";
        ret += " REFERENCES " + ((Class<?>) (info[1])).getSimpleName();
        ret += " (" + info[2] + ")";
        ret += " ON DELETE "
                + transForeignAction((ForeignOnDeleteAction) info[3]);
        ret += " ON UPDATE NO ACTION";

        return ret;
    }
}

package com.kurtzhi.pedx.sql;

import java.lang.reflect.Field;

import com.kurtzhi.pedx.FieldConstraint;
import com.kurtzhi.pedx.ForeignOnDeleteAction;

public abstract class FieldHelper {
    static String transForeignAction(ForeignOnDeleteAction action) {
        if (action == ForeignOnDeleteAction.NOACTION) {
            return "NO ACTION";
        } else if (action == ForeignOnDeleteAction.CASCADE) {
            return "CASCADE";
        }
        return "";
    }

    public static String translateFieldName(Field field) {
        return field.getName();
    }

    abstract String embodyCommonKey(String table, FieldConstraint type,
            Field field);

    abstract String embodyCompositeKey(String table, Field[] fields);

    abstract String embodyDataType(Field field, int size);

    abstract String embodyForeignKey(String table, Field field, Object[] info);
}
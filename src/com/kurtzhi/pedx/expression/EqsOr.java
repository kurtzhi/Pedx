package com.kurtzhi.pedx.expression;

import com.kurtzhi.pedx.Pedx;

public class EqsOr extends Expression {
    public EqsOr(Class<?> dboClass, String[] fields, Object[] values) {
        int fieldLen = fields.length;
        if (fieldLen != values.length) {
            Pedx.logger.error("Fields length and values length not match");
        }
        Expression ep = null;
        for (int i = 1; i < fieldLen; i++) {
            if (i == 0) {
                ep = new Eq(dboClass, fields[i], values[i]);
            } else {
                ep.or(new Eq(dboClass, fields[i], values[i]));
            }
        }
        this._val = ep.toString();
        this._regDesignatedTypes = ep._regDesignatedTypes;
        this._regDesignatedValues = ep._regDesignatedValues;
    }
}
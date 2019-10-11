package com.kurtzhi.pedx.datatype;

import com.kurtzhi.pedx.Pedx;
import com.kurtzhi.pedx.misc.ElementDataType;
import com.kurtzhi.pedx.misc.DataTypeComparable;
import com.kurtzhi.pedx.sql.QuotationType;

public final class PdxInteger extends ElementDataType implements
        DataTypeComparable {
    private long _val = 0L;

    public long get() {
        return this._val;
    }

    @Override
    public QuotationType getQuotationType() {
        return QuotationType.DirectAccess;
    }

    @Override
    public Object serialize() {
        return this.get();
    }

    public void set(long val) {
        if (val >= -9223372036854775808L && val <= 9223372036854775807L) {
            this._val = val;
        } else {
            Pedx.logger.error("Value exceed bounds of signed long");
        }
    }

    @Override
    public String toString() {
        return Long.toString(this._val);
    }

    @Override
    public int valCode() {
        return new Long(this._val).hashCode();
    }
}
package com.kurtzhi.pedx.datatype;

import com.kurtzhi.pedx.Pedx;
import com.kurtzhi.pedx.misc.ElementDataType;
import com.kurtzhi.pedx.misc.DataTypeComparable;
import com.kurtzhi.pedx.sql.QuotationType;

public final class PdxByte extends ElementDataType implements
        DataTypeComparable {
    private int _val = 0;

    public int get() {
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

    public void set(int val) {
        if (val >= 0 && val <= 255) {
            this._val = val;
        } else {
            Pedx.logger.error("Value exceed bounds of byte");
        }
    }

    @Override
    public String toString() {
        return Integer.toString(this._val);
    }

    @Override
    public int valCode() {
        return this._val;
    }
}
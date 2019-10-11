package com.kurtzhi.pedx.misc;

import com.kurtzhi.pedx.Pedx;
import com.kurtzhi.pedx.sql.QuotationType;

public abstract class BinaryDataType extends ElementDataType implements
        DataTypeComparable {
    byte[] _val;

    public byte[] get() {
        return this._val;
    }

    abstract public int getMaxLength();

    @Override
    public QuotationType getQuotationType() {
        return QuotationType.DirectAccess;
    }

    @Override
    public Object serialize() {
        return this.get();
    }

    public void set(byte[] val) {
        if (val == null) {
            return;
        }
        if (val.length <= getMaxLength()) {
            this._val = val;
        } else {
            Pedx.logger.error("Value exceed max length of binary data type");
        }
    }

    @Override
    public String toString() {
        return new String(this._val);
    }

    @Override
    public int valCode() {
        if (this._val == null) {
            return 0;
        }
        return new String(this._val).hashCode();
    }
}

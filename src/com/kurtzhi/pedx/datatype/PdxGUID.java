package com.kurtzhi.pedx.datatype;

import java.util.UUID;

import com.kurtzhi.pedx.misc.ElementDataType;
import com.kurtzhi.pedx.misc.DataTypeComparable;
import com.kurtzhi.pedx.sql.QuotationType;

public class PdxGUID extends ElementDataType implements DataTypeComparable {
    private UUID _val;

    public PdxGUID() {
        this.set(this.generate());
    }

    public String generate() {
        return UUID.randomUUID().toString();
    }

    public String get() {
        return this._val.toString();
    }

    @Override
    public QuotationType getQuotationType() {
        return QuotationType.Quotable;
    }

    @Override
    public Object serialize() {
        return this.get();
    }

    public void set(String val) {
        this._val = UUID.fromString(val);
    }

    @Override
    public String toString() {
        return this._val.toString();
    }

    @Override
    public int valCode() {
        return this._val.hashCode();
    }
}

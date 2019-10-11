package com.kurtzhi.pedx.datatype;

import com.kurtzhi.pedx.Pedx;
import com.kurtzhi.pedx.misc.ElementDataType;
import com.kurtzhi.pedx.misc.DataTypeComparable;
import com.kurtzhi.pedx.sql.QuotationType;

public final class PdxFloat extends ElementDataType implements
        DataTypeComparable {
    private double _val = 0.0D;

    public double get() {
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

    public void set(double val) {
        if (val == 0 || (val >= 2.23E-308 && val <= 1.79E+308)
                || (val >= -1.79E+308 && val <= -2.22E-308)) {
            this._val = val;
        } else {
            Pedx.logger.error("Value exceed bounds of signed double");
        }
    }

    @Override
    public String toString() {
        return Double.toString(this._val);
    }

    @Override
    public int valCode() {
        return new Double(this._val).hashCode();
    }
}

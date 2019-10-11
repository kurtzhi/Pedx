package com.kurtzhi.pedx.datatype;

import com.kurtzhi.pedx.Pedx;
import com.kurtzhi.pedx.misc.ElementDataType;
import com.kurtzhi.pedx.misc.DataTypeComparable;
import com.kurtzhi.pedx.sql.QuotationType;

public class PdxClob extends ElementDataType implements DataTypeComparable {
    String _val = "";

    public String get() {
        return this._val;
    }

    public int getMaxLength() {
        return 2147483647; // 2G, 2^31-1
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
        if (val == null) {
            return;
        }
        if (val.length() <= getMaxLength()) {
            this._val = val;
        } else {
            Pedx.logger
                    .error("Value exceed the max length of character data type");
        }
    }

    @Override
    public String toString() {
        return this._val;
    }

    @Override
    public int valCode() {
        if (this._val == null) {
            return 0;
        }
        return this._val.hashCode();
    }
}
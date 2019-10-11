package com.kurtzhi.pedx.datatype;

import com.kurtzhi.pedx.misc.ElementDataType;
import com.kurtzhi.pedx.misc.DataTypeComparable;
import com.kurtzhi.pedx.misc.DateTimeHelper;
import com.kurtzhi.pedx.misc.DateTimeType;
import com.kurtzhi.pedx.misc.W3cDate;
import com.kurtzhi.pedx.sql.QuotationType;

public class PdxDate extends ElementDataType implements DataTypeComparable {
    private W3cDate _val;

    public PdxDate() {
        this._val = new W3cDate();
    }

    public W3cDate deserialize(String dateStr) {
        return DateTimeHelper.objectize(DateTimeType.Date, dateStr);
    }

    public W3cDate get() {
        return this._val;
    }

    @Override
    public QuotationType getQuotationType() {
        return QuotationType.DirectAccess;
    }

    @Override
    public Object serialize() {
        return DateTimeHelper.storablize(DateTimeType.Date, this._val);
    }

    public void set(W3cDate val) {
        this._val = val;
    }

    @Override
    public String toString() {
        return this._val.getUTCDate();
    }

    @Override
    public int valCode() {
        return this._val.getDateTimeByTimezone(" ", "+0000").hashCode();
    }
}

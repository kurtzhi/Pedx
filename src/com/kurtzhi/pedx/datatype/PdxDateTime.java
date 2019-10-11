package com.kurtzhi.pedx.datatype;

import java.text.ParseException;

import com.kurtzhi.pedx.misc.ElementDataType;
import com.kurtzhi.pedx.misc.DataTypeComparable;
import com.kurtzhi.pedx.misc.DateTimeHelper;
import com.kurtzhi.pedx.misc.DateTimeType;
import com.kurtzhi.pedx.misc.W3cDate;
import com.kurtzhi.pedx.sql.QuotationType;

public class PdxDateTime extends ElementDataType implements DataTypeComparable {
    private W3cDate _val;

    public PdxDateTime() {
        this._val = new W3cDate();
    }

    public W3cDate deserialize(String dateTimeStr) throws ParseException {
        return DateTimeHelper.objectize(DateTimeType.DateTime, dateTimeStr);
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
        return DateTimeHelper.storablize(DateTimeType.DateTime, this._val);
    }

    public void set(W3cDate val) {
        this._val = val;
    }

    @Override
    public String toString() {
        return this._val.getUTCDateTime(" ");
    }

    @Override
    public int valCode() {
        if (this._val == null) {
            return 0;
        }

        return this._val.getDateTimeByTimezone(" ", "+0000").hashCode();
    }
}

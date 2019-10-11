package com.kurtzhi.pedx.misc;

import com.kurtzhi.pedx.sql.QuotationType;

public abstract class ElementDataType {
    public abstract QuotationType getQuotationType();

    public abstract Object serialize();

    @Override
    public abstract String toString();

    public abstract int valCode();
}

package com.kurtzhi.pedx.datatype;

import com.kurtzhi.pedx.misc.BinaryDataType;
import com.kurtzhi.pedx.misc.DataTypeComparable;

public class PdxBinary extends BinaryDataType implements DataTypeComparable {
    byte[] _val;

    @Override
    public int getMaxLength() {
        return 254;
    }
}

package com.kurtzhi.pedx.datatype;

import com.kurtzhi.pedx.misc.BinaryDataType;
import com.kurtzhi.pedx.misc.DataTypeComparable;

public class PdxBlob extends BinaryDataType implements DataTypeComparable {

    @Override
    public int getMaxLength() {
        return 2147483647; // 2G, 2^31-1
    }
}

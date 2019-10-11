package com.kurtzhi.storage.test.jdo;

import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.FieldConstraint;
import com.kurtzhi.pedx.Proto;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxGUID;

public class HardwareProducer extends Dbo {
    @Proto(FieldConstraint.SIMPLEKEY)
    public PdxGUID harewareProducerId;

    @Proto(value = FieldConstraint.UNIQUE, size = 32)
    public PdxCharacter harewareProducerName;
}

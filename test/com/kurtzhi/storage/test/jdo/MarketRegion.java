package com.kurtzhi.storage.test.jdo;

import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.base.FieldConstraint;
import com.kurtzhi.pedx.base.Proto;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxGUID;

public class MarketRegion extends Dbo {
    @Proto(value = FieldConstraint.SIMPLEKEY)
    public PdxGUID regionId;

    @Proto(value = FieldConstraint.UNIQUE, size = 32)
    public PdxCharacter regionName;
}

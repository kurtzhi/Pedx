package com.kurtzhi.storage.test.jdo;

import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.FieldConstraint;
import com.kurtzhi.pedx.ForeignOnDeleteAction;
import com.kurtzhi.pedx.Proto;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxGUID;

public class Seller extends Dbo {
    @Proto(value = { FieldConstraint.FOREIGN }, refDbo = MarketRegion.class, refOnDelAction = ForeignOnDeleteAction.CASCADE)
    public PdxGUID regionId;

    @Proto(value = { FieldConstraint.SIMPLEKEY })
    public PdxGUID sellerId;

    @Proto(value = FieldConstraint.UNIQUE, size = 32)
    public PdxCharacter sellerName;
}

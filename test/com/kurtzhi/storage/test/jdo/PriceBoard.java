package com.kurtzhi.storage.test.jdo;

import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.base.FieldConstraint;
import com.kurtzhi.pedx.base.ForeignOnDeleteAction;
import com.kurtzhi.pedx.base.Proto;
import com.kurtzhi.pedx.datatype.PdxFloat;
import com.kurtzhi.pedx.datatype.PdxGUID;

public class PriceBoard extends Dbo {
    @Proto(value = { FieldConstraint.FOREIGN, FieldConstraint.COMPOSITEKEY }, refDbo = MobilePhone.class, refOnDelAction = ForeignOnDeleteAction.CASCADE)
    public PdxGUID mobileId;

    @Proto
    public PdxFloat price;

    @Proto(value = { FieldConstraint.FOREIGN, FieldConstraint.COMPOSITEKEY }, refDbo = Seller.class, refOnDelAction = ForeignOnDeleteAction.CASCADE)
    public PdxGUID sellerId;
}

package com.kurtzhi.storage.test.jdo;

import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.FieldConstraint;
import com.kurtzhi.pedx.ForeignOnDeleteAction;
import com.kurtzhi.pedx.Proto;
import com.kurtzhi.pedx.datatype.PdxFloat;
import com.kurtzhi.pedx.datatype.PdxGUID;

public class PriceBoard extends Dbo {
    @Proto(value = { FieldConstraint.FOREIGN, FieldConstraint.COMPOSITEKEY }, refDbo = MobilePhone.class, refField = "mobilePhoneId", refOnDelAction = ForeignOnDeleteAction.CASCADE)
    public PdxGUID mobileId;

    @Proto
    public PdxFloat price;

    @Proto(value = { FieldConstraint.FOREIGN, FieldConstraint.COMPOSITEKEY }, refDbo = Seller.class, refOnDelAction = ForeignOnDeleteAction.CASCADE)
    public PdxGUID sellerId;
}

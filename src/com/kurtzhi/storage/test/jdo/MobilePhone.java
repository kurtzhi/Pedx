package com.kurtzhi.storage.test.jdo;

import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.FieldConstraint;
import com.kurtzhi.pedx.Proto;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxGUID;

public class MobilePhone extends Dbo {
    @Proto(value = FieldConstraint.SIMPLEKEY)
    public PdxGUID mobilePhoneId;

    @Proto(value = FieldConstraint.UNIQUE, size = 32)
    public PdxCharacter mobilePhoneName;

    @Proto(value = FieldConstraint.FOREIGN, refDbo = HardwareProducer.class, refField = "harewareProducerId")
    public PdxGUID mobileProducer;

    @Proto(value = FieldConstraint.FOREIGN, refDbo = Seller.class, refField = "sellerId")
    public PdxGUID mobileSeller;

    @Proto(value = FieldConstraint.FOREIGN, refDbo = Software.class, refField = "softwareId")
    public PdxGUID OS;
}

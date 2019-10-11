package com.kurtzhi.storage.test.jdo;

import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.base.FieldConstraint;
import com.kurtzhi.pedx.base.Proto;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxGUID;

public class MobilePhone extends Dbo {
    @Proto(value = FieldConstraint.SIMPLEKEY)
    public PdxGUID mobilePhoneId;

    @Proto(value = FieldConstraint.UNIQUE, size = 32)
    public PdxCharacter mobilePhoneName;
    /*
    @Proto(value = FieldConstraint.FOREIGN, refDbo = Device.class)
    public PdxGUID deviceId;

    @Proto(value = FieldConstraint.FOREIGN, refDbo = HarewareProducer.class, refField = "hwProducterId")
    public PdxGUID mobileProducer;

    @Proto(value = FieldConstraint.FOREIGN, refDbo = Seller.class, refField = "sellerId")
    public PdxGUID mobileSeller;

    @Proto(value = FieldConstraint.FOREIGN, refDbo = Software.class, refField = "softwareId")
    public PdxGUID OS;*/
}

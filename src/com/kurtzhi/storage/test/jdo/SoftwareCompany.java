package com.kurtzhi.storage.test.jdo;

import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.FieldConstraint;
import com.kurtzhi.pedx.Proto;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxGUID;

public class SoftwareCompany extends Dbo {
    @Proto(value = FieldConstraint.SIMPLEKEY)
    public PdxGUID swCompanyId;

    @Proto(value = FieldConstraint.UNIQUE, size = 32)
    public PdxCharacter swCompanyName;
}

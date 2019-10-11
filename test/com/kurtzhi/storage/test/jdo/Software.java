package com.kurtzhi.storage.test.jdo;

import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.base.FieldConstraint;
import com.kurtzhi.pedx.base.ForeignOnDeleteAction;
import com.kurtzhi.pedx.base.Proto;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxGUID;

public class Software extends Dbo {
    @Proto(value = FieldConstraint.FOREIGN, refDbo = SwCompany.class, refField = "swCompanyId", refOnDelAction = ForeignOnDeleteAction.NOACTION)
    public PdxGUID softwareCompany;

    @Proto(value = FieldConstraint.SIMPLEKEY)
    public PdxGUID softwareId;

    @Proto(value = FieldConstraint.UNIQUE, size = 32)
    public PdxCharacter softwareName;
}

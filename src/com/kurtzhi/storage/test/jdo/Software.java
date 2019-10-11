package com.kurtzhi.storage.test.jdo;

import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.FieldConstraint;
import com.kurtzhi.pedx.ForeignOnDeleteAction;
import com.kurtzhi.pedx.Proto;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxGUID;

public class Software extends Dbo {
    @Proto(value = FieldConstraint.FOREIGN, refDbo = SoftwareCompany.class, refField = "swCompanyId", refOnDelAction = ForeignOnDeleteAction.NOACTION)
    public PdxGUID softwareCompany;

    @Proto(value = FieldConstraint.SIMPLEKEY)
    public PdxGUID softwareId;

    @Proto(value = FieldConstraint.UNIQUE, size = 32)
    public PdxCharacter softwareName;
}

package com.kurtzhi.storage.test.jdo;

import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.base.FieldConstraint;
import com.kurtzhi.pedx.base.ForeignOnDeleteAction;
import com.kurtzhi.pedx.base.Proto;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxGUID;

public class HarewareProducer extends Dbo {
    @Proto(FieldConstraint.SIMPLEKEY)
    public PdxGUID hwProducerId;
    
    @Proto(value = FieldConstraint.FOREIGN, refDbo = Person.class, refField = "personId", refOnDelAction = ForeignOnDeleteAction.NOACTION)
    public PdxGUID hwProducerCeoId;

    @Proto(value = FieldConstraint.UNIQUE, size = 32)
    public PdxCharacter hwProducerName;
}


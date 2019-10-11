package com.kurtzhi.storage.test.jdo;

import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.FieldConstraint;
import com.kurtzhi.pedx.ForeignOnDeleteAction;
import com.kurtzhi.pedx.Proto;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxGUID;

public class GameConsole extends Dbo {
    @Proto(value = FieldConstraint.SIMPLEKEY)
    public PdxGUID gcId;

    @Proto(value = FieldConstraint.UNIQUE, size = 32)
    public PdxCharacter gcName;

    @Proto(value = FieldConstraint.FOREIGN, refDbo = HardwareProducer.class, refField = "harewareProducerId")
    public PdxGUID gcProducer;

    @Proto(value = FieldConstraint.FOREIGN, refDbo = TvChannel.class, refField = "channelId", refOnDelAction = ForeignOnDeleteAction.NOACTION)
    public PdxGUID tvChannel;
}

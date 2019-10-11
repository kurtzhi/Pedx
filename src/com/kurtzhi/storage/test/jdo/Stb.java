package com.kurtzhi.storage.test.jdo;

import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.FieldConstraint;
import com.kurtzhi.pedx.ForeignOnDeleteAction;
import com.kurtzhi.pedx.Proto;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxGUID;

public class Stb extends Dbo {
    @Proto(value = FieldConstraint.FOREIGN, refDbo = TvChannel.class, refField = "channelId", refOnDelAction = ForeignOnDeleteAction.NOACTION)
    public PdxGUID tvChannel;

    @Proto(value = FieldConstraint.SIMPLEKEY)
    public PdxGUID stbId;

    @Proto(value = FieldConstraint.UNIQUE, size = 32)
    public PdxCharacter stbName;
}

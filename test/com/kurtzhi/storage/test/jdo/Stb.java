package com.kurtzhi.storage.test.jdo;

import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.base.FieldConstraint;
import com.kurtzhi.pedx.base.ForeignOnDeleteAction;
import com.kurtzhi.pedx.base.Proto;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxGUID;

public class Stb extends Dbo {
    @Proto(value = FieldConstraint.FOREIGN, refDbo = TvChannel.class, refField = "channelId", refOnDelAction = ForeignOnDeleteAction.NOACTION)
    public PdxGUID channel;

    @Proto(value = FieldConstraint.SIMPLEKEY)
    public PdxGUID stbId;

    @Proto(value = FieldConstraint.UNIQUE, size = 32)
    public PdxCharacter stbName;
}

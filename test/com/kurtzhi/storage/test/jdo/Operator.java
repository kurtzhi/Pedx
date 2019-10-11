package com.kurtzhi.storage.test.jdo;

import java.text.SimpleDateFormat;
import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.base.Derived;
import com.kurtzhi.pedx.base.FieldConstraint;
import com.kurtzhi.pedx.base.Proto;
import com.kurtzhi.pedx.datatype.PdxBinary;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxDate;
import com.kurtzhi.pedx.datatype.PdxGUID;
import com.kurtzhi.pedx.misc.ZoneDateTime;

public class Operator extends Dbo {
    @Proto(FieldConstraint.SIMPLEKEY)
    public PdxGUID operatorId;

    @Proto(value = {FieldConstraint.UNIQUE}, size = 64)
    public PdxCharacter operatorName;

    @Proto
    public PdxDate operatorBirth;

    @Proto(size = 6)
    public PdxBinary operatorPermissions;

    @Derived
    public PrivilegeHolder privHolder;

    @Derived
    private int age;
    
    public class PrivilegeHolder{
        boolean canRead;
        boolean canUpdate;
        boolean canInsert;
        boolean canDelete;
        boolean canExecute;
        boolean canBackup;
    }
    
    protected void computeAge() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy");
        ZoneDateTime now = new ZoneDateTime();
        String current = dateFormatter.format(now.getDate());
        String birthYear = dateFormatter.format(this.operatorBirth.get().getDate());
        this.age = Integer.parseInt(current) - Integer.parseInt(birthYear);
    }
    
    protected void computePrivHolder() {
        byte[] privs = this.operatorPermissions.get();
        if (privs != null) {
            this.privHolder = new PrivilegeHolder();
            this.privHolder.canRead = privs[0] == 1 ? true : false;
            this.privHolder.canInsert = privs[1] == 1 ? true : false;
            this.privHolder.canUpdate = privs[2] == 1 ? true : false;
            this.privHolder.canDelete = privs[3] == 1 ? true : false;
            this.privHolder.canExecute = privs[4] == 1 ? true : false;
            this.privHolder.canBackup = privs[5] == 1 ? true : false;
        }
    }
}

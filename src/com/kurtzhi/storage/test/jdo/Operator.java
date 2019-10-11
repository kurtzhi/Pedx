package com.kurtzhi.storage.test.jdo;

import java.text.SimpleDateFormat;

import com.kurtzhi.pedx.Computed;
import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.FieldConstraint;
import com.kurtzhi.pedx.Proto;
import com.kurtzhi.pedx.datatype.PdxBinary;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxClob;
import com.kurtzhi.pedx.datatype.PdxDate;
import com.kurtzhi.pedx.datatype.PdxDateTime;
import com.kurtzhi.pedx.datatype.PdxGUID;
import com.kurtzhi.pedx.misc.W3cDate;

public class Operator extends Dbo {
    public class PrivilegeHolder {
        public boolean canBackup;
        public boolean canDelete;
        public boolean canExecute;
        public boolean canInsert;
        public boolean canRead;
        public boolean canUpdate;
    }

    @Computed
    public int operatorAge;

    @Proto(FieldConstraint.SIMPLEKEY)
    public PdxGUID operatorId;

    @Proto(value = { FieldConstraint.UNIQUE }, size = 64)
    public PdxCharacter operatorName;

    @Proto(size=1113)
    public PdxClob operatorFile;

    @Proto
    public PdxDate operatorBirth;

    @Proto
    public PdxDateTime operatorCreationtime;

    @Proto(size = 6)
    public PdxBinary operatorPermissions;

    @Computed
    public PrivilegeHolder privHolder;

    protected void computeOperatorAge() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy");
        W3cDate now = new W3cDate();
        String current = dateFormatter.format(now.getDate());
        String birthYear = dateFormatter.format(this.operatorBirth.get()
                .getDate());
        this.operatorAge = Integer.parseInt(current)
                - Integer.parseInt(birthYear);
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
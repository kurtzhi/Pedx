package com.kurtzhi.pedx;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.kurtzhi.pedx.connectionpool.ConnectionPool;
import com.kurtzhi.pedx.connectionpool.DbProxy;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxClob;
import com.kurtzhi.pedx.expression.Eq;
import com.kurtzhi.pedx.expression.Expression;
import com.kurtzhi.pedx.misc.BinaryDataType;
import com.kurtzhi.pedx.misc.ElementDataType;
import com.kurtzhi.pedx.misc.DboHelper;
import com.kurtzhi.pedx.misc.FieldSetter;
import com.kurtzhi.pedx.sql.FieldHelper;
import com.kurtzhi.pedx.sql.QuotationType;
import com.kurtzhi.pedx.sql.TableHelper;

public class Dbo {
    private class Fields2ConditionsResult {
        public String condition;
        public int validCount;

        public Fields2ConditionsResult(String cond, int validCnt) {
            this.condition = cond;
            this.validCount = validCnt;
        }
    }

    private ArrayList<Field> _regFields;
    private ConnectionPool _connPool = ConnectionPool.getConnectionPool();
    private Map<Field, Integer> _fieldSnapshot;
    private Map<Class<?>, FieldSetter> _fs = DboHelper.ObtainFieldSetter();
    private ResultSet _rs;
    private int _state;
    private String _table;
    private String _db;

    private TableHelper _th = Pedx.getTableHelper();

    protected Dbo() {
        this._rs = null;
        this._state = 0;
        this._table = this.getTableName();
        this._regFields = new ArrayList<Field>();
        this._db = Pedx.getDatabaseName();

        Field[] fields = DboHelper.getProtoFields(this.getClass());
        for (Field field : fields) {
            try {
                field.set(this, field.getType().newInstance());
            } catch (IllegalArgumentException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            } catch (IllegalAccessException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            } catch (InstantiationException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            }
        }
        this.keepSnapshot();
    }

    public final boolean delete(Transaction trans) {
        DbProxy proxy;
        if (trans == null || (proxy = trans.proxy) == null) {
            return false;
        }
        
        boolean ret = false;
        String queryStr = "DELETE FROM " + this._table + " WHERE ";
        String cond = "";
        Fields2ConditionsResult f2cResult;

        Field simpleKey = DboHelper.getSimpleKey(this.getClass());
        if (simpleKey != null) {
            f2cResult = this.fields2Conditions(new Field[] { simpleKey }, true);
            cond += f2cResult.condition;
        }

        Field[] compositeKeys = DboHelper.getCompositeKeys(this.getClass());
        int length = compositeKeys.length;
        if (length > 0) {
            f2cResult = this.fields2Conditions(compositeKeys, true);
            if (f2cResult.validCount != 0 && f2cResult.validCount != length) {
                Pedx.logger.error("Some composite keys missing");
            } else {
                if (!cond.isEmpty()) {
                    cond += " AND " + f2cResult.condition;
                } else {
                    cond = f2cResult.condition;
                }
            }
        }

        Field[] unqs = DboHelper.getUniqueKeys(this.getClass());
        length = unqs.length;
        if (length > 0) {
            f2cResult = this.fields2Conditions(unqs, true);
            if (!f2cResult.condition.isEmpty()) {
                if (!cond.isEmpty()) {
                    cond += " AND " + f2cResult.condition;
                } else {
                    cond = f2cResult.condition;
                }
            }
        }

        if (!cond.isEmpty()) {
            queryStr += cond;
            proxy.prepareStatement(queryStr);
            this.fillStatementDesignatedValues(proxy);
            if (proxy.executeUpdate()) {
                this._state = 0;
                this.keepSnapshot();
                ret = true;
            }
        }
        return ret;
    }

    private void evaluateIndirectFields() {
        Class<?> dboClass = this.getClass();
        Field[] fields = DboHelper.GetDerivedFields(dboClass);
        String name;
        Method m = null;
        for (Field field : fields) {
            name = field.getName();
            name = "compute" + name.substring(0, 1).toUpperCase()
                    + name.substring(1);
            try {
                m = dboClass.getDeclaredMethod(name);
            } catch (NoSuchMethodException e) {
            } catch (SecurityException e) {
            }
            if (m != null) {
                m.setAccessible(true);
                try {
                    m.invoke(this);
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        }
    }

    private Fields2ConditionsResult fields2Conditions(Field[] fields,
            boolean ignoreCompare) {
        String cond = "";
        Class<?> fieldType = null;
        QuotationType fieldQuotationType = null;

        int length = fields.length;
        int validCount = 0;
        String beginStr = "";
        String endStr = "";
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                try {
                    if (ignoreCompare
                            || ((ElementDataType) (fields[i].get(this)))
                                    .valCode() != this._fieldSnapshot.get(
                                    fields[i]).intValue()) {
                        if (i != 0) {
                            cond += " AND ";
                        }
                        cond += FieldHelper.translateFieldName(fields[i])
                                + " = ";
                        fieldType = fields[i].getType();
                        try {
                            fieldQuotationType = (QuotationType) fieldType
                                    .getMethod("getQuotationType").invoke(
                                            fieldType.newInstance());
                            if (fieldQuotationType == QuotationType.Quotable) {
                                beginStr = endStr = "'";
                            }
                        } catch (IllegalAccessException e) {
                            Pedx.logger.error(e.getMessage(),
                                    e.fillInStackTrace());
                        } catch (IllegalArgumentException e) {
                            Pedx.logger.error(e.getMessage(),
                                    e.fillInStackTrace());
                        } catch (InvocationTargetException e) {
                            Pedx.logger.error(e.getMessage(),
                                    e.fillInStackTrace());
                        } catch (NoSuchMethodException e) {
                            Pedx.logger.error(e.getMessage(),
                                    e.fillInStackTrace());
                        } catch (InstantiationException e) {
                            Pedx.logger.error(e.getMessage(),
                                    e.fillInStackTrace());
                        }

                        try {
                            if (BinaryDataType.class
                                    .isAssignableFrom(fieldType)
                                    || (!this._db.equals("oracle") && PdxCharacter.class == fieldType)
                                    || PdxClob.class == fieldType) {
                                cond += "?";
                                this._regFields.add(fields[i]);
                            } else {
                                cond += beginStr
                                        + ((ElementDataType) (fields[i]
                                                .get(this))).serialize()
                                        + endStr;
                            }
                        } catch (IllegalArgumentException e) {
                            Pedx.logger.error(e.getMessage(),
                                    e.fillInStackTrace());
                        } catch (IllegalAccessException e) {
                            Pedx.logger.error(e.getMessage(),
                                    e.fillInStackTrace());
                        }
                        validCount++;
                    }
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        }
        return new Fields2ConditionsResult(cond, validCount);
    }

    private void fillStatementDesignatedValues(DbProxy proxy) {
        if (proxy != null && this._regFields.size() > 0) {
            int length = this._regFields.size();
            Field field;
            Class<?> fieldType;
            for (int i = 0; i < length; i++) {
                /*
                 * SQL Server 2012: set[N]?String, get[N]?String Oracle 11g:
                 * get[N]?String DB2 V10: direct value assign,
                 * [{set}|{get}]{1}String MySQL: [{set}|{get}]{1}[N]?String
                 */
                try {
                    field = this._regFields.get(i);
                    fieldType = field.getType();
                    if (fieldType == PdxCharacter.class) {
                        if (this._db.equals("db2")) {
                            proxy.setString(i + 1,
                                    (String) ((PdxCharacter) (field.get(this)))
                                            .serialize());
                        } else {
                            proxy.setNString(i + 1,
                                    (String) ((PdxCharacter) (field.get(this)))
                                            .serialize());
                        }
                    } else if (fieldType == PdxClob.class) {
                        if (this._db.equals("db2")) {
                            proxy.setClob(i + 1, (String) ((PdxClob) (field
                                    .get(this))).serialize());
                        } else {
                            proxy.setNClob(i + 1, (String) ((PdxClob) (field
                                    .get(this))).serialize());
                        }
                    } else {
                        proxy.setBytes(i + 1, (byte[]) ((BinaryDataType) (field
                                .get(this))).serialize());
                    }
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        }
        this._regFields.clear();
    }

    private String getKeyCond() {
        Expression operator = null;
        Condition keyCond = null;

        Field field = DboHelper.getSimpleKey(this.getClass());
        Field[] fields;
        if (field != null) {
            try {
                operator = new Eq(this.getClass(), field.getName(),
                        field.get(this));
            } catch (IllegalArgumentException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            } catch (IllegalAccessException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            }
            keyCond = new Condition(operator);
        } else if ((fields = DboHelper.getCompositeKeys(this.getClass())).length > 1) {
            try {
                for (Field f : fields) {
                    if (operator == null) {
                        operator = new Eq(this.getClass(), f.getName(),
                                f.get(this));
                    } else {
                        operator.and(new Eq(this.getClass(), f.getName(), f
                                .get(this)));
                    }
                }
            } catch (IllegalArgumentException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            } catch (IllegalAccessException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            }
            keyCond = new Condition(operator);
        }

        if (keyCond != null) {
            return keyCond.toString();
        } else {
            return "";
        }
    }

    private String getTableName() {
        return this._th.translateTableName(this.getClass());
    }

    private void keepSnapshot() {
        if (this._fieldSnapshot == null) {
            this._fieldSnapshot = new HashMap<Field, Integer>();
        }
        Field[] fields = DboHelper.getProtoFields(this.getClass());

        ElementDataType fi = null;
        for (Field field : fields) {
            try {
                fi = (ElementDataType) field.get(this);
            } catch (IllegalArgumentException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            } catch (IllegalAccessException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            }
            if (fi != null) {
                this._fieldSnapshot.put(field, fi.valCode());
            }
        }
    }

    public final boolean load() {
        String queryStr = "SELECT * FROM " + this._table + " WHERE ";
        String cond = "";
        Fields2ConditionsResult f2cResult;

        Field simpleKey = DboHelper.getSimpleKey(this.getClass());
        if (simpleKey != null) {
            f2cResult = this
                    .fields2Conditions(new Field[] { simpleKey }, false);
            cond += f2cResult.condition;
        }

        Field[] compositeKey = DboHelper.getCompositeKeys(this.getClass());
        int length = compositeKey.length;
        if (length > 0) {
            f2cResult = this.fields2Conditions(compositeKey, false);
            if (f2cResult.validCount != 0 && f2cResult.validCount != length) {
                Pedx.logger.error("Some composite keys missing");
            } else {
                if (!cond.isEmpty()) {
                    cond += " AND " + f2cResult.condition;
                } else {
                    cond = f2cResult.condition;
                }
            }
        }

        Field[] uniqueFields = DboHelper.getUniqueKeys(this.getClass());
        length = uniqueFields.length;
        if (length > 0) {
            f2cResult = this.fields2Conditions(uniqueFields, false);
            if (!f2cResult.condition.isEmpty()) {
                if (!cond.isEmpty()) {
                    cond += " AND " + f2cResult.condition;
                } else {
                    cond = f2cResult.condition;
                }
            }
        }

        if (!cond.isEmpty()) {
            queryStr += cond;
            DbProxy proxy = this._connPool.request();
            proxy.prepareStatement(queryStr);
            this.fillStatementDesignatedValues(proxy);

            try {
                this._rs = proxy.executeQuery();
                if (this._rs.next() && this._rs.getRow() == 1) {
                    Field[] fields = DboHelper.getProtoFields(this.getClass());
                    for (Field field : fields) {
                        this._fs.get(field.getType())
                                .set(field, this._rs, this);
                    }
                    this._state = 1;
                    this.keepSnapshot();
                    this.evaluateIndirectFields();

                    return true;
                }
                if (!this._rs.isClosed()) {
                    this._rs.close();
                }
            } catch (SQLException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            } finally {
                this._connPool.recycle(proxy);
            }
        }
        return false;
    }

    public boolean modify(Transaction trans) {
        DbProxy proxy = null;;
        if (trans != null && (proxy = trans.proxy) != null) {
            return false;
        }
        boolean ret = false;
        String queryStr = "UPDATE " + this._table + " SET ";
        String fieldsStr = "";
        String valuesStr = "";
        int ifFieldChanged = 0;

        Field[] fields = DboHelper.getProtoFields(this.getClass());
        try {
            int length = fields.length;
            QuotationType fieldQuotationType = null;
            Class<?> fieldType = null;
            String beginStr = "";
            String endStr = "";
            for (int i = 0; i < length; i++) {
                if (((ElementDataType) (fields[i].get(this))).valCode() != this._fieldSnapshot
                        .get(fields[i]).intValue()) {
                    ifFieldChanged = 1;

                    fieldsStr = FieldHelper.translateFieldName(fields[i]);

                    try {
                        fieldType = fields[i].getType();
                        fieldQuotationType = (QuotationType) fieldType
                                .getMethod("getQuotationType").invoke(
                                        fieldType.newInstance());
                    } catch (IllegalAccessException e) {
                        Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                    } catch (IllegalArgumentException e) {
                        Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                    } catch (InvocationTargetException e) {
                        Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                    } catch (NoSuchMethodException e) {
                        Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                    } catch (SecurityException e) {
                        Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                    } catch (InstantiationException e) {
                        Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                    }

                    if (fieldQuotationType == QuotationType.Quotable) {
                        beginStr = endStr = "'";
                    }
                    if (BinaryDataType.class.isAssignableFrom(fieldType)
                            || (!this._db.equals("oracle") && PdxCharacter.class == fieldType)
                            || PdxClob.class == fieldType) {
                        valuesStr = "?";
                        this._regFields.add(fields[i]);
                    } else {
                        valuesStr = beginStr
                                + ((ElementDataType) (fields[i].get(this)))
                                        .serialize() + endStr;
                    }
                    queryStr += fieldsStr + " = " + valuesStr + ", ";
                }
            }
            if (ifFieldChanged == 1) {
                queryStr = queryStr.substring(0, queryStr.lastIndexOf(","));
            }
        } catch (IllegalArgumentException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        } catch (IllegalAccessException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }

        queryStr += " WHERE " + getKeyCond();

        if (ifFieldChanged == 1) {
            proxy.prepareStatement(queryStr);
            this.fillStatementDesignatedValues(proxy);
            if (proxy.executeUpdate()) {
                this._state = 1;
                this.keepSnapshot();
                ret = true;
            }
        }

        return ret;
    }

    private boolean produce(Transaction trans) {
        DbProxy proxy = null;
        if (trans != null && (proxy = trans.proxy) != null) {
            return false;
        }
        boolean ret = false;
        String queryString = "INSERT INTO " + this._table;
        String fieldsStr = "";
        String valuesStr = "";

        Field[] fields = DboHelper.getProtoFields(this.getClass());
        try {
            int length = fields.length;
            QuotationType fieldQuotationType = null;
            Class<?> fieldType = null;
            String beginStr = "";
            String endStr = "";
            for (int i = 0; i < length; i++) {

                if (i != 0) {
                    fieldsStr += ", ";
                    valuesStr += ", ";
                }

                fieldsStr += FieldHelper.translateFieldName(fields[i]);

                try {
                    fieldType = fields[i].getType();
                    fieldQuotationType = (QuotationType) fieldType.getMethod(
                            "getQuotationType").invoke(fieldType.newInstance());
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (NoSuchMethodException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (SecurityException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InstantiationException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }

                if (fieldQuotationType == QuotationType.Quotable) {
                    beginStr = endStr = "'";
                }
                if (BinaryDataType.class.isAssignableFrom(fieldType)
                        || (!this._db.equals("oracle") && PdxCharacter.class == fieldType)
                        || PdxClob.class == fieldType) {
                    valuesStr += "?";
                    this._regFields.add(fields[i]);
                } else {
                    valuesStr += beginStr
                            + ((ElementDataType) (fields[i].get(this)))
                                    .serialize() + endStr;
                }
            }
        } catch (IllegalArgumentException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        } catch (IllegalAccessException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }

        queryString += " (" + fieldsStr + ")" + " VALUES (" + valuesStr + ")";

        proxy.prepareStatement(queryString);
        this.fillStatementDesignatedValues(proxy);
        if (proxy.executeUpdate()) {
            this._state = 1;
            this.keepSnapshot();
            ret = true;
        }
        return ret;
    }

    public final boolean save(Transaction trans) {
        if (this._state == 0) {
            return produce(trans);
        } else if (this._state == 1 && this._fieldSnapshot != null) {
            return modify(trans);
        }
        
        return false;
    }
}
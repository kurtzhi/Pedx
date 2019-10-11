package com.kurtzhi.pedx.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.kurtzhi.pedx.FieldConstraint;
import com.kurtzhi.pedx.Proto;
import com.kurtzhi.pedx.misc.DboHelper;

public abstract class TableHelper {
    FieldHelper _fieldHelper; // Value automatically set by Pedx
    String lineBreak = "\n";
    String clauseStart = lineBreak + " ";
    String clauseEnd = ",";
    String clauseBreak = clauseEnd + clauseStart;
    int cnt;
    String comments;
    Field[] fields;
    ArrayList<Integer> fieldsCapacity;
    ArrayList<Field> fieldsNotNull;

    String subsequentSql;

    String tableName = "";

    static ArrayList<Object> hookHelperMaterialization(String cmd) {
        ArrayList<Object> res = new ArrayList<Object>();
        if (cmd.equals("db2")) {
            res.add(new Db2DatabaseHelper());
            res.add(new Db2FieldHelper());
            res.add(new Db2BatchQueryHelper());
            res.add(new Db2TableHelper());
        } else if (cmd.equals("sqlserver")) {
            res.add(new SqlServerDatabaseHelper());
            res.add(new SqlServerFieldHelper());
            res.add(new SqlServerBatchQueryHelper());
            res.add(new SqlServerTableHelper());
        } else if (cmd.equals("oracle")) {
            res.add(new OracleDatabaseHelper());
            res.add(new OracleFieldHelper());
            res.add(new OracleBatchQueryHelper());
            res.add(new OracleTableHelper());
        } else if (cmd.equals("mysql")) {
            res.add(new MysqlDatabaseHelper());
            res.add(new MysqlFieldHelper());
            res.add(new MysqlBatchQueryHelper());
            res.add(new MysqlTableHelper());
        }

        return res;
    }

    static String hookObtainBatchQueryString(Object object, int[] batchInfo,
            String[] queryInfo) {
        return ((BatchQueryHelper) object).obtainQueryString(batchInfo,
                queryInfo);
    }

    private String createBegin(Class<?> dboClass) {
        return "CREATE TABLE " + tableName + " (";
    }

    abstract String createColumns();

    abstract String createEnd(Class<?> dboClass);

    private String createFields(Class<?> dboClass) {
        String sql = "";
        ArrayList<FieldConstraint[]> tableFieldTypes = new ArrayList<FieldConstraint[]>();
        fields = DboHelper.getProtoFields(dboClass);
        cnt = fields.length;
        fieldsCapacity = new ArrayList<Integer>(cnt);
        fieldsNotNull = new ArrayList<Field>();
        for (int i = 0; i < cnt; i++) {
            if (fields[i].isAnnotationPresent(Proto.class)) {
                Proto elementField = fields[i].getAnnotation(Proto.class);
                fieldsCapacity.add(elementField.size());
                FieldConstraint[] types = elementField.value();
                tableFieldTypes.add(types);
                if (!FieldTypesCompatibilityChecker.CheckPerField(types)) {
                    return sql;
                }

                for (FieldConstraint fieldType : types) {
                    if (fieldType == FieldConstraint.SIMPLEKEY
                            || fieldType == FieldConstraint.UNIQUE
                            || fieldType == FieldConstraint.FOREIGN
                            || fieldType == FieldConstraint.COMPOSITEKEY) {
                        fieldsNotNull.add(fields[i]);
                        break;
                    }
                }
            }
        }

        if (!FieldTypesCompatibilityChecker.CheckAcrossTable(tableFieldTypes)) {
            return sql;
        }

        sql += createColumns();

        Field field;
        if ((field = DboHelper.getSimpleKey(dboClass)) != null) {
            sql += clauseBreak
                    + _fieldHelper.embodyCommonKey(tableName,
                            FieldConstraint.SIMPLEKEY, field);

        } else if ((fields = DboHelper.getCompositeKeys(dboClass)).length > 1) {
            sql += clauseBreak
                    + _fieldHelper.embodyCompositeKey(tableName, fields);
        }

        if ((cnt = (fields = DboHelper.getUniqueKeys(dboClass)).length) > 0) {
            for (int j = 0; j < cnt; j++) {
                sql += clauseBreak
                        + _fieldHelper.embodyCommonKey(tableName,
                                FieldConstraint.UNIQUE, fields[j]);
            }
        }

        ArrayList<Object[]> fis;
        if ((cnt = (fis = DboHelper.getForeignKeyInfos(dboClass)).size()) > 0) {
            comments += "-- Please create the following tables first --"
                    + lineBreak;
            comments += "-- ";
            for (int j = 0; j < cnt; j++) {
                sql += clauseBreak
                        + _fieldHelper.embodyForeignKey(tableName,
                                (Field) (fis.get(j))[0], fis.get(j));
                comments += translateTableName((Class<?>) (fis.get(j))[1])
                        + " ";
            }
            comments += " --" + lineBreak;
        }

        if ((cnt = (fields = DboHelper.getIndexFields(dboClass)).length) > 0) {
            for (int j = 0; j < cnt; j++) {
                this.subsequentSql += lineBreak
                        + _fieldHelper.embodyCommonKey(tableName,
                                FieldConstraint.INDEX, fields[j]);
            }
        }

        return sql;
    }

    public String generateCreateSQL(Class<?> dboClass) {
        comments = "";
        subsequentSql = "";
        tableName = dboClass.getSimpleName();
        String sql = createBegin(dboClass);
        sql += createFields(dboClass);
        sql += createEnd(dboClass);
        sql += subsequentSql.replaceFirst(clauseEnd, "") + "\n";
        subsequentSql = "";
        return comments + sql;
    }

    public String translateTableName(Class<?> dboClass) {
        return dboClass.getSimpleName();
    }
}

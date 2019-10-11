package com.kurtzhi.pedx;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kurtzhi.pedx.connectionpool.ConnectionPool;
import com.kurtzhi.pedx.connectionpool.DbProxy;
import com.kurtzhi.pedx.datatype.PdxBinary;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxDate;
import com.kurtzhi.pedx.datatype.PdxDateTime;
import com.kurtzhi.pedx.misc.DboHelper;
import com.kurtzhi.pedx.misc.FieldObjectizer;
import com.kurtzhi.pedx.misc.FieldReadablizer;
import com.kurtzhi.pedx.sql.FieldHelper;
import com.kurtzhi.pedx.sql.TableHelper;

public class Query {
    protected class FieldRef {
        Class<?> dbo;
        String fieldName;
    }

    private String _cond;
    private String _joinCond;
    private String _queryFrom;
    private String[] _queryInfo; // fields tables condition order auto_order
    private String _querySort;
    private String _queryString;
    private String _queryWhere;
    private ResultSet _rs;
    private Order _sorter;
    private TableHelper _tableHelper;
    private ConnectionPool _connPool;
    private ArrayList<Class<?>> _calculateMasterDepthCache = new ArrayList<Class<?>>();
    private ArrayList<Class<?>> _connectedDbos = new ArrayList<Class<?>>();
    private ArrayList<Map<String, Object>> _data;
    private ArrayList<Class<?>> _dbos;
    private ArrayList<Field> _fields;
    private ArrayList<Field> _joinedFieldsList;
    private ArrayList<Class<?>> _principleDbos = new ArrayList<Class<?>>();
    private Map<Class<?>, Field[]> _dboFields;
    private Map<Field, String> _fieldName;
    private Map<Field, String> _fieldNameSql;
    private Map<Class<?>, FieldReadablizer> _fieldGeneralizer;
    private Map<Class<?>, FieldObjectizer> _fieldSetters;
    private Map<Field, Class<?>> _fieldTables;
    private Map<Field, Class<?>> _fieldTypes;
    private Map<Field, ArrayList<Field>> _joinedFieldsMap;
    private Map<Class<?>, ArrayList<Class<?>>> _masterSlaves = new HashMap<Class<?>, ArrayList<Class<?>>>();
    private Map<String, Field> _nameFields;
    private Map<Class<?>, ArrayList<Field>> _siblingForeignKeys = new HashMap<Class<?>, ArrayList<Field>>();
    private Map<Field, FieldRef> _siblingForeignKeysLink = new HashMap<Field, FieldRef>();
    private Map<Class<?>, ArrayList<Class<?>>> _siblingOrphans = new HashMap<Class<?>, ArrayList<Class<?>>>();
    private Map<Field, FieldRef> _slaveForeignKeyRef = new HashMap<Field, FieldRef>();
    private Map<Class<?>, ArrayList<Field>> _slaveForeignKeys = new HashMap<Class<?>, ArrayList<Field>>();
    private Map<Class<?>, ArrayList<Class<?>>> _slaveMasters = new HashMap<Class<?>, ArrayList<Class<?>>>();
    private Map<Class<?>, String> _tableNames;
    private int _countAll;
    private int _status;
    private int _countQuery;
    private int _batchCount;
    private int _rowsPerBatch = 20;
    private int _weightMagnifyFactor = 10000;
    private int[] _batchInfo;
    private int[] _principleDboWeight;
    private Condition _condition;

    public Query(Class<?> dboClass) {
        this.init(new Class<?>[] { dboClass }, null, null, 0);
    }

    public Query(Class<?> dboClass, Condition condition, Order order) {
        this.init(new Class<?>[] { dboClass }, condition, order, 0);
    }

    public Query(Class<?> dboClass, Condition condition, Order order,
            int rowsPerBatch) {
        this.init(new Class<?>[] { dboClass }, condition, order, rowsPerBatch);
    }

    public Query(Class<?>[] dboClasses, Condition condition, Order order) {
        this.init(dboClasses, condition, order, 0);
    }

    public Query(Class<?>[] dboClasses, Condition condition, Order order,
            int rowsPerBatch) {
        this.init(dboClasses, condition, order, rowsPerBatch);
    }

    private void amendSqlFields() {
        _joinedFieldsMap = new HashMap<Field, ArrayList<Field>>();
        _joinedFieldsList = new ArrayList<Field>();
        int len = _fields.size();
        String name;
        Class<?> type;
        Field field1;
        Field field2;
        ArrayList<Field> linkedFields;

        for (int i = 0; i < len; i++) {
            field1 = _fields.get(i);
            name = _fieldName.get(field1);
            type = _fieldTypes.get(field1);
            for (int j = i + 1; j < len; j++) {
                field2 = _fields.get(j);
                if (name == _fieldName.get(field2)
                        && type == _fieldTypes.get(field2)) {

                    if (!_joinedFieldsList.contains(field1)) {
                        _joinedFieldsList.add(field1);
                    }

                    if ((linkedFields = _joinedFieldsMap.get(field1)) == null) {
                        linkedFields = new ArrayList<Field>();
                    }
                    linkedFields.add(field2);
                    _joinedFieldsMap.put(field1, linkedFields);
                }
            }
        }

        String fieldSqlName;
        for (Field f1 : _joinedFieldsList) {
            linkedFields = _joinedFieldsMap.get(f1);
            for (Field f2 : linkedFields) {
                fieldSqlName = _fieldNameSql.get(f1);
                if (!fieldSqlName.contains(" AS ")) {
                    _fieldNameSql.put(f1, _tableNames.get(_fieldTables.get(f1))
                            + "." + fieldSqlName + " AS " + fieldSqlName);
                }
                _fields.remove(f2);
            }
        }
    }

    private void assemblyQueryString() {
        String qTalbes = "";
        String qFields = "";
        int length = _dbos.size();
        for (int i = 0; i < length; i++) {
            if (i != 0) {
                qTalbes += ", ";
            }
            qTalbes += _tableNames.get(_dbos.get(i));
        }
        _queryInfo[1] = qTalbes;

        length = _fields.size();
        for (int i = 0; i < length; i++) {
            if (i != 0) {
                qFields += ", ";
            }
            qFields += _fieldNameSql.get(_fields.get(i));
        }
        _queryInfo[0] = qFields;

        _queryFrom = " FROM " + qTalbes;
        _queryString = "SELECT " + qFields + _queryFrom;

        if (_joinCond != null && !_joinCond.isEmpty()) {
            if (_cond.isEmpty()) {
                _cond = _joinCond;
            } else {
                _cond += " AND " + _joinCond;
            }
            _queryWhere = " WHERE " + _cond;
            _queryInfo[2] = _cond;
        } else if (!_cond.isEmpty()) {
            _queryWhere = " WHERE " + _cond;
            _queryInfo[2] = _cond;
        }
        _queryString += _queryWhere;

        if (_sorter != null) {
            _querySort = " ORDER BY " + _sorter.toString();
            _queryInfo[3] = _sorter.toString();
        } else {
            Class<?> dbo;
            if (_dbos.size() == 1) {
                dbo = _dbos.get(0);
            } else {
                dbo = _principleDbos.get(0);
            }
            String orderString = "";
            Field sk = DboHelper.getSimpleKey(dbo);
            if (sk == null) {
                Field[] ck = DboHelper.getCompositeKeys(dbo);
                for (Field field : ck) {
                    orderString += " " + FieldHelper.translateFieldName(field)
                            + " ASC,";
                }
                orderString.substring(0, orderString.length() - 2);
            } else {
                orderString = FieldHelper.translateFieldName(sk) + " ASC";
            }
            _queryInfo[4] = orderString;
        }
        _queryString += _querySort;
    }

    public void batchSelect(int batchIndex) {
        if (_status == -1) {
            terminateApp();
            return;
        }
        if (_rowsPerBatch > 0) {
            this.realSelect(batchIndex);
        } else {
            Pedx.logger.error("Batch select not supported");
        }
    }

    public int batchSize() {
        return (int) Math.ceil((double) _countAll / (double) _rowsPerBatch);
    }

    private void buildJoinConditions(Class<?> master) {
        ArrayList<Class<?>> slaves;
        if ((slaves = _masterSlaves.get(master)) != null) {
            String cond;
            for (Class<?> slave : slaves) {
                ArrayList<Field> slaveFks = _slaveForeignKeys.get(slave);
                for (Field fk : slaveFks) {
                    FieldRef refInfo = _slaveForeignKeyRef.get(fk);
                    if (refInfo.dbo == master) {
                        cond = " AND " + _tableNames.get(master) + "."
                                + refInfo.fieldName + " = "
                                + _tableNames.get(slave) + "."
                                + FieldHelper.translateFieldName(fk);
                        if (_joinCond.indexOf(cond) == -1) {
                            _joinCond += cond;
                        } else {
                            return;
                        }
                    }
                }

                if (slave != null) {
                    buildJoinConditions(slave);
                }
            }
        }

        ArrayList<Class<?>> siblings;
        if ((siblings = _siblingOrphans.get(master)) == null) {
            return;
        }
        String lc;
        String rc;
        for (Class<?> sibling : siblings) {
            ArrayList<Field> siblingFks = _siblingForeignKeys.get(sibling);
            for (Field fk : siblingFks) {
                FieldRef refInfo = _siblingForeignKeysLink.get(fk);
                if (refInfo.dbo == master) {
                    lc = _tableNames.get(master) + "." + refInfo.fieldName;
                    rc = _tableNames.get(sibling) + "."
                            + FieldHelper.translateFieldName(fk);
                    if (_joinCond.indexOf(lc + " = " + rc) == -1
                            && _joinCond.indexOf(rc + " = " + lc) == -1) {
                        _joinCond += " AND " + rc + " = " + lc;
                    } else {
                        return;
                    }
                }
            }

            if (sibling != null) {
                buildJoinConditions(sibling);
            }
        }
    }

    private int calculateMasterDepth(Class<?> start) {
        _calculateMasterDepthCache.clear();
        return realCalculateMasterDepth(start);
    }

    public int count() {
        return _countAll == 0 ? _countQuery : _countAll;
    }

    public int countBatch() {
        return _countQuery;
    }

    private void extractData() {
        Map<String, Object> row = null;
        int rowSize = _fields.size();
        Field field;
        String fieldName;

        try {
            if (null != _data) {
                _data.clear();
            } else {
                _data = new ArrayList<Map<String, Object>>();
            }
            _countQuery = 0;
            if (_rs != null) {
                while (false != _rs.next()) {
                    row = new HashMap<String, Object>(rowSize);
                    for (int j = 0; j < rowSize; j++) {
                        field = _fields.get(j);
                        fieldName = _fieldName.get(field);
                        row.put(fieldName,
                                _fieldGeneralizer.get(_fieldTypes.get(field))
                                        .readablize(fieldName, _rs));
                    }
                    _data.add(row);
                    _countQuery++;
                }
                _rs.close();
            }
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }
    }

    public Object getField(String field, int row) {
        if (_status == -1) {
            terminateApp();
            return null;
        }

        if (row >= 0 && row < _countQuery) {
            return _data.get(row).get(field);
        }
        return null;
    }

    public List<Object> fetchColumn(String field) {
        if (_status == -1) {
            terminateApp();
            return null;
        }
        if (_nameFields.get(field) != null) {
            ArrayList<Object> fields = new ArrayList<Object>(_countQuery);
            for (int i = 0; i < _countQuery; i++) {
                fields.add(_data.get(i).get(field));
            }
            return fields;
        }
        return null;
    }

    public List<Object> fetchColumn(String field, int startRow,
            int endRow) {
        if (_status == -1) {
            terminateApp();
            return null;
        }
        if (_nameFields.get(field) != null && startRow >= 0 && endRow <= _countQuery
                && startRow <= endRow) {
            int length = endRow - startRow + 1;
            ArrayList<Object> fields = new ArrayList<Object>(_countQuery);
            for (int i = 0; i < length; i++) {
                fields.add(_data.get(startRow + i).get(field));
            }
            return fields;
        }
        return null;
    }

    public List<Map<String, Object>> fetchColumnsAsoc(String[] fields) {
        if (_status == -1) {
            terminateApp();
            return null;
        }
        for (String f : fields) {
            if (_nameFields.get(f) == null) {
                return null;
            }
        }
        
        ArrayList<Map<String, Object>> res = new ArrayList<Map<String, Object>>(_countQuery);
        for (int i = 0; i < _countQuery; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            for (String f : fields) {
                map.put(f, _data.get(i).get(f));
            }
            res.add(map);
        }
        
        return res;
    }

    public List<Map<String, Object>> fetchColumnsAsoc(String fields[], int startRow,
            int endRow) {
        if (_status == -1) {
            terminateApp();
            return null;
        }
        for (String f : fields) {
            if (_nameFields.get(f) == null) {
                return null;
            }
        }
        
        if (startRow >= 0 && endRow <= _countQuery && startRow <= endRow) {
            int len = endRow - startRow + 1;
            ArrayList<Map<String, Object>> res = new ArrayList<Map<String, Object>>(len);
            for (int i = 0; i < len; i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                for (String f : fields) {
                    map.put(f, _data.get(startRow + i).get(f));
                }
            }
            return res;
        }
        
        return null;
    }

    public Object[][] fetchColumnsBrief(String[] fields) {
        if (_status == -1) {
            terminateApp();
            return null;
        }
        for (String f : fields) {
            if (_nameFields.get(f) == null) {
                return null;
            }
        }
        int j, len = fields.length;
        Object[][] res = new Object[_countQuery][];
        for (int i = 0; i < _countQuery; i++) {
            Object[] row = new Object[len];
            j = 0;
            for (String f : fields) {
                row[j++] = _data.get(i).get(f);
            }
            res[i] = row;
        }
        
        return res;
    }

    public Object[][] fetchColumnsBrief(String fields[], int startRow,
            int endRow) {
        if (_status == -1) {
            terminateApp();
            return null;
        }
        for (String f : fields) {
            if (_nameFields.get(f) == null) {
                return null;
            }
        }

        if (startRow >= 0 && endRow <= _countQuery && startRow <= endRow) {
            int j, len = fields.length;
            int cnt = endRow - startRow + 1;
            Object[][] res = new Object[cnt][];
            for (int i = 0; i < cnt; i++) {
                Object[] row = new Object[len];
                j = 0;
                for (String f : fields) {
                    row[j++] = _data.get(startRow + i).get(f);
                }
            }
            return res;
        }
        
        return null;
    }

    public Map<String, Object> fetchRow(int row) {
        if (_status == -1) {
            terminateApp();
            return null;
        }
        if (row >= 0 && row < _countQuery) {
            return _data.get(row);
        }

        return null;
    }

    public ArrayList<Map<String, Object>> fetchRows() {
        if (_status == -1) {
            terminateApp();
            return null;
        }
        return _data;
    }

    public ArrayList<Map<String, Object>> fetchRows(int start, int end) {
        if (_status == -1) {
            terminateApp();
            return null;
        }
        if (start >= 0 && end < _countQuery && start < end) {
            int length = end - start + 1;
            ArrayList<Map<String, Object>> res = new ArrayList<Map<String, Object>>(
                    length);
            for (int i = 0; i < length; i++) {
                res.add(_data.get(start + i));
            }
            return res;
        }
        return null;
    }

    private void getAllRowsCount() {
        String queryString = "SELECT COUNT(*) ";
        queryString += _queryFrom + _queryWhere + _querySort;
        DbProxy proxy = _connPool.request();
        try {
            proxy.prepareStatement(queryString);
            _rs = proxy.executeQuery();
            _rs.next();
            _countAll = _rs.getInt(1);
            _rs.close();
        } catch (SQLException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        } finally {
            if (proxy != null) {
                _connPool.recycle(proxy);
            }
        }
    }

    public Dbo getDbo(Class<?> dboClass, int row) {
        if (_status == -1) {
            terminateApp();
            return null;
        }
        if (row >= 0 && row < _countQuery) {
            if (_dbos.indexOf(dboClass) != -1) {
                Map<String, Object> data = _data.get(row);
                Field[] fields = _dboFields.get(dboClass);

                Dbo dbo = null;
                try {
                    dbo = (Dbo) dboClass.newInstance();
                } catch (InstantiationException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                    return null;
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                    return null;
                }
                for (Field field : fields) {
                    _fieldSetters.get(_fieldTypes.get(field)).objectize(field,
                            data.get(_fieldName.get(field)), dbo);
                }

                try {
                    Field f = dbo.getClass().getSuperclass()
                            .getDeclaredField("_state");
                    f.setAccessible(true);
                    f.set(dbo, 1);
                    Method m = dbo.getClass().getSuperclass()
                            .getDeclaredMethod("keepSnapshot");
                    m.setAccessible(true);
                    m.invoke(dbo);
                    m = dbo.getClass().getSuperclass()
                            .getDeclaredMethod("evaluateIndirectFields");
                    m.setAccessible(true);
                    m.invoke(dbo);
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (SecurityException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (NoSuchMethodException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (NoSuchFieldException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                return dbo;
            }
        }
        return null;
    }

    private void init(Class<?>[] dboClasses, Condition condition, Order sorter,
            int rowsPerBatch) {
        this._status = -1;
        if (!Pedx.isProperlyInitialized()) {
            this._status = -1;
            terminateApp();
            return;
        }

        if (condition != null && condition._error) {
            this._status = -1;
            terminateApp();
            return;
        }
        _connPool = ConnectionPool.getConnectionPool();
        _tableHelper = Pedx.getTableHelper();
        _fieldGeneralizer = DboHelper.ObtainFieldReadablizer();
        _fieldSetters = DboHelper.ObtainFieldObjectizer();
        _queryWhere = "";
        _querySort = "";
        _condition = condition;
        _cond = condition != null ? condition.toString() : "";
        _sorter = sorter;
        _queryInfo = new String[5];
        _rowsPerBatch = rowsPerBatch > 0 ? rowsPerBatch : 0;

        testDbo(dboClasses);
        initDbo();
        assemblyQueryString();

        if (_rowsPerBatch > 0) {
            getAllRowsCount();
            _batchInfo = new int[3];
            _batchInfo[1] = _rowsPerBatch;
            _batchInfo[2] = _countAll;
            _batchCount = (int) Math.ceil((double) _countAll
                    / (double) _rowsPerBatch);
        }

        _nameFields = new HashMap<String, Field>();
        int len = _fields.size();
        Field field;
        for (int i = 0; i < len; i++) {
            field = _fields.get(i);
            _nameFields.put(_fieldName.get(field), field);
        }
        this._status = 1;
    }

    private void initDbo() {
        _tableNames = new HashMap<Class<?>, String>();
        _dboFields = new HashMap<Class<?>, Field[]>();
        _fieldName = new HashMap<Field, String>();
        _fieldNameSql = new HashMap<Field, String>();
        _fieldTables = new HashMap<Field, Class<?>>();
        _fieldTypes = new HashMap<Field, Class<?>>();
        _fields = new ArrayList<Field>();

        String fieldName = null;
        for (Class<?> dboClass : _dbos) {
            _tableNames
                    .put(dboClass, _tableHelper.translateTableName(dboClass));
            Field[] fields = DboHelper.getProtoFields(dboClass);
            _dboFields.put(dboClass, fields);
            for (Field field : fields) {
                _fields.add(field);
                fieldName = FieldHelper.translateFieldName(field);
                _fieldName.put(field, fieldName);
                _fieldNameSql.put(field, fieldName);
                _fieldTables.put(field, dboClass);
                _fieldTypes.put(field, field.getType());
            }
        }

        if (_dbos.size() > 1) {
            amendSqlFields();
            organiseForeignRelations();
            parseJoinedCondition();
        }
    }

    private void organiseForeignRelations() {
        int cnt;
        ArrayList<Class<?>> orphanDbos = new ArrayList<Class<?>>();
        Map<Class<?>, ArrayList<Object[]>> lostedDboForeigns = new HashMap<Class<?>, ArrayList<Object[]>>();
        ArrayList<Field> fields;
        ArrayList<Class<?>> tmpDbos;

        // Organize master-slave(parent-child) relationships
        Field field;
        Class<?> refDbo;
        String refFieldName;
        for (Class<?> dbo : _dbos) {
            ArrayList<Object[]> foreignInfos = DboHelper
                    .getForeignKeyInfos(dbo);
            if (foreignInfos != null && foreignInfos.size() > 0) {
                cnt = 0;
                for (Object[] frgnInfo : foreignInfos) {
                    refDbo = (Class<?>) frgnInfo[1];
                    if (_dbos.contains(refDbo)) {
                        if ((fields = _slaveForeignKeys.get(dbo)) == null) {
                            fields = new ArrayList<Field>();
                        }
                        field = (Field) frgnInfo[0];
                        refFieldName = (String) frgnInfo[2];
                        fields.add(field);
                        _slaveForeignKeys.put(dbo, fields);
                        FieldRef refInfo = new FieldRef();
                        refInfo.dbo = refDbo;
                        refInfo.fieldName = refFieldName;
                        _slaveForeignKeyRef.put(field, refInfo);
                        if ((tmpDbos = _masterSlaves.get(refDbo)) == null) {
                            tmpDbos = new ArrayList<Class<?>>();
                            tmpDbos.add(dbo);
                            _masterSlaves.put(refDbo, tmpDbos);
                        } else if (!tmpDbos.contains(dbo)) {
                            tmpDbos.add(dbo);
                            _masterSlaves.put(refDbo, tmpDbos);
                        }
                        if ((tmpDbos = _slaveMasters.get(dbo)) == null) {
                            tmpDbos = new ArrayList<Class<?>>();
                            tmpDbos.add(refDbo);
                            _slaveMasters.put(dbo, tmpDbos);
                        } else if (!tmpDbos.contains(refDbo)) {
                            tmpDbos.add(refDbo);
                            _slaveMasters.put(dbo, tmpDbos);
                        }
                        cnt++;
                    } else {
                        ArrayList<Object[]> fis;
                        if ((fis = lostedDboForeigns.get(dbo)) == null) {
                            fis = new ArrayList<Object[]>();
                        }
                        fis.add(frgnInfo);
                        lostedDboForeigns.put(dbo, fis);
                    }
                }
                if (cnt == 0) {
                    orphanDbos.add(dbo);
                }
            } else {
                _principleDbos.add(dbo);
            }
        }

        // Organize sibling relationships between orphans
        Class<?>[] dbos = lostedDboForeigns.keySet().toArray(
                new Class<?>[lostedDboForeigns.size()]);
        int length = dbos.length;
        Field field2;
        Class<?> refDbo2;
        String refFieldName2;
        for (int i = 0; i < length; i++) {
            for (Object[] foreignInfo : lostedDboForeigns.get(dbos[i])) {
                field = (Field) foreignInfo[0];
                refDbo = (Class<?>) foreignInfo[1];
                refFieldName = (String) foreignInfo[2];
                for (int j = i + 1; j < length; j++) {
                    for (Object[] foreignInfo2 : lostedDboForeigns.get(dbos[j])) {
                        field2 = (Field) foreignInfo2[0];
                        refDbo2 = (Class<?>) foreignInfo2[1];
                        refFieldName2 = (String) foreignInfo2[2];
                        if (refDbo == refDbo2
                                && refFieldName.equals(refFieldName2)) {
                            if ((fields = _siblingForeignKeys.get(dbos[i])) == null) {
                                fields = new ArrayList<Field>();
                            }
                            fields.add(field);
                            _siblingForeignKeys.put(dbos[i], fields);

                            FieldRef fr = new FieldRef();
                            fr.dbo = dbos[j];
                            fr.fieldName = FieldHelper
                                    .translateFieldName(field2);
                            _siblingForeignKeysLink.put(field, fr);

                            if ((fields = _siblingForeignKeys.get(dbos[j])) == null) {
                                fields = new ArrayList<Field>();
                            }
                            fields.add(field2);
                            _siblingForeignKeys.put(dbos[j], fields);

                            fr = new FieldRef();
                            fr.dbo = dbos[i];
                            fr.fieldName = FieldHelper
                                    .translateFieldName(field);
                            _siblingForeignKeysLink.put(field2, fr);

                            // Let sibling dbos map to each other
                            if ((tmpDbos = _siblingOrphans.get(dbos[i])) == null) {
                                tmpDbos = new ArrayList<Class<?>>();
                                tmpDbos.add(dbos[j]);
                                _siblingOrphans.put(dbos[i], tmpDbos);
                            } else if (!tmpDbos.contains(dbos[j])) {
                                tmpDbos.add(dbos[j]);
                                _siblingOrphans.put(dbos[i], tmpDbos);
                            }
                            if ((tmpDbos = _siblingOrphans.get(dbos[j])) == null) {
                                tmpDbos = new ArrayList<Class<?>>();
                                tmpDbos.add(dbos[i]);
                                _siblingOrphans.put(dbos[j], tmpDbos);
                            } else if (!tmpDbos.contains(dbos[i])) {
                                tmpDbos.add(dbos[i]);
                                _siblingOrphans.put(dbos[j], tmpDbos);
                            }
                        }
                    }
                }
            }
        }
        _principleDbos.addAll(orphanDbos);

        // Check connectivity of dbos
        registerConnectedDbos(_dbos.get(0));

        if (!_connectedDbos.containsAll(_dbos)) {
            _dbos.removeAll(_connectedDbos);
            String isolatedDbos = "";
            for (Class<?> dbo : _dbos) {
                isolatedDbos += ", " + dbo.getSimpleName();
            }
            isolatedDbos.replaceFirst(", ", "");
            Pedx.logger.error("Cannot join with isolated dbo(s): "
                    + isolatedDbos);
            return;
        }

        length = _principleDbos.size();
        _principleDboWeight = new int[length];
        int i = 0;
        for (Class<?> dbo : _principleDbos) {
            _principleDboWeight[i] = _weightMagnifyFactor
                    * calculateMasterDepth(dbo) + i;
            i++;
        }
        Arrays.sort(_principleDboWeight);
    }

    private void parseJoinedCondition() {
        _joinCond = "";
        for (int i = _principleDboWeight.length - 1; i >= 0; i--) {
            buildJoinConditions(_principleDbos.get(_principleDboWeight[i]
                    % _weightMagnifyFactor));
        }
        /*
         * for (Class<?> principleDbo : principleDbos) {
         * buildJoinConditions(principleDbo); }
         */
        _joinCond = _joinCond.replaceFirst(" AND ", "");

        _masterSlaves.clear();
        _slaveMasters.clear();
        _slaveForeignKeys.clear();
        _slaveForeignKeyRef.clear();
        _siblingOrphans.clear();
        _siblingForeignKeys.clear();
        _siblingForeignKeysLink.clear();
    }

    private int realCalculateMasterDepth(Class<?> start) {
        int depth = 0;
        if (start != null) {
            if (!_calculateMasterDepthCache.contains(start)) {
                _calculateMasterDepthCache.add(start);
                depth++;
            } else {
                return depth;
            }

            ArrayList<Class<?>> relatives;
            if ((relatives = _masterSlaves.get(start)) != null) {
                for (Class<?> relative : relatives) {
                    depth += realCalculateMasterDepth(relative);
                }
            }
            if ((relatives = _siblingOrphans.get(start)) != null) {
                for (Class<?> relative : relatives) {
                    depth += realCalculateMasterDepth(relative);
                }
            }
        }

        return depth;
    }

    private void realSelect(int batchIndex) {
        String queryString = "";
        if (_rowsPerBatch > 0) {
            if (batchIndex + 1 > _batchCount) {
                Pedx.logger
                        .error("Index specified for batch select out of range");
            }
            _batchInfo[0] = batchIndex;

            try {
                Method m;
                Class<?>[] params = new Class<?>[3];
                params[0] = Object.class;
                params[1] = int[].class;
                params[2] = String[].class;
                m = TableHelper.class.getDeclaredMethod(
                        "hookObtainBatchQueryString", params);
                m.setAccessible(true);
                queryString = (String) m.invoke(null,
                        Pedx.getBatchQueryHelper(), _batchInfo, _queryInfo);
            } catch (NoSuchMethodException e) {
                queryString = "";
            } catch (IllegalArgumentException e) {
                queryString = "";
            } catch (IllegalAccessException e) {
                queryString = "";
            } catch (InvocationTargetException e) {
                queryString = "";
            }
        } else {
            queryString = _queryString;
        }

        if (queryString.isEmpty()) {
            return;
        }

        DbProxy proxy = _connPool.request();
        proxy.prepareStatement(queryString);
        if (_condition != null && _condition.hasDesignatedTypes()) {
            fillStatementDesignatedValues(proxy);
        }
        _rs = proxy.executeQuery();
        extractData();
        if (proxy != null) {
            _connPool.recycle(proxy);
        }
    }

    private void fillStatementDesignatedValues(DbProxy proxy) {
        ArrayList<Class<?>> dataTypes = _condition._regDesignatedTypes;
        ArrayList<Object> values = _condition._regDesignatedValues;
        int length = dataTypes.size();
        Class<?> fieldType;
        String db = Pedx.getDatabaseName();
        for (int i = 0; i < length; i++) {
            try {
                fieldType = dataTypes.get(i);
                if (fieldType == PdxCharacter.class) {
                    if (db.equals("db2")) {
                        proxy.setString(i + 1, (String) values.get(i));
                    } else {
                        proxy.setNString(i + 1, (String) values.get(i));
                    }
                } else if (fieldType == PdxBinary.class) {
                    proxy.setBytes(i + 1, (byte[]) values.get(i));
                } else if (fieldType == PdxDate.class) {
                    proxy.setLong(i + 1, Long.parseLong((String) values.get(i)));
                } else if (fieldType == PdxDateTime.class) {
                    proxy.setBigDecimal(i + 1, (String) values.get(i));
                }
            } catch (IllegalArgumentException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            }
        }
    }

    private void registerConnectedDbos(Class<?> start) {
        if (start != null) {
            if (!_connectedDbos.contains(start)) {
                _connectedDbos.add(start);
            } else {
                return;
            }
            ArrayList<Class<?>> relatives;
            if ((relatives = _slaveMasters.get(start)) != null) {
                for (Class<?> relative : relatives) {
                    if (!_connectedDbos.contains(relative)) {
                        registerConnectedDbos(relative);
                    }
                }
            }
            if ((relatives = _masterSlaves.get(start)) != null) {
                for (Class<?> relative : relatives) {
                    if (!_connectedDbos.contains(relative)) {
                        registerConnectedDbos(relative);
                    }
                }
            }
            if ((relatives = _siblingOrphans.get(start)) != null) {
                for (Class<?> relative : relatives) {
                    if (!_connectedDbos.contains(relative)) {
                        registerConnectedDbos(relative);
                    }
                }
            }
        }
    }

    public void select() {
        if (_status == -1) {
            terminateApp();
            return;
        }
        this.realSelect(0);
    }

    private void terminateApp() {
        System.err.println("Queryer is not properly initialized.");
    }

    private void testDbo(Class<?>[] dboClasses) {
        _dbos = new ArrayList<Class<?>>();

        for (Class<?> dboClass : dboClasses) {
            if (Dbo.class.isAssignableFrom(dboClass)) {
                _dbos.add(dboClass);
            }
        }

        if (dboClasses.length != _dbos.size()) {
            Pedx.logger.error("Non dbo class detected");
        }
    }
}

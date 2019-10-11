package com.kurtzhi.pedx.misc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.kurtzhi.pedx.Computed;
import com.kurtzhi.pedx.Dbo;
import com.kurtzhi.pedx.FieldConstraint;
import com.kurtzhi.pedx.Pedx;
import com.kurtzhi.pedx.Proto;
import com.kurtzhi.pedx.datatype.PdxBinary;
import com.kurtzhi.pedx.datatype.PdxBlob;
import com.kurtzhi.pedx.datatype.PdxByte;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxClob;
import com.kurtzhi.pedx.datatype.PdxDate;
import com.kurtzhi.pedx.datatype.PdxDateTime;
import com.kurtzhi.pedx.datatype.PdxFloat;
import com.kurtzhi.pedx.datatype.PdxGUID;
import com.kurtzhi.pedx.datatype.PdxInteger;
import com.kurtzhi.pedx.sql.FieldHelper;

public class DboHelper {
    static ArrayList<Class<?>> _compatibleDataTypes = null;
    static Map<Class<?>, Method> _dataTypeSetter = null;
    static Map<Class<?>, Field[]> _dboCompositeKeys = new HashMap<Class<?>, Field[]>();
    static Map<Class<?>, Field[]> _dboDerivedFields = new HashMap<Class<?>, Field[]>();
    static Map<Class<?>, Field[]> _dboProtoFields = new HashMap<Class<?>, Field[]>();
    static Map<Class<?>, Field> _dboSimpleKey = new HashMap<Class<?>, Field>();
    static Map<Class<?>, Field[]> _dboUniqueKeys = new HashMap<Class<?>, Field[]>();
    private static Map<Class<?>, FieldObjectizer> _fieldObjectizer = null;
    static Map<Class<?>, FieldReadablizer> _fieldReadablizer = null;
    static Map<Class<?>, FieldSetter> _fieldSetter = null;

    public static Field[] getCompositeKeys(Class<?> dbo) {
        Field[] protos = getProtoFields(dbo);
        ArrayList<Field> fields = new ArrayList<Field>();
        Class<? extends Annotation> fieldType = Proto.class;

        for (Field field : protos) {
            try {
                Method m = fieldType.getDeclaredMethod("value");
                Object annt = field.getAnnotation(fieldType);
                try {
                    Object[] constraints = (Object[]) m.invoke(annt);
                    for (Object cstr : constraints) {
                        if (cstr == FieldConstraint.COMPOSITEKEY) {
                            fields.add(field);
                            break;
                        }
                    }
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            } catch (NoSuchMethodException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            }
        }

        return fields.toArray(new Field[fields.size()]);
    }

    public static Field[] GetDerivedFields(Class<?> dbo) {

        Field[] allFields = dbo.getDeclaredFields();
        ArrayList<Field> fields = new ArrayList<Field>();

        for (Field field : allFields) {
            if (field.isAnnotationPresent(Computed.class)) {
                fields.add(field);
            }
        }

        return fields.toArray(new Field[fields.size()]);
    }

    public static ArrayList<Object[]> getForeignKeyInfos(Class<?> dbo) {
        Field[] protos = getProtoFields(dbo);
        ArrayList<Object[]> foreignInfos = new ArrayList<Object[]>();
        Class<? extends Annotation> fieldType = Proto.class;

        for (Field field : protos) {
            try {
                Object annt = field.getAnnotation(fieldType);
                Method m = fieldType.getDeclaredMethod("value");
                try {
                    Object[] constraints = (Object[]) m.invoke(annt);
                    for (Object cstr : constraints) {
                        if (cstr == FieldConstraint.FOREIGN) {
                            m = fieldType.getDeclaredMethod("refDbo");
                            Object refDbo = m.invoke(annt);
                            if (refDbo != Dbo.class) {
                                m = fieldType
                                        .getDeclaredMethod("refOnDelAction");
                                Object refOnDelAction = m.invoke(annt);
                                m = fieldType.getDeclaredMethod("refField");
                                Object refFieldName = m.invoke(annt);
                                refFieldName = ((String) refFieldName)
                                        .isEmpty() ? FieldHelper
                                        .translateFieldName(field)
                                        : refFieldName;
                                Object[] info = new Object[4];
                                info[0] = field;
                                info[1] = refDbo;
                                info[2] = refFieldName;
                                info[3] = refOnDelAction;
                                foreignInfos.add(info);
                            }
                            break;
                        }
                    }
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            } catch (NoSuchMethodException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            }
        }

        return foreignInfos;
    }

    public static Field[] getIndexFields(Class<?> dbo) {
        Field[] protos = getProtoFields(dbo);
        ArrayList<Field> fields = new ArrayList<Field>();
        Class<? extends Annotation> fieldType = Proto.class;
        String method = "value";

        for (Field field : protos) {
            try {
                Object ant = field.getAnnotation(fieldType);
                Method m = fieldType.getDeclaredMethod(method);
                try {
                    Object[] objs = (Object[]) m.invoke(ant);
                    for (Object obj : objs) {
                        if (obj == FieldConstraint.INDEX) {
                            fields.add(field);
                        }
                    }
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            } catch (NoSuchMethodException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            }
        }

        return fields.toArray(new Field[fields.size()]);
    }

    public static Field[] getProtoFields(Class<?> dbo) {
        Field[] allFields = dbo.getDeclaredFields();
        ArrayList<Field> fields = new ArrayList<Field>();

        for (Field field : allFields) {
            if (field.isAnnotationPresent(Proto.class)) {
                fields.add(field);
            }
        }

        return fields.toArray(new Field[fields.size()]);
    }

    public static Field getSimpleKey(Class<?> dbo) {
        Field simpKey = null;
        Field[] protos = getProtoFields(dbo);
        Class<? extends Annotation> fieldType = Proto.class;

        for (Field field : protos) {
            try {
                Method m = fieldType.getDeclaredMethod("value");
                Object annt = field.getAnnotation(fieldType);
                try {
                    Object[] constraints = (Object[]) m.invoke(annt);
                    if (constraints[0] == FieldConstraint.SIMPLEKEY) {
                        simpKey = field;
                        break;
                    }
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            } catch (NoSuchMethodException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            }
        }

        return simpKey;
    }

    public static Field[] getUniqueKeys(Class<?> dbo) {
        Field[] protos = getProtoFields(dbo);
        ArrayList<Field> fields = new ArrayList<Field>();
        Class<? extends Annotation> fieldType = Proto.class;

        for (Field field : protos) {
            try {
                Method m = fieldType.getDeclaredMethod("value");
                Object annt = field.getAnnotation(fieldType);
                try {
                    Object[] constraints = (Object[]) m.invoke(annt);
                    for (Object cstr : constraints) {
                        if (cstr == FieldConstraint.UNIQUE) {
                            fields.add(field);
                            break;
                        }
                    }
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            } catch (NoSuchMethodException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            }
        }

        return fields.toArray(new Field[fields.size()]);
    }

    private static void InitDataTypeSetter() {
        _dataTypeSetter = new HashMap<Class<?>, Method>();
        try {
            _dataTypeSetter.put(PdxBinary.class,
                    PdxBinary.class.getMethod("set", byte[].class));
            _dataTypeSetter.put(PdxBlob.class,
                    PdxBlob.class.getMethod("set", byte[].class));
            _dataTypeSetter.put(PdxByte.class,
                    PdxByte.class.getMethod("set", int.class));
            _dataTypeSetter.put(PdxCharacter.class,
                    PdxCharacter.class.getMethod("set", String.class));
            _dataTypeSetter.put(PdxClob.class,
                    PdxClob.class.getMethod("set", String.class));
            _dataTypeSetter.put(PdxDate.class,
                    PdxDate.class.getMethod("set", W3cDate.class));
            _dataTypeSetter.put(PdxDateTime.class,
                    PdxDateTime.class.getMethod("set", W3cDate.class));
            _dataTypeSetter.put(PdxFloat.class,
                    PdxFloat.class.getMethod("set", double.class));
            _dataTypeSetter.put(PdxGUID.class,
                    PdxGUID.class.getMethod("set", String.class));
            _dataTypeSetter.put(PdxInteger.class,
                    PdxInteger.class.getMethod("set", long.class));
        } catch (NoSuchMethodException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }
    }

    private static void InitFieldObjectizer() {
        ObtainDataTypeSetter();
        _fieldObjectizer = new HashMap<Class<?>, FieldObjectizer>();

        _fieldObjectizer.put(PdxDate.class, new FieldObjectizer() {
            @Override
            public void objectize(Field field, Object data, Dbo dbo) {
                Method setter = _dataTypeSetter.get(field.getType());
                Object instance = null;
                try {
                    instance = field.get(dbo);
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                try {
                    setter.invoke(instance, DateTimeHelper.objectize(
                            DateTimeType.Date, (String) data));
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        });

        _fieldObjectizer.put(PdxDateTime.class, new FieldObjectizer() {
            @Override
            public void objectize(Field field, Object data, Dbo dbo) {
                Method setter = _dataTypeSetter.get(field.getType());
                Object instance = null;
                try {
                    instance = field.get(dbo);
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                try {
                    setter.invoke(instance, DateTimeHelper.objectize(
                            DateTimeType.DateTime, (String) data));
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        });

        FieldObjectizer objectizer = new FieldObjectizer() {
            @Override
            public void objectize(Field field, Object data, Dbo dbo) {
                Method setter = _dataTypeSetter.get(field.getType());
                Object instance = null;
                try {
                    instance = field.get(dbo);
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                try {
                    setter.invoke(instance, data);
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        };

        _fieldObjectizer.put(PdxGUID.class, objectizer);
        _fieldObjectizer.put(PdxByte.class, objectizer);
        _fieldObjectizer.put(PdxInteger.class, objectizer);
        _fieldObjectizer.put(PdxFloat.class, objectizer);
        _fieldObjectizer.put(PdxCharacter.class, objectizer);
        _fieldObjectizer.put(PdxClob.class, objectizer);
        _fieldObjectizer.put(PdxBinary.class, objectizer);
        _fieldObjectizer.put(PdxBlob.class, objectizer);
    }

    private static void InitFieldReadablizer() {
        _fieldReadablizer = new HashMap<Class<?>, FieldReadablizer>();

        _fieldReadablizer.put(PdxByte.class, new FieldReadablizer() {
            @Override
            public Object readablize(String field, ResultSet result) {
                try {
                    return result.getInt(field);
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                return 0;
            }
        });

        FieldReadablizer longReadablizer = new FieldReadablizer() {
            @Override
            public Object readablize(String field, ResultSet result) {
                try {
                    return result.getLong(field);
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                return 0;
            }
        };

        _fieldReadablizer.put(PdxInteger.class, longReadablizer);

        FieldReadablizer doubleReadablizer = new FieldReadablizer() {
            @Override
            public Object readablize(String field, ResultSet result) {
                try {
                    return result.getDouble(field);
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                return 0;
            }
        };

        _fieldReadablizer.put(PdxFloat.class, doubleReadablizer);

        _fieldReadablizer.put(PdxDate.class, new FieldReadablizer() {
            @Override
            public Object readablize(String field, ResultSet result) {
                try {
                    return DateTimeHelper.readablize(DateTimeType.Date,
                            new W3cDate(Long.toString(result.getLong(field))));
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                return "";
            }
        });

        _fieldReadablizer.put(PdxDateTime.class, new FieldReadablizer() {
            @Override
            public Object readablize(String field, ResultSet result) {
                try {
                    return DateTimeHelper.readablize(DateTimeType.DateTime,
                            new W3cDate(result.getString(field)));
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                return "";
            }
        });

        _fieldReadablizer.put(PdxGUID.class, new FieldReadablizer() {
            @Override
            public Object readablize(String field, ResultSet result) {
                try {
                    return result.getString(field);
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                return "";
            }
        });

        _fieldReadablizer.put(PdxCharacter.class, new FieldReadablizer() {
            @Override
            public Object readablize(String field, ResultSet result) {
                try {
                    if (Pedx.getDatabaseName().equals("db2")) {
                        return result.getString(field);
                    } else {
                        return result.getNString(field);
                    }
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                return "";
            }
        });

        FieldReadablizer clobReadablizer = new FieldReadablizer() {
            @Override
            public Object readablize(String field, ResultSet result) {
                try {
                    String str;
                    if (Pedx.getDatabaseName().equals("db2")) {
                        Clob clob = result.getClob(field);
                        str = clob.getSubString(1, (int) clob.length());
                        clob.free();
                    } else {
                        NClob nclob = result.getNClob(field);
                        str = nclob.getSubString(1, (int) nclob.length());
                        nclob.free();
                    }
                    return str;
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                return "";
            }
        };
        _fieldReadablizer.put(PdxClob.class, clobReadablizer);

        FieldReadablizer binReadablizer = new FieldReadablizer() {
            @Override
            public Object readablize(String field, ResultSet result) {
                try {
                    return result.getBytes(field);
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                return null;
            }
        };
        _fieldReadablizer.put(PdxBinary.class, binReadablizer);

        FieldReadablizer blobReadablizer = new FieldReadablizer() {
            @Override
            public Object readablize(String field, ResultSet result) {
                try {
                    Blob blob = result.getBlob(field);
                    byte[] bytes = blob.getBytes(1, (int) blob.length());
                    blob.free();
                    return bytes;
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                return null;
            }
        };
        _fieldReadablizer.put(PdxBlob.class, blobReadablizer);
    }

    private static void InitFieldSetter() {
        ObtainDataTypeSetter();
        _fieldSetter = new HashMap<Class<?>, FieldSetter>();
        _fieldSetter.put(PdxByte.class, new FieldSetter() {

            @Override
            public void set(Field field, ResultSet result, Object dbo) {
                Method setter = _dataTypeSetter.get(field.getType());
                Object instance = null;
                try {
                    instance = field.get(dbo);
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                try {
                    setter.invoke(instance, result.getInt(FieldHelper
                            .translateFieldName(field)));
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        });

        FieldSetter lFieldSetter = new FieldSetter() {
            @Override
            public void set(Field field, ResultSet result, Object dbo) {
                Method setter = _dataTypeSetter.get(field.getType());
                Object instance = null;
                try {
                    instance = field.get(dbo);
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                try {
                    setter.invoke(instance, result.getLong(FieldHelper
                            .translateFieldName(field)));
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        };
        _fieldSetter.put(PdxInteger.class, lFieldSetter);

        FieldSetter dFieldSetter = new FieldSetter() {
            @Override
            public void set(Field field, ResultSet result, Object dbo) {
                Method setter = _dataTypeSetter.get(field.getType());
                Object instance = null;
                try {
                    instance = field.get(dbo);
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                try {
                    setter.invoke(instance, result.getDouble(FieldHelper
                            .translateFieldName(field)));
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        };
        _fieldSetter.put(PdxFloat.class, dFieldSetter);

        _fieldSetter.put(PdxDate.class, new FieldSetter() {
            @Override
            public void set(Field field, ResultSet result, Object dbo) {
                Method setter = _dataTypeSetter.get(field.getType());
                Object instance = null;
                try {
                    instance = field.get(dbo);
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                try {
                    setter.invoke(instance, DateTimeHelper.objectize(
                            DateTimeType.Date, Long.toString(result
                                    .getLong(FieldHelper
                                            .translateFieldName(field)))));
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        });

        _fieldSetter.put(PdxDateTime.class, new FieldSetter() {
            @Override
            public void set(Field field, ResultSet result, Object dbo) {
                Method setter = _dataTypeSetter.get(field.getType());
                Object instance = null;
                try {
                    instance = field.get(dbo);
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                try {
                    setter.invoke(instance, DateTimeHelper.objectize(
                            DateTimeType.DateTime, result.getString(FieldHelper
                                    .translateFieldName(field))));
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        });

        _fieldSetter.put(PdxCharacter.class, new FieldSetter() {
            @Override
            public void set(Field field, ResultSet result, Object dbo) {
                Method setter = _dataTypeSetter.get(field.getType());
                Object instance = null;
                try {
                    instance = field.get(dbo);
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                try {
                    if (Pedx.getDatabaseName().equals("db2")) {
                        setter.invoke(instance, result.getString(FieldHelper
                                .translateFieldName(field)));
                    } else {
                        setter.invoke(instance, result.getNString(FieldHelper
                                .translateFieldName(field)));
                    }
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        });

        FieldSetter characterLargeBlock = new FieldSetter() {
            @Override
            public void set(Field field, ResultSet result, Object dbo) {
                Method setter = _dataTypeSetter.get(field.getType());
                Object instance = null;
                try {
                    instance = field.get(dbo);
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                try {
                    if (Pedx.getDatabaseName().equals("db2")) {
                        Clob clob = result.getClob(FieldHelper
                                .translateFieldName(field));
                        setter.invoke(instance,
                                clob.getSubString(1, (int) clob.length()));
                        clob.free();
                    } else {
                        NClob nclob = result.getNClob(FieldHelper
                                .translateFieldName(field));
                        setter.invoke(instance,
                                nclob.getSubString(1, (int) nclob.length()));
                        nclob.free();
                    }
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        };
        _fieldSetter.put(PdxClob.class, characterLargeBlock);

        _fieldSetter.put(PdxGUID.class, new FieldSetter() {
            @Override
            public void set(Field field, ResultSet result, Object dbo) {
                Method setter = _dataTypeSetter.get(field.getType());
                Object instance = null;
                try {
                    instance = field.get(dbo);
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                try {
                    setter.invoke(instance, result.getString(FieldHelper
                            .translateFieldName(field)));
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        });

        FieldSetter binaryBlock = new FieldSetter() {
            @Override
            public void set(Field field, ResultSet result, Object dbo) {
                Method setter = _dataTypeSetter.get(field.getType());
                Object instance = null;
                try {
                    instance = field.get(dbo);
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                try {
                    setter.invoke(instance, result.getBytes(FieldHelper
                            .translateFieldName(field)));
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        };
        _fieldSetter.put(PdxBinary.class, binaryBlock);

        FieldSetter binaryLargeBlock = new FieldSetter() {
            @Override
            public void set(Field field, ResultSet result, Object dbo) {
                Method setter = _dataTypeSetter.get(field.getType());
                Object instance = null;
                try {
                    instance = field.get(dbo);
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
                try {
                    Blob blob = result.getBlob(FieldHelper
                            .translateFieldName(field));
                    setter.invoke(instance,
                            blob.getBytes(1, (int) blob.length()));
                    blob.free();
                } catch (IllegalAccessException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (IllegalArgumentException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (InvocationTargetException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                } catch (SQLException e) {
                    Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        };
        _fieldSetter.put(PdxBlob.class, binaryLargeBlock);
    }

    public static ArrayList<Class<?>> ObtainCompatibleDataTypes() {
        if (null == _compatibleDataTypes) {
            _compatibleDataTypes = new ArrayList<Class<?>>();
            _compatibleDataTypes.add(Byte.class);
            _compatibleDataTypes.add(String.class);
            _compatibleDataTypes.add(Short.class);
            _compatibleDataTypes.add(Integer.class);
            _compatibleDataTypes.add(Long.class);
            _compatibleDataTypes.add(Float.class);
            _compatibleDataTypes.add(Double.class);
            _compatibleDataTypes.add(byte[].class);
            _compatibleDataTypes.add(PdxBinary.class);
            // _compatibleDataTypes.add(PdxBlob.class);
            _compatibleDataTypes.add(PdxCharacter.class);
            // _compatibleDataTypes.add(PdxClob.class);
            _compatibleDataTypes.add(W3cDate.class);
            _compatibleDataTypes.add(PdxDate.class);
            _compatibleDataTypes.add(PdxDateTime.class);
        }

        return _compatibleDataTypes;
    }

    private static Map<Class<?>, Method> ObtainDataTypeSetter() {
        if (null == _dataTypeSetter) {
            InitDataTypeSetter();
        }

        return _dataTypeSetter;
    }

    public static Map<Class<?>, FieldObjectizer> ObtainFieldObjectizer() {
        if (_fieldObjectizer == null) {
            InitFieldObjectizer();
        }
        return _fieldObjectizer;
    }

    public static Map<Class<?>, FieldReadablizer> ObtainFieldReadablizer() {
        if (_fieldReadablizer == null) {
            InitFieldReadablizer();
        }
        return _fieldReadablizer;
    }

    public static Map<Class<?>, FieldSetter> ObtainFieldSetter() {
        if (_fieldSetter == null) {
            InitFieldSetter();
        }
        return _fieldSetter;
    }
}

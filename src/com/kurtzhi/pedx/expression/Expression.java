package com.kurtzhi.pedx.expression;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.kurtzhi.pedx.Pedx;
import com.kurtzhi.pedx.datatype.PdxBinary;
import com.kurtzhi.pedx.datatype.PdxBlob;
import com.kurtzhi.pedx.datatype.PdxCharacter;
import com.kurtzhi.pedx.datatype.PdxClob;
import com.kurtzhi.pedx.datatype.PdxDate;
import com.kurtzhi.pedx.datatype.PdxDateTime;
import com.kurtzhi.pedx.misc.DataTypeComparable;
import com.kurtzhi.pedx.misc.DboHelper;
import com.kurtzhi.pedx.misc.W3cDate;
import com.kurtzhi.pedx.sql.FieldHelper;
import com.kurtzhi.pedx.sql.QuotationType;
import com.kurtzhi.pedx.sql.TableHelper;

public class Expression {
    ArrayList<Class<?>> _compatibleDataTypes = DboHelper
            .ObtainCompatibleDataTypes();
    TableHelper _tableHelper = Pedx.getTableHelper();
    protected String _val = "";
    protected ArrayList<Class<?>> _regDesignatedTypes = new ArrayList<Class<?>>();
    protected ArrayList<Object> _regDesignatedValues = new ArrayList<Object>();
    protected boolean _error = false;

    public Expression and(Expression expr) {
        if (expr == null) {
            return this;
        }
        if (!this._val.isEmpty()) {
            this._val += " AND";
        }
        this._val += " " + expr.toString();
        this._regDesignatedTypes.addAll(expr._regDesignatedTypes);
        this._regDesignatedValues.addAll(expr._regDesignatedValues);
        _error = _error || expr._error;
        return this;
    }

    public Expression or(Expression expr) {
        if (expr == null) {
            return this;
        }
        if (!this._val.isEmpty()) {
            this._val += " OR";
        }
        this._val += " " + expr.toString();
        this._regDesignatedTypes.addAll(expr._regDesignatedTypes);
        this._regDesignatedValues.addAll(expr._regDesignatedValues);
        _error = _error || expr._error;
        return this;
    }

    @Override
    public String toString() {
        return this._val;
    }

    private void assignWrongValue(Class<?> fieldType, Class<?> valueType) {
        _error = true;
        Pedx.logger.error("Can not assign " + valueType.getSimpleName()
                + " value to data of type " + fieldType.getName());
    }

    protected void parseExpression(String operator, Class<?> dboClass,
            String fieldName, Object value) {
        if (value == null) {
            _error = true;
            Pedx.logger.error("Expression value should not be null");
            return;
        }

        Field field = null;
        try {
            field = dboClass.getField(fieldName);
        } catch (NoSuchFieldException e) {
            _error = true;
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            return;
        }
        Class<?> type = field.getType();
        if (type == PdxClob.class || type == PdxBlob.class) {
            _error = true;
            Pedx.logger
                    .error("Could not compare column with blob, clob datatype.");
            return;
        }

        String str = this._tableHelper.translateTableName(dboClass) + ".";
        str += FieldHelper.translateFieldName(field) + " " + operator + " ";

        if (type.getInterfaces()[0] == DataTypeComparable.class) {
            QuotationType fieldQuotationType = null;
            try {
                fieldQuotationType = (QuotationType) type.getMethod(
                        "getQuotationType").invoke(type.newInstance());
            } catch (IllegalArgumentException e) {
                _error = true;
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                return;
            } catch (InvocationTargetException e) {
                _error = true;
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                return;
            } catch (NoSuchMethodException e) {
                _error = true;
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                return;
            } catch (InstantiationException e) {
                _error = true;
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                return;
            } catch (IllegalAccessException e) {
                _error = true;
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                return;
            }
            try {
                Class<?> valueClass = value.getClass();
                if (valueClass == PdxClob.class || valueClass == PdxBlob.class) {
                    _error = true;
                    Pedx.logger
                            .error("Could not assign blob, clob value to a field.");
                    return;
                }
                String db = Pedx.getDatabaseName();
                if (_compatibleDataTypes.contains(valueClass)) {
                    if (fieldQuotationType == QuotationType.Quotable) {
                        Object val = "";
                        if (valueClass == String.class) {
                            val = value;
                        } else if (valueClass == PdxCharacter.class) {
                            val = ((PdxCharacter) value).get();
                        } else if (valueClass == PdxClob.class) {
                            val = ((PdxClob) value).get();
                        }
                        if (type == PdxCharacter.class) {
                            if (db.equals("oracle")) {
                                str += "'" + val + "'";
                            } else {
                                str += "?";
                                this._regDesignatedTypes.add(type);
                                this._regDesignatedValues.add(val);
                            }
                        } else {
                            str += "'" + val + "'";
                        }
                    } else {
                        if (type == PdxBinary.class) {
                            str += "?";
                            _regDesignatedTypes.add(PdxBinary.class);
                            if (valueClass == byte[].class) {
                                _regDesignatedValues.add(value);
                            } else if (valueClass == PdxBinary.class) {
                                _regDesignatedValues.add(((PdxBinary) value)
                                        .get());
                            } else {
                                assignWrongValue(type, valueClass);
                                return;
                            }
                        } else if ((type == PdxDate.class || type == PdxDateTime.class)) {
                            if (valueClass == PdxDate.class
                                    || valueClass == PdxDateTime.class) {
                                Method m = valueClass.getDeclaredMethod("get");
                                m.setAccessible(true);
                                W3cDate wd = (W3cDate) m.invoke(value);
                                if (wd == null) {
                                    _error = true;
                                    Pedx.logger
                                            .error("Could not assign null date and datetime value to a date or datetime field.");
                                    return;
                                }
                                str += "?";
                                if (type == PdxDate.class) {
                                    m = W3cDate.class
                                            .getDeclaredMethod("getSerializedPedxUTCDate");
                                    m.setAccessible(true);
                                    _regDesignatedTypes.add(PdxDate.class);
                                } else {
                                    m = W3cDate.class
                                            .getDeclaredMethod("getSerializedPedxUTCDateTime");
                                    m.setAccessible(true);
                                    _regDesignatedTypes.add(PdxDateTime.class);
                                }
                                _regDesignatedValues.add(m.invoke(wd));
                                // str += m.invoke(wd); // Direct assign
                            } else if (valueClass == W3cDate.class) {
                                if (type == PdxDate.class) {
                                    Method m = valueClass
                                            .getDeclaredMethod("getSerializedPedxUTCDate");
                                    m.setAccessible(true);
                                    str += "?";
                                    _regDesignatedTypes.add(PdxDate.class);
                                    _regDesignatedValues.add(m.invoke(value));
                                    // str += m.invoke(value); // Direct assign
                                } else if (type == PdxDateTime.class) {
                                    Method m = valueClass
                                            .getDeclaredMethod("getSerializedPedxUTCDateTime");
                                    m.setAccessible(true);
                                    str += "?";
                                    _regDesignatedTypes.add(PdxDateTime.class);
                                    _regDesignatedValues.add(m.invoke(value));
                                    // str += m.invoke(value); // Direct assign
                                } else {
                                    assignWrongValue(type, valueClass);
                                    return;
                                }
                            } else {
                                assignWrongValue(type, valueClass);
                                return;
                            }
                        } else {
                            str += value;
                        }
                    }
                } else if (valueClass.getInterfaces().length > 0
                        && valueClass.getInterfaces()[0] == DataTypeComparable.class) {
                    QuotationType valueQuotationType = (QuotationType) valueClass
                            .getMethod("getQuotationType").invoke(
                                    valueClass.newInstance());
                    if (fieldQuotationType != valueQuotationType) {
                        assignWrongValue(type, valueClass);
                        return;
                    }

                    Object realVal;
                    if ((realVal = valueClass.getMethod("get").invoke(value)) == null) {
                        _error = true;
                        Pedx.logger
                                .error("Expression value should not be null");
                        return;
                    }

                    if (fieldQuotationType == QuotationType.Quotable) {
                        str += "'" + realVal + "'";
                    } else if (fieldQuotationType == QuotationType.DirectAccess) {
                        str += realVal;
                    }
                } else {
                    _error = true;
                    Pedx.logger.error("Wrong expression value type");
                    return;
                }
                this._val = str;
            } catch (IllegalArgumentException e) {
                _error = true;
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                return;
            } catch (IllegalAccessException e) {
                _error = true;
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                return;
            } catch (InvocationTargetException e) {
                _error = true;
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                return;
            } catch (InstantiationException e) {
                _error = true;
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                return;
            } catch (NoSuchMethodException e) {
                _error = true;
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
                return;
            }
        }
    }
}

package com.kurtzhi.pedx;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.kurtzhi.pedx.misc.DataTypeComparable;
import com.kurtzhi.pedx.misc.DboHelper;
import com.kurtzhi.pedx.sql.FieldHelper;
import com.kurtzhi.pedx.sql.TableHelper;

public class Order {
    ArrayList<Class<?>> _compatibleDataTypes = DboHelper
            .ObtainCompatibleDataTypes();
    TableHelper _tableHelper = Pedx.getTableHelper();
    String _val = null;

    public Order(Class<?> dboClass, String field, OrderType sortType) {
        Field f = null;
        try {
            f = dboClass.getField(field);
        } catch (NoSuchFieldException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        } catch (SecurityException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }

        Class<?> type = f.getType();
        Proto ef = null;
        if (DataTypeComparable.class.isAssignableFrom(type)) {
            if ((ef = f.getAnnotation(Proto.class)) != null) {
                FieldConstraint[] fts = ef.value();
                for (FieldConstraint ft : fts) {
                    if (ft == FieldConstraint.NONE) {
                        Pedx.logger
                                .error("Could not sort because no indexed field");
                    } else {
                        this._val = this._tableHelper
                                .translateTableName(dboClass)
                                + "."
                                + FieldHelper.translateFieldName(f);
                        this._val += (sortType == OrderType.Asc) ? " ASC"
                                : " DESC";
                        break;
                    }
                }
            }
        }
    }

    public Order and(Order sorter) {
        if (sorter == null) {
            return this;
        }
        this._val += ", " + sorter.toString();
        return this;
    }

    @Override
    public String toString() {
        return this._val;
    }

}

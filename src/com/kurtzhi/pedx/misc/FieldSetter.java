package com.kurtzhi.pedx.misc;

import java.lang.reflect.Field;
import java.sql.ResultSet;

public interface FieldSetter {
    void set(Field field, ResultSet result, Object dbo);
}

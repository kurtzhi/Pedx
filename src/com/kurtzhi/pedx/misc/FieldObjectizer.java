package com.kurtzhi.pedx.misc;

import java.lang.reflect.Field;

import com.kurtzhi.pedx.Dbo;

public interface FieldObjectizer {
    void objectize(Field field, Object data, Dbo dbo);
}

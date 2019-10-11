package com.kurtzhi.pedx.misc;

import java.sql.ResultSet;

public interface FieldReadablizer {
    Object readablize(String field, ResultSet result);
}

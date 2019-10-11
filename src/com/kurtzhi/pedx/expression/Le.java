package com.kurtzhi.pedx.expression;

public class Le extends Expression {

    public Le(Class<?> dboClass, String field, Object value) {
        parseExpression("<=", dboClass, field, value);
    }
}
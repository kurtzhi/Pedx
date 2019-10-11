package com.kurtzhi.pedx.expression;

public class Eq extends Expression {

    public Eq(Class<?> dboClass, String field, Object value) {
        parseExpression("=", dboClass, field, value);
    }
}
package com.kurtzhi.pedx.expression;

public class Gt extends Expression {

    public Gt(Class<?> dboClass, String field, Object value) {
        parseExpression(">", dboClass, field, value);
    }
}
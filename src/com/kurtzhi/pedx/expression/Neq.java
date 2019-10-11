package com.kurtzhi.pedx.expression;

public class Neq extends Expression {

    public Neq(Class<?> dboClass, String field, Object value) {
        parseExpression("<>", dboClass, field, value);
    }
}
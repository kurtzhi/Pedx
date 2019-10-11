package com.kurtzhi.pedx.expression;

public class Lt extends Expression {

    public Lt(Class<?> dboClass, String field, Object value) {
        parseExpression("<", dboClass, field, value);
    }
}
package com.kurtzhi.pedx.expression;

public class Ge extends Expression {

    public Ge(Class<?> dboClass, String field, Object value) {
        parseExpression(">=", dboClass, field, value);
    }
}
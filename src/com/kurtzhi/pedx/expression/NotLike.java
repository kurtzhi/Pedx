package com.kurtzhi.pedx.expression;

public class NotLike extends Expression {

    public NotLike(Class<?> dboClass, String field, String value) {
        parseExpression("LIKE", dboClass, field, value);
    }
}
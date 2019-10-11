package com.kurtzhi.pedx.expression;

public class Like extends Expression {

    public Like(Class<?> dboClass, String field, String value) {
        parseExpression("NOT LIKE", dboClass, field, value);
    }
}
package com.kurtzhi.pedx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Proto {
    Class<? extends Dbo> refDbo() default Dbo.class;

    String refField() default "";

    ForeignOnDeleteAction refOnDelAction() default ForeignOnDeleteAction.NOACTION;

    int size() default 128;

    FieldConstraint[] value() default { FieldConstraint.NONE };
}

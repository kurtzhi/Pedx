package com.kurtzhi.pedx;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import com.kurtzhi.pedx.expression.Expression;

public class Condition {
    private String _val = "";
    protected ArrayList<Class<?>> _regDesignatedTypes = new ArrayList<Class<?>>();
    protected ArrayList<Object> _regDesignatedValues = new ArrayList<Object>();
    boolean _error = false;

    public Condition(Condition cond) {
        if (cond != null) {
            this._val = "(" + cond.toString() + ")";
            this._regDesignatedTypes.addAll(cond._regDesignatedTypes);
            this._regDesignatedValues.addAll(cond._regDesignatedValues);
            _error = _error || cond._error;
        }
    }

    @SuppressWarnings("unchecked")
    public Condition(Expression expr) {
        if (expr != null) {
            this._val = "(" + expr.toString() + ")";
            Field f;
            try {
                f = expr.getClass().getSuperclass()
                        .getDeclaredField("_regDesignatedTypes");
                f.setAccessible(true);
                this._regDesignatedTypes
                        .addAll((Collection<? extends Class<?>>) f.get(expr));
                f = expr.getClass().getSuperclass()
                        .getDeclaredField("_regDesignatedValues");
                f.setAccessible(true);
                this._regDesignatedValues
                        .addAll((Collection<? extends Object>) f.get(expr));
                f = expr.getClass().getSuperclass().getDeclaredField("_error");
                f.setAccessible(true);
                _error = _error || (Boolean) f.get(expr);
            } catch (NoSuchFieldException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            } catch (SecurityException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            } catch (IllegalArgumentException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            } catch (IllegalAccessException e) {
                Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
            }
        }
    }

    public Condition and(Condition cond) {
        if (cond == null) {
            return this;
        }
        if (!this._val.isEmpty()) {
            this._val += " AND";
        }
        this._val += " (" + cond.toString() + ")";
        this._regDesignatedTypes.addAll(cond._regDesignatedTypes);
        this._regDesignatedValues.addAll(cond._regDesignatedValues);
        _error = _error || cond._error;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Condition and(Expression expr) {
        if (expr == null) {
            return this;
        }
        if (!this._val.isEmpty()) {
            this._val += " AND";
        }
        this._val += " (" + expr.toString() + ")";
        Field f;
        try {
            f = expr.getClass().getSuperclass()
                    .getDeclaredField("_regDesignatedTypes");
            f.setAccessible(true);
            this._regDesignatedTypes.addAll((Collection<? extends Class<?>>) f
                    .get(expr));
            f = expr.getClass().getSuperclass()
                    .getDeclaredField("_regDesignatedValues");
            f.setAccessible(true);
            this._regDesignatedValues.addAll((Collection<? extends Object>) f
                    .get(expr));
            f = expr.getClass().getSuperclass().getDeclaredField("_error");
            f.setAccessible(true);
            _error = _error || (Boolean) f.get(expr);
        } catch (NoSuchFieldException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        } catch (SecurityException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        } catch (IllegalArgumentException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        } catch (IllegalAccessException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }
        return this;
    }

    public Condition or(Condition cond) {
        if (cond == null) {
            return this;
        }
        if (!this._val.isEmpty()) {
            this._val += " OR";
        }
        this._val += " (" + cond.toString() + ")";
        this._regDesignatedTypes.addAll(cond._regDesignatedTypes);
        this._regDesignatedValues.addAll(cond._regDesignatedValues);
        _error = _error || cond._error;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Condition or(Expression expr) {
        if (expr == null) {
            return this;
        }
        if (!this._val.isEmpty()) {
            this._val += " OR";
        }
        this._val += " (" + expr.toString() + ")";
        Field f;
        try {
            f = expr.getClass().getSuperclass()
                    .getDeclaredField("_regDesignatedTypes");
            f.setAccessible(true);
            this._regDesignatedTypes.addAll((Collection<? extends Class<?>>) f
                    .get(expr));
            f = expr.getClass().getSuperclass()
                    .getDeclaredField("_regDesignatedValues");
            f.setAccessible(true);
            this._regDesignatedValues.addAll((Collection<? extends Object>) f
                    .get(expr));
            f = expr.getClass().getSuperclass().getDeclaredField("_error");
            f.setAccessible(true);
            _error = _error || (Boolean) f.get(expr);
        } catch (NoSuchFieldException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        } catch (SecurityException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        } catch (IllegalArgumentException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        } catch (IllegalAccessException e) {
            Pedx.logger.error(e.getMessage(), e.fillInStackTrace());
        }
        return this;
    }

    public boolean hasDesignatedTypes() {
        return this._regDesignatedTypes.size() > 0;
    }

    @Override
    public String toString() {
        return this._val;
    }
}

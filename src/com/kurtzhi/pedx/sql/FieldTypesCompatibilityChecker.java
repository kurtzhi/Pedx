package com.kurtzhi.pedx.sql;

import java.util.ArrayList;

import com.kurtzhi.pedx.FieldConstraint;
import com.kurtzhi.pedx.Pedx;

class FieldTypesCompatibilityChecker {
    static public boolean CheckAcrossTable(
            ArrayList<FieldConstraint[]> tableFieldTypes) {
        int len = tableFieldTypes.size();
        if (len > 1) {
            for (int i = 0; i < len; i++) {
                for (FieldConstraint fieldType1 : tableFieldTypes.get(i)) {
                    for (int j = i + 1; j < len; j++) {
                        for (FieldConstraint fieldType2 : tableFieldTypes
                                .get(j)) {
                            if (!CheckAcrossTable(fieldType1, fieldType2)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    static public boolean CheckAcrossTable(FieldConstraint typeOfField1,
            FieldConstraint typeOfField2) {
        switch (typeOfField1) {
        case SIMPLEKEY:
            switch (typeOfField2) {
            case SIMPLEKEY:
            case COMPOSITEKEY:
                Pedx.logger.error("Ambiguous key of dbo detected");
                return false;
                
            default:
                return true;
            }

        case COMPOSITEKEY:
            switch (typeOfField2) {
            case SIMPLEKEY:
                Pedx.logger.error("Ambiguous key of dbo detected");
                return false;
                
            default:
                return true;
            }
            
        default:
            return true;
        }
    }

    static public boolean CheckFieldsMapBetweenTables(FieldConstraint[] types,
            FieldConstraint[] anotherTypes) {
        boolean isCompatible = false;

        for (FieldConstraint type : types) {
            for (FieldConstraint anotherType : anotherTypes) {
                switch (type) {
                case SIMPLEKEY:
                    switch (anotherType) {
                    case FOREIGN:
                        isCompatible = true;
                        
                    default:
                        break;
                    }
                    break;

                case COMPOSITEKEY:
                    switch (anotherType) {
                    case FOREIGN:
                        isCompatible = true;
                        
                    default:
                        break;
                    }
                    break;

                case FOREIGN:
                    switch (anotherType) {
                    case FOREIGN:
                    case SIMPLEKEY:
                    case COMPOSITEKEY:
                        isCompatible = true;
                        
                    default:
                        break;
                    }
                    
                default:
                    break;
                }
                break;
            }
        }

        return isCompatible;
    }

    static public boolean CheckPerField(FieldConstraint type1,
            FieldConstraint type2) {
        switch (type1) {
        case COMPOSITEKEY:
            switch (type2) {
            case FOREIGN:
                return true;
                
            default:
                break;
            }

        case FOREIGN:
            switch (type2) {
            case COMPOSITEKEY:
            case UNIQUE:
                return true;
                
            default:
                break;
            }

        case UNIQUE:
            switch (type2) {
            case FOREIGN:
                return true;
                
            default:
                break;
            }
            
        default:
            return false;
        }
    }

    static public boolean CheckPerField(FieldConstraint[] types) {
        int len = types.length;
        if (len > 1) {
            for (int i = 0; i < len; i++) {
                for (int j = i + 1; j < len; j++) {
                    if (!CheckPerField(types[i], types[j])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}

package com.kurtzhi.pedx.misc;

public class DateTimeHelper {
    public static W3cDate objectize(DateTimeType type, String dateStr) {
        if (dateStr.matches("[12]{1}[\\d]{13,19}")) {
            return new W3cDate(dateStr);
        }
        return new W3cDate(dateStr, "+0000");
    }

    public static String readablize(DateTimeType type, W3cDate val) {
        switch (type) {
        case Date:
            return val.getUTCDate();

        case DateTime:
            return val.getUTCDateTime(" ");
        }

        return null;
    }

    public static String storablize(DateTimeType type, W3cDate val) {
        switch (type) {
        case Date:
            return val.getSerializedPedxUTCDate();

        case DateTime:
            return val.getSerializedPedxUTCDateTime();
        }

        return null;
    }
}

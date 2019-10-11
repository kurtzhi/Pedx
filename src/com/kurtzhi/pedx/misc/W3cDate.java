package com.kurtzhi.pedx.misc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import com.kurtzhi.pedx.Pedx;

public class W3cDate {
    Date _date;

    public W3cDate() {
        this._date = new Date();
    }

    public W3cDate(long UnixTimestamp) {
        this._date = new Date(UnixTimestamp * 1000);
    }

    public W3cDate(String dateTimeStr, String timezone) {
        String dtStr = dateTimeStr;
        boolean isBC = dtStr.substring(0, 1).equals("-");
        dtStr = dtStr.replaceFirst(" UTC", "Z");
        dtStr = dtStr.substring(0, 1).replaceFirst("[+-]", "")
                + dtStr.substring(1);
        int dtLength = dtStr.length();
        int dtDelimiterIdx = dtStr.indexOf("T");
        switch (dtDelimiterIdx) {
        case -1:
            dtDelimiterIdx = dtStr.indexOf(" ");
            switch (dtDelimiterIdx) {
            case -1:
                boolean isContinue = false;
                if (dtStr.matches("([\\d]{1,9})(-[\\d]{1,2}){0,2}")) {
                    String d = (isBC ? "-" : "")
                            + dtStr.substring(0, dtStr.indexOf("-"));
                    int v = Integer.parseInt(d);
                    if (v >= -292269054 && v <= 292278994) {
                        dtDelimiterIdx = dtStr.length();
                        isContinue = true;
                    }
                }
                if (!isContinue) {
                    Pedx.logger.error("Date string '" + dateTimeStr
                            + "' can't identified.");
                    return;
                }
                break;
            default:
                dtStr = dtStr.replaceFirst(" ", "T").replaceAll(" ", "");
            }
            break;

        default:
            dtStr = dtStr.replaceAll(" ", "");
            break;
        }
        dtLength = dtStr.length();
        String dateStr = dtStr.substring(0, dtDelimiterIdx);
        dateStr = dateStr.substring(0, 1).replaceFirst("[\\W]", "")
                + dateStr.substring(1);
        String datePtn = "";
        int idx = dateStr.indexOf("-");
        int len = dateStr.length();
        if (idx == -1) {
            for (int i = 0; i < len; i++) {
                datePtn += "y";
            }
        } else {
            for (int i = 0; i < idx; i++) {
                datePtn += "y";
            }
            int idx2;
            if (dateStr.indexOf("W") != -1) { // Week date
                dateStr = dateStr.replaceAll("-", "");
                datePtn += dateStr.split("W")[1].length() == 2 ? "'W'ww"
                        : "'W'wwu";
            } else if ((idx = dateStr.indexOf("-")) == (idx2 = dateStr
                    .lastIndexOf("-"))) {
                len = dateStr.length();
                if ((idx + 1) + 1 == len) {
                    datePtn += "M";
                } else if ((idx + 2) + 1 == len) {
                    datePtn += "MM";
                } else if ((idx + 3) + 1 == len) {
                    datePtn += "DDD";
                }
                dateStr = dateStr.replaceFirst("-", "");
            } else {
                if (dateStr.matches("[\\d]+-[\\d]{1,2}-[\\d]{1,2}")) {
                    String m = dateStr.substring(idx + 1, idx2);
                    String d = dateStr.substring(idx2 + 1);
                    m = m.length() == 1 ? "0" + m : m;
                    d = d.length() == 1 ? "0" + d : d;
                    dateStr = dateStr.substring(0, idx) + m + d;
                    datePtn += "MMdd";
                } else {
                    Pedx.logger.error("Date string '" + dateTimeStr
                            + "' can't identified.");
                    return;
                }
            }
        }
        String timePtn = "";
        String timeStr = "";
        String zoneStr = "";
        String zonePtn = "";
        if (dtDelimiterIdx + 1 < dtLength) {
            timeStr = dtStr.substring(dtDelimiterIdx + 1).split("[Z\\W&&[^:]]")[0];
            zoneStr = dtStr.substring(dtDelimiterIdx + 1).replaceFirst(timeStr,
                    "");
            String[] tm = timeStr.split(":");
            timePtn = "HHmmss";
            switch (tm.length) {
            case 3:
                tm[2] = tm[2].length() == 1 ? "0" + tm[2] : tm[2];
            case 2:
                tm[1] = tm[1].length() == 1 ? "0" + tm[1] : tm[1];
            case 1:
                tm[0] = tm[0].length() == 1 ? "0" + tm[0] : tm[0];
            }
            timePtn = timePtn.substring(0, tm.length * 2);
            timeStr = Arrays.toString(tm).replaceAll("[\\W]", "");
            if (zoneStr.isEmpty() && timezone.length() > 0) {
                zoneStr = parseTimezone(timezone);
            }
            zonePtn = zoneStr.isEmpty() ? "" : "X";
        } else {
            zoneStr = "Z";
            zonePtn = "X";
        }

        dtStr = (isBC ? "-" : "") + dateStr + timeStr + zoneStr;
        String dateTimePtn = (isBC ? "y" : "") + datePtn + timePtn + zonePtn;
        SimpleDateFormat formatter = new SimpleDateFormat(dateTimePtn);
        try {
            this._date = formatter.parse(dtStr);
        } catch (ParseException e) {
            Pedx.logger.error(e.getMessage());
        }
    }

    private String parseTimezone(String timezone) {
        if (timezone.matches("(UTC)|(GMT)|(utc)|(gmt)|Z")) {
            timezone = "Z";
        } else if (timezone.matches("[+-]?([\\d]{1,2}){1}:?[\\d]{0,2}")) {
            String[] s = (timezone.replaceFirst("[+-]", "")).split(":");
            s[0] = (s[0].length() == 1 || s[0].length() == 3) ? "0" + s[0]
                    : s[0];
            if (s.length > 1 && s[1].length() == 1) {
                s[1] = "0" + s[1];
            }
            timezone = (timezone.indexOf("-") == 0 ? "-" : "+")
                    + Arrays.toString(s).replaceAll("\\W", "");
        }

        return timezone;
    }

    public Date getDate() {
        return this._date;
    }

    public String getUTCDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+00:00"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(this._date);
    }

    public String getUTCTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+00:00"));
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(this._date);
    }

    public String getUTCDateTime(String dateTimeDelimiter) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+00:00"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'"
                + dateTimeDelimiter + "'HH:mm:ss");
        return formatter.format(this._date);
    }

    public String getUTCDateWithEra() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+00:00"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd G");
        return formatter.format(this._date);
    }

    public String getUTCDateTimeWithEra(String dateTimeDelimiter) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+00:00"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'"
                + dateTimeDelimiter + "'HH:mm:ss G");
        return formatter.format(this._date);
    }

    public String getDateByTimezone(String timezone) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"
                + parseTimezone(timezone)));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(this._date);
    }

    public String getTimeByTimezone(String timezone) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"
                + parseTimezone(timezone)));
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(this._date);
    }

    public String getDateTimeByTimezone(String dateTimeDelimiter,
            String timezone) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"
                + parseTimezone(timezone)));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'"
                + dateTimeDelimiter + "'HH:mm:ss");
        return formatter.format(this._date);
    }

    public String getDateTimeWithEraByTimezone(String dateTimeDelimiter,
            String timezone) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"
                + parseTimezone(timezone)));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'"
                + dateTimeDelimiter + "'HH:mm:ss G");
        return formatter.format(this._date);
    }

    public long getUnixTimestamp() {
        return this._date.getTime() / 1000;
    }

    W3cDate(String serializedDateTime) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+00:00"));
        SimpleDateFormat formatter;
        serializedDateTime = (serializedDateTime.substring(0, 1).equals("1") ? "BC"
                : "AD")
                + serializedDateTime.substring(1);
        switch (serializedDateTime.length()) {
        case 15:
            formatter = new SimpleDateFormat("GyyyyyyyyyMMdd");
            try {
                this._date = formatter.parse(serializedDateTime);
            } catch (ParseException e) {
                Pedx.logger.error(e.getMessage());
            }
            break;

        case 21:
            formatter = new SimpleDateFormat("GyyyyyyyyyMMddHHmmss");
            try {
                this._date = formatter.parse(serializedDateTime);
            } catch (ParseException e) {
                Pedx.logger.error(e.getMessage());
            }
            break;
        }
    }

    String getSerializedPedxUTCDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+00:00"));
        SimpleDateFormat formatter = new SimpleDateFormat("GyyyyyyyyyMMdd");
        return formatter.format(this._date).replaceFirst("BC", "1")
                .replaceFirst("AD", "2");
    }

    String getSerializedPedxUTCDateTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+00:00"));
        SimpleDateFormat formatter = new SimpleDateFormat(
                "GyyyyyyyyyMMddHHmmss");
        return formatter.format(this._date).replaceFirst("BC", "1")
                .replaceFirst("AD", "2");
    }
}

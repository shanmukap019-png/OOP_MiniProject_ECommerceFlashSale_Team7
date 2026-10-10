package com.flashdeal.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Date/time formatting utilities using modern java.time package.
 */
public final class DateUtil {

    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final DateTimeFormatter TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private DateUtil() {
        // Prevent instantiation
    }

    public static String formatDisplay(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DISPLAY_FORMATTER);
    }

    public static String formatIso(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(ISO_FORMATTER);
    }

    public static LocalDateTime parseIso(String isoString) {
        if (isoString == null || isoString.trim().isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(isoString.trim(), ISO_FORMATTER);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    public static String formatTimeOnly(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(TIME_ONLY_FORMATTER);
    }
}

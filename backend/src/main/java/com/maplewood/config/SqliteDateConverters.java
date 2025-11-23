package com.maplewood.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Consolidated SQLite date/time converters for JPA entities.
 * SQLite stores dates as TEXT, so we need custom converters.
 */
public class SqliteDateConverters {

    @Converter(autoApply = true)
    public static class LocalDateConverter implements AttributeConverter<LocalDate, String> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public String convertToDatabaseColumn(LocalDate localDate) {
            return localDate == null ? null : localDate.format(FORMATTER);
        }

        @Override
        public LocalDate convertToEntityAttribute(String dateString) {
            if (dateString == null || dateString.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(dateString, FORMATTER);
            } catch (Exception e) {
                return null;
            }
        }
    }

    @Converter(autoApply = true)
    public static class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {
        private static final DateTimeFormatter SQLITE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public String convertToDatabaseColumn(LocalDateTime localDateTime) {
            return localDateTime == null ? null : localDateTime.format(SQLITE_FORMATTER);
        }

        @Override
        public LocalDateTime convertToEntityAttribute(String dateTimeString) {
            if (dateTimeString == null || dateTimeString.isEmpty()) {
                return null;
            }
            try {
                if (dateTimeString.contains("T")) {
                    return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
                return LocalDateTime.parse(dateTimeString, SQLITE_FORMATTER);
            } catch (Exception e) {
                return null;
            }
        }
    }
}


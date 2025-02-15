package org.surkov.hranalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Класс для представления образования.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Education {
    /**
     * Список учебных заведений.
     */
    private List<School> schools;

    /**
     * Статический внутренний класс для представления учебного заведения.
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class School {
        /**
         * Название университета.
         */
        private String universityName;

        /**
         * Полученная степень.
         */
        private String degree;

        /**
         * Даты начала и окончания учебы.
         */
        private String startDate;

        /**
         * Даты начала и окончания учебы.
         */
        private String endDate;

        /**
         * Специализация.
         */
        private String specialization;
    }
}

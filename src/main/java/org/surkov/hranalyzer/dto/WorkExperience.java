package org.surkov.hranalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Класс для представления опыта работы.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkExperience {
    /**
     * Список записей о местах работы.
     */
    private List<Job> jobs;

    /**
     * Статический внутренний класс для представления записи о месте работы.
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Job {
        /**
         * Название компании.
         */
        private String companyName;

        /**
         * Должность.
         */
        private String position;

        /**
         * Дата начала работы.
         */
        private String startDate;

        /**
         * Дата окончания работы.
         */
        private String endDate;

        /**
         * Краткое описание обязанностей и достижений.
         */
        private String description;
    }
}
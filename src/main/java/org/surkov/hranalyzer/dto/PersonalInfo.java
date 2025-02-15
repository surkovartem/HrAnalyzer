package org.surkov.hranalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс для представления личных данных.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonalInfo {
    /**
     * Имя человека.
     */
    private String name;

    /**
     * Возраст человека.
     */
    private int age;

    /**
     * Предпочтительный способ связи.
     */
    private String contacts;

    /**
     * Место проживания.
     */
    private String residence;
}
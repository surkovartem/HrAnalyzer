package org.surkov.hranalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс для представления резюме.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Resume {
    /**
     * Личные данные.
     */
    private PersonalInfo personalInfo;

    /**
     * Опыт работы.
     */
    private WorkExperience workExperience;

    /**
     * Образование.
     */
    private Education education;

    /**
     * Навыки.
     */
    private Skills skills;

    /**
     * Дополнительная информация.
     */
    private AdditionalInfo additionalInfo;
}
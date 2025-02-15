package org.surkov.hranalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс для представления дополнительной информации.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalInfo {
    /**
     * Наличие водительских прав.
     */
    private boolean driverLicense;

    /**
     * Наличие собственного автомобиля.
     */
    private boolean ownsCar;

    /**
     * Дополнительная информация.
     */
    private String otherInfo;
}
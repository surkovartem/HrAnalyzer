package org.surkov.hranalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Класс для представления навыков.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Skills {
    /**
     * Список навыков.
     */
    private List<Skill> skills;

    /**
     * Статический внутренний класс для представления навыка.
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Skill {
        /**
         * Тип навыка.
         */
        private String skillType;

        /**
         * Название навыка.
         */
        private String skillName;

        /**
         * Уровень владения навыком.
         */
        private String proficiencyLevel;
    }
}

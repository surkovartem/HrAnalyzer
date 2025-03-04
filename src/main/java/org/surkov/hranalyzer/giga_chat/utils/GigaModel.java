package org.surkov.hranalyzer.giga_chat.utils;

import lombok.experimental.UtilityClass;

/**
 * Модели для генерации.
 */
@UtilityClass
public final class GigaModel {

    /**
     * Легкая модель для простых задач,
     * требующих максимальной скорости работы.
     */
    public static final String GIGA_MODEL_LITE = "GigaChat";

    /**
     * Продвинутая модель для сложных задач,
     * требующих креативности и лучшего следования инструкциям.
     */
    public static final String GIGA_MODEL_PRO = "GigaChat-Pro";

    /**
     * Продвинутая модель для сложных задач,
     * требующих высокого уровня креативности и качества работы.
     */
    public static final String GIGA_MODEL_MAX = "GigaChat-Max";
}

package org.surkov.hranalyzer.giga_chat.utils;

/**
 * Утилитный класс для работы с конфиденциальными данными.
 * Предоставляет методы для маскировки данных, чтобы скрыть их содержимое в логах или других выходных данных.
 */
public class SecurityUtils {

    /**
     * Количество символов, остающихся видимыми в начале и конце строки при маскировке.
     */
    private static final int MASK_VISIBLE_CHARS = 4;

    /**
     * Маскирует конфиденциальные данные в строке, оставляя видимыми только первые и последние символы.
     *
     * @param data Строка с данными для маскировки. Может быть {@code null}.
     * @return Замаскированная строка, например, "abcd****wxyz", или "****", если строка слишком короткая или {@code null}.
     */
    public static String maskSensitiveData(final String data) {
        if (data == null || data.length() <= MASK_VISIBLE_CHARS * 2) {
            return "****";
        }

        String start = data.substring(0, MASK_VISIBLE_CHARS);
        String end = data.substring(data.length() - MASK_VISIBLE_CHARS);
        return start + "****" + end;
    }
}

package org.surkov.hranalyzer.service.extraction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Перечисление, представляющее поддерживаемые типы файлов.
 */
@Getter
@RequiredArgsConstructor
public enum FileType {

    /**
     * Тип файла PDF.
     */
    PDF(".pdf"),
    /**
     * Тип файла DOCX.
     */
    DOCX(".docx"),
    /**
     * Тип файла TXT.
     */
    TXT(".txt"),
    /**
     * Тип файла RTF.
     */
    RTF(".rtf");

    /**
     * Расширение файла, соответствующее данному типу.
     */
    private final String extension;

    /**
     * Получает тип файла по его расширению.
     *
     * @param extension Расширение файла (например, ".pdf").
     * @return Тип файла, соответствующий расширению.
     * @throws IllegalArgumentException Расширение не соответствует ни одному
     *                                  из поддерживаемых типов файлов.
     */
    public static FileType fromExtension(final String extension) {
        return Arrays.stream(FileType.values())
                .filter(type -> type.getExtension().equalsIgnoreCase(extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported file type: " + extension
                ));
    }
}

package org.surkov.hranalyzer.service.extraction;

import lombok.Getter;

import java.util.Arrays;

/**
 * Перечисление, представляющее поддерживаемые типы файлов.
 */
@Getter
public enum FileType {

    PDF(".pdf"),
    DOCX(".docx"),
    TXT(".txt"),
    RTF(".rtf");

    private final String extension;

    FileType(String extension) {
        this.extension = extension;
    }

    /**
     * Получает тип файла по его расширению.
     *
     * @param extension Расширение файла (например, ".pdf").
     * @return Тип файла, соответствующий расширению.
     * @throws IllegalArgumentException Если расширение не соответствует ни одному из поддерживаемых типов файлов.
     */
    public static FileType fromExtension(String extension) {
        return Arrays.stream(FileType.values())
                .filter(type -> type.getExtension().equalsIgnoreCase(extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported file type: " + extension));
    }

    /**
     * Получает тип файла по имени файла.
     *
     * @param fileName Имя файла.
     * @return Тип файла.
     * @throws IllegalArgumentException Если тип файла не поддерживается.
     */
    public static FileType fromFileName(String fileName) {
        String ext = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            ext = fileName.substring(i);
        }
        return fromExtension(ext);
    }
}
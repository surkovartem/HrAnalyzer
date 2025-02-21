package org.surkov.hranalyzer.service.extraction;

import lombok.Getter;

import java.util.Arrays;

/**
 * Перечисление для типов файлов, поддерживаемых приложением.
 */
@Getter
public enum FileType {
    PDF(".pdf"),
    DOCX(".docx"),
    TXT(".txt"),
    RTF(".rtf");

    private final String extension;

    /**
     * Конструктор для инициализации расширения файла.
     *
     * @param extension расширение файла
     */
    FileType(String extension) {
        this.extension = extension;
    }

    /**
     * Возвращает тип файла по расширению.
     *
     * @param extension расширение файла
     * @return тип файла
     * @throws IllegalArgumentException если тип файла не поддерживается
     */
    public static FileType fromExtension(String extension) {
        return Arrays.stream(FileType.values())
                .filter(type -> type.getExtension().equalsIgnoreCase(extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported file type: " + extension));
    }

    /**
     * Возвращает тип файла по имени файла.
     *
     * @param fileName имя файла
     * @return тип файла
     * @throws IllegalArgumentException если тип файла не поддерживается
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
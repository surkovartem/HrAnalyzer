/**
 * Пакет, предоставляющий компоненты для взаимодействия с GigaChat API.
 *
 * <p>Этот пакет содержит классы для аутентификации, отправки запросов, обработки ответов и управления токенами доступа.
 * Он также включает в себя исключения, DTO, перечисления и утилиты, специфичные для GigaChat API.</p>
 *
 * <p>Основные компоненты пакета:</p>
 * <ul>
 *     <li>{@link org.surkov.hranalyzer.giga_chat.GigaChatDialog} - главный компонент для взаимодействия с GigaChat.</li>
 *     <li>{@link org.surkov.hranalyzer.giga_chat.GigaChatApiClient} - клиент для отправки запросов к API.</li>
 *     <li>{@link org.surkov.hranalyzer.giga_chat.TokenManager} - менеджер для получения и обновления токенов доступа.</li>
 *     <li>{@link org.surkov.hranalyzer.giga_chat.HttpClientWrapper} - обертка над HTTP-клиентом.</li>
 * </ul>
 *
 * <p>Подпакеты:</p>
 * <ul>
 *     <li>{@link org.surkov.hranalyzer.giga_chat.config} - конфигурационные классы.</li>
 *     <li>{@link org.surkov.hranalyzer.giga_chat.dto} - Data Transfer Objects (DTO).</li>
 *     <li>{@link org.surkov.hranalyzer.giga_chat.enumiration} - перечисления.</li>
 *     <li>{@link org.surkov.hranalyzer.giga_chat.exception} - исключения.</li>
 *     <li>{@link org.surkov.hranalyzer.giga_chat.utils} - утилитарные классы.</li>
 * </ul>
 *
 * @since 1.0
 */
package org.surkov.hranalyzer.giga_chat;

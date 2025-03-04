package org.surkov.hranalyzer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.EnableRetry;

import java.time.LocalDateTime;

@Slf4j
@EnableRetry
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
})
public class HrAnalyzerApplication {

    public static void main(final String[] args) {
        SpringApplication.run(HrAnalyzerApplication.class, args);
    }

    /**
     * Обработчик события запуска приложения.
     * Логирует информацию о запуске микросервиса.
     */
    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted() {
        logApplicationEvent("запущен", "Время запуска");
    }

    /**
     * Обработчик события готовности приложения к работе.
     * Логирует информацию о готовности микросервиса.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logApplicationEvent("готов к работе", "Время готовности");
    }

    /**
     * Обработчик события ошибки запуска приложения.
     * Логирует информацию об ошибке запуска микросервиса.
     *
     * @param event Событие с информацией об ошибке.
     */
    @EventListener(ApplicationFailedEvent.class)
    public void onApplicationFailed(final ApplicationFailedEvent event) {
        log.error("Микросервис запущен с ошибкой!");

        // Проверяем, есть ли исключение
        Throwable exception = event.getException();
        if (exception != null) {
            log.error("Ошибка: {}", exception.getMessage());
            log.error("Тип исключения: {}", exception.getClass().getName());
            log.error("Стек ошибок:", exception);
        } else {
            log.warn("Ошибка запуска приложения без исключения.");
        }
    }

    /**
     * Вспомогательный метод для логирования событий приложения.
     *
     * @param status    Статус приложения (например, "запущен", "готов к работе").
     * @param timeLabel Метка времени (например, "Время запуска", "Время готовности").
     */
    private void logApplicationEvent(final String status, final String timeLabel) {
        log.info("Микросервис {}! {} {}", status, timeLabel, LocalDateTime.now());
    }
}

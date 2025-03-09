package org.surkov.hranalyzer.giga_chat;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.surkov.hranalyzer.giga_chat.config.GigaChatConfig;
import org.surkov.hranalyzer.giga_chat.exception.AuthenticationException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Менеджер токенов для работы с GigaChat API.
 * Отвечает за получение, обновление и хранение токена доступа, обеспечивая потокобезопасность.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenManager {

    /**
     * Конфигурация GigaChat API, содержащая URL, учетные данные и параметры ретраев.
     */
    private final GigaChatConfig config;

    /**
     * Обертка над HTTP-клиентом для выполнения запросов к API.
     */
    private final HttpClientWrapper httpClientWrapper;

    /**
     * Токен доступа для аутентификации в API.
     * Хранится в атомарной ссылке для обеспечения потокобезопасности.
     */
    private final AtomicReference<String> accessToken = new AtomicReference<>("");

    /**
     * Время истечения срока действия токена доступа (в миллисекундах).
     * Хранится в атомарном объекте для обеспечения потокобезопасности.
     */
    private final AtomicLong tokenExpirationTime = new AtomicLong(0);

    /**
     * Флаг, указывающий, обновляется ли токен в данный момент.
     * Используется для предотвращения одновременного обновления токена несколькими потоками.
     */
    private final AtomicBoolean isRefreshing = new AtomicBoolean(false);

    /**
     * Блокировка для синхронизации потоков при обновлении токена.
     */
    private final Lock lock = new ReentrantLock();

    /**
     * Условие для ожидания завершения обновления токена.
     */
    private final Condition tokenRefreshed = lock.newCondition();

    /**
     * Множитель для перевода секунд в миллисекунды.
     */
    private static final int SECONDS_TO_MILLISECONDS = 1000;

    /**
     * Получает текущий токен доступа, обновляя его, если это необходимо.
     *
     * @return Токен доступа в виде строки.
     */
    public String getAccessToken() {
        if (isTokenExpiredOrExpiringSoon()) {
            refreshToken();
        }
        return accessToken.get();
    }

    /**
     * Проверяет, истек ли токен или скоро истечет.
     *
     * @return {@code true}, если токен истек или скоро истечет, иначе {@code false}.
     */
    private boolean isTokenExpiredOrExpiringSoon() {
        long tokenExpirationThreshold = tokenExpirationTime.get() - config.getTokenRefreshBufferMs();
        return System.currentTimeMillis() >= tokenExpirationThreshold;
    }

    /**
     * Обновляет токен доступа, если он не обновляется в данный момент.
     * Если обновление уже выполняется другим потоком, текущий поток ожидает завершения обновления.
     */
    private void refreshToken() {
        if (isRefreshing.compareAndSet(false, true)) {
            lock.lock();
            try {
                fetchAccessToken();
                tokenRefreshed.signalAll();
            } finally {
                isRefreshing.set(false);
                lock.unlock();
            }
        } else {
            waitForTokenRefresh();
        }
    }

    /**
     * Ожидает завершения обновления токена другим потоком.
     * Использует механизм блокировки и условия для эффективного ожидания без активного ожидания.
     *
     * @throws AuthenticationException ожидание обновления токена было прервано.
     */
    private void waitForTokenRefresh() {
        lock.lock();
        try {
            while (isRefreshing.get()) {
                try {
                    tokenRefreshed.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AuthenticationException("Прервано ожидание обновления токена", e);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Получает новый токен доступа от GigaChat API.
     * Метод выполняется с механизмом повторных попыток в случае ошибок аутентификации.
     *
     * @throws AuthenticationException не удалось получить токен доступа.
     */
    @Retryable(
            retryFor = AuthenticationException.class,
            backoff = @Backoff(
                    delayExpression = "#{@gigaChatConfig.retryInitialDelayMs}",
                    multiplierExpression = "#{@gigaChatConfig.retryDelayMultiplier}"
            )
    )
    public void fetchAccessToken() {
        try {
            String credentials = config.getClientId() + ":" + config.getClientSecret();
            String authKey = Base64.getEncoder().encodeToString(
                    credentials.getBytes(StandardCharsets.UTF_8)
            );

            RequestBody formBody = new FormBody.Builder()
                    .add("scope", "GIGACHAT_API_PERS")
                    .build();
            Request request = new Request.Builder()
                    .url(config.getAuthUrl())
                    .post(formBody)
                    .addHeader(
                            "Content-Type",
                            "application/x-www-form-urlencoded"
                    )
                    .addHeader(
                            "Accept",
                            "application/json"
                    )
                    .addHeader(
                            "RqUID",
                            UUID.randomUUID().toString()
                    )
                    .addHeader(
                            "Authorization",
                            "Basic " + authKey
                    )
                    .build();

            JsonNode responseBody = httpClientWrapper.executeRequest(request, "Не удалось получить токен доступа");
            if (!responseBody.has("access_token") || !responseBody.has("expires_at")) {
                log.error("Некорректный ответ токена: отсутствует access_token или expires_at");
                throw new AuthenticationException("Некорректный ответ токена");
            }

            accessToken.set(responseBody.get("access_token").asText());
            long expiresAtSec = responseBody.get("expires_at").asLong();
            tokenExpirationTime.set(expiresAtSec * SECONDS_TO_MILLISECONDS);
            log.info("Токен доступа успешно получен, истекает: {}", tokenExpirationTime.get());
        } catch (Exception e) {
            log.error("Ошибка при получении токена доступа", e);
            throw new AuthenticationException("Ошибка при получении токена доступа", e);
        }
    }
}

package org.surkov.hranalyzer.giga_chat.config;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * Конфигурация для создания OkHttpClient с поддержкой SSL/TLS.
 */
@Configuration
@RequiredArgsConstructor
public class OkHttpClientConfig {

    private final GigaChatConfig gigaChatConfig;

    /**
     * Создает и настраивает OkHttpClient с SSL/TLS.
     *
     * @return настроенный OkHttpClient
     */
    @Bean
    public OkHttpClient okHttpClient() {
        try {
            validateCertFile();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            try (FileInputStream certInput = new FileInputStream(gigaChatConfig.getCertPath())) {
                X509Certificate caCert = (X509Certificate) cf.generateCertificate(certInput);

                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);
                keyStore.setCertificateEntry("caCert", caCert);

                TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm()
                );
                tmf.init(keyStore);

                SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
                sslContext.init(null, tmf.getTrustManagers(), null);

                return new OkHttpClient.Builder()
                        .sslSocketFactory(
                                sslContext.getSocketFactory(),
                                (X509TrustManager) tmf.getTrustManagers()[0]
                        )
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to configure OkHttpClient", e);
        }
    }

    /**
     * Проверяет наличие сертификата и его читаемость.
     * Если файл не найден или отсутствует, выбрасывается исключение {@code IllegalStateException}
     */
    private void validateCertFile() {
        File certFile = new File(gigaChatConfig.getCertPath());
        if (!certFile.exists() || !certFile.canRead()) {
            throw new IllegalStateException(
                    "Certificate file not found or unreadable: "
                            + gigaChatConfig.getCertPath()
            );
        }
    }
}

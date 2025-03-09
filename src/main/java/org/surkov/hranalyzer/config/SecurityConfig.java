package org.surkov.hranalyzer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация безопасности приложения.
 * Настраивает базовую HTTP-аутентификацию и правила доступа к ресурсам.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Имя пользователя для доступа к API.
     * Загружается из конфигурации приложения.
     */
    @Value("${api.user.username}")
    private String username;

    /**
     * Пароль пользователя для доступа к API.
     * Загружается из конфигурации приложения.
     */
    @Value("${api.user.password}")
    private String password;

    /**
     * Настраивает цепочку фильтров безопасности.
     * Определяет правила доступа к различным ресурсам приложения.
     *
     * @param http объект конфигурации HTTP-безопасности
     * @return настроенная цепочка фильтров безопасности
     * @throws Exception если возникла ошибка при настройке
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Разрешаем доступ к документации Swagger без аутентификации
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Для всех остальных запросов требуем аутентификацию
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Создает сервис для работы с данными пользователей.
     * Настраивает одного пользователя с учетными данными из конфигурации.
     *
     * @return сервис для работы с данными пользователей
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username(username)
                .password(passwordEncoder().encode(password))
                .roles("API_USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Создает кодировщик паролей для безопасного хранения.
     *
     * @return кодировщик паролей BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
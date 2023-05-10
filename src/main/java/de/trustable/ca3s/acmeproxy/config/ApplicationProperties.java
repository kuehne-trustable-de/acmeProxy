package de.trustable.ca3s.acmeproxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Ca 3 S Acme Proxy.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
}

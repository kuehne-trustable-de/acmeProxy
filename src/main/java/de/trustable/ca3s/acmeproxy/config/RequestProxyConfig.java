package de.trustable.ca3s.acmeproxy.config;

import com.nimbusds.jose.JOSEException;
import de.trustable.ca3s.acmeproxy.service.JWSService;
import de.trustable.ca3s.acmeproxy.service.dto.RemoteRequestProxyConfigView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
public class RequestProxyConfig {

    transient Logger LOG = LoggerFactory.getLogger(RequestProxyConfig.class);

    private RemoteRequestProxyConfigView remoteRequestProxyConfigView;
    private Instant remoteRequestProxyConfigNextUpdate = Instant.now();

    private final String remoteAcmeServer;
    private final long requestProxyConfigId;
    private final JWSService jwsService;

    private RestTemplate restTemplate = new RestTemplate();

    public RequestProxyConfig(@Value("${acme.proxy.remote.server:http://localhost:8080}") String remoteAcmeServer,
                              @Value("${acme.proxy.id:-1}") long requestProxyConfigId,
                              JWSService jwsService) {
        this.remoteAcmeServer = remoteAcmeServer;
        this.requestProxyConfigId = requestProxyConfigId;
        this.jwsService = jwsService;
    }

    public RemoteRequestProxyConfigView getConfig() {

        if(remoteRequestProxyConfigNextUpdate.isAfter(Instant.now())){
            return remoteRequestProxyConfigView;
        }

        String resourceUrlConfig = remoteAcmeServer + "/api/request-proxy-configs/remote-config/{requestProxyId}";

        try {
            LOG.debug("requestproxy configuration at {}", resourceUrlConfig);

            ResponseEntity<RemoteRequestProxyConfigView> remoteRequestProxyConfigViewResponseEntity;

            String jwt = jwsService.buildJWT();
            remoteRequestProxyConfigViewResponseEntity = restTemplate.exchange(
                resourceUrlConfig,
                HttpMethod.POST,
                buildHttpEntityBody(jwt),
                RemoteRequestProxyConfigView.class,
                requestProxyConfigId);

            LOG.info("server returns configuration {}", remoteRequestProxyConfigViewResponseEntity.getBody());

            remoteRequestProxyConfigView = remoteRequestProxyConfigViewResponseEntity.getBody();
            remoteRequestProxyConfigNextUpdate = Instant.now().plus(1, ChronoUnit.MINUTES) ;
            return remoteRequestProxyConfigView;

        } catch (
            ResourceAccessException resourceAccessException) {
            LOG.debug("ca3s server not accessible");
            throw resourceAccessException;
        } catch ( JOSEException | HttpClientErrorException exception) {
            LOG.warn("problem retrieving remote configuration data: {}", exception.getMessage());
            throw new ResourceAccessException(exception.getMessage());
        }
    }

    HttpEntity<String> buildHttpEntityBody(final String jws) throws JOSEException {

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(APPLICATION_JSON));

        return new HttpEntity<>(jws, headers);
    }

}

package de.trustable.ca3s.acmeproxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import de.trustable.ca3s.acmeproxy.config.RequestProxyConfig;
import de.trustable.ca3s.acmeproxy.service.dto.*;
import de.trustable.ca3s.challenge.ChallengeValidator;
import de.trustable.ca3s.challenge.exception.ChallengeDNSException;
import de.trustable.ca3s.challenge.exception.ChallengeDNSIdentifierException;
import de.trustable.ca3s.challenge.exception.ChallengeUnknownHostException;
import de.trustable.ca3s.challenge.exception.ChallengeValidationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class ChallengeScheduler {

    transient Logger LOG = LoggerFactory.getLogger(ChallengeScheduler.class);

    private final String remoteAcmeServer;

    private final RequestProxyConfig requestProxyConfig;

    private final ChallengeValidator challengeValidator;
    private RestTemplate restTemplate = new RestTemplate();

    private final JWSService jwsService;
    private final ObjectMapper objectMapper;

    public ChallengeScheduler(@Value("${acme.proxy.remote.server:http://localhost:8080}") String remoteAcmeServer,
                              RequestProxyConfig requestProxyConfig,
                              @Value("${acme.proxy.dns.server:}") String resolverHost,
                              @Value("${acme.proxy.dns.port:53}") int resolverPort,
                              @Value("${acme.proxy.http.timeoutMilliSec:1000}") int timeoutMilliSec,
                              JWSService jwsService,
                              ObjectMapper objectMapper) {
        this.remoteAcmeServer = remoteAcmeServer;
        this.requestProxyConfig = requestProxyConfig;
        this.jwsService = jwsService;
        this.objectMapper = objectMapper;

        challengeValidator = new ChallengeValidator(resolverHost,
            resolverPort,
            timeoutMilliSec,
            null, null);
    }

    @Scheduled(fixedDelay = 5000)
    public void runFiveSeconds() {

        String resourceUrlPending = remoteAcmeServer + "/api/acme-challenges/pending/request-proxy-configs/{requestProxyId}";
        String resourceUrlValidation = remoteAcmeServer + "/api/acme-challenges/validation";

        try {
            RemoteRequestProxyConfigView remoteRequestProxyConfigView = requestProxyConfig.getConfig();

            LOG.debug("checking for challenges at {}", remoteAcmeServer);
            ResponseEntity<AcmeChallenges> responseEntity;

            try {
                String jwt = jwsService.buildJWT();
                responseEntity = restTemplate.exchange(
                    resourceUrlPending,
                    HttpMethod.POST,
                    buildHttpEntityBody(jwt),
                    AcmeChallenges.class,
                    remoteRequestProxyConfigView.getId());

                LOG.info("server returns #{} pending challenge", responseEntity.getBody().size());
            } catch (JOSEException e) {
                LOG.warn("problem creating JWS for validation payload", e);

                // give up for now, retry later ...
                return;
            }

            LOG.debug("challenge request returns {}", responseEntity.getStatusCode());

            if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                AcmeChallenges acmeChallenges = responseEntity.getBody();
                for (AcmeChallenge acmeChallenge : acmeChallenges) {
                    LOG.debug("pending challenge request relevant for this proxy: {}", acmeChallenge);

                    AcmeChallengeValidation acmeChallengeValidation = new AcmeChallengeValidation();
                    acmeChallengeValidation.setChallengeId(acmeChallenge.getChallengeId());
                    acmeChallengeValidation.setRequestProxyConfigId(remoteRequestProxyConfigView.getId());

                    try {
                        Collection<String> challengeResponses = processChallenge(acmeChallenge);
                        acmeChallengeValidation.setStatus(ChallengeStatus.VALID);
                        acmeChallengeValidation.setResponses(challengeResponses.toArray(new String[0]));

                    } catch (ChallengeUnknownHostException e) {
                        acmeChallengeValidation.setStatus(ChallengeStatus.INVALID);
                        acmeChallengeValidation.setError(e.getMessage());
                    } catch (ChallengeValidationFailedException |
                             ChallengeDNSException |
                             ChallengeDNSIdentifierException |
                             GeneralSecurityException e) {
                        acmeChallengeValidation.setStatus(ChallengeStatus.PENDING);
                        acmeChallengeValidation.setError(e.getMessage());
                    }

                    try {
                        String payload = objectMapper.writeValueAsString(acmeChallengeValidation);
                        LOG.debug("serialized acmeChallengeValidation: '{}'", payload);

                        String jws = jwsService.buildEmbeddingJWS(payload);

                        ResponseEntity<?> response = restTemplate.exchange(
                            resourceUrlValidation,
                            HttpMethod.POST,
                            buildHttpEntityBody(jws),
                            Void.class);

                        LOG.info("challenge update response {}", response);
                    } catch (JOSEException | JsonProcessingException e) {
                        LOG.warn("problem creating JWS for validation payload", e);
                    }
                }
            }
        } catch (ResourceAccessException resourceAccessException) {
            LOG.debug("ca3s server not accessible");
        } catch (HttpClientErrorException httpClientErrorException) {
            if (httpClientErrorException.getRawStatusCode() == 404) {
                LOG.debug("no pending challenges");
            } else {
                LOG.warn("problem retrieving pending challenges: {}", httpClientErrorException.getMessage());
            }
        }
    }

    private Collection<String> processChallenge(AcmeChallenge acmeChallenge) throws ChallengeUnknownHostException, ChallengeValidationFailedException, ChallengeDNSException, ChallengeDNSIdentifierException, GeneralSecurityException {
        Collection<String> challengeResponses = new ArrayList<>();
        switch (acmeChallenge.getType()) {
            case AcmeChallenge.CHALLENGE_TYPE_HTTP_01:
                challengeResponses.add(challengeValidator.retrieveChallengeHttp(acmeChallenge.getValue(), acmeChallenge.getToken()));
                break;
            case AcmeChallenge.CHALLENGE_TYPE_DNS_01:
                challengeResponses.addAll(challengeValidator.retrieveChallengeDNS(acmeChallenge.getValue()));
                break;
            case AcmeChallenge.CHALLENGE_TYPE_ALPN_01:
                challengeResponses.add(challengeValidator.retrieveChallengeALPN(acmeChallenge.getValue()));
                break;

            default:
                LOG.warn("unexpected challenge type: {}", acmeChallenge.getType());
                break;
        }
        LOG.debug("challenge response for '{}' found: {}", acmeChallenge.getValue(), challengeResponses);
        return challengeResponses;
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


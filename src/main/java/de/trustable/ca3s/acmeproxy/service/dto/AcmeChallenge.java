package de.trustable.ca3s.acmeproxy.service.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * A AcmeChallenge.
 */

public class AcmeChallenge implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long challengeId;

    private String type;
    private String value;
    private String token;
    private Instant validated;
    private String lastError;
    private ChallengeStatus status;


    public Long getChallengeId() {
        return this.challengeId;
    }

    public AcmeChallenge challengeId(Long challengeId) {
        this.setChallengeId(challengeId);
        return this;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getType() {
        return this.type;
    }

    public AcmeChallenge type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public AcmeChallenge value(String value) {
        this.value = value;
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getToken() {
        return this.token;
    }

    public AcmeChallenge token(String token) {
        this.setToken(token);
        return this;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getValidated() {
        return this.validated;
    }

    public AcmeChallenge validated(Instant validated) {
        this.setValidated(validated);
        return this;
    }

    public void setValidated(Instant validated) {
        this.validated = validated;
    }

    public ChallengeStatus getStatus() {
        return this.status;
    }

    public AcmeChallenge status(ChallengeStatus status) {
        this.setStatus(status);
        return this;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }



    // prettier-ignore
    @Override
    public String toString() {
        return "AcmeChallenge{" +
            ", challengeId=" + getChallengeId() +
            ", type='" + getType() + "'" +
            ", value='" + getValue() + "'" +
            ", token='" + getToken() + "'" +
            ", validated='" + getValidated() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }

    public static final String CHALLENGE_TYPE_HTTP_01 = "http-01";
    public static final String CHALLENGE_TYPE_DNS_01 = "dns-01";
    public static final String CHALLENGE_TYPE_ALPN_01 = "tls-alpn-01";

}

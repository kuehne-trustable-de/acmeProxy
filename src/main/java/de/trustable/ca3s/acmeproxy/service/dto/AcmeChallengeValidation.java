package de.trustable.ca3s.acmeproxy.service.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A AcmeChallengeValidation object.
 */

public class AcmeChallengeValidation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long challengeId;
    private Long requestProxyConfigId;

    private String[] responses;
    private String error;
    private ChallengeStatus status;


    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public Long getRequestProxyConfigId() {
        return requestProxyConfigId;
    }

    public void setRequestProxyConfigId(Long requestProxyConfigId) {
        this.requestProxyConfigId = requestProxyConfigId;
    }

    public String[] getResponses() {
        return responses;
    }

    public void setResponses(String[] responses) {
        this.responses = responses;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ChallengeStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AcmeChallenge{" +
            ", challengeId=" + getChallengeId() +
            ", requestProxyConfigId=" + getRequestProxyConfigId() +
            ", responses='" + String.join(",", getResponses()) + "'" +
            ", error='" + getError() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }

    public Map<String,Object> toJsonObject(){
        HashMap<String,Object> map = new HashMap();

        map.put("challengeId", getChallengeId());
        map.put("requestProxyConfigId", getRequestProxyConfigId());
        map.put("responses", getResponses());
        map.put("error", getError());
        map.put("status", getStatus());
        return map;
    }
}

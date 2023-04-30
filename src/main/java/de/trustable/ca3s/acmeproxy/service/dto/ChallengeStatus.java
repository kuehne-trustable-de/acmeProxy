package de.trustable.ca3s.acmeproxy.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ChallengeStatus enumeration.
 */
public enum ChallengeStatus {
    PENDING, VALID, INVALID, DEACTIVATED, EXPIRED, REVOKED;


	private static final Logger LOG = LoggerFactory.getLogger(ChallengeStatus.class);

	@JsonCreator
	public static ChallengeStatus forValues(String value) {

		for (ChallengeStatus stat : ChallengeStatus.values()) {
			if (stat.toString().equalsIgnoreCase(value)) {
				return stat;
			}
		}
		LOG.warn("trying to build ChallengeStatus from an unexpected value '{}'", value);
		return null;
	}

	@JsonValue
    public String getValue() {
        return this.toString().toLowerCase();
    }

}

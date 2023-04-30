package de.trustable.ca3s.acmeproxy.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Map;


@Service
public class JWSService {

    private static final Logger log = LoggerFactory.getLogger(JWSService.class);

    private final long requestProxyConfigId;
    private final String secretPassphrase;
    private final String salt;
    private final int iterations;
    private final String pbeAlgo;

    private byte[] sharedSecret = new byte[0];

    public JWSService( @Value("${acme.proxy.id:-1}") long requestProxyConfigId,
                       @Value("${acme.proxy.connection.secret:#{null}}") String secretPassphrase,
                      @Value("${acme.proxy.connection.salt:ca3sSalt}") String salt,
                      @Value("${acme.proxy.connection.iterations:4567}") int iterations,
                      @Value("${acme.proxy.connection.pbeAlgo:PBKDF2WithHmacSHA256}") String pbeAlgo) {

        this.requestProxyConfigId = requestProxyConfigId;
        this.secretPassphrase = secretPassphrase;
        this.salt = salt;
        this.iterations = iterations;
        this.pbeAlgo = pbeAlgo;
    }

    byte[] getSalt() {
        return salt.getBytes();
    }

    int getIterations() {
        return iterations;
    }

    public String buildEmbeddingJWS(final String content) throws JOSEException {

        JWSSigner signer = new MACSigner(sharedSecret);

        // The payload which will not be encoded and must be passed to
        // the JWS consumer in a detached manner
        Payload payload = new Payload(content);

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
            .keyID("rid-" + requestProxyConfigId)
            .build();

        JWSObject jwsObject = new JWSObject(header, payload);

        // Apply the HMAC
        jwsObject.sign(signer);

        boolean isDetached = false;
        String jws = jwsObject.serialize(isDetached);

        log.debug("JWS: '{}'", jws );

        return jws;
    }

    public String buildJWT() throws JOSEException {

        JWSSigner signer = new MACSigner(sharedSecret);

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
            .keyID("rid-" + requestProxyConfigId)
            .build();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .expirationTime(new Date(new Date().getTime() + 60 * 1000))
            .build();

        SignedJWT signedJWT = new SignedJWT(header, claimsSet);

        // Apply the HMAC
        signedJWT.sign(signer);

        String jwt = signedJWT.serialize();

        log.debug("JWT: '{}'", jwt );

        return jwt;
    }

    public byte[] getSharedSecret() throws GeneralSecurityException {
        String passphrase = getPassphrase();
        return getSharedSecret(passphrase);
    }

    public byte[] getSharedSecret(String passphrase) throws GeneralSecurityException {

        PBEKeySpec spec = new PBEKeySpec(
            passphrase.toCharArray(),
            getSalt(),
            getIterations(),
            256
        );
        SecretKeyFactory skf = SecretKeyFactory.getInstance(pbeAlgo);

        byte[] derivedSecret = skf.generateSecret(spec).getEncoded();

        log.debug("NOT FOR PRODUCTION: calculated secret as " + Base64.getEncoder().encodeToString(derivedSecret));

        return derivedSecret;
    }

    private String getPassphrase() {
        String passphrase = "s3cr3t";

        if (secretPassphrase != null) {
            // check content from command line / property file
            passphrase = secretPassphrase;
        } else {
            int passLen = passphrase.length();
            log.debug("NOT FOR PRODUCTION: default secret used : '{}'",
                "*****" + passphrase.substring(passLen-3, passLen)
            );
        }
        return passphrase;
    }

    @PostConstruct
    public void init() {
        try {
            sharedSecret = getSharedSecret();
        } catch (GeneralSecurityException e) {
            log.warn("problem starting the JWSService", e);
        }
    }

}

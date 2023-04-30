package de.trustable.ca3s.acmeproxy.service;

import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JWSServiceTest {

    @Test
    void getSharedSecret() throws GeneralSecurityException {

        JWSService subject = new JWSService( 1,
            "s3cr3t",
            "ca3sSalt",
            4567,
            "PBKDF2WithHmacSHA256");

        byte[] sharedSecretBytes = subject.getSharedSecret("S3cr3t!S");
        String sharedSecretString = Base64.getEncoder().encodeToString(sharedSecretBytes);

        assertEquals( "Bm9rujt6U/jym7/lSb1RF1j1FyRXCDeh4WHHczmPSK0=", sharedSecretString);
    }
}

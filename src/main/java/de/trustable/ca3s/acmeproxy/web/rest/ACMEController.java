package de.trustable.ca3s.acmeproxy.web.rest;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;

@Controller
public class ACMEController {

	  public static final URI NO_INSTANCE = null;
	  public static final String NO_DETAIL = null;
	  
	  public static final String APPLICATION_JWS_VALUE = "application/jws";
	  public static final String APPLICATION_JOSE_JSON_VALUE = "application/jose+json";
	  public static final String APPLICATION_PKIX_CERT_VALUE = "application/pkix-cert";
	  public static final String APPLICATION_PEM_CERT_CHAIN_VALUE = "application/pem-certificate-chain";
	  public static final String APPLICATION_PEM_CERT_VALUE = "application/pem-certificate";
	  public static final String APPLICATION_PEM_FILE_VALUE = "application/x-pem-file";
	  public static final MediaType APPLICATION_JOSE_JSON = MediaType.parseMediaType(APPLICATION_JOSE_JSON_VALUE);
	  public static final MediaType APPLICATION_JWS = MediaType.parseMediaType(APPLICATION_JWS_VALUE);
	  public static final MediaType APPLICATION_PKIX_CERT = MediaType.parseMediaType(APPLICATION_PKIX_CERT_VALUE);
	  public static final MediaType APPLICATION_PEM_CERT_CHAIN = MediaType.parseMediaType(APPLICATION_PEM_CERT_CHAIN_VALUE);
	  public static final MediaType APPLICATION_PEM_CERT = MediaType.parseMediaType(APPLICATION_PEM_CERT_VALUE);
	  public static final MediaType APPLICATION_PEM_FILE = MediaType.parseMediaType(APPLICATION_PEM_FILE_VALUE);
	  
	  
}

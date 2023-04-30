package de.trustable.ca3s.acmeproxy.web.api;

import de.trustable.ca3s.acmeproxy.service.api.dto.DirectoryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static de.trustable.ca3s.acmeproxy.web.rest.ACMEController.*;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequestUri;

@Component
public class AcmeApiImpl implements AcmeApiDelegate{

    transient Logger LOG = LoggerFactory.getLogger(AcmeApiImpl.class);

    public static final String REPLAY_NONCE_HEADER = "Replay-Nonce";

    private final String targetUrl;
    private final String remoteAcmeServer;
    private long requestProxyConfigId;

    private RestTemplate restTemplate = new RestTemplate();

    public AcmeApiImpl(@Value( "${acme.proxy.remote.server:http://localhost:8080}") String remoteAcmeServer,
                       @Value("${acme.proxy.id:-1}") long requestProxyConfigId) {

        this.remoteAcmeServer = remoteAcmeServer;
        this.requestProxyConfigId = requestProxyConfigId;

        this.targetUrl = remoteAcmeServer + "/acme/{realm}/";

        LOG.debug("remoteAcmeServer: '{}'", remoteAcmeServer);
        LOG.debug("target ACME server Url: '{}'", targetUrl);

        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    /**
     * POST /acme/{realm}/acct/changeKey
     *
     * @param realm  (required)
     * @param body  (required)
     * @return OK (status code 200)
     * @see AcmeApi#changeKey
     */
    public ResponseEntity<Object> changeKey(String realm,
                                             String body,
                                            @RequestHeader MultiValueMap<String, String> headers) {

        String resourceUrl = targetUrl +"acct/changeKey";
        LOG.debug("forwarding {realm}/acct/changeKey", realm);
        ResponseEntity<Object> responseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm);
        return logResponseEntity(responseEntity);
    }

    /**
     * POST /acme/{realm}/newOrder
     *
     * @param realm  (required)
     * @param body  (required)
     * @return OK (status code 200)
     * @see AcmeApi#consumingPostedJws1
     */
    public ResponseEntity<Object> consumingPostedJws1(String realm,
                                                       String body,
                                                      @RequestHeader MultiValueMap<String, String> headers) {

        String resourceUrl = targetUrl + "newOrder";
        LOG.debug("forwarding {realm}/newOrder", realm);
        ResponseEntity<Object> responseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm);
        return logResponseEntity(responseEntity);
    }

    /**
     * POST /acme/{realm}/newAccount
     *
     * @param realm  (required)
     * @param body  (required)
     * @return OK (status code 200)
     * @see AcmeApi#consumingPostedJws2
     */
    public ResponseEntity<Object> consumingPostedJws2(String realm,
                                                       String body,
                                                      @RequestHeader MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "newAccount";

        LOG.debug("forwarding {realm}/newAccount", realm);
        ResponseEntity<Object> responseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm);
        return logResponseEntity(responseEntity);
    }

    /**
     * POST /acme/{realm}/order/finalize/{orderId}
     *
     * @param orderId  (required)
     * @param realm  (required)
     * @param body  (required)
     * @return OK (status code 200)
     * @see AcmeApi#finalizeOrder
     */
    public ResponseEntity<Object> finalizeOrder(Long orderId,
                                                 String realm,
                                                 String body,
                                                @RequestHeader MultiValueMap<String, String> headers) {

        String resourceUrl = targetUrl + "order/finalize/{orderId}";
        LOG.debug("forwarding {realm}/order/finalize/{orderId}", realm, orderId);
        ResponseEntity<Object> responseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm, orderId);
        return logResponseEntity(responseEntity);
    }

    /**
     * POST /acme/{realm}/acct/{accountId}/orders
     *
     * @param accountId  (required)
     * @param realm  (required)
     * @param body  (required)
     * @param cursor  (optional, public to 0)
     * @return OK (status code 200)
     * @see AcmeApi#getAccountOrders
     */
    public ResponseEntity<Object> getAccountOrders(Long accountId,
                                                    String realm,
                                                    String body,
                                                    String cursor,
                                                   @RequestHeader MultiValueMap<String, String> headers) {

        String resourceUrl = targetUrl + "acct/{accountId}/orders";
        LOG.debug("forwarding {realm}/acct/{accountId}/orders", realm, accountId);
        ResponseEntity<Object> responseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm, accountId);
        return logResponseEntity(responseEntity);
    }

    /**
     * GET /acme/{realm}/authorization/{authorizationId}
     *
     * @param authorizationId  (required)
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#getAuthorization
     */
    public ResponseEntity<Object> getAuthorization(Long authorizationId,
                                                    String realm) {

        String resourceUrl = targetUrl + "authorization/{authorizationId}";
        LOG.debug("forwarding {realm}/authorization/{authorizationId}", realm, authorizationId);
        ResponseEntity<Object> responseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.GET, buildHttpEntity(), Object.class, realm, authorizationId);
        return logResponseEntity(responseEntity);
    }

    /**
     * GET /acme/{realm}/cert/{certId}
     *
     * @param certId  (required)
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#getCertificatePKIX
     */
    public ResponseEntity<Object> getCertificatePKIX(Long certId,
                                                      String realm) {
        String resourceUrl = targetUrl + "cert/{certId}";
        LOG.debug("forwarding {realm}/cert/{certId}", realm, certId);
        ResponseEntity<Object> responseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.GET, buildHttpEntity(), Object.class, realm, certId);
        return logResponseEntity(responseEntity);
    }


    /**
     * GET /acme/{realm}/challenge/{challengeId}
     *
     * @param challengeId  (required)
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#getChallenge
     */
    public ResponseEntity<Object> getChallenge(Long challengeId,
                                                String realm) {
        String resourceUrl = targetUrl + "challenge/{challengeId}";
        LOG.debug("forwarding {realm}/challenge/{challengeId}", realm, challengeId);
        ResponseEntity<Object> responseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.GET, buildHttpEntity(), Object.class, realm, challengeId);
        return logResponseEntity(responseEntity);
    }

    /**
     * GET /acme/{realm}/directory
     *
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#getDirectory
     */
    public ResponseEntity<DirectoryResponse> getDirectory(String realm) {
        String resourceUrl = targetUrl + "directory";
        LOG.debug("forwarding GET {realm}/directory", realm);

        ResponseEntity<DirectoryResponse> directoryResponseResponseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.GET, buildHttpEntity(), DirectoryResponse.class, realm);

        return directoryResponseResponseEntity;
    }

    /**
     * POST /acme/{realm}/directory
     *
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#getDirectory1
     */
    public ResponseEntity<DirectoryResponse> getDirectory1(String realm) {
        String resourceUrl = targetUrl + "directory";
        LOG.debug("forwarding POST {realm}/directory", realm);
        ResponseEntity<DirectoryResponse> directoryResponseResponseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(), DirectoryResponse.class, realm);

        return directoryResponseResponseEntity;
    }

    /**
     * POST /acme/{realm}/order/{orderId}
     *
     * @param orderId  (required)
     * @param realm  (required)
     * @param body  (required)
     * @return OK (status code 200)
     * @see AcmeApi#postAsGetOrder
     */
    public ResponseEntity<Object> postAsGetOrder(Long orderId,
                                                  String realm,
                                                  String body,
                                                 @RequestHeader MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "order/{orderId}";
        LOG.debug("forwarding {realm}/order/{orderId}", realm, orderId);
        ResponseEntity<Object> responseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm, orderId);
        return logResponseEntity(responseEntity);
    }

    /**
     * POST /acme/{realm}/authorization/{authorizationId}
     *
     * @param authorizationId  (required)
     * @param realm  (required)
     * @param body  (required)
     * @return OK (status code 200)
     * @see AcmeApi#postAuthorization
     */
    public ResponseEntity<Object> postAuthorization(Long authorizationId,
                                                     String realm,
                                                     String body,
                                                    @RequestHeader MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "authorization/{authorizationId}";
        LOG.debug("forwarding {realm}/authorization/{authorizationId}", realm, authorizationId);
        ResponseEntity<Object> responseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm, authorizationId);
        return logResponseEntity(responseEntity);
    }

    /**
     * POST /acme/{realm}/challenge/{challengeId}
     *
     * @param challengeId  (required)
     * @param realm  (required)
     * @param body  (required)
     * @return OK (status code 200)
     * @see AcmeApi#postChallenge
     */
    public ResponseEntity<Object> postChallenge(Long challengeId,
                                                 String realm,
                                                 String body,
                                                @RequestHeader MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "challenge/{challengeId}";
        LOG.debug("forwarding {realm}/challenge/{challengeId}", realm, challengeId);
        ResponseEntity<Object> responseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm, challengeId);
        return logResponseEntity(responseEntity);
    }

    /**
     * POST /acme/{realm}/cert/{certId}
     *
     * @param contentType  (required)
     * @param certId  (required)
     * @param realm  (required)
     * @param body  (required)
     * @param accept  (optional, public to application/pem-certificate-chain)
     * @return OK (status code 200)
     * @see AcmeApi#retrieveCertificate
     */
    public ResponseEntity<?> retrieveCertificate(String contentType,
                                                 Long certId,
                                                 String realm,
                                                 String body,
                                                 String accept,
                                                 @RequestHeader MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "cert/{certId}";
        String mediaType = headers.getFirst(HttpHeaders.ACCEPT);
        LOG.debug("forwarding {realm}/cert/{certId} with media type {}", realm, certId, mediaType);

        if( "application/pkix-cert".equals(mediaType)){
            ResponseEntity<byte[]> responseEntity =
                restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), byte[].class, realm, certId);
            return logResponseEntity(responseEntity);

        }else {
            ResponseEntity<String> responseEntity =
                restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), String.class, realm, certId);
            return logResponseEntity(responseEntity);
        }
    }

    /**
     * POST /acme/{realm}/cert/revoke
     *
     * @param realm  (required)
     * @param body  (required)
     * @return OK (status code 200)
     * @see AcmeApi#revokeCertificate
     */
    public ResponseEntity<?> revokeCertificate(String realm,
                                               String body,
                                               @RequestHeader MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "cert/revoke";
        LOG.debug("forwarding {realm}/cert/revoke", realm);
        ResponseEntity<Object> responseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm);
        return logResponseEntity(responseEntity);
    }

    /**
     * POST /acme/{realm}/acct/{accountId}
     *
     * @param accountId  (required)
     * @param realm  (required)
     * @param body  (required)
     * @return OK (status code 200)
     * @see AcmeApi#updateAccount
     */
    public ResponseEntity<?> updateAccount(Long accountId,
                                           String realm,
                                           String body,
                                           @RequestHeader MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "acct/{accountId}";
        LOG.debug("forwarding {realm}/acct/{accountId}", realm, accountId);
        ResponseEntity<Object> responseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm, accountId);
        return logResponseEntity(responseEntity);
    }

    /**
     * GET /acme/{realm}/newNonce
     *
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#viaGet
     */
    public ResponseEntity<String> viaGet(String realm) {
        String resourceUrl = targetUrl + "newNonce";
        ResponseEntity<String> responseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.GET, buildHttpEntity(), String.class, realm);
        LOG.debug("forwarding GET {}/newNonce : ", realm, responseEntity.getBody());

        return logResponseEntity( responseEntity);
    }

    /**
     * POST /acme/{realm}/newNonce
     *
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#viaPost
     */
    public ResponseEntity<String> viaPost(String realm) {
        String resourceUrl = targetUrl + "newNonce";
        ResponseEntity<String> responseEntity =
        restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(), String.class, realm);
        LOG.debug("forwarding POST {}/newNonce: {}", realm, responseEntity.getBody());

        return logResponseEntity(responseEntity);
    }

    /**
     * HEAD /acme/{realm}/newNonce
     *
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#viaHead
     */
    public ResponseEntity<String> viaHead(String realm) {
        LOG.debug("forwarding HEAD {realm}/newNonce", realm);

        ResponseEntity<String> stringResponseEntity = viaPost(realm);

        HttpHeaders headers = new HttpHeaders();
        headers.addAll(stringResponseEntity.getHeaders());
        headers.add(REPLAY_NONCE_HEADER, stringResponseEntity.getBody());

        return new ResponseEntity("",
            headers,
            stringResponseEntity.getStatusCode());
    }

    <T> ResponseEntity<T> logResponseEntity(final ResponseEntity<T> responseEntity){

        LOG.debug("response status {}", responseEntity.getStatusCode());
        return responseEntity;
    }

    HttpEntity buildHttpEntity() {

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(APPLICATION_JWS);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        headers.add(HEADER_X_CA3S_FORWARDED_HOST, fromCurrentRequestUri().build().toString());
        headers.add(HEADER_X_CA3S_PROXY_ID, "" + requestProxyConfigId);


        return new HttpEntity<>(headers);
    }

    HttpEntity buildHttpEntity(final MultiValueMap<String, String> callerHeaders, final String body) {

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(callerHeaders);

        headers.add(HEADER_X_CA3S_FORWARDED_HOST, fromCurrentRequestUri().build().toString());
        headers.add(HEADER_X_CA3S_PROXY_ID, "" + requestProxyConfigId);

        return new HttpEntity<>(body, headers);
    }
}

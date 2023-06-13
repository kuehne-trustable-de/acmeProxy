package de.trustable.ca3s.acmeproxy.web.api;

import de.trustable.ca3s.acmeproxy.config.RequestProxyConfig;
import de.trustable.ca3s.acmeproxy.service.api.dto.DirectoryResponse;
import de.trustable.ca3s.acmeproxy.service.dto.RemoteRequestProxyConfigView;
import de.trustable.ca3s.acmeproxy.service.dto.problem.AcmeProblemException;
import de.trustable.ca3s.acmeproxy.service.dto.problem.ProblemDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

import static de.trustable.ca3s.acmeproxy.web.rest.ACMEController.*;
import static org.springframework.http.CacheControl.noStore;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequestUri;

@Component
public class AcmeApiImpl implements AcmeApiDelegate {

    transient Logger LOG = LoggerFactory.getLogger(AcmeApiImpl.class);

    public static final String ACME_ERROR_URI_NAMESPACE = "urn:ietf:params:acme:error";
    public static final URI REALM_DOES_NOT_EXIST = URI.create(ACME_ERROR_URI_NAMESPACE + ":accountDoesNotExist");
    public static final URI NO_INSTANCE = null;

    public static final String REPLAY_NONCE_HEADER = "Replay-Nonce";

    private final String targetUrl;

    private final RequestProxyConfig requestProxyConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    private final Set<String> forwardResponseHeaderSet = new HashSet<>();

    public AcmeApiImpl(@Value("${acme.proxy.remote.server:http://localhost:8080}") String remoteAcmeServer,
                       @Value("${acme.proxy.headers.forward.response:Location,Link,Replay-Nonce,Retry-After,Content-Type}") String[] forwardResponseHeaders,
                       RequestProxyConfig requestProxyConfig) {

        this.targetUrl = remoteAcmeServer + "/acme/{realm}/";
        this.requestProxyConfig = requestProxyConfig;
        this.forwardResponseHeaderSet.addAll(Arrays.asList(forwardResponseHeaders));

        LOG.debug("remoteAcmeServer: '{}'", remoteAcmeServer);
        LOG.debug("target ACME server Url: '{}'", targetUrl);
        LOG.debug("forward response header headers: '{}'", Arrays.asList(forwardResponseHeaders));

        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    /**
     * POST /acme/{realm}/acct/changeKey
     *
     * @param realm (required)
     * @param body  (required)
     * @return OK (status code 200)
     * @see AcmeApi#changeKey
     */
    public ResponseEntity<Object> changeKey(String realm,
                                            String body,
                                            MultiValueMap<String, String> headers) {

        String resourceUrl = targetUrl + "acct/changeKey";
        checkRealm(realm, resourceUrl);
        LOG.debug("forwarding {}/acct/changeKey", realm);
        ResponseEntity<Object> responseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm);

        ResponseEntity<Object> acmeResponseEntity = handleResponseEntity(responseEntity);
        return logResponseEntity(acmeResponseEntity);
    }

    /**
     * POST /acme/{realm}/newOrder
     *
     * @param realm (required)
     * @param body  (required)
     * @return OK (status code 200)
     * @see AcmeApi#consumingPostedJws1
     */
    public ResponseEntity<Object> consumingPostedJws1(String realm,
                                                      String body,
                                                      MultiValueMap<String, String> headers) {

        String resourceUrl = targetUrl + "newOrder";
        checkRealm(realm, resourceUrl);
        LOG.debug("forwarding {}/newOrder", realm);
        ResponseEntity<Object> responseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm);

        ResponseEntity<Object> acmeResponseEntity = handleResponseEntity(responseEntity);
        return logResponseEntity(acmeResponseEntity);
    }

    /**
     * POST /acme/{realm}/newAccount
     *
     * @param realm (required)
     * @param body  (required)
     * @return OK (status code 200)
     * @see AcmeApi#consumingPostedJws2
     */
    public ResponseEntity<Object> consumingPostedJws2(String realm,
                                                      String body,
                                                      MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "newAccount";

        checkRealm(realm, resourceUrl);
        LOG.debug("forwarding {}/newAccount", realm);
        ResponseEntity<Object> responseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm);

        ResponseEntity<Object> acmeResponseEntity = handleResponseEntity(responseEntity);
        return logResponseEntity(acmeResponseEntity);
    }

    /**
     * POST /acme/{realm}/order/finalize/{orderId}
     *
     * @param orderId (required)
     * @param realm   (required)
     * @param body    (required)
     * @return OK (status code 200)
     * @see AcmeApi#finalizeOrder
     */
    public ResponseEntity<Object> finalizeOrder(Long orderId,
                                                String realm,
                                                String body,
                                                MultiValueMap<String, String> headers) {

        String resourceUrl = targetUrl + "order/finalize/{orderId}";
        checkRealm(realm, resourceUrl);
        LOG.debug("forwarding {}/order/finalize/{}", realm, orderId);
        ResponseEntity<Object> responseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm, orderId);

        ResponseEntity<Object> acmeResponseEntity = handleResponseEntity(responseEntity);
        return logResponseEntity(acmeResponseEntity);
    }

    /**
     * POST /acme/{realm}/acct/{accountId}/orders
     *
     * @param accountId (required)
     * @param realm     (required)
     * @param body      (required)
     * @param cursor    (optional, public to 0)
     * @return OK (status code 200)
     * @see AcmeApi#getAccountOrders
     */
    public ResponseEntity<Object> getAccountOrders(Long accountId,
                                                   String realm,
                                                   String body,
                                                   String cursor,
                                                   MultiValueMap<String, String> headers) {

        String resourceUrl = targetUrl + "acct/{accountId}/orders";
        checkRealm(realm, resourceUrl);
        LOG.debug("forwarding {}/acct/{}/orders", realm, accountId);
        ResponseEntity<Object> responseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm, accountId);

        ResponseEntity<Object> acmeResponseEntity = handleResponseEntity(responseEntity);
        return logResponseEntity(acmeResponseEntity);
    }

    /**
     * GET /acme/{realm}/authorization/{authorizationId}
     *
     * @param authorizationId (required)
     * @param realm           (required)
     * @return OK (status code 200)
     * @see AcmeApi#getAuthorization
     */
    public ResponseEntity<Object> getAuthorization(Long authorizationId,
                                                   String realm,
                                                   MultiValueMap<String, String> headers) {

        String resourceUrl = targetUrl + "authorization/{authorizationId}";
        checkRealm(realm, resourceUrl);
        LOG.debug("forwarding {}/authorization/{}", realm, authorizationId);
        ResponseEntity<Object> responseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.GET, buildHttpEntity(headers), Object.class, realm, authorizationId);

        ResponseEntity<Object> acmeResponseEntity = handleResponseEntity(responseEntity);
        return logResponseEntity(acmeResponseEntity);
    }

    /**
     * GET /acme/{realm}/cert/{certId}
     *
     * @param certId (required)
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#getCertificatePKIX
     */
    public ResponseEntity<Object> getCertificatePKIX(Long certId,
                                                     String realm,
                                                     MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "cert/{certId}";
        checkRealm(realm, resourceUrl);
        LOG.debug("forwarding {}/cert/{}", realm, certId);
        ResponseEntity<Object> responseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.GET, buildHttpEntity(headers), Object.class, realm, certId);

        ResponseEntity<Object> acmeResponseEntity = handleResponseEntity(responseEntity);
        return logResponseEntity(acmeResponseEntity);
    }


    /**
     * GET /acme/{realm}/challenge/{challengeId}
     *
     * @param challengeId (required)
     * @param realm       (required)
     * @return OK (status code 200)
     * @see AcmeApi#getChallenge
     */
    public ResponseEntity<Object> getChallenge(Long challengeId,
                                               String realm,
                                               MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "challenge/{challengeId}";
        checkRealm(realm, resourceUrl);
        LOG.debug("forwarding {}/challenge/{}", realm, challengeId);
        ResponseEntity<Object> responseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.GET, buildHttpEntity(headers), Object.class, realm, challengeId);

        ResponseEntity<Object> acmeResponseEntity = handleResponseEntity(responseEntity);
        return logResponseEntity(acmeResponseEntity);
    }

    /**
     * GET /acme/{realm}/directory
     *
     * @param realm (required)
     * @return OK (status code 200)
     * @see AcmeApi#getDirectory
     */
    public ResponseEntity<DirectoryResponse> getDirectory(String realm,
                                                          MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "directory";
        checkRealm(realm, resourceUrl);
        LOG.debug("forwarding GET {}/directory", realm);

        ResponseEntity<DirectoryResponse> directoryResponseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.GET, buildHttpEntity(headers), DirectoryResponse.class, realm);

        return ResponseEntity.status(directoryResponseEntity.getStatusCode())
            .headers(filterResponseHttpHeaders(directoryResponseEntity))
            .body(directoryResponseEntity.getBody());

    }

    /**
     * POST /acme/{realm}/directory
     *
     * @param realm (required)
     * @return OK (status code 200)
     * @see AcmeApi#getDirectory1
     */
    public ResponseEntity<DirectoryResponse> getDirectoryPost(String realm,
                                                              MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "directory";
        checkRealm(realm, resourceUrl);
        LOG.debug("forwarding POST {}/directory", realm);
        ResponseEntity<DirectoryResponse> directoryResponseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers), DirectoryResponse.class, realm);

        return ResponseEntity.status(directoryResponseEntity.getStatusCode())
            .headers(filterResponseHttpHeaders(directoryResponseEntity))
            .body(directoryResponseEntity.getBody());
    }

    /**
     * POST /acme/{realm}/order/{orderId}
     *
     * @param orderId (required)
     * @param realm   (required)
     * @param body    (required)
     * @return OK (status code 200)
     * @see AcmeApi#postAsGetOrder
     */
    public ResponseEntity<Object> postAsGetOrder(Long orderId,
                                                 String realm,
                                                 String body,
                                                 MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "order/{orderId}";
        checkRealm(realm, resourceUrl);
        LOG.debug("forwarding {}/order/{}", realm, orderId);
        ResponseEntity<Object> responseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm, orderId);

        ResponseEntity<Object> acmeResponseEntity = handleResponseEntity(responseEntity);
        return logResponseEntity(acmeResponseEntity);
    }

    /**
     * POST /acme/{realm}/authorization/{authorizationId}
     *
     * @param authorizationId (required)
     * @param realm           (required)
     * @param body            (required)
     * @return OK (status code 200)
     * @see AcmeApi#postAuthorization
     */
    public ResponseEntity<Object> postAuthorization(Long authorizationId,
                                                    String realm,
                                                    String body,
                                                    MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "authorization/{authorizationId}";
        checkRealm(realm, resourceUrl);
        LOG.debug("forwarding {}/authorization/{}", realm, authorizationId);
        ResponseEntity<Object> responseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm, authorizationId);

        ResponseEntity<Object> acmeResponseEntity = handleResponseEntity(responseEntity);
        return logResponseEntity(acmeResponseEntity);
    }

    /**
     * POST /acme/{realm}/challenge/{challengeId}
     *
     * @param challengeId (required)
     * @param realm       (required)
     * @param body        (required)
     * @return OK (status code 200)
     * @see AcmeApi#postChallenge
     */
    public ResponseEntity<Object> postChallenge(Long challengeId,
                                                String realm,
                                                String body,
                                                MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "challenge/{challengeId}";
        checkRealm(realm, resourceUrl);
        LOG.debug("forwarding {}/challenge/{}", realm, challengeId);
        ResponseEntity<Object> responseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm, challengeId);

        ResponseEntity<Object> acmeResponseEntity = handleResponseEntity(responseEntity);
        return logResponseEntity(acmeResponseEntity);
    }

    /**
     * POST /acme/{realm}/cert/{certId}
     *
     * @param contentType (required)
     * @param certId      (required)
     * @param realm       (required)
     * @param body        (required)
     * @param accept      (optional, public to application/pem-certificate-chain)
     * @return OK (status code 200)
     * @see AcmeApi#retrieveCertificate
     */
    public ResponseEntity<?> retrieveCertificate(String contentType,
                                                 Long certId,
                                                 String realm,
                                                 String body,
                                                 String accept,
                                                 MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "cert/{certId}";
        checkRealm(realm, resourceUrl);

        String mediaType = headers.getFirst(HttpHeaders.ACCEPT);
        LOG.debug("forwarding {}/cert/{} with media type {}", realm, certId, mediaType);

        if ("application/pkix-cert".equals(mediaType)) {
            ResponseEntity<byte[]> responseEntity =
                restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), byte[].class, realm, certId);

            return logResponseEntity(
                ResponseEntity.status(responseEntity.getStatusCode())
                    .headers(filterResponseHttpHeaders(responseEntity))
                    .body(responseEntity.getBody()));

        } else {
            ResponseEntity<String> responseEntity =
                restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), String.class, realm, certId);

            return logResponseEntity(
                ResponseEntity.status(responseEntity.getStatusCode())
                .headers(filterResponseHttpHeaders(responseEntity))
                .body(responseEntity.getBody()));
        }
    }

    /**
     * POST /acme/{realm}/cert/revoke
     *
     * @param realm (required)
     * @param body  (required)
     * @return OK (status code 200)
     * @see AcmeApi#revokeCertificate
     */
    public ResponseEntity<?> revokeCertificate(String realm,
                                               String body,
                                               MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "cert/revoke";
        checkRealm(realm, resourceUrl);
        LOG.debug("forwarding {}/cert/revoke", realm);
        ResponseEntity<Object> responseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm);

        ResponseEntity<Object> acmeResponseEntity = handleResponseEntity(responseEntity);
        return logResponseEntity(acmeResponseEntity);
    }

    /**
     * POST /acme/{realm}/acct/{accountId}
     *
     * @param accountId (required)
     * @param realm     (required)
     * @param body      (required)
     * @return OK (status code 200)
     * @see AcmeApi#updateAccount
     */
    public ResponseEntity<?> updateAccount(Long accountId,
                                           String realm,
                                           String body,
                                           MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "acct/{accountId}";
        checkRealm(realm, resourceUrl);
        LOG.debug("forwarding {}/acct/{}", realm, accountId);
        ResponseEntity<Object> responseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(headers, body), Object.class, realm, accountId);

        ResponseEntity<Object> acmeResponseEntity = handleResponseEntity(responseEntity);
        return logResponseEntity(acmeResponseEntity);
    }

    /**
     * GET /acme/{realm}/newNonce
     *
     * @param realm (required)
     * @return OK (status code 200)
     * @see AcmeApi#viaGet
     */
    public ResponseEntity<String> viaGet(String realm,
                                         MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "newNonce";
        checkRealm(realm, resourceUrl);
        ResponseEntity<String> responseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.GET, buildHttpEntity(), String.class, realm);
        LOG.debug("forwarding GET {}/newNonce : {}", realm, responseEntity.getHeaders().getFirst(REPLAY_NONCE_HEADER));

        return logResponseEntity( ResponseEntity.noContent().headers(filterResponseHttpHeaders(responseEntity))
            .cacheControl(noStore()).build());
    }

    /**
     * POST /acme/{realm}/newNonce
     *
     * @param realm (required)
     * @return OK (status code 200)
     * @see AcmeApi#viaPost
     */
    public ResponseEntity<String> viaPost(String realm,
                                          MultiValueMap<String, String> headers) {
        String resourceUrl = targetUrl + "newNonce";
        checkRealm(realm, resourceUrl);
        ResponseEntity<String> responseEntity =
            restTemplate.exchange(resourceUrl, HttpMethod.POST, buildHttpEntity(), String.class, realm);
        LOG.debug("forwarding POST {}/newNonce: {}", realm, responseEntity.getHeaders().getFirst(REPLAY_NONCE_HEADER));

        return logResponseEntity( ResponseEntity.noContent().headers(filterResponseHttpHeaders(responseEntity))
            .cacheControl(noStore()).build());
    }

    /**
     * HEAD /acme/{realm}/newNonce
     *
     * @param realm (required)
     * @return OK (status code 200)
     * @see AcmeApi#viaHead
     */
    public ResponseEntity<String> viaHead(String realm, MultiValueMap<String, String> headers) {

        String resourceUrl = targetUrl + "newNonce";
        checkRealm(realm, resourceUrl);

        LOG.debug("forwarding HEAD {}/newNonce", realm);

        ResponseEntity<String> stringResponseEntity = viaPost(realm, headers);

        headers.addAll(filterResponseHttpHeaders(stringResponseEntity));

        return new ResponseEntity<>("",
            headers,
            stringResponseEntity.getStatusCode());
    }

    <T> ResponseEntity<T> logResponseEntity(final ResponseEntity<T> responseEntity) {

        HttpHeaders headers = responseEntity.getHeaders();
        for( String header : responseEntity.getHeaders().keySet()) {
            LOG.debug("response header {} : {}", header, headers.get(header));
        }

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
        headers.add(HEADER_X_CA3S_PROXY_ID, String.valueOf(requestProxyConfig.getConfig().getId()));


        return new HttpEntity<>(headers);
    }

    HttpEntity buildHttpEntity(final MultiValueMap<String, String> callerHeaders) {

        HttpHeaders headers = processHttpHeaders(callerHeaders);

        return new HttpEntity<>(headers);
    }

    HttpEntity buildHttpEntity(final MultiValueMap<String, String> callerHeaders, final String body) {

        HttpHeaders headers = processHttpHeaders(callerHeaders);

        return new HttpEntity<>(body, headers);
    }

    private HttpHeaders processHttpHeaders(MultiValueMap<String, String> callerHeaders) {
        for(String headerName: callerHeaders.keySet()){
            if( callerHeaders.containsKey(headerName)) {
                LOG.debug("incoming header '{}' with value(s) '{}'", headerName, String.join(",", callerHeaders.get(headerName)));
            }else{
                LOG.debug("incoming header '{}' without value", headerName);
            }
        }

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(callerHeaders);

        headers.add(HEADER_X_CA3S_FORWARDED_HOST, fromCurrentRequestUri().build().toString());
        headers.add(HEADER_X_CA3S_PROXY_ID, String.valueOf(requestProxyConfig.getConfig().getId()));
        return headers;
    }


    private ResponseEntity<Object> handleResponseEntity(ResponseEntity<Object> responseEntity) {

        Object body = responseEntity.getBody();
        if( body == null){
            return ResponseEntity.status(responseEntity.getStatusCode())
                .headers(filterResponseHttpHeaders(responseEntity))
                .build();
        }else {
            return ResponseEntity.status(responseEntity.getStatusCode())
                .headers(filterResponseHttpHeaders(responseEntity))
                .body(responseEntity.getBody());
        }
    }

    private HttpHeaders filterResponseHttpHeaders(ResponseEntity responseEntity) {
        HttpHeaders responseHeaders = responseEntity.getHeaders();
        HttpHeaders relevantHeaders = new HttpHeaders();
        for( String headerName: responseHeaders.keySet()){
            if( forwardResponseHeaderSet.contains(headerName)){
                List<String> headerList = responseHeaders.get(headerName);
                if( headerList == null){
                    headerList = new ArrayList<>();
                }
                relevantHeaders.addAll(headerName, headerList);
            }
        }
        return relevantHeaders;
    }

    void checkRealm(final String realm, final String resourceUrl) {
        RemoteRequestProxyConfigView remoteRequestProxyConfigView = requestProxyConfig.getConfig();
        if (Arrays.stream(remoteRequestProxyConfigView.getAcmeRealmArr()).noneMatch(r -> Objects.equals(r, realm))) {

            String msg = "unexpected realm '" + realm + "' calling '" + resourceUrl + "'";
            LOG.warn(msg);
            final ProblemDetail problem = new ProblemDetail(REALM_DOES_NOT_EXIST, msg,
                HttpStatus.BAD_REQUEST, "", NO_INSTANCE);
            throw new AcmeProblemException(problem);
        }
    }

}

package de.trustable.ca3s.acmeproxy.web.api;


import de.trustable.ca3s.acmeproxy.service.api.dto.DirectoryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.NativeWebRequest;

import javax.annotation.Generated;
import java.util.Optional;

/**
 * A delegate to be called by the {@link AcmeApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-03-27T13:33:05.045561200+02:00[Europe/Berlin]")
public interface AcmeApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /acme/{realm}/acct/changeKey
     *
     * @param realm  (required)
     * @param body  (required)
     * @param headers
     * @return OK (status code 200)
     * @see AcmeApi#changeKey
     */
    default ResponseEntity<Object> changeKey(String realm,
                                             String body, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /acme/{realm}/newOrder
     *
     * @param realm  (required)
     * @param body  (required)
     * @param headers
     * @return OK (status code 200)
     * @see AcmeApi#consumingPostedJws1
     */
    default ResponseEntity<Object> consumingPostedJws1(String realm,
                                                       String body, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /acme/{realm}/newAccount
     *
     * @param realm  (required)
     * @param body  (required)
     * @param headers
     * @return OK (status code 200)
     * @see AcmeApi#consumingPostedJws2
     */
    default ResponseEntity<Object> consumingPostedJws2(String realm,
                                                       String body, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /acme/{realm}/order/finalize/{orderId}
     *
     * @param orderId  (required)
     * @param realm  (required)
     * @param body  (required)
     * @param headers
     * @return OK (status code 200)
     * @see AcmeApi#finalizeOrder
     */
    default ResponseEntity<Object> finalizeOrder(Long orderId,
                                                 String realm,
                                                 String body, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /acme/{realm}/acct/{accountId}/orders
     *
     * @param accountId  (required)
     * @param realm  (required)
     * @param body  (required)
     * @param cursor  (optional, default to 0)
     * @param headers
     * @return OK (status code 200)
     * @see AcmeApi#getAccountOrders
     */
    default ResponseEntity<Object> getAccountOrders(Long accountId,
                                                    String realm,
                                                    String body,
                                                    String cursor, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /acme/{realm}/authorization/{authorizationId}
     *
     * @param authorizationId  (required)
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#getAuthorization
     */
    default ResponseEntity<Object> getAuthorization(Long authorizationId,
        String realm, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /acme/{realm}/cert/{certId}
     *
     * @param certId  (required)
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#getCertificatePKIX
     */
    default ResponseEntity<Object> getCertificatePKIX(Long certId,
        String realm, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /acme/{realm}/challenge/{challengeId}
     *
     * @param challengeId  (required)
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#getChallenge
     */
    default ResponseEntity<Object> getChallenge(Long challengeId,
        String realm, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /acme/{realm}/directory
     *
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#getDirectory
     */
    default ResponseEntity<DirectoryResponse> getDirectory(String realm, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * POST /acme/{realm}/directory
     *
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#getDirectory1
     */
    default ResponseEntity<DirectoryResponse> getDirectoryPost(String realm, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * POST /acme/{realm}/order/{orderId}
     *
     * @param orderId  (required)
     * @param realm  (required)
     * @param body  (required)
     * @param headers
     * @return OK (status code 200)
     * @see AcmeApi#postAsGetOrder
     */
    default ResponseEntity<Object> postAsGetOrder(Long orderId,
                                                  String realm,
                                                  String body, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /acme/{realm}/authorization/{authorizationId}
     *
     * @param authorizationId  (required)
     * @param realm  (required)
     * @param body  (required)
     * @param headers
     * @return OK (status code 200)
     * @see AcmeApi#postAuthorization
     */
    default ResponseEntity<Object> postAuthorization(Long authorizationId,
                                                     String realm,
                                                     String body, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /acme/{realm}/challenge/{challengeId}
     *
     * @param challengeId  (required)
     * @param realm  (required)
     * @param body  (required)
     * @param headers
     * @return OK (status code 200)
     * @see AcmeApi#postChallenge
     */
    default ResponseEntity<Object> postChallenge(Long challengeId,
                                                 String realm,
                                                 String body, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /acme/{realm}/cert/{certId}
     *
     * @param contentType  (required)
     * @param certId  (required)
     * @param realm  (required)
     * @param body  (required)
     * @param accept  (optional, default to application/pem-certificate-chain)
     * @param headers
     * @return OK (status code 200)
     * @see AcmeApi#retrieveCertificate
     */
    default ResponseEntity<?> retrieveCertificate(String contentType,
                                                  Long certId,
                                                  String realm,
                                                  String body,
                                                  String accept, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /acme/{realm}/cert/revoke
     *
     * @param realm  (required)
     * @param body  (required)
     * @param headers
     * @return OK (status code 200)
     * @see AcmeApi#revokeCertificate
     */
    default ResponseEntity<?> revokeCertificate(String realm,
                                                String body, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /acme/{realm}/acct/{accountId}
     *
     * @param accountId  (required)
     * @param realm  (required)
     * @param body  (required)
     * @param headers
     * @return OK (status code 200)
     * @see AcmeApi#updateAccount
     */
    default ResponseEntity<?> updateAccount(Long accountId,
                                            String realm,
                                            String body, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /acme/{realm}/newNonce
     *
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#viaGet
     */
    default ResponseEntity<String> viaGet(String realm, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /acme/{realm}/newNonce
     *
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#viaGet
     */
    default ResponseEntity<String> viaPost(String realm, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * HEAD /acme/{realm}/newNonce
     *
     * @param realm  (required)
     * @return OK (status code 200)
     * @see AcmeApi#viaHead
     */
    default ResponseEntity<String> viaHead(String realm, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}

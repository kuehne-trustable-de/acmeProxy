/*^
  ===========================================================================
  ACME server
  ===========================================================================
  Copyright (C) 2017-2018 DENIC eG, 60329 Frankfurt am Main, Germany
  ===========================================================================
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
  ===========================================================================
*/

package de.trustable.ca3s.acmeproxy.web.rest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import de.trustable.ca3s.acmeproxy.dto.AcmeRequestContainer;

/*
 * 7.4.  Applying for Certificate Issuance

 The client begins the certificate issuance process by sending a POST
 request to the server's new-order resource.  The body of the POST is
 a JWS object whose JSON payload is a subset of the order object
 defined in Section 7.1.3, containing the fields that describe the
 certificate to be issued:

 identifiers (required, array of object):  An array of identifier
    objects that the client wishes to submit an order for.

    type (required, string):  The type of identifier.

    value (required, string):  The identifier itself.

 notBefore (optional, string):  The requested value of the notBefore
    field in the certificate, in the date format defined in [RFC3339].

 notAfter (optional, string):  The requested value of the notAfter
    field in the certificate, in the date format defined in [RFC3339].

 POST /acme/new-order HTTP/1.1
 Host: example.com
 Content-Type: application/jose+json

 {
   "protected": base64url({
     "alg": "ES256",
     "kid": "https://example.com/acme/acct/evOfKhNU60wg",
     "nonce": "5XJ1L3lEkMG7tR6pA00clA",
     "url": "https://example.com/acme/new-order"
   }),
   "payload": base64url({
     "identifiers": [
       { "type": "dns", "value": "example.com" }
     ],
     "notBefore": "2016-01-01T00:04:00+04:00",
     "notAfter": "2016-01-08T00:04:00+04:00"
   }),
   "signature": "H6ZXtGjTZyUnPeKn...wEA4TklBdh3e454g"
 }

 The server MUST return an error if it cannot fulfill the request as
 specified, and MUST NOT issue a certificate with contents other than
 those requested.  If the server requires the request to be modified
 in a certain way, it should indicate the required changes using an
 appropriate error type and description.

If the server is willing to issue the requested certificate, it
 responds with a 201 (Created) response.  The body of this response is
 an order object reflecting the client's request and any
 authorizations the client must complete before the certificate will
 be issued.

 HTTP/1.1 201 Created
 Replay-Nonce: MYAuvOpaoIiywTezizk5vw
 Location: https://example.com/acme/order/TOlocE8rfgo

 {
   "status": "pending",
   "expires": "2016-01-01T00:00:00Z",

   "notBefore": "2016-01-01T00:00:00Z",
   "notAfter": "2016-01-08T00:00:00Z",

   "identifiers": [
     { "type": "dns", "value": "example.com" },
   ],

   "authorizations": [
     "https://example.com/acme/authz/PAniVnsZcis",
   ],

   "finalize": "https://example.com/acme/order/TOlocE8rfgo/finalize"
 }

 The order object returned by the server represents a promise that if
 the client fulfills the server's requirements before the "expires"
 time, then the server will be willing to finalize the order upon
 request and issue the requested certificate.  In the order object,
 any authorization referenced in the "authorizations" array whose
 status is "pending" represents an authorization transaction that the
 client must complete before the server will issue the certificate
 (see Section 7.5).  If the client fails to complete the required
 actions before the "expires" time, then the server SHOULD change the
 status of the order to "invalid" and MAY delete the order resource.
 Clients MUST NOT make any assumptions about the sort order of
 "identifiers" or "authorizations" elements in the returned order
 object.

 Once the client believes it has fulfilled the server's requirements,
 it should send a POST request to the order resource's finalize URL.
 The POST body MUST include a CSR:

 csr (required, string):  A CSR encoding the parameters for the
    certificate being requested [RFC2986].  The CSR is sent in the
    base64url-encoded version of the DER format.  (Note: Because this
    field uses base64url, and does not include headers, it is
    different from PEM.).

 POST /acme/order/TOlocE8rfgo/finalize HTTP/1.1
 Host: example.com
 Content-Type: application/jose+json

 {
   "protected": base64url({
     "alg": "ES256",
     "kid": "https://example.com/acme/acct/evOfKhNU60wg",
     "nonce": "MSF2j2nawWHPxxkE3ZJtKQ",
     "url": "https://example.com/acme/order/TOlocE8rfgo/finalize"
   }),
   "payload": base64url({
     "csr": "MIIBPTCBxAIBADBFMQ...FS6aKdZeGsysoCo4H9P",
   }),
   "signature": "uOrUfIIk5RyQ...nw62Ay1cl6AB"
 }

 The CSR encodes the client's requests with regard to the content of
 the certificate to be issued.  The CSR MUST indicate the exact same
 set of requested identifiers as the initial new-order request.
 Identifiers of type "dns" MUST appear either in the commonName
 portion of the requested subject name, or in an extensionRequest
 attribute [RFC2985] requesting a subjectAltName extension, or both.
 (These identifiers may appear in any sort order.)  Specifications
 that define new identifier types must specify where in the
 certificate signing request these identifiers can appear.

 A request to finalize an order will result in error if the CA is
 unwilling to issue a certificate corresponding to the submitted CSR.
 For example:

 o  If the order indicated does not have status "ready"

 o  If the CSR and order identifiers differ

 o  If the account is not authorized for the identifiers indicated in
    the CSR

 o  If the CSR requests extensions that the CA is not willing to
    include

 In such cases, the problem document returned by the server SHOULD use
 error code "badCSR", and describe specific reasons the CSR was
 rejected in its "details" field.  After returning such an error, the
 server SHOULD leave the order in the "ready" state, to allow the
 client to submit a new finalize request with an amended CSR.

 A request to finalize an order will return the order to be finalized.
 The client should begin polling the order by sending a POST-as-GET
 request to the order resource to obtain its current state.  The
 status of the order will indicate what action the client should take:

 o  "invalid": The certificate will not be issued.  Consider this
    order process abandoned.

 o  "pending": The server does not believe that the client has
    fulfilled the requirements.  Check the "authorizations" array for
    entries that are still pending.

 o  "ready": The server agrees that the requirements have been
    fulfilled, and is awaiting finalization.  Submit a finalization
    request.

 o  "processing": The certificate is being issued.  Send a POST-as-GET
    request after the time given in the "Retry-After" header field of
    the response, if any.

 o  "valid": The server has issued the certificate and provisioned its
    URL to the "certificate" field of the order.  Download the
    certificate.


 HTTP/1.1 200 OK
 Replay-Nonce: CGf81JWBsq8QyIgPCi9Q9X
 Location: https://example.com/acme/order/TOlocE8rfgo

 {
   "status": "valid",
   "expires": "2015-12-31T00:17:00.00-09:00",

   "notBefore": "2015-12-31T00:17:00.00-09:00",
   "notAfter": "2015-12-31T00:17:00.00-09:00",

   "identifiers": [
     { "type": "dns", "value": "example.com" },
     { "type": "dns", "value": "www.example.com" }
   ],

   "authorizations": [
     "https://example.com/acme/authz/PAniVnsZcis",
     "https://example.com/acme/authz/r4HqLzrSrpI"
   ],

   "finalize": "https://example.com/acme/order/TOlocE8rfgo/finalize",

   "certificate": "https://example.com/acme/cert/mAt3xBGaobw"
 }

 */

@Controller
@RequestMapping("/acme/{realm}/newOrder")
public class NewOrderController extends ACMEController {

	private static final Logger LOG = LoggerFactory.getLogger(NewOrderController.class);

	@Autowired
	private WebSocketUtil wsUtil;

	@RequestMapping(method = POST, consumes = APPLICATION_JOSE_JSON_VALUE)
	public ResponseEntity<?> consumingPostedJoseJson(@RequestBody final String requestBody,
			@PathVariable final String realm) {
		LOG.info("Received consumingPostedJoseJson request ");

		AcmeRequestContainer arc = new AcmeRequestContainer("newOrder", realm, requestBody);

		ResponseEntity<?> re = wsUtil.send(arc);
		return re;
	}

	@RequestMapping(method = POST, consumes = APPLICATION_JWS_VALUE)
	public ResponseEntity<?> consumingPostedJws(@RequestBody final String requestBody,
			@PathVariable final String realm) {
		LOG.info("Received consumingPostedJws request ");

		AcmeRequestContainer arc = new AcmeRequestContainer("newOrder", realm, requestBody);

		ResponseEntity<?> re = wsUtil.send(arc);
		return re;
	}

}

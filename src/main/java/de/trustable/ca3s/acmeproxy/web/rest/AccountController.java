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

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.trustable.ca3s.acmeproxy.dto.AcmeRequestContainer;


/*
 * 7.1.2.  Account Objects

   An ACME account resource represents a set of metadata associated with
   an account.  Account resources have the following structure:

   status (required, string):  The status of this account.  Possible
      values are: "valid", "deactivated", and "revoked".  The value
      "deactivated" should be used to indicate client-initiated
      deactivation whereas "revoked" should be used to indicate server-
      initiated deactivation.  (See Section 7.1.6)

   contact (optional, array of string):  An array of URLs that the
      server can use to contact the client for issues related to this
      account.  For example, the server may wish to notify the client
      about server-initiated revocation or certificate expiration.  For
      information on supported URL schemes, see Section 7.3

   termsOfServiceAgreed (optional, boolean):  Including this field in a
      new-account request, with a value of true, indicates the client's
      agreement with the terms of service.  This field is not updateable
      by the client.

   orders (required, string):  A URL from which a list of orders
      submitted by this account can be fetched via a POST-as-GET
      request, as described in Section 7.1.2.1.

   {
     "status": "valid",
     "contact": [
       "mailto:cert-admin@example.com",
       "mailto:admin@example.com"
     ],
     "termsOfServiceAgreed": true,
     "orders": "https://example.com/acme/acct/evOfKhNU60wg/orders"
   }

 */
@Controller
@RequestMapping("/acme/{realm}/acct")
public class AccountController extends ACMEController {

  private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

  @Autowired
  private WebSocketUtil wsUtil;


	@RequestMapping(value = "/changeKey", method = POST, consumes = APPLICATION_JOSE_JSON_VALUE)
	public ResponseEntity<?> changeKey( @RequestBody final String requestBody, @PathVariable final String realm) {

		LOG.info("Received change key request");
		
    	AcmeRequestContainer arc = new AcmeRequestContainer("acct/changeKey", realm, requestBody);
    	
    	ResponseEntity<?> re = wsUtil.send(arc);
    	
      	return re;

	}


	@RequestMapping(value = "/{accountId}", method = POST, consumes = APPLICATION_JOSE_JSON_VALUE)
	public ResponseEntity<?> updateAccount(@PathVariable final long accountId, @PathVariable final String realm, @RequestBody final String requestBody) {

		LOG.info("Received updateAccount request for '{}'", accountId);

    	AcmeRequestContainer arc = new AcmeRequestContainer("acct", realm, requestBody);
    	arc.addPathVariable("accountId", accountId);
    	
    	ResponseEntity<?> re = wsUtil.send(arc);
    	
      	return re;

	}

	/*
  private ResponseEntity<?> updateAccount(final Account.Id accountId, final String requestBody, final AccountRequest
          accountRequest) {
    LOG.info("Updating ACCOUNT '{}': {}", accountId, requestBody);
    final AccountRequest.Payload payload = accountRequest.convert(requestBody).getPayload();
    final Optional<Account> optUpdatedAccount = accountDAO.updateWith(accountId, payload.getContacts());
    
    if (optUpdatedAccount.isPresent()) {
      return ok(optUpdatedAccount.get());
    }

    final HttpHeaders additionalHeaders = new HttpHeaders();
    additionalHeaders.setContentType(APPLICATION_PROBLEM_JSON);
    return status(NOT_FOUND).headers(additionalHeaders).body(new ProblemDetail(ACCOUNT_DOES_NOT_EXIST, "Account is " +
            "unknown", NOT_FOUND,
            "Account ID '" + accountId + "'", NO_INSTANCE));
  }
*/
  
/*
  7.1.2.1.  Orders List

  Each account object includes an "orders" URL from which a list of
  orders created by the account can be fetched via POST-as-GET request.
  The result of the request MUST be a JSON object whose "orders" field
  is an array of URLs, each identifying an order belonging to the
  account.  The server SHOULD include pending orders, and SHOULD NOT
  include orders that are invalid in the array of URLs.  The server MAY
  return an incomplete list, along with a Link header field with a
  "next" link relation indicating where further entries can be
  acquired.

HTTP/1.1 200 OK
Content-Type: application/json
Link: <https://example.com/acme/acct/evOfKhNU60wg/orders?cursor=2>;rel="next"

{
 "orders": [
   "https://example.com/acme/order/TOlocE8rfgo",
   "https://example.com/acme/order/4E16bbL5iSw",
    
   "https://example.com/acme/order/neBHYLfw0mg"
 ]
}
*/
  @RequestMapping(value = "/{accountId}/orders", method = POST, consumes = APPLICATION_JOSE_JSON_VALUE)
  public ResponseEntity<?> getAccountOrders (@PathVariable final long accountId, @PathVariable final String realm, @RequestParam(name="cursor", defaultValue = "0") String cursorParam,  @RequestBody final String requestBody) {
	  
		LOG.info("Received getAccountOrders request for '{}', cursor '{}'", accountId, cursorParam);
		
    	AcmeRequestContainer arc = new AcmeRequestContainer("acct/orders", realm, requestBody);
    	arc.addPathVariable("accountId", accountId);
    	arc.addPathVariable("cursorParam", cursorParam);
    	
    	ResponseEntity<?> re = wsUtil.send(arc);
    	
      	return re;

  }

}

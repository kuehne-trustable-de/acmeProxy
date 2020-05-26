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

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import de.trustable.ca3s.acmeproxy.dto.AcmeRequestContainer;

@Controller
@RequestMapping("/acme/{realm}/cert")
public class ACMECertificateController extends ACMEController {

    private static final Logger LOG = LoggerFactory.getLogger(ACMECertificateController.class);

    @Autowired
    private WebSocketUtil wsUtil;


    @RequestMapping(value = "/{certId}", method = GET)
    public ResponseEntity<?> getCertificatePKIX(@PathVariable final long certId, 
    		@RequestHeader(name="Accept",  defaultValue=APPLICATION_PEM_CERT_CHAIN_VALUE)  final String accept,
    		@PathVariable final String realm) {

		LOG.info("Received certificate request for id {}", certId);
		
    	AcmeRequestContainer arc = new AcmeRequestContainer("cert-get", realm);
    	arc.addPathVariable("certId", certId);
    	arc.addPathVariable("accept", accept);
    	
    	ResponseEntity<?> re = wsUtil.send(arc);
      	return re;
    }



	@RequestMapping(value = "/revoke", method = POST, consumes = APPLICATION_JOSE_JSON_VALUE)
	public ResponseEntity<?> revokeCertificate(@RequestBody final String requestBody,
			@PathVariable final String realm) {

		LOG.info("Received revoke request for certificate ");
		
    	AcmeRequestContainer arc = new AcmeRequestContainer("cert/revoke", realm, requestBody);
    	
    	ResponseEntity<?> re = wsUtil.send(arc);
      	return re;
	}

    /**
     * Retrieve a certificate as a PEM structure containing the complete chain
     * Bug in certbot: content type set to 'application/pkix-cert' despite containing a JWT in the request body, as usual.
     * 
     * 
     */
	@RequestMapping(value = "/{certId}", method = POST, consumes = {APPLICATION_JOSE_JSON_VALUE, APPLICATION_PKIX_CERT_VALUE})
	public ResponseEntity<?>  retrieveCertificate(@RequestBody final String requestBody,
			@RequestHeader(name="Accept",  defaultValue=APPLICATION_PEM_CERT_CHAIN_VALUE) final String accept, 
			@RequestHeader("Content-Type") final String contentType, 
			@PathVariable final long certId,
			@PathVariable final String realm) {

    	AcmeRequestContainer arc = new AcmeRequestContainer("cert", realm, requestBody);
    	arc.addPathVariable("certId", certId);
    	arc.addPathVariable("contentType", contentType);
    	arc.addPathVariable("accept", accept);
    	
    	ResponseEntity<?> re = wsUtil.send(arc);
      	return re;
	}

}

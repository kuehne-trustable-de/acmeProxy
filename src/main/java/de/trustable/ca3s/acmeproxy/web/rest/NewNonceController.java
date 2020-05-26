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
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import de.trustable.ca3s.acmeproxy.dto.AcmeRequestContainer;

@Controller
@RequestMapping("/acme/{realm}/newNonce")
/**
 * Produces <a href="https://de.wikipedia.org/wiki/Nonce">Nonces</a> containing
 * 16 random bytes followed by 8 bytes from epoch seconds.
 */
public class NewNonceController extends ACMEController {

	@Autowired
	private WebSocketUtil wsUtil;

	private static final Logger LOG = LoggerFactory.getLogger(NewNonceController.class);

	@RequestMapping(method = { GET, POST, HEAD })
	public ResponseEntity<?> getNonce(@PathVariable final String realm) {
		LOG.info("New NONCE requested");

		AcmeRequestContainer arc = new AcmeRequestContainer("newNonce", realm);

		ResponseEntity<?> re = wsUtil.send(arc);
		return re;

	}

}

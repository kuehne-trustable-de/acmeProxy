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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
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

@Controller
@RequestMapping("/acme/{realm}/challenge")
public class ChallengeController extends ACMEController {

	private static final Logger LOG = LoggerFactory.getLogger(ChallengeController.class);

	@Autowired
	private WebSocketUtil wsUtil;

	@RequestMapping(value = "/{challengeId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getChallenge(@PathVariable final long challengeId, @PathVariable final String realm) {

		LOG.debug("Received Challenge request ");

		AcmeRequestContainer arc = new AcmeRequestContainer("challenge-get", realm);
		arc.addPathVariable("challengeId", challengeId);

		ResponseEntity<?> re = wsUtil.send(arc);
		return re;
	}

	@RequestMapping(value = "/{challengeId}", method = POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JOSE_JSON_VALUE)
	public ResponseEntity<?> postChallenge(@RequestBody final String requestBody, @PathVariable final long challengeId,
			@PathVariable final String realm) {

		LOG.debug("Received Challenge request ");

		AcmeRequestContainer arc = new AcmeRequestContainer("challenge", realm, requestBody);
		arc.addPathVariable("challengeId", challengeId);

		ResponseEntity<?> re = wsUtil.send(arc);
		return re;
	}

}

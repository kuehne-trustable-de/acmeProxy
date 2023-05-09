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

package de.trustable.ca3s.acmeproxy.config.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.trustable.ca3s.acmeproxy.service.dto.problem.AcmeProblemException;
import de.trustable.ca3s.acmeproxy.service.dto.problem.ProblemDetail;
import de.trustable.ca3s.acmeproxy.web.api.AcmeApiImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.net.URI;

import static de.trustable.ca3s.acmeproxy.web.api.AcmeApiImpl.ACME_ERROR_URI_NAMESPACE;
import static de.trustable.ca3s.acmeproxy.web.api.AcmeApiImpl.NO_INSTANCE;

/**
 * Handle the restification of ACME exception centrally
 *
 * @author kuehn
 *
 */
@ControllerAdvice
@Immutable
public final class AcmeProblemAdvice {

    transient Logger LOG = LoggerFactory.getLogger(AcmeProblemAdvice.class);

    public static final MediaType APPLICATION_PROBLEM_JSON = new MediaType("application", "problem+json");

    public static final URI URI_INTERNAL_PROBLEM = URI.create(ACME_ERROR_URI_NAMESPACE + ":serverInternal");

    final private ObjectMapper mapper;

    public AcmeProblemAdvice(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @ExceptionHandler(value = AcmeProblemException.class)
	public ResponseEntity<ProblemDetail> respondTo(final AcmeProblemException exception) {

        LOG.info("Handling AcmeProblemException: {}", exception.getMessage());
		final ProblemDetail problem = exception.getProblem();
		final HttpStatus status = problem.getStatus();
		return ResponseEntity.status(status).contentType(APPLICATION_PROBLEM_JSON).body(problem);
	}


    @ExceptionHandler(value = HttpClientErrorException.class)
    public ResponseEntity<ProblemDetail> respondTo(final HttpClientErrorException exception) {

        LOG.info("Handling HttpClientErrorException: {}", exception.getMessage());

        try {
            final ProblemDetail problem = mapper.readValue(exception.getResponseBodyAsByteArray(), ProblemDetail.class);
            final HttpStatus status = problem.getStatus();
            return ResponseEntity.status(status).contentType(APPLICATION_PROBLEM_JSON).body(problem);
        } catch (IOException e) {
            final ProblemDetail problem2 = new ProblemDetail(URI_INTERNAL_PROBLEM,
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR, "", NO_INSTANCE);
            final HttpStatus status = problem2.getStatus();
            return ResponseEntity.status(status).contentType(APPLICATION_PROBLEM_JSON).body(problem2);
        }
    }
    @ExceptionHandler(value = HttpServerErrorException.class)
    public ResponseEntity<ProblemDetail> respondTo(final HttpServerErrorException exception) {

        LOG.info("Handling HttpServerErrorException: {}", exception.getMessage());

        try {
            final ProblemDetail problem = mapper.readValue(exception.getResponseBodyAsByteArray(), ProblemDetail.class);
            final HttpStatus status = problem.getStatus();
            return ResponseEntity.status(status).contentType(APPLICATION_PROBLEM_JSON).body(problem);
        } catch (IOException e) {
            final ProblemDetail problem2 = new ProblemDetail(URI_INTERNAL_PROBLEM,
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR, "", NO_INSTANCE);
            final HttpStatus status = problem2.getStatus();
            return ResponseEntity.status(status).contentType(APPLICATION_PROBLEM_JSON).body(problem2);
        }
    }

}

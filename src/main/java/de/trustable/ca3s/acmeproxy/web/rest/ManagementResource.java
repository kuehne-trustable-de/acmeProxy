package de.trustable.ca3s.acmeproxy.web.rest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for connecting and websocket initialization
 */
@RestController
@RequestMapping("/management")
public class ManagementResource {

	private static final Logger LOG = LoggerFactory.getLogger(ManagementResource .class);

	@RequestMapping(value = "/connect", method = POST)
    public ResponseEntity<?> connect() {

		LOG.debug("received connect request");
		
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

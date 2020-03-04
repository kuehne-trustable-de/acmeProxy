package de.trustable.ca3s.acmeproxy.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Service;

import de.trustable.ca3s.acmeproxy.dto.AcmeRequestContainer;
import de.trustable.ca3s.acmeproxy.dto.AcmeResponseContainer;

@Service
public class WebSocketUtil {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketUtil.class);

	public ResponseEntity<?> send(AcmeRequestContainer arc) {
		
//		AcmeResponseContainer arespc = callServer(arc);
		AcmeResponseContainer arespc = new AcmeResponseContainer(309);
		
		return fromAcmeResponseContainer(arespc);

	}
	
	ResponseEntity<?> fromAcmeResponseContainer(AcmeResponseContainer arespc){

		int status = arespc.getStatus();
		
		LOG.debug("forwarding a response of status {}");
		
		BodyBuilder builder = ResponseEntity.status(status);
		for( String headerName: arespc.getHeaders().keySet()) {
			String headerValue = arespc.getHeaders().get(headerName);
			builder.header(headerName, headerValue);
			LOG.debug("forwarding response header'{}' with value '{}' ", headerName, headerValue);
		}
		
		if( (arespc.getContent() != null) && (arespc.getContent().trim().length() > 0 )) {
			LOG.debug("forwarding a response with content {}", arespc.getContent());
			return builder.body(arespc.getContent());
		}else {
			LOG.debug("forwarding a response without content");
			return builder.build();
		}
	}
}

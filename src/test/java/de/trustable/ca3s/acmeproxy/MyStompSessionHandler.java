package de.trustable.ca3s.acmeproxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import de.trustable.ca3s.acmeproxy.dto.AcmeRequestContainer;
import de.trustable.ca3s.acmeproxy.dto.AcmeResponseContainer;

import java.lang.reflect.Type;

/**
 * This class is an implementation for <code>StompSessionHandlerAdapter</code>.
 * Once a connection is established, We subscribe to /topic/messages and 
 * send a sample message to server.
 * 
 * @author Kalyan
 *
 */
public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    private Logger logger = LogManager.getLogger(MyStompSessionHandler.class);

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
    	
        logger.info("New session established : " + session.getSessionId());
        
//        session.subscribe("/topic/messages", this);
//        logger.info("Subscribed to /topic/messages");
        
        session.subscribe("/topic/acmeProxied", this);
        logger.info("Subscribed to /topic/acmeProxied");
        
//        session.subscribe("/user/topic/acmeProxied", this);
//        logger.info("Subscribed to /user/topic/acmeProxied");
        
//        session.subscribe("/topic/acmeProxy", this);
//        logger.info("Subscribed to /topic/acmeProxy");
        
        
        session.send("/requestProxy/acmeProxy", getSampleMessage());
        logger.info("Message sent to websocket server");
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.error("Got an exception", exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return AcmeResponseContainer.class;
    }

    
    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
    	
    	
        logger.info("Received message for {} ", headers.getDestination());
    	if( payload instanceof AcmeResponseContainer) {
	    	AcmeResponseContainer msg = (AcmeResponseContainer) payload;
	        logger.info("Received : " + msg.getStatus() + " for : " + msg.getContent());
    	}else {
	        logger.info("Received payload class {}", payload.getClass().getName());
	        logger.info("Received payload {}", payload.toString());
    	}
    }

    /**
     * A sample message instance.
     * @return instance of <code>Message</code>
     */
    private AcmeRequestContainer getSampleMessage() {
    	AcmeRequestContainer msg = new AcmeRequestContainer("path", "realm");
        return msg;
    }
}
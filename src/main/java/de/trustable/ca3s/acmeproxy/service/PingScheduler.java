package de.trustable.ca3s.acmeproxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.trustable.ca3s.acmeproxy.dto.AcmeRequestContainer;
import de.trustable.ca3s.acmeproxy.dto.ConnectInfo;

@Component
public class PingScheduler {

	transient Logger LOG = LoggerFactory.getLogger(PingScheduler.class);

    @Autowired
    private SimpMessagingTemplate template;
    
	@Scheduled(fixedDelay = 30000)
	public void runMinute() {

		LOG.info("PingScheduler minute, sendig to {}", template.getDefaultDestination());

    	AcmeRequestContainer msg = new AcmeRequestContainer("directory", "realm");
		template.convertAndSend("/requestProxy/acmeProxy", msg);	
		
        ConnectInfo ci = new ConnectInfo();
        template.convertAndSend("/requestProxy/connected", ci);	
	}

}

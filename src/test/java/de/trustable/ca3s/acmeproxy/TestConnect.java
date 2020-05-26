package de.trustable.ca3s.acmeproxy;

import java.util.Scanner;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class TestConnect {

	static String URL = "ws://localhost:8085/acmeProxy";

	public static void main(String[] args) {

		System.out.println("TestConnect started");
		
		WebSocketClient client = new StandardWebSocketClient();
		 
		WebSocketStompClient stompClient = new WebSocketStompClient(client);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		 
		StompSessionHandler sessionHandler = new MyStompSessionHandler();
		stompClient.connect(URL, sessionHandler);

		System.out.println("TestConnect connected ...");

		new Scanner(System.in).nextLine(); // Don't close immediately.
	}

}


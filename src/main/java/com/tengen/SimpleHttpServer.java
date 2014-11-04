package com.tengen;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;


@SuppressWarnings("restriction")
public class SimpleHttpServer {

	public static void main(String[] args) {
		com.sun.net.httpserver.HttpServer server;
		try {
			server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress("localhost", 9999),0);
			ExecutorService service = Executors.newFixedThreadPool(20);
			server.setExecutor(service);
			server.createContext("/test", new RequestHandler());
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

@SuppressWarnings("restriction")
class RequestHandler implements com.sun.net.httpserver.HttpHandler{

	@Override
	public void handle(HttpExchange request) throws IOException {
		request.sendResponseHeaders( 200, "<html><head></head><body><h3>Test Server Begins</h3></body></html>".getBytes().length );
		request.getResponseBody().write("<html><head></head><body><h3>Test Server Begins</h3></body></html>".getBytes());
		request.getResponseBody().close();
	}
	
}

package com.gurkan.socketprogramming.core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.net.ssl.HttpsURLConnection;

/**
 * Custom connection protocol with network connection
 * @author gilleez
 */
public class GServer extends GContext implements GServerFunctions{

	private static ServerSocket server = null;
	private CustomThreadFactory threadFactory = new CustomThreadFactory();
	public static int BUFFER_SIZE = 2048;
	
	public GServer() {
		init();
	}
	
	public void init(){
		try {
			server = new ServerSocket(9999);
			ExecutorService service = Executors.newFixedThreadPool(10,threadFactory);
			while(true){
				Socket acceptedSocket = server.accept();
				service.execute(new WorkerThread(acceptedSocket));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ServerSocket getServer() {
		return server;
	}

	public static void main(String[] args) {
		GServer openServer = new GServer();
	}
}

class GContext {
	
	private ConcurrentHashMap<String,Object> map = new ConcurrentHashMap<String, Object>();
	
}

class CustomThreadFactory implements ThreadFactory{

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r);
	}
	
}

class WorkerThread implements Runnable{

	private Socket socket;
	
	public WorkerThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		new RequestHandlerImpl().service(socket);
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

enum RequestTypes{
	GET,POST
}


class RequestHandlerImpl extends RequestHandler{

	private OutputStream writer = null;
	private InputStream reader = null;
	private Properties headers = new Properties();
	private String context;
	private String HTTPVersion;
	
	private void storeHeaders(String line){
		String []keyAndValue = line.split(":");
		headers.put(keyAndValue[0].trim(), keyAndValue[1].trim());
	}
	
	@Override
	public void service(Socket acceptedSocket) {
		try {
			//System.out.println("Connection Accepted : "+Thread.currentThread().getName());
			reader = acceptedSocket.getInputStream();
			writer = acceptedSocket.getOutputStream();
			
			byte[] arr = new byte[GServer.BUFFER_SIZE];
			int length = reader.read(arr);
			//After some time it gives -1 to check connection
			if(length != -1){
				String input = new String(arr, 0, length);
				String [] lines = input.split("\r\n");
				
				for (int i = 1; i < lines.length; i++) {
					storeHeaders(lines[i]);
				}
				String []split = lines[0].split(" ");
				context = split[1];
				HTTPVersion = split[2];
				if(split[0].equals(RequestTypes.GET.name())){
					doGet();
				}
				if(split[0].equals(RequestTypes.POST.name())){
					doPost();
				}
				
			}else {
				writer.write(-1);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {
				writer.close();
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}

	}

	@Override
	public void doGet() {
		System.out.println("DO GET TRIGGERED");
		System.out.println(context);
		StringBuffer buf = new StringBuffer("HTTP/1.1 200 OK\r\n");
		buf.append("Date: ").append(new Date().toString()).append("\r\n");
		buf.append("Content-Type: ").append("text/html\r\n");
		
		StringBuffer content = new StringBuffer("<html><head></head><body>GALIP MERHABA</body></html>");
		buf.append("Content-Lenght: ").append(content.toString().getBytes().length).append("\r\n");
		buf.append("\r\n");
		buf.append(content.toString());
		//System.out.println(buf.toString());
		try {
			writer.write(buf.toString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void doPost() {
		System.out.println("DO POST TRIGGERED");
		System.out.println(context);
	}

	public InputStream getReader() {
		return reader;
	}

	public void setReader(InputStream reader) {
		this.reader = reader;
	}

	public OutputStream getWriter() {
		return writer;
	}

	public void setWriter(OutputStream writer) {
		this.writer = writer;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getHTTPVersion() {
		return HTTPVersion;
	}

	public void setHTTPVersion(String hTTPVersion) {
		HTTPVersion = hTTPVersion;
	}
	
}

interface GServerFunctions {
	public void init();
}

abstract class RequestHandler {
	public abstract void service(Socket socket);
	public abstract void doGet();
	public abstract void doPost();
}
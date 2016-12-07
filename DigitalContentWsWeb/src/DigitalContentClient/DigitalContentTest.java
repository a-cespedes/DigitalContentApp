package DigitalContentClient;

import java.io.*;
import java.net.*;

import javax.ws.rs.core.MediaType;

public class DigitalContentTest {
	
	public static void main(String[] args) {
		
	String key = "";
	
	//Test for method PUT
		
	try{
		URL url = new URL ("http://localhost:8080/DigitalContentWsWeb/DigitalContentWs/PUT/contents");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("PUT");
		conn.setRequestProperty("Content-Type", "application/json");
		
		String input = "{\"description\":\"fast furious 1\",\"path\":\"/contents/f&f1.mp4\",\"owner\":\"pepito\"}";

		OutputStream os = conn.getOutputStream();
		os.write(input.getBytes());
		os.flush();

		if(conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {

			throw new RuntimeException("Failed: HTTP errorcode: " + conn.getResponseCode()); }

		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String output;

		while((output = br.readLine()) != null){
			
			key = output;

			System.out.println("\nPUT. Response: " + output );

		}
		
	}
		
	catch (IOException e) { 
		e.printStackTrace();
	}
	
	//Test for method GET
	
	try{
		URL url = new URL ("http://localhost:8080/DigitalContentWsWeb/DigitalContentWs/GET/contents/" + key);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
		if(conn.getResponseCode() != 200) {

			throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode()); }

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output;

			while((output = br.readLine()) != null){

				System.out.println("\nGET. Response: " + output );
			}
			conn.disconnect();

		}
	
	catch (IOException e) { 
			e.printStackTrace();
	}
	
	//Test for method DELETE
	
	try{
		URL url = new URL ("http://localhost:8080/DigitalContentWsWeb/DigitalContentWs/DELETE/contents/" + key);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("DELETE");

		if(conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {

			throw new RuntimeException("Failed: HTTP errorcode: " + conn.getResponseCode()); }

		else{

			System.out.println("\nDELETE. Response : OK.");

		}
		
	}
		
	catch (IOException e) { 
		e.printStackTrace();
	}
	
	//Test for method GET by owner
	
	try{
		URL url = new URL ("http://localhost:8080/DigitalContentWsWeb/DigitalContentWs/GET/contents/owner/pepito" );
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
		if(conn.getResponseCode() != 200) {

			throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode()); }

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output;

			while((output = br.readLine()) != null){

				System.out.println("\nGET. Response: " + output );
			}
			conn.disconnect();

		}
		
		catch (IOException e) { 
				e.printStackTrace();
		}

	
	//Test for method GET by description
	
	try{
		URL url = new URL ("http://localhost:8080/DigitalContentWsWeb/DigitalContentWs/GET/contents/search/furious");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
		if(conn.getResponseCode() != 200) {

			throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode()); }

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output;

			while((output = br.readLine()) != null){

				System.out.println("\nGET. Response: " + output );
			}
			conn.disconnect();

	}
			
	catch (IOException e) { 
		e.printStackTrace();
	}
	}
	
}

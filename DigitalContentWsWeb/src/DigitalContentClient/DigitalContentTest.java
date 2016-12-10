package DigitalContentClient;

import java.io.*;
import java.net.*;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.ws.rs.core.MediaType;

public class DigitalContentTest {
	
	public static void main(String[] args) {
		
	String key = UUID.randomUUID().toString().replaceAll("-", "");;
	
	//Test for method POST
		
	try{
		URL url = new URL ("http://localhost:8080/DigitalContentWsWeb/DigitalContentWs/contents");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		jsonBuilder.add("key", key);
		jsonBuilder.add("path","/contents/f&f1.mp4");
		jsonBuilder.add("owner","pepito");
		jsonBuilder.add("description","fast furious 1");
		JsonObject o = jsonBuilder.build();
		
		OutputStream os = conn.getOutputStream();
		JsonWriter jsonWriter = Json.createWriter(os);
		
		jsonWriter.writeObject(o);
		jsonWriter.close();

		if(conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {

			throw new RuntimeException("Failed: HTTP errorcode: " + conn.getResponseCode()); }

		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String output;

		while((output = br.readLine()) != null){
			
			key = output;

			System.out.println("\nPOST. Response: " + output );

		}
		
	}
		
	catch (IOException e) { 
		e.printStackTrace();
	}
	
	//Test for method GET
	
	try{
		URL url = new URL ("http://localhost:8080/DigitalContentWsWeb/DigitalContentWs/contents/" + key);
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
	
	//Test for method PUT changing description
	
	try{
		URL url = new URL ("http://localhost:8080/DigitalContentWsWeb/DigitalContentWs/contents/" + key);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("PUT");
		conn.setRequestProperty("Content-Type", "application/json");
		
		String input =  "fast furious 3";

		OutputStream os = conn.getOutputStream();
		os.write(input.getBytes());
		os.flush();
		
		if(conn.getResponseCode() != 200) {

			throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode()); }

		else{

			System.out.println("\nPUT. Response : OK.");

		}

	}
					
	catch (IOException e) { 
		e.printStackTrace();
	}
	
	//Test for method DELETE
	
	try{
		URL url = new URL ("http://localhost:8080/DigitalContentWsWeb/DigitalContentWs/contents/" + key);
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
		URL url = new URL ("http://localhost:8080/DigitalContentWsWeb/DigitalContentWs/contents/owner/pepito" );
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
		URL url = new URL ("http://localhost:8080/DigitalContentWsWeb/DigitalContentWs/contents/search/furious");
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

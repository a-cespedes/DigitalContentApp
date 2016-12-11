package RMIApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.json.*;
import javax.ws.rs.core.MediaType;
import javax.wsdl.extensions.http.HTTPUrlEncoded;

public class WSAccessImpl implements WSAccess{
	
	private String wsUrl = "http://localhost:8080/DigitalContentWsWeb/DigitalContentWs/contents/";
	
	@Override
	public ContentInfo getContent(String key) throws IOException{
		try{
    		URL url = new URL (wsUrl + key);
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    		conn.setRequestMethod("GET");
    		conn.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
    		if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {    		
	    		JsonReader jr = Json.createReader(conn.getInputStream());
	    		JsonObject o = jr.readObject();
	    		jr.close();
	    		conn.disconnect();
	    		ContentInfo ci = new ContentInfo();
	    		ci.setKey(o.getString("key"));
	    		ci.setDescription(o.getString("description"));
	    		ci.setOwner(o.getString("owner"));
	    		ci.setPath(o.getString("path"));
	    		return ci;
    		}
    		else if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND){
				
				return null;	
				
			}
			else{

				throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode()); 
				
			}

    	}
    	
    	catch (IOException e) { 
    		e.printStackTrace();
    	}
		return null;
	}

	@Override
	public String upload(ContentInfo ci) throws IOException {
		try{
			URL url = new URL (wsUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
			jsonBuilder.add("key", ci.getKey());
			jsonBuilder.add("path",ci.getPath());
			jsonBuilder.add("owner",ci.getOwner());
			jsonBuilder.add("description", ci.getDescription());
			JsonObject o = jsonBuilder.build();

			OutputStream os = conn.getOutputStream();
			JsonWriter jsonWriter = Json.createWriter(os);
			
			jsonWriter.writeObject(o);
			jsonWriter.close();

			if(conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {

				throw new RuntimeException("Failed: HTTP errorcode: " + conn.getResponseCode()); }

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output;
			String key = "";
			while((output = br.readLine()) != null){
				
				key = output;

			}
			return key;
			
		}
			
		catch (IOException e) { 
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public void delete(String key) throws IOException {
		try{
			URL url = new URL (wsUrl + key);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("DELETE");

			if(conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
				throw new RuntimeException("Failed: HTTP errorcode: " + conn.getResponseCode()); 			
			}		
		}
			
		catch (IOException e) { 
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void modifyContent(String key,String description) throws IOException {
		try{
			URL url = new URL (wsUrl + key);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");

			OutputStream os = conn.getOutputStream();
			os.write(description.getBytes());
			os.flush();
			
			if(conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode()); 		
			}

		}
						
		catch (IOException e) { 
			e.printStackTrace();
		}
		
	}
	
	@Override
	public Set<ContentInfo> list(String owner) throws IOException {
		try{
			URL url = new URL (wsUrl + "owner/" + owner );
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
			
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

				JsonReader jr = Json.createReader(conn.getInputStream());
				JsonArray a = jr.readArray();
				Set<ContentInfo> results = new HashSet<>();		
				for(int i = 0; i < a.size();i++){
					JsonObject o = a.getJsonObject(i);	
					ContentInfo ci = new ContentInfo();
		    		ci.setKey(o.getString("key"));
		    		ci.setDescription(o.getString("description"));
		    		ci.setOwner(o.getString("owner"));
		    		ci.setPath(o.getString("path"));
		    		results.add(ci);
				}
				jr.close();
	    		conn.disconnect();
					
	    		return results;
			}
    		
    		else if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND){
				
				return new HashSet<>();	
				
			}
			else{

				throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode()); 
				
			}

		}
			
		catch (IOException e) { 
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public Set<ContentInfo> search(String word) throws IOException {
		try{
			URL url = new URL (wsUrl + "search/" + word );
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
				JsonReader jr = Json.createReader(conn.getInputStream());
				JsonArray a = jr.readArray();
				Set<ContentInfo> results = new HashSet<>();		
				for(int i = 0; i < a.size();i++){
					JsonObject o = a.getJsonObject(i);	
					ContentInfo ci = new ContentInfo();
		    		ci.setKey(o.getString("key"));
		    		ci.setDescription(o.getString("description"));
		    		ci.setOwner(o.getString("owner"));
		    		ci.setPath(o.getString("path"));
		    		results.add(ci);
				}
				jr.close();
	    		conn.disconnect();
					
	    		return results;
			}
			else if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND){
				
				return new HashSet<>();	
				
			}
			else{

				throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode()); 
				
			}
		}
			
		catch (IOException e) { 
			e.printStackTrace();
		}
		return null;
	}

}

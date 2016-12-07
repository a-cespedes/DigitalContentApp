package DigitalContentWsWeb;

import javax.enterprise.context.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import com.ibm.wsdl.util.IOUtils;

import java.io.IOException;
import java.nio.channels.IllegalSelectorException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import DigitalContentInfo.DigitalContent;


@RequestScoped
@Path("")
@Produces({ "application/xml", "application/json" })
@Consumes({ "application/xml", "application/json" })
public class DigitalContentWebService {
	
	@Path("/GET/contents/{id}")
	@GET
	@Produces("application/json")
	public Response getContents(@PathParam("id") String key){		
		try{
			Connection connection = getDataSourceConnection();
			Statement st = connection.createStatement();
			ResultSet r = st.executeQuery("select c.* from public.contents c where c.content_key = '" + key + "'");
			DigitalContent dc = new DigitalContent();
			if(!r.isBeforeFirst()){
				return Response.status(404).entity("Not Found").build();
			}
			else{
				while(r.next()){		
					dc.setKey(r.getString("content_key"));
					dc.setPath(r.getString("path"));
					dc.setDescription(r.getString("description"));
					dc.setOwner(r.getString("content_owner"));
				}
				connection.close();
				st.close();
				return Response.status(200).entity(dc).build();
			}
		}catch(SQLException ex){
			return Response.status(500).entity("SQL error").build();
		}catch (IllegalStateException ex){
			return Response.status(500).entity("DataSource error.").build();
		}
	}
	
	
	@PUT
	@Path("/PUT/contents")
	@Consumes("application/json")
	@Produces("application/json")
	public Response upload(DigitalContent dc){
		try{
			Connection connection = getDataSourceConnection();
			Statement st = connection.createStatement();
			String key = UUID.randomUUID().toString().replaceAll("-", "");;
			st.executeUpdate("INSERT INTO public.contents (content_key,path,description,content_owner) "
					+ "VALUES('" + key + "', " 
					+ " '" + dc.getPath() + "', " 
					+ " '" + dc.getDescription() + "', " 
					+ " '" + dc.getOwner() + "')");
			connection.close();
			st.close();
			return Response.status(201).entity(key).build();
			
		}catch(SQLException ex){
			return Response.status(500).entity("SQL error").build();
		}catch (IllegalStateException ex){
			return Response.status(500).entity("DataSource error.").build();
		}
		
	}
	
	@Path("/DELETE/contents/{id}")
	@DELETE
	@Produces("application/json")
	public Response delete(@PathParam("id") String key){
		try{
			Connection connection = getDataSourceConnection();
			Statement st = connection.createStatement();
			st.executeUpdate("delete from public.contents c where c.content_key = '" + key + "'");
			connection.close();
			st.close();
			return Response.status(204).build();
		}catch(SQLException ex){
			return Response.status(500).entity("SQL error").build();
		}catch (IllegalStateException ex){
			return Response.status(500).entity("DataSource error.").build();
		}
	}
	
	@Path("/GET/contents/owner/{name}")
	@GET
	@Produces("application/json")
	public Response getContentsByOwner(@PathParam("name") String owner){		
		try{
			Connection connection = getDataSourceConnection();
			Statement st = connection.createStatement();
			ResultSet r = st.executeQuery("select c.* from public.contents c where c.content_owner = '" + owner + "'");
			if(!r.isBeforeFirst()){
				return Response.status(404).entity("Not Found").build();
			}
			else{
				Set<DigitalContent> results = new HashSet<>();
				while(r.next()){		
					DigitalContent dc = new DigitalContent();
					dc.setKey(r.getString("content_key"));
					dc.setPath(r.getString("path"));
					dc.setDescription(r.getString("description"));
					dc.setOwner(r.getString("content_owner"));
					results.add(dc);
				}
				connection.close();
				st.close();
				return Response.status(200).entity(results).build();
			}
		}catch(SQLException ex){
			return Response.status(500).entity("SQL error").build();
		}catch (IllegalStateException ex){
			return Response.status(500).entity("DataSource error.").build();
		}
	}
	
	@Path("/GET/contents/search/{word}")
	@GET
	@Produces("application/json")
	public Response getContentsBySearch(@PathParam("word") String description){		
		try{
			Connection connection = getDataSourceConnection();
			Statement st = connection.createStatement();
			ResultSet r = st.executeQuery("select c.* from public.contents c where c.description LIKE '%" + description + "%'");
			if(!r.isBeforeFirst()){
				return Response.status(404).entity("Not Found").build();
			}
			else{
				Set<DigitalContent> results = new HashSet<>();
				while(r.next()){		
					DigitalContent dc = new DigitalContent();
					dc.setKey(r.getString("content_key"));
					dc.setPath(r.getString("path"));
					dc.setDescription(r.getString("description"));
					dc.setOwner(r.getString("content_owner"));
					results.add(dc);
				}
				connection.close();
				st.close();
				return Response.status(200).entity(results).build();
			}
		}catch(SQLException ex){
			return Response.status(500).entity("SQL error").build();
		}catch (IllegalStateException ex){
			return Response.status(500).entity("DataSource error.").build();
		}
	}
	
	private Connection getDataSourceConnection() throws IllegalStateException{
		try{
			InitialContext cxt = new InitialContext();
			DataSource ds = (DataSource) cxt.lookup("java:/PostgresXADS");			
			return ds.getConnection();
		}catch(NamingException | SQLException ex){
			throw new IllegalStateException();
		}
	}
}

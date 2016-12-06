package DigitalContentWsWeb;

import javax.enterprise.context.RequestScoped;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
		String result;
		try{
			InitialContext cxt = new InitialContext();
			if (cxt != null){
				DataSource ds = (DataSource) cxt.lookup("java:/PostgresXADS");			
				if (ds == null) result ="KO.DataSource";
				else{
					Connection connection = ds.getConnection();
					Statement st = connection.createStatement();
					ResultSet r = st.executeQuery("select c.* from public.contents c where c.content_key = '" + key + "'");
					DigitalContent dc = new DigitalContent();
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
			}
			}
			catch(Exception e){ e.printStackTrace(); result = "KO.SQL " + e.getMessage(); }
		return Response.status(500).build();
	}
	
	
	@PUT
	@Path("/PUT/contents")
	@Consumes("application/json")
	@Produces("application/json")
	public Response upload(DigitalContent dc){
		String result;
		try{
			InitialContext cxt = new InitialContext();
			if (cxt != null){
				DataSource ds = (DataSource) cxt.lookup("java:/PostgresXADS");			
				if (ds == null) result ="KO.DataSource";
				else{
					Connection connection = ds.getConnection();
					Statement st = connection.createStatement();
					String key = UUID.randomUUID().toString().replaceAll("-", "");;
					int res = st.executeUpdate("INSERT INTO public.contents (content_key,path,description,content_owner) "
							+ "VALUES('" + key + "', " 
							+ " '" + dc.getPath() + "', " 
							+ " '" + dc.getDescription() + "', " 
							+ " '" + dc.getOwner() + "')");
					connection.close();
					st.close();
					return Response.status(201).entity(key).build();
				}
			}
			}
			catch(Exception e){ e.printStackTrace(); result = "KO.SQL " + e.getMessage(); }
		return Response.status(500).entity(dc).build();
	}

}

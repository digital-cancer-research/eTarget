package org.digitalecmt.etarget.rest;

import javax.ws.rs.POST;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.text.StringEscapeUtils;
import org.digitalecmt.etarget.API;
import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dao.PersonDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.gson.Gson;


@Path("/discussMTB")
public class DiscussMTB {
	private static final Logger log = Logger.getLogger(FMTumour.class.getName());
	private ApplicationContext appContext;
	
	public DiscussMTB() {
		appContext= new AnnotationConfigApplicationContext(TargetConfiguration.class);
	}
	
	@Path("{person_id}")
	@Produces({MediaType.APPLICATION_JSON})
	@POST
	public Response postDiscussMTB(String value, @HeaderParam("x-ms-client-principal-name") String loggedInUserID , @PathParam("person_id") String personID) {
		loggedInUserID = StringEscapeUtils.unescapeHtml4(loggedInUserID);
		if (!new API().isUserPermittedEndpoint(loggedInUserID, "DiscussMTB")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return Response.status(403).entity("User not permitted to nominate patients for MTB.").build();
		}
		Map<String,String> result = new HashMap<String,String>();
		log.info("DiscussMTB called " + personID + " " + value);
		try {
			Integer person_id = Integer.parseInt(personID);
			PersonDAO person = appContext.getBean(PersonDAO.class);
			if(person.updateDisussionStatus(person_id, (value.toLowerCase().compareTo("true")==0?Boolean.TRUE:Boolean.FALSE))==1) {
				result.put("success", "true");
			} else {
				result.put("success", "false");
			}
			return Response.status(200).entity(new Gson().toJson(result)).build();
			
		} catch(NumberFormatException ex) {
			return Response.status(404).entity("person_id must be a number").build();
		}
	}
}

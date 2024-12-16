package org.digitalecmt.etarget.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.digitalecmt.etarget.API;
import org.springframework.context.ApplicationContext;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

@Path("/patientletter")
public class PatientLetter {
	private static final Logger log = Logger.getLogger(PatientLetter.class.getName());

	static ResourceBundle resource;
	static String storageConnection;
	static String containerName;
	
	public PatientLetter() {
		resource = ResourceBundle.getBundle("config");
	}
	
	@Path("/{person_id}/{provider}/{type}/{timepoint}/{version}")
	@Produces("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
	@GET
	public Response getPatientLetter(
			@HeaderParam("x-ms-client-principal-name") String loggedInUserID, 
			@PathParam("person_id") Integer person_id, 
			@PathParam("provider") String provider,
			@PathParam("type") String type,
			@PathParam("timepoint") Integer timepoint,
			@PathParam("version") Integer version)
	{
		if (!new API().isUserPermittedEndpoint(loggedInUserID, "GeneratePatientLetter")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return Response.status(403).entity("User not permitted to generate patient letters.").build();
		}
		
		log.info("patient letter for " + person_id + " " + provider +" "+ type +" " + timepoint +" " + version);
		if(version==0) {
			log.warning("version 0 requested, invalid request assume version 1");
			version=1;
		}
		try {
			String filename=person_id+"_"+provider+"_"+type+"_"+timepoint+"v"+version+ ".docx";
			
			
			org.digitalecmt.etarget.support.PatientLetter pl = new org.digitalecmt.etarget.support.PatientLetter();
			XWPFDocument document=pl.generatePatientLetter(person_id, provider, type, timepoint, version);

			StreamingOutput outStream = new StreamingOutput() {
				@Override
				public void write(final OutputStream output) throws IOException, WebApplicationException {
						document.write(output);
				}
			};
			return Response.ok(outStream).type("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
					.header("Content-Disposition", "filename=\"" + filename + "\"") // optional
					.build();

		} catch (Exception ex) {
			log.log(Level.SEVERE, "storage exception ", ex);
		}
		return Response.serverError().build();
		
	}
	

}

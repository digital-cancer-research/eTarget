package org.digitalecmt.etarget.rest;

/*-
 * #%L
 * eTarget Maven Webapp
 * %%
 * Copyright (C) 2017 - 2021 digital ECMT
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.text.StringEscapeUtils;
import org.digitalecmt.etarget.API;
import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dao.IHCReportDAO;
import org.digitalecmt.etarget.dao.TumourNgsDAO;
import org.digitalecmt.etarget.dbentities.TumourNgs;
import org.digitalecmt.etarget.dbentities.TumourNgsExt;
import org.digitalecmt.etarget.support.Formater;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.gson.Gson;

@Path("/IHCReport")
public class IHCReport {
	private static final Logger log = Logger.getLogger(IHCReport.class.getName());
	private ApplicationContext appContext;
	
	public IHCReport() {
		appContext = new AnnotationConfigApplicationContext(TargetConfiguration.class);
	}
	
	@Path("{person_id}")
	@Produces({MediaType.APPLICATION_JSON})
	@GET
	public Response getIHCReport(@HeaderParam("x-ms-client-principal-name") String loggedInUserID , @PathParam("person_id") String personID) {
		Integer personIDint = Integer.parseInt(personID);
		loggedInUserID = StringEscapeUtils.unescapeHtml4(loggedInUserID);
		if (!new API().isUserPermittedEndpoint(loggedInUserID, "IHCReport")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			return Response.status(403).entity("User not permitted to get IHC Reports.").build();
		}
		log.info("FMTumor called " + personID);
		
		IHCReportDAO ihcReportDAO = appContext.getBean(IHCReportDAO.class);
		TumourNgsDAO tumourDAO = appContext.getBean(TumourNgsDAO.class);
		List<org.digitalecmt.etarget.dbentities.IHCReport> ihcReport = ihcReportDAO.getIHCReport(personIDint);
		List<TumourNgs> specimen = tumourDAO.findFMTumourByPersonID(personIDint);
		TumourNgsExt.adjustBaselines(specimen);
		Map<String,Map<String,Map<String,String>>> ihcReportFormated= Formater.formatIHCReport(ihcReport,specimen);
		Formater.cleanIHCTumour(ihcReportFormated);
		return Response.status(200).entity(new Gson().toJson(ihcReportFormated)).build();
	}
}

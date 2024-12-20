package org.digitalecmt.etarget;

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


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.digitalecmt.etarget.dao.EditLockDAO;
import org.digitalecmt.etarget.dao.GeneSubsetDAO;
import org.digitalecmt.etarget.dao.NgsLibDAO;
import org.digitalecmt.etarget.dbentities.GeneSubsetExt;
import org.digitalecmt.etarget.dbentities.NgsLibExt;
import org.digitalecmt.etarget.support.Formater;

import com.google.gson.Gson;

public class CTDNANGSSubset extends API {

	private static final Logger log = Logger.getLogger(CTDNANGSSubset.class.getName());
	
	// Vars
	String personID;
	int personIDint;
	String userID;

	// Constructor
	public CTDNANGSSubset(String loggedInUserID, String dbPersonID) {
		userID = loggedInUserID;
		personID = dbPersonID;
		personIDint = Integer.parseInt(dbPersonID);
	}
	
	public String processRequest() {
		if (!super.isUserPermittedEndpoint(userID, "CTDNANGSSubset")) {
			// Stop the request if user doesn't have permission for this API or web
			// component
			Map<String,String> response = new HashMap<>();
	    	response.put("success", "false");
	    	response.put("error", "User not permitted to access: CTDNANGSSubset");
	    	return new Gson().toJson(response);
		}
		//lock patient
		EditLockDAO editLockDao = this.getContext().getBean(EditLockDAO.class);
		editLockDao.lock(userID, personIDint);
		//Get gene names
//		GeneNameDAO geneNameDao = this.getContext().getBean(GeneNameDAO.class);
//		List<GeneName> geneNames= geneNameDao.getGeneNamesByPersonID(personIDint);
		//Get the general info for bloods
		NgsLibDAO ngsLibDao = this.getContext().getBean(NgsLibDAO.class);
		List<NgsLibExt> results = NgsLibExt.getNgsLibExtList(ngsLibDao.findNgsLibByPersonID(personIDint));
		for(NgsLibExt entry: results) {
			System.out.println(entry.getDate_issued());
			entry.setTumourPanelName(ngsLibDao.findTumourPanelName(entry.getDate_issued()));
		}
		log.info("NGS Lib database search count" + results.size());
		Map<String,Object> nlm = Formater.getNgsLibMap(results);
		log.info(nlm.toString());
		
		//get the specific info for genes
		GeneSubsetDAO geneSubsetDao = this.getContext().getBean(GeneSubsetDAO.class);
		List<GeneSubsetExt> generesults = GeneSubsetExt.getGeneSubsetExtList(geneSubsetDao.findGeneSubsetByPersonID(personIDint));
		log.info("gen result size: " + generesults.size());
		
		nlm=Formater.addGeneSubset(generesults, nlm);
//		nlm.put("listGenes", Formater.getGeneNameList(geneNames));
		log.info(nlm.toString());
		return new Gson().toJson(nlm);
	}	
}


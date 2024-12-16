package org.digitalecmt.etarget.support;

import java.awt.Image;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.digitalecmt.etarget.config.TargetConfiguration;
import org.digitalecmt.etarget.dao.FoundationMedicineDAO;
import org.digitalecmt.etarget.dbentities.CopyNumberAlteration;
import org.digitalecmt.etarget.dbentities.FMSample;
import org.digitalecmt.etarget.dbentities.Rearrangement;
import org.digitalecmt.etarget.dbentities.ShortVariant;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class PatientLetter {

	private static final Logger log = Logger.getLogger(PatientLetter.class.getName());
	static ResourceBundle resource;
	static String storageConnection;
	static String containerName;
	private ApplicationContext appContext;
	private String logoURL="";

	public PatientLetter() {
		resource = ResourceBundle.getBundle("application");
		ResourceBundle config = ResourceBundle.getBundle("config");
		appContext = new AnnotationConfigApplicationContext(TargetConfiguration.class);
		logoURL = System.getProperty("user.dir")+"/"+config.getString("web.path")+"/"+resource.getString("application.patientLetterLogo");
		log.info("logoURL "+logoURL);
		log.info(System.getProperty("user.dir"));
	}

	// {person_id}/{provider}/{type}/{timepoint}/{version}
	public XWPFDocument generatePatientLetter(Integer person_id, String provider_short, String type, Integer timepoint,
			Integer version) {
		boolean hasAlterations = false;
		XWPFDocument document_alterations = new XWPFDocument();
		XWPFDocument document_no_alterations = new XWPFDocument();
		XWPFParagraph logo= document_alterations.createParagraph();
		logo.setAlignment(ParagraphAlignment.LEFT);
		XWPFRun logoRun = logo.createRun();
		try {
			logoRun.addPicture(new FileInputStream(logoURL),
					  XWPFDocument.PICTURE_TYPE_PNG, logoURL,
					  Units.toEMU(150), Units.toEMU(50));
			logoRun.addBreak();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		}
		XWPFParagraph logo2= document_no_alterations.createParagraph();
		logo2.setAlignment(ParagraphAlignment.LEFT);
		XWPFRun logoRun2 = logo2.createRun();
		try {
			logoRun2.addPicture(new FileInputStream(logoURL),
					  XWPFDocument.PICTURE_TYPE_PNG, logoURL,
					  Units.toEMU(150), Units.toEMU(50));
			logoRun2.addBreak();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
		}
		XWPFParagraph intro = document_alterations.createParagraph();
		intro.setAlignment(ParagraphAlignment.LEFT);
		XWPFRun introRun = intro.createRun();
		introRun.setText(
				"Thank you for your participation in the TARGET National study. I am writing to update you about the genomic testing results. "
						+ "We have analysed the following and have identified the following genomic changes in your sample: ");
		introRun.setFontSize(11);
		introRun.setFontFamily("Calibri");
		introRun.addBreak();

		XWPFParagraph sample = document_alterations.createParagraph();

		XWPFParagraph headline = document_alterations.createParagraph();
		XWPFRun headlineRun = headline.createRun();
		headlineRun.setText("Gene Alterations");
		headlineRun.setBold(true);

		XWPFParagraph alterations = document_alterations.createParagraph();
		alterations.setSpacingBetween(1.2);
		if (provider_short.toLowerCase().compareTo("fm") == 0 && type.compareTo("tumour") == 0) {
			Integer measurementGenePanelId = this.getFMTumourSample(person_id, timepoint, version, sample);
			hasAlterations = this.processFMAlterationData(measurementGenePanelId, alterations);
		}
		else if(provider_short.toLowerCase().compareTo("fm") == 0 && type.compareTo("blood") == 0) {
			Integer measurementGenePanelId = this.getFMBloodSample(person_id, timepoint, version, sample);
			hasAlterations = this.processFMAlterationData(measurementGenePanelId, alterations);
		}
		if (hasAlterations) {
			XWPFParagraph nextSteps = document_alterations.createParagraph();
			XWPFRun nextStepsRun = nextSteps.createRun();
			nextStepsRun.addBreak();
			nextStepsRun.setText(
					"We will be in contact with you separately if any of these genomic changes could make you eligible for a clinical trial. "
					+ "Otherwise, these findings do not affect any other plans that we may have for you. You do remain on our general waiting "
					+ "list should a clinical trial slot become available on a study that we think may be of benefit to you. ");
			nextStepsRun.addBreak();
			nextStepsRun.addBreak();
			nextStepsRun.setText("We choose to send you this information by letter should you wish to show the results to any other doctors "
					+ "that you may see in relation to future treatment options.");
			nextStepsRun.addBreak();
			nextStepsRun.addBreak();
			nextStepsRun.setText("A copy of this letter has been sent to your GP and your treating oncologist.");
			nextStepsRun.addBreak();
			nextStepsRun.addBreak();
			nextStepsRun.setText("If you have any further questions regarding these results, please contact us on ");
			XWPFRun nextStepsRedRun = nextSteps.createRun();
			nextStepsRedRun.setText("[INSERT SITE CONTACT PHONE NUMBER HERE]");
			nextStepsRedRun.setColor("e7371e");
			XWPFRun nextStepsContRun = nextSteps.createRun();
			nextStepsContRun.setText(".");
			nextStepsContRun.addBreak();
			nextStepsContRun.addBreak();
			nextStepsContRun.setText("Yours sincerely,");
			nextStepsContRun.addBreak();
			nextStepsContRun.addBreak();
			nextStepsContRun.addBreak();
			nextStepsContRun.addBreak();
			XWPFRun nextStepsContRedRun = nextSteps.createRun();
			nextStepsContRedRun.setText("[Insert investigator/Research Nurse name] ");
			nextStepsContRedRun.setColor("e7371e");

		} else {
			generatePatientLetterNoAlterations(document_no_alterations, person_id, timepoint, version, provider_short, type);
		}

		if (hasAlterations) {
			return document_alterations;
		} else {
			return document_no_alterations;
		}

	}

	private Integer getFMBloodSample(Integer person_id, Integer timepoint, Integer version, XWPFParagraph paragraph) {
		FoundationMedicineDAO blood = appContext.getBean(FoundationMedicineDAO.class);
		List<FMSample> samples = blood.getBloodSamplesForPerson(person_id);
		FMSample sample=null;
		int v=1;
		for(FMSample sam : samples) {
			System.out.println(sam.getBaseline_number() + " " + sam.getMeasurement_gene_panel_id());
			if(sam.getBaseline_number()==timepoint) {
				if(v==version) {
					sample=sam;
				} else {
					v++;
				}
			}
		}
		if(sample==null) {
			return -1;
		}
		paragraph.setSpacingBetween(1.2);
		XWPFRun sampleRun = paragraph.createRun();
		sampleRun.setText("Blood sample from " + sample.getSpecimentDateFormatted());
		sampleRun.addBreak();
		sampleRun.setText("TMB: " + sample.getTmb_status() + "; " + sample.getTmb_score() + " " + sample.getTmb_unit());
		sampleRun.addBreak();
		String ms_status = "Cannot Be Determined";
		if (sample.getMicrosatellite_instability_status().toUpperCase().compareTo("MSI-H") != 0) {
			ms_status = "MSI-High Not Detected";
		}
		if (sample.getMicrosatellite_instability_status().toUpperCase().compareTo("MSI-H") == 0) {
			ms_status = "MSI-High";
		}

		sampleRun.setText("Microsatellite Status: " + ms_status);
		sampleRun.addBreak();
		String tfs_text="";
		Float tfs=sample.getTumour_fraction_score();
		if(tfs==null) {
			tfs_text="Not reported";
		}
		else if(tfs==0) {
			tfs_text="Elevated Tumour Fraction Not Detected";
		} else if(tfs==-1) {
			tfs_text="Cannot Be Determined";
		} else {
			tfs_text=sample.getTumour_fraction_score().toString()+" "+sample.getTumour_fraction_unit();
		}
		
		sampleRun.setText("Tumour Fraction Score: " + tfs_text);
		sampleRun.addBreak();
		return sample.getMeasurement_gene_panel_id();
	}
	
	private Integer getFMTumourSample(Integer person_id, Integer timepoint, Integer version, XWPFParagraph paragraph) {
		FoundationMedicineDAO tumour = appContext.getBean(FoundationMedicineDAO.class);
		paragraph.setSpacingBetween(1.2);
		List<FMSample> samples = tumour.getTumourSamplesForPerson(person_id);
		Collections.sort(samples, (sample1, sample2) -> {
			return (sample1.getBaseline_number()==sample2.getBaseline_number())? 
					sample1.getMeasurement_gene_panel_id()-sample2.getMeasurement_gene_panel_id() :
						sample1.getBaseline_number()-sample2.getBaseline_number();
		});
		FMSample sample=null;
		int v=1;
		for(FMSample sam : samples) {
			System.out.println(sam.getBaseline_number() + " " + sam.getMeasurement_gene_panel_id());
			if(sam.getBaseline_number()==timepoint) {
				if(v==version) {
					sample=sam;
				} else {
					v++;
				}
			}
		}
		XWPFRun sampleRun = paragraph.createRun();
		sampleRun.setText("Tumour sample from " + sample.getSpecimentDateFormatted());
		sampleRun.addBreak();
		sampleRun.setText("TMB: " + sample.getTmb_status() + "; " + sample.getTmb_score() + " " + sample.getTmb_unit());
		sampleRun.addBreak();
		String ms_status = "Cannot Be Determined";
		if (sample.getMicrosatellite_instability_status().toUpperCase().compareTo("MSS") == 0) {
			ms_status = "MS-Stable";
		}
		if (sample.getMicrosatellite_instability_status().toUpperCase().compareTo("MSI-H") == 0) {
			ms_status = "MSI-High";
		}
		if (sample.getMicrosatellite_instability_status().toUpperCase().compareTo("MSI-L") == 0) {
			ms_status = "MS-Equivocal";
		}

		sampleRun.setText("Microsatellite Status: " + ms_status);
		sampleRun.addBreak();
		return sample.getMeasurement_gene_panel_id();
	}

	private boolean processFMAlterationData(Integer measurementGenePanelId, XWPFParagraph paragraph) {
		boolean hasAlterations = false;
		FoundationMedicineDAO tumour = appContext.getBean(FoundationMedicineDAO.class);
		XWPFRun alterationsRun=paragraph.createRun();;
		List<ShortVariant> shortvariants = tumour.getSignificantShortVariants(measurementGenePanelId);
		if(shortvariants.size()>0) {
			XWPFRun svRun = paragraph.createRun();
			svRun.setBold(true);
			svRun.setFontSize(11);
			svRun.setUnderline(UnderlinePatterns.SINGLE);
			svRun.addBreak();
			svRun.setText("Short Variants");
			svRun.addBreak();
			svRun.addBreak();
			alterationsRun = paragraph.createRun();
		}
		for (ShortVariant sh : shortvariants) {
			if(sh.isSignificant()) {
				Map<String, String> shf = Formater.formatShortVariant(sh);
				alterationsRun.setText(shf.get("geneName") + " " + shf.get("amino_acid_chage") + ", variant allele frequency = "
						+ shf.get("variant_allele_frequency") + " %");
				alterationsRun.addBreak();
				hasAlterations = true;
			}
		}
		List<CopyNumberAlteration> copynumberalteration = tumour.getSignificantCopyNumberAlterations(measurementGenePanelId);
		if(copynumberalteration.size()>0) {
			XWPFRun cnaRun = paragraph.createRun();
			cnaRun.setBold(true);
			cnaRun.setFontSize(11);
			cnaRun.setUnderline(UnderlinePatterns.SINGLE);
			cnaRun.addBreak();
			cnaRun.setText("Copy Number Changes");
			cnaRun.addBreak();
			cnaRun.addBreak();
			alterationsRun = paragraph.createRun();
		}
		for (CopyNumberAlteration cna : copynumberalteration) {
			if(cna.isSignificant()) {
				Map<String, String> cnaf = Formater.formatCopyNumberAlteration(cna);
				String equivocal = (cnaf.get("equivocal").compareTo("True") == 0) ? " equivocal" : "";
				alterationsRun.setText(cnaf.get("geneName") + " " + cnaf.get("type") + equivocal + ", copy number "
						+ cnaf.get("copy_number"));
				alterationsRun.addBreak();
				hasAlterations = true;
			}
		}
		List<Rearrangement> rearrangement = tumour.getSignificantRearrangements(measurementGenePanelId);
		if(rearrangement.size()>0) {
			XWPFRun rRun = paragraph.createRun();
			rRun.setBold(true);
			rRun.setFontSize(11);
			rRun.setUnderline(UnderlinePatterns.SINGLE);
			rRun.addBreak();
			rRun.setText("Rearrangements");
			rRun.addBreak();
			rRun.addBreak();
			alterationsRun = paragraph.createRun();
		}
		for (Rearrangement r : rearrangement) {
			if(r.isSignificant()) {
				Map<String, String> rf = Formater.formatRearrangement(r);
				String vaf = rf.get("variant_allele_frequency");
				if (vaf.compareTo("Not reported") != 0) {
					vaf = vaf + " %";
				}
				alterationsRun.setText(rf.get("gene1") + " " + rf.get("gene2") + " " + rf.get("rearrangement_type")
						+ ", variant allele frequency = " + vaf);
				alterationsRun.addBreak();
				hasAlterations = true;
			}
		}
		return hasAlterations;
	}

	private void generatePatientLetterNoAlterations(XWPFDocument document_no_alterations, Integer person_id, Integer timepoint, Integer version,
			String provider_short, String type) {
		XWPFParagraph intro = document_no_alterations.createParagraph();
		intro.setAlignment(ParagraphAlignment.LEFT);
		XWPFRun introRun = intro.createRun();
		introRun.setText(
				"Thank you for your participation in the TARGET National study. I am writing to update you about the genomic "
						+ "testing results. We have analysed the following:  ");
		introRun.setFontSize(11);
		introRun.setFontFamily("Calibri");
		introRun.addBreak();
		XWPFParagraph sample = document_no_alterations.createParagraph();

		if (provider_short.toLowerCase().compareTo("fm") == 0 && type.compareTo("tumour") == 0) {
			this.getFMTumourSample(person_id, timepoint, version, sample);
		} else if (provider_short.toLowerCase().compareTo("fm") == 0 && type.compareTo("blood") == 0) {
			this.getFMBloodSample(person_id, timepoint, version, sample);
		}
		XWPFParagraph nextSteps = document_no_alterations.createParagraph();
		XWPFRun nextStepsRun = nextSteps.createRun();
		nextStepsRun.addBreak();
		nextStepsRun.setText("No DNA mutations were identified in your sample from the analyses performed. We have not therefore "
				+ "identified any specific targeted clinical trial for you.");
		nextStepsRun.addBreak();
		nextStepsRun.addBreak();
		nextStepsRun.setText("We choose to send you this information by letter should you wish to show the results to any other doctors "
				+ "that you may see in relation to future treatment options.");
		nextStepsRun.addBreak();
		nextStepsRun.addBreak();
		nextStepsRun.setText("A copy of this letter has been sent to your GP and your treating oncologist.");
		nextStepsRun.addBreak();
		nextStepsRun.addBreak();
		nextStepsRun.setText("If you have any further questions regarding these results, please contact us on ");
		XWPFRun nextStepsRedRun = nextSteps.createRun();
		nextStepsRedRun.setText("[INSERT SITE CONTACT PHONE NUMBER HERE]");
		nextStepsRedRun.setColor("e7371e");
		XWPFRun nextStepsContRun = nextSteps.createRun();
		nextStepsContRun.setText(".");
		nextStepsContRun.addBreak();
		nextStepsContRun.addBreak();
		nextStepsContRun.setText("Yours sincerely,");
		nextStepsContRun.addBreak();
		nextStepsContRun.addBreak();
		nextStepsContRun.addBreak();
		nextStepsContRun.addBreak();
		XWPFRun nextStepsContRedRun = nextSteps.createRun();
		nextStepsContRedRun.setText("[Insert investigator/Research Nurse name] ");
		nextStepsContRedRun.setColor("e7371e");
	}

}

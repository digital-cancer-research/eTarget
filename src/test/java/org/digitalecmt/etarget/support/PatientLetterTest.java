package org.digitalecmt.etarget.support;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Test;


public class PatientLetterTest {

	@Test
	public void tumour_with_variants() {
		org.digitalecmt.etarget.support.PatientLetter pl = new org.digitalecmt.etarget.support.PatientLetter();
		Integer patientId = 19;
		String provider="fm";
		String type="tumour";
		Integer timepoint=1;
		Integer version=1;
		
		XWPFDocument document =pl.generatePatientLetter(patientId,provider,type,timepoint,version);
		FileOutputStream out;
		String file="/tmp/TAR00050_fm_tumour_1v1.docx";
		try {
			out = new FileOutputStream(file);
			document.write(out);
			out.close();
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		File f = new File(file);
		assertTrue(f.exists());
	}
	
	@Test
	public void blood_with_variants() {
		org.digitalecmt.etarget.support.PatientLetter pl = new org.digitalecmt.etarget.support.PatientLetter();
		Integer patientId = 20;
		String provider="fm";
		String type="blood";
		Integer timepoint=1;
		Integer version=1;
		
		XWPFDocument document =pl.generatePatientLetter(patientId,provider,type,timepoint,version);
		FileOutputStream out;
		String file="/tmp/TAR0010100_fm_blood_1v1.docx";
		try {
			out = new FileOutputStream(file);
			document.write(out);
			out.close();
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		File f = new File(file);
		assertTrue(f.exists());
	}
	
	@Test
	public void blood_with_variants_v2() {
		org.digitalecmt.etarget.support.PatientLetter pl = new org.digitalecmt.etarget.support.PatientLetter();
		Integer patientId = 20;
		String provider="fm";
		String type="blood";
		Integer timepoint=1;
		Integer version=2;
		
		XWPFDocument document =pl.generatePatientLetter(patientId,provider,type,timepoint,version);
		FileOutputStream out;
		String file="/tmp/TAR0010100_fm_blood_1v2.docx";
		try {
			out = new FileOutputStream(file);
			document.write(out);
			out.close();
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		File f = new File(file);
		assertTrue(f.exists());
	}
	
	@Test
	public void blood_without_variants() {
		org.digitalecmt.etarget.support.PatientLetter pl = new org.digitalecmt.etarget.support.PatientLetter();
		Integer patientId = 21;
		String provider="fm";
		String type="blood";
		Integer timepoint=1;
		Integer version=1;
		
		XWPFDocument document =pl.generatePatientLetter(patientId,provider,type,timepoint,version);
		FileOutputStream out;
		String file="/tmp/TAR0010101_fm_blood_1v1.docx";
		try {
			out = new FileOutputStream(file);
			document.write(out);
			out.close();
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		File f = new File(file);
		assertTrue(f.exists());
	}

	@Test
	public void tumour_without_variants() {
		org.digitalecmt.etarget.support.PatientLetter pl = new org.digitalecmt.etarget.support.PatientLetter();
		Integer patientId = 21;
		String provider="fm";
		String type="tumour";
		Integer timepoint=1;
		Integer version=1;
		
		XWPFDocument document =pl.generatePatientLetter(patientId,provider,type,timepoint,version);
		FileOutputStream out;
		String file="/tmp/TAR0010101_fm_tumour_1v1.docx";
		try {
			out = new FileOutputStream(file);
			document.write(out);
			out.close();
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		File f = new File(file);
		assertTrue(f.exists());
	}
	
}

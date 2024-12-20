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

import static org.junit.Assert.*;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class SearchTest extends JerseyTest {
	
	@Override
    protected javax.ws.rs.core.Application configure() {
        return new ResourceConfig(Search.class);
    }
	
	@Test
	public void test() {
		String result = target("search/genes").request().header("x-ms-client-principal-name", "anja.leblanc@apps.idecide.science").get(String.class);
		System.out.println(result);
		assertTrue(result!=null && result.length()>0);
		assertTrue(result.contains("AKT1"));
	}

	@Test
	public void test1() {
		String result = target("search/conditions").request().header("x-ms-client-principal-name", "anja.leblanc@apps.idecide.science").get(String.class);
		System.out.println(result);
		assertTrue(result!=null && result.length()>0);
	}
	
	@Test
	public void test2() {
		String result = target("search/patients").request().header("x-ms-client-principal-name", "anja.leblanc@apps.idecide.science").get(String.class);
		System.out.println(result);
		assertTrue(result!=null && result.length()>0);
		assertTrue(result.contains("Lung"));
	}
	
	@Test
	public void test3() {
		String result = target("search/conditions/Breast").request().header("x-ms-client-principal-name", "anja.leblanc@apps.idecide.science").get(String.class);
		System.out.println(result);
		assertTrue(result!=null && result.length()>0);
		assertTrue(result.contains("TAR00017"));
	}
	
	@Test
	public void test4() {
		String result = target("search/genes/alk").request().header("x-ms-client-principal-name", "anja.leblanc@apps.idecide.science").get(String.class);
		System.out.println(result);
		assertTrue(result!=null && result.length()>0);
		assertTrue(result.contains("TAR00050"));
	}
	
	@Test
	public void test5() {
		String result = target("search/pdxcdxPatients").request().header("x-ms-client-principal-name", "anja.leblanc@apps.idecide.science").get(String.class);
		System.out.println(result);
		assertTrue(result!=null && result.length()>0);
		assertTrue(result.contains("TAR00001"));
	}


}

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

import static org.junit.Assert.assertTrue;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

public class FMTumourTest extends JerseyTest{
	
	@Override
    protected javax.ws.rs.core.Application configure() {
        return new ResourceConfig(FMTumour.class);
    }
	
	@Test
    public void fmGetTumourDataTest() {
        String response = target("FMTumour/19").request().header("x-ms-client-principal-name", "anja.leblanc@apps.idecide.science").get(String.class);
        System.out.println(response);
        Assert.assertTrue(response.length()>0);
        Assert.assertTrue(response.contains("\"tumourID\":\"S25/00223-1\""));
    }

	@Test
    public void fmUserNotAllowedTest() {
		javax.ws.rs.core.Response response = target("FMTumour/19").request().header("x-ms-client-principal-name", "anja@apps.idecide.science").get();
        assertTrue(response.getStatus()==403);
        System.out.println(response);
	}
	
	@Test
    public void fmUserWrongUserIDTest() {
		javax.ws.rs.core.Response response = target("FMTumour/a19").request().header("x-ms-client-principal-name", "anja.leblanc@apps.idecide.science").get();
        assertTrue(response.getStatus()==404);
        System.out.println(response);
	}
}

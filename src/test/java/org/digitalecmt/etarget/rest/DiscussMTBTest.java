package org.digitalecmt.etarget.rest;

import static org.junit.Assert.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class DiscussMTBTest extends JerseyTest{

	@Override
    protected javax.ws.rs.core.Application configure() {
        return new ResourceConfig(DiscussMTB.class);
    }
	
	@Test
	public void test() {
		Response response = target("discussMTB/10").request().header("x-ms-client-principal-name", "anja@bindrich.de")
				.post(Entity.text("true"));
		assertEquals(200, response.getStatus());
		String result = response.readEntity(String.class);
		System.out.println(result);
		assertTrue(result.contains("success"));
		assertTrue(result.contains("true"));
	}
	
	@Test
	public void test2() {
		Response response = target("discussMTB/10").request().header("x-ms-client-principal-name", "richard.hoskins@apps.idecide.science")
				.post(Entity.text("true"));
		assertEquals(403, response.getStatus());
	}
	
	@Test
	public void test3() {
		Response response = target("discussMTB/100").request().header("x-ms-client-principal-name", "anja@bindrich.de")
				.post(Entity.text("true"));
		assertEquals(200, response.getStatus());
		String result = response.readEntity(String.class);
		System.out.println(result);
		assertTrue(result.contains("success"));
		assertTrue(result.contains("false"));
	}

}

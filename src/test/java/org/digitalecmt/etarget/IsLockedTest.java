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

import static org.junit.Assert.*;

import org.junit.Test;

public class IsLockedTest {

	@Test
	public void test1() {
		IsLocked islocked = new IsLocked("anja.leblanc@apps.idecide.science", "12");
		String result = islocked.processRequest();
		System.out.println(result);
		assertTrue(result.contains("\"success\":\"true\""));
		assertTrue(result.contains("\"locked\":\"false\""));
	}
	
	@Test
	public void test2() {
		IsLocked islocked = new IsLocked(null, "12");
		String result = islocked.processRequest();
		System.out.println(result);
		assertTrue(result.contains("\"success\":\"false\""));
	}
	
	@Test
	public void test3() {
		IsLocked islocked = new IsLocked("anja.leblanc@apps.idecide.science", null);
		String result = islocked.processRequest();
		System.out.println(result);
		assertTrue(result.contains("\"success\":\"false\""));
	}
	
	@Test
	public void test4() {
		IsLocked islocked = new IsLocked("anja.leblanc@apps.idecide.science", "");
		String result = islocked.processRequest();
		System.out.println(result);
		assertTrue(result.contains("\"success\":\"false\""));
	}
	
	@Test
	public void test5() {
		IsLocked islocked = new IsLocked(null, null);
		String result = islocked.processRequest();
		System.out.println(result);
		assertTrue(result.contains("\"success\":\"false\""));
	}
}
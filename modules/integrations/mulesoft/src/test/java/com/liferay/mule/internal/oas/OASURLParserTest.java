/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.oas;

import java.net.MalformedURLException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Matija Petanjek
 */
public class OASURLParserTest {

	@Test
	public void testGetAuthorityWithScheme() throws MalformedURLException {
		OASURLParser oasURLParser = new OASURLParser(OPEN_API_URL);

		Assert.assertEquals(
			"http://localhost:8080", oasURLParser.getAuthorityWithScheme());
	}

	@Test
	public void testGetHost() throws MalformedURLException {
		OASURLParser oasURLParser = new OASURLParser(OPEN_API_URL);

		Assert.assertEquals("localhost", oasURLParser.getHost());
	}

	@Test
	public void testGetJaxRSAppBase() throws MalformedURLException {
		OASURLParser oasURLParser = new OASURLParser(OPEN_API_URL);

		Assert.assertEquals(
			"/headless-commerce-admin-catalog", oasURLParser.getJaxRSAppBase());
	}

	@Test
	public void testGetPort() throws MalformedURLException {
		OASURLParser oasURLParser = new OASURLParser(OPEN_API_URL);

		Assert.assertEquals("8080", oasURLParser.getPort());
	}

	@Test
	public void testGetPortWhenDefaultPort() throws MalformedURLException {
		OASURLParser oasURLParser = new OASURLParser(
			"http://localhost/o/headless-commerce-admin-catalog/v1.0" +
				"/openapi.json");

		Assert.assertEquals("", oasURLParser.getPort());
	}

	@Test
	public void testGetScheme() throws MalformedURLException {
		OASURLParser oasURLParser = new OASURLParser(OPEN_API_URL);

		Assert.assertEquals("http", oasURLParser.getScheme());
	}

	@Test
	public void testGetServerBaseURL() throws MalformedURLException {
		OASURLParser oasURLParser = new OASURLParser(OPEN_API_URL);

		Assert.assertEquals(
			"http://localhost:8080/o/headless-commerce-admin-catalog",
			oasURLParser.getServerBaseURL());
	}

	@Test
	public void testGetServerBaseURLWithCustomJaxRSAppBase()
		throws MalformedURLException {

		OASURLParser oasURLParser = new OASURLParser(OPEN_API_URL);

		Assert.assertEquals(
			"http://localhost:8080/o/custom-jax-rs-app",
			oasURLParser.getServerBaseURL("/custom-jax-rs-app"));
	}

	@Test(expected = MalformedURLException.class)
	public void testOASURLParserWithMalformedURL()
		throws MalformedURLException {

		new OASURLParser(
			"http://localhost:8080/o/headless-commerce-admin-catalog/1.0" +
				"/openapi.json");
	}

	private static final String OPEN_API_URL =
		"http://localhost:8080/o/headless-commerce-admin-catalog/v1.0" +
			"/openapi.json";

}
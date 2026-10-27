/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.json.web.service.client.internal;

import com.liferay.portal.json.web.service.client.JSONWebServiceException;
import com.liferay.portal.json.web.service.client.keystore.KeyStoreLoader;
import com.liferay.portal.json.web.service.client.server.simulator.HTTPSServerSimulator;
import com.liferay.portal.json.web.service.client.server.simulator.constants.SimulatorConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.security.KeyStore;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Igor Beslic
 */
public class JSONWebServiceClientImplSSLGetTest
	extends BaseJSONWebServiceClientTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void test200OKOnGetIfTLS12() throws Exception {
		System.setProperty("https.protocols", "TLSv1.2");

		JSONWebServiceClientImpl jsonWebServiceClientImpl =
			_createJsonWebServiceClient();

		HTTPSServerSimulator.start("TLSv1.2");

		String json = jsonWebServiceClientImpl.doGet(
			"/testGet/", getParameters("200"));

		HTTPSServerSimulator.stop();

		Assert.assertTrue(
			json,
			json.contains(
				SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS));
	}

	@Test
	public void test200OKOnGetIfTLS13() throws Exception {
		System.setProperty("https.protocols", "TLSv1.3");

		JSONWebServiceClientImpl jsonWebServiceClientImpl =
			_createJsonWebServiceClient();

		HTTPSServerSimulator.start("TLSv1.3");

		String json = jsonWebServiceClientImpl.doGet(
			"/testGet/", getParameters("200"));

		HTTPSServerSimulator.stop();

		Assert.assertTrue(
			json,
			json.contains(
				SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS));
	}

	@Test(expected = JSONWebServiceException.class)
	public void testJSONWebServiceExceptionOnGetIfTLS10() throws Exception {
		System.setProperty("https.protocols", "TLSv1.1");

		JSONWebServiceClientImpl jsonWebServiceClientImpl =
			_createJsonWebServiceClient();

		HTTPSServerSimulator.start("TLSv1");

		try {
			String json = jsonWebServiceClientImpl.doGet(
				"/testGet/", getParameters("200"));

			Assert.assertTrue(
				json,
				json.contains(
					SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS));
		}
		finally {
			HTTPSServerSimulator.stop();
		}
	}

	@Test(expected = JSONWebServiceException.class)
	public void testJSONWebServiceExceptionOnGetIfTLS11() throws Exception {
		JSONWebServiceClientImpl jsonWebServiceClientImpl =
			_createJsonWebServiceClient();

		HTTPSServerSimulator.start("TLSv1.1");

		try {
			String json = jsonWebServiceClientImpl.doGet(
				"/testGet/", getParameters("200"));

			Assert.assertTrue(
				json,
				json.contains(
					SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS));
		}
		finally {
			HTTPSServerSimulator.stop();
		}
	}

	@Test(expected = JSONWebServiceException.class)
	public void testJSONWebServiceExceptionOnGetIfUntrustedCertificate()
		throws Exception {

		System.setProperty("https.protocols", "TLSv1.2");

		JSONWebServiceClientImpl jsonWebServiceClientImpl =
			new JSONWebServiceClientImpl();

		Map<String, Object> properties = getBaseProperties();

		properties.put("hostPort", 9443);

		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

		keyStore.load(null);

		properties.put("keyStore", keyStore);

		properties.put("protocol", "https");
		properties.put("trustSelfSignedCertificates", false);

		jsonWebServiceClientImpl.activate(properties);

		HTTPSServerSimulator.start("TLSv1.2");

		try {
			jsonWebServiceClientImpl.doGet("/testGet/", getParameters("200"));
		}
		finally {
			HTTPSServerSimulator.stop();
		}
	}

	private JSONWebServiceClientImpl _createJsonWebServiceClient()
		throws Exception {

		JSONWebServiceClientImpl jsonWebServiceClientImpl =
			new JSONWebServiceClientImpl();

		Map<String, Object> properties = getBaseProperties();

		properties.put(
			"headers", "headerKey1=headerValue1;Accept=application/json;");
		properties.put("hostPort", 9443);

		KeyStoreLoader keyStoreLoader = new KeyStoreLoader();

		properties.put(
			"keyStore", keyStoreLoader.getKeyStore("localhost.jks", "liferay"));

		properties.put("protocol", "https");

		jsonWebServiceClientImpl.activate(properties);

		return jsonWebServiceClientImpl;
	}

}
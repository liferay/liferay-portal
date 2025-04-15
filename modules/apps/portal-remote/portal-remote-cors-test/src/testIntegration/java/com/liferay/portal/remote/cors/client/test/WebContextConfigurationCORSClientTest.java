/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.cors.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.remote.cors.configuration.WebContextCORSConfiguration;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.HttpMethod;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marta Medio
 */
@RunWith(Arquillian.class)
public class WebContextConfigurationCORSClientTest
	extends BaseCORSClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		registerJaxRsApplication(
			new CORSTestApplication(), _PATH,
			HashMapDictionaryBuilder.<String, Object>put(
				"osgi.jaxrs.name", "test-cors"
			).build());

		createFactoryConfiguration(
			WebContextCORSConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"headers",
				StringBundler.concat(
					"Access-Control-Allow-Credentials: true|",
					"Access-Control-Allow-Headers: *|",
					"Access-Control-Allow-Methods: *|",
					"Access-Control-Allow-Origin: http://localhost:8080 ",
					"http://127.0.0.1:8080 ::1")
			).put(
				"servlet.context.helper.select.filter",
				"(osgi.jaxrs.name=test-cors)"
			).build());
	}

	@Test
	public void testApplicationCORSForGuestUser() throws Exception {
		assertJaxRSUrl(_URL, HttpMethod.GET, false, false);
		assertJaxRSUrl(_URL, HttpMethod.GET, false, true, "::1");
		assertJaxRSUrl(
			_URL, HttpMethod.GET, false, true, "http://127.0.0.1:8080");
		assertJaxRSUrl(
			_URL, HttpMethod.GET, false, true, "http://localhost:8080");
		assertJaxRSUrl(_URL, HttpMethod.OPTIONS, false, false);
		assertJaxRSUrl(_URL, HttpMethod.OPTIONS, false, true, "::1");
		assertJaxRSUrl(
			_URL, HttpMethod.OPTIONS, false, true, "http://127.0.0.1:8080");
		assertJaxRSUrl(
			_URL, HttpMethod.OPTIONS, false, true, "http://localhost:8080");
	}

	@Test
	public void testApplicationCORSWithoutOAuth2() throws Exception {
		assertJaxRSUrl(_URL, HttpMethod.GET, true, false);
		assertJaxRSUrl(_URL, HttpMethod.GET, true, false, "::1");
		assertJaxRSUrl(
			_URL, HttpMethod.GET, true, false, "http://127.0.0.1:8080");
		assertJaxRSUrl(
			_URL, HttpMethod.GET, true, false, "http://localhost:8080");
		assertJaxRSUrl(_URL, HttpMethod.OPTIONS, true, false);
		assertJaxRSUrl(_URL, HttpMethod.OPTIONS, true, true, "::1");
		assertJaxRSUrl(
			_URL, HttpMethod.OPTIONS, true, true, "http://127.0.0.1:8080");
		assertJaxRSUrl(
			_URL, HttpMethod.OPTIONS, true, true, "http://localhost:8080");
	}

	private static final String _PATH = RandomTestUtil.randomString();

	private static final String _URL = "/" + _PATH + "/cors-app";

}
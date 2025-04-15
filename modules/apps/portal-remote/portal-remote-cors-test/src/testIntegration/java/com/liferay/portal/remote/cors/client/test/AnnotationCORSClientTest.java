/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.cors.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
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
public class AnnotationCORSClientTest extends BaseCORSClientTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		registerJaxRsApplication(
			new CORSTestApplication(), _PATH,
			HashMapDictionaryBuilder.<String, Object>put(
				"liferay.cors.annotation", true
			).build());
	}

	@Test
	public void testApplicationAnnotationCORSForGuestUser() throws Exception {
		assertJaxRSUrl(_URL, HttpMethod.OPTIONS, true, true);
		assertJaxRSUrl(_URL, HttpMethod.GET, true, false);
	}

	@Test
	public void testApplicationAnnotationCORSWithoutOAuth2() throws Exception {
		assertJaxRSUrl(_URL, HttpMethod.OPTIONS, true, true);
		assertJaxRSUrl(_URL, HttpMethod.GET, true, false);
	}

	private static final String _PATH = RandomTestUtil.randomString();

	private static final String _URL = "/" + _PATH + "/cors-app";

}
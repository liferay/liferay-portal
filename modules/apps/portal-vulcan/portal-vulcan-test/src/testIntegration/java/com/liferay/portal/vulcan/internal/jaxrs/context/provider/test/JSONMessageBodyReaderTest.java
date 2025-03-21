/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider.test;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.jackson.databind.deser.JSONStringStdDeserializer;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;

import java.util.Collections;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class JSONMessageBodyReaderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			JSONMessageBodyReaderTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			Application.class, new TestApplication(),
			HashMapDictionaryBuilder.<String, Object>put(
				"liferay.auth.verifier", true
			).put(
				"liferay.jackson", false
			).put(
				"liferay.oauth2", false
			).put(
				"osgi.jaxrs.application.base", "/test-vulcan"
			).put(
				"osgi.jaxrs.extension.select",
				"(osgi.jaxrs.name=Liferay.Vulcan)"
			).build());
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testDeserializeJSONString() throws Exception {

		// As JSON array

		JSONAssert.assertEquals(
			"{\"jsonString\":\"[{\\\"key\\\":\\\"value\\\"}]\"}",
			HTTPTestUtil.invokeToJSONObject(
				"{\"jsonString\":[{\"key\":\"value\"}]}", "test-vulcan/test",
				Http.Method.POST
			).toString(),
			true);

		// As JSON object

		JSONAssert.assertEquals(
			"{\"jsonString\":\"{\\\"key\\\":\\\"value\\\"}\"}",
			HTTPTestUtil.invokeToJSONObject(
				"{\"jsonString\":{\"key\":\"value\"}}", "test-vulcan/test",
				Http.Method.POST
			).toString(),
			true);

		// As JSON string

		JSONAssert.assertEquals(
			"{\"jsonString\":\"{\\\"key\\\":\\\"value\\\"}\"}",
			HTTPTestUtil.invokeToJSONObject(
				"{\"jsonString\":\"{\\\"key\\\":\\\"value\\\"}\"}",
				"test-vulcan/test", Http.Method.POST
			).toString(),
			true);
	}

	public static class TestApplication extends Application {

		@Override
		public Set<Object> getSingletons() {
			return Collections.singleton(this);
		}

		@Consumes("application/json")
		@Path("/test")
		@POST
		@Produces("application/json")
		public TestDTO testClass(TestDTO testDTO) {
			return testDTO;
		}

	}

	public static class TestDTO {

		@JsonDeserialize(using = JSONStringStdDeserializer.class)
		public String jsonString;

	}

	private ServiceRegistration<Application> _serviceRegistration;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider.test;

import com.fasterxml.jackson.annotation.JsonFilter;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.vulcan.internal.test.util.URLConnectionUtil;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;

import java.util.Collections;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alejandro Hernández
 */
@RunWith(Arquillian.class)
public class JSONMessageBodyWriterTest {

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			JSONMessageBodyWriterTest.class);

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
	public void testFieldsFilterNestedJSONObject() throws Exception {
		JSONObject jsonObject = _createJSONObject(
			"http://localhost:8080/o/test-vulcan/test-class?" +
				"fields=string,testClass,testClass.number");

		Assert.assertFalse(jsonObject.has("number"));
		Assert.assertEquals("hello", jsonObject.getString("string"));

		JSONObject testClassJSONObject = jsonObject.getJSONObject("testClass");

		Assert.assertEquals(6L, testClassJSONObject.getLong("number"));
		Assert.assertFalse(testClassJSONObject.has("string"));
		Assert.assertFalse(testClassJSONObject.has("testClass"));
	}

	@Test
	public void testFieldsFilterRootJSONObject() throws Exception {
		JSONObject jsonObject = _createJSONObject(
			"http://localhost:8080/o/test-vulcan/test-class?fields=string");

		Assert.assertFalse(jsonObject.has("number"));
		Assert.assertFalse(jsonObject.has("testClass"));
		Assert.assertEquals("hello", jsonObject.getString("string"));
	}

	@Test
	public void testIsWrittenToJSON() throws Exception {
		JSONObject jsonObject = _createJSONObject(
			"http://localhost:8080/o/test-vulcan/test-class");

		Assert.assertEquals(1L, jsonObject.getLong("number"));
		Assert.assertEquals("hello", jsonObject.getString("string"));

		JSONObject testClassJSONObject = jsonObject.getJSONObject("testClass");

		Assert.assertEquals(6L, testClassJSONObject.getLong("number"));
		Assert.assertEquals("hi", testClassJSONObject.getString("string"));
		Assert.assertTrue(testClassJSONObject.isNull("testClass"));
	}

	@Test
	@TestInfo("LPD-50142")
	public void testUnsafeSupplierFieldsJSONObject() throws Exception {
		HTTPTestUtil.invokeToJSONObject(
			null, "test-vulcan/test-class?fields=property1UnsafeSupplier",
			Http.Method.GET);

		Assert.assertTrue(_property1UnsafeSupplierComputed);
		Assert.assertFalse(_property2UnsafeSupplierComputed);
		Assert.assertFalse(_property3UnsafeSupplierComputed);

		_property1UnsafeSupplierComputed = false;
		_property2UnsafeSupplierComputed = false;
		_property3UnsafeSupplierComputed = false;

		HTTPTestUtil.invokeToJSONObject(
			null, "test-vulcan/test-class?fields=property3UnsafeSupplier",
			Http.Method.GET);

		Assert.assertFalse(_property1UnsafeSupplierComputed);
		Assert.assertFalse(_property2UnsafeSupplierComputed);
		Assert.assertTrue(_property3UnsafeSupplierComputed);

		_property1UnsafeSupplierComputed = false;
		_property2UnsafeSupplierComputed = false;
		_property3UnsafeSupplierComputed = false;

		HTTPTestUtil.invokeToJSONObject(
			null, "test-vulcan/test-class?fields=testClass", Http.Method.GET);

		Assert.assertTrue(_property1UnsafeSupplierComputed);
		Assert.assertTrue(_property2UnsafeSupplierComputed);
		Assert.assertTrue(_property3UnsafeSupplierComputed);

		_property1UnsafeSupplierComputed = false;
		_property2UnsafeSupplierComputed = false;
		_property3UnsafeSupplierComputed = false;

		HTTPTestUtil.invokeToJSONObject(
			null,
			"test-vulcan/test-class?restrictFields=property1UnsafeSupplier",
			Http.Method.GET);

		Assert.assertTrue(_property1UnsafeSupplierComputed);
		Assert.assertTrue(_property2UnsafeSupplierComputed);
		Assert.assertTrue(_property3UnsafeSupplierComputed);

		_property1UnsafeSupplierComputed = false;
		_property2UnsafeSupplierComputed = false;
		_property3UnsafeSupplierComputed = false;

		HTTPTestUtil.invokeToJSONObject(
			null,
			"test-vulcan/test-class?restrictFields=property1UnsafeSupplier," +
				"testClass",
			Http.Method.GET);

		Assert.assertFalse(_property1UnsafeSupplierComputed);
		Assert.assertTrue(_property2UnsafeSupplierComputed);
		Assert.assertTrue(_property3UnsafeSupplierComputed);

		_property1UnsafeSupplierComputed = false;
		_property2UnsafeSupplierComputed = false;
		_property3UnsafeSupplierComputed = false;

		HTTPTestUtil.invokeToJSONObject(
			null, "test-vulcan/test-class", Http.Method.GET);

		Assert.assertTrue(_property1UnsafeSupplierComputed);
		Assert.assertTrue(_property2UnsafeSupplierComputed);
		Assert.assertTrue(_property3UnsafeSupplierComputed);
	}

	public class TestApplication extends Application {

		@Override
		public Set<Object> getSingletons() {
			return Collections.singleton(this);
		}

		@GET
		@Path("/test-class")
		@Produces(MediaType.APPLICATION_JSON)
		public TestClass testClass() {
			return new TestClass(1L, "hello", new TestClass(6L, "hi", null));
		}

		@JsonFilter("Liferay.Vulcan")
		public class TestClass {

			public TestClass(Long number, String string, TestClass testClass) {
				this.number = number;
				this.string = string;
				this.testClass = testClass;
			}

			public final Long number;

			public UnsafeSupplier<String, Exception> property1UnsafeSupplier =
				() -> {
					_property1UnsafeSupplierComputed = true;

					return RandomTestUtil.randomString();
				};

			public UnsafeSupplier<String, Exception> property2UnsafeSupplier =
				() -> {
					_property2UnsafeSupplierComputed = true;

					return RandomTestUtil.randomString();
				};

			public UnsafeSupplier<String, Exception> property3UnsafeSupplier =
				() -> {
					_property3UnsafeSupplierComputed = true;

					return null;
				};

			public final String string;
			public final TestClass testClass;

		}

	}

	private JSONObject _createJSONObject(String urlString) throws Exception {
		return JSONFactoryUtil.createJSONObject(
			URLConnectionUtil.read(urlString));
	}

	private boolean _property1UnsafeSupplierComputed;
	private boolean _property2UnsafeSupplierComputed;
	private boolean _property3UnsafeSupplierComputed;
	private ServiceRegistration<Application> _serviceRegistration;

}
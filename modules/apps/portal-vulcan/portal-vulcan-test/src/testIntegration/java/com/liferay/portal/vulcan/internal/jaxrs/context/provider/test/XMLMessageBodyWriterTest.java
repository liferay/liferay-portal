/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider.test;

import com.fasterxml.jackson.annotation.JsonFilter;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.vulcan.internal.test.util.URLConnectionUtil;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;

import java.io.InputStream;

import java.net.URLConnection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
 * @author Ivica Cardic
 */
@RunWith(Arquillian.class)
public class XMLMessageBodyWriterTest {

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(XMLMessageBodyWriterTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			Application.class, new TestApplication(),
			HashMapDictionaryBuilder.<String, Object>put(
				"liferay.auth.verifier", true
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
	public void testFieldsFilterRootJSONObject() throws Exception {
		Document document = _getDocument(
			"http://localhost:8080/o/test-vulcan/test-class?fields=string");

		Element testClassElement = document.getRootElement();

		Assert.assertNull(testClassElement.element("number"));
		Assert.assertNull(testClassElement.element("testClass"));
		Assert.assertEquals("hello", testClassElement.elementText("string"));
	}

	@Test
	public void testIsListWrittenToXML() throws Exception {
		Document document = _getDocument(
			"http://localhost:8080/o/test-vulcan/test-classes");

		Element rootElement = document.getRootElement();

		List<Element> elements = rootElement.elements();

		Element element = elements.get(0);

		Assert.assertEquals(
			1L, GetterUtil.getLong(element.elementText("number")));
		Assert.assertEquals("hello", element.elementText("string"));

		Element innerTestClassElement = element.element("testClass");

		Assert.assertEquals(
			6L,
			GetterUtil.getLong(innerTestClassElement.elementText("number")));
		Assert.assertEquals("hi", innerTestClassElement.elementText("string"));
		Assert.assertNull(innerTestClassElement.element("testClass"));

		element = elements.get(1);

		Assert.assertEquals(
			2L, GetterUtil.getLong(element.elementText("number")));
		Assert.assertEquals("world", element.elementText("string"));
	}

	@Test
	public void testIsWrittenToXML() throws Exception {
		Document document = _getDocument(
			"http://localhost:8080/o/test-vulcan/test-class");

		Element testClassElement = document.getRootElement();

		Assert.assertEquals(
			1L, GetterUtil.getLong(testClassElement.elementText("number")));
		Assert.assertEquals("hello", testClassElement.elementText("string"));

		Element innerTestClassElement = testClassElement.element("testClass");

		Assert.assertEquals(
			6L,
			GetterUtil.getLong(innerTestClassElement.elementText("number")));
		Assert.assertEquals("hi", innerTestClassElement.elementText("string"));
		Assert.assertNull(innerTestClassElement.element("testClass"));
	}

	public static class TestApplication extends Application {

		@Override
		public Set<Object> getSingletons() {
			return Collections.singleton(this);
		}

		@GET
		@Path("/test-class")
		@Produces(MediaType.APPLICATION_XML)
		public TestClass testClass() {
			return TestClass.of(1L, "hello", TestClass.of(6L, "hi", null));
		}

		@GET
		@Path("/test-classes")
		@Produces(MediaType.APPLICATION_XML)
		public List<TestClass> testClasses() {
			return Arrays.asList(
				TestClass.of(1L, "hello", TestClass.of(6L, "hi", null)),
				TestClass.of(2L, "world", null));
		}

	}

	@JsonFilter("Liferay.Vulcan")
	public static class TestClass {

		public static TestClass of(
			Long number, String string, TestClass testClass) {

			return new TestClass(number, string, testClass);
		}

		public TestClass(Long number, String string, TestClass testClass) {
			this.number = number;
			this.string = string;
			this.testClass = testClass;
		}

		public final Long number;
		public final String string;
		public final TestClass testClass;

	}

	private Document _getDocument(String urlString) throws Exception {
		URLConnection urlConnection = URLConnectionUtil.createURLConnection(
			urlString);

		urlConnection.setRequestProperty("Accept", MediaType.APPLICATION_XML);

		try (InputStream inputStream = urlConnection.getInputStream()) {
			String content = StringUtil.read(inputStream);

			return SAXReaderUtil.read(content);
		}
	}

	private ServiceRegistration<Application> _serviceRegistration;

}
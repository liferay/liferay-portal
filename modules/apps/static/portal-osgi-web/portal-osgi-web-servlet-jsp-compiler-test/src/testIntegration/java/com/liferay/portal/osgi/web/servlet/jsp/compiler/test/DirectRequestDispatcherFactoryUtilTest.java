/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.jsp.compiler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Servlet;

import java.io.InputStream;

import java.net.URL;

import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class DirectRequestDispatcherFactoryUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			DirectRequestDispatcherFactoryUtilTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_bundle = bundleContext.installBundle(
			DirectRequestDispatcherFactoryUtilTest.class.getName(),
			_createBundle());

		_bundle.start();

		ServiceTracker<Servlet, Servlet> serviceTracker = new ServiceTracker<>(
			bundleContext,
			FrameworkUtil.createFilter(
				StringBundler.concat(
					"(&(objectClass=", Servlet.class.getName(), ")(",
					HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "=",
					_WEB_CONTEXT_PATH.substring(1), ")(",
					HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN,
					"=*.jsp))")),
			null);

		serviceTracker.open();

		try {
			Assert.assertNotNull(serviceTracker.waitForService(2000));
		}
		finally {
			serviceTracker.close();
		}
	}

	@After
	public void tearDown() throws Exception {
		_bundle.uninstall();
	}

	@Test
	public void testGetRequestDispatcher() throws Exception {
		String contextPath = PortalUtil.getPathModule() + _WEB_CONTEXT_PATH;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DirectRequestDispatcherFactoryUtil.class.getName(),
				LoggerTestUtil.DEBUG)) {

			String[] lines = StringUtil.splitLines(
				StringUtil.trim(
					URLUtil.toString(
						new URL(
							StringBundler.concat(
								"http://localhost:",
								PortalUtil.getPortalServerPort(false),
								contextPath, "/outer.jsp")))));

			Assert.assertEquals(Arrays.toString(lines), 2, lines.length);

			String[] values = StringUtil.split(lines[0]);

			Assert.assertEquals(lines[0], 7, values.length);
			Assert.assertEquals(contextPath, values[0]);
			Assert.assertEquals(values[2], values[1]);
			Assert.assertEquals("/inner.jsp", values[3]);
			Assert.assertEquals("/inner.jsp", values[4]);
			Assert.assertEquals("null", values[5]);
			Assert.assertEquals(values[2], values[6]);

			Assert.assertEquals("null,null", lines[1]);

			List<String> messages = logCapture.getMessages();

			Assert.assertEquals(messages.toString(), 0, messages.size());
		}
	}

	private InputStream _createBundle() throws Exception {
		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream();

			JarOutputStream jarOutputStream = new JarOutputStream(
				unsyncByteArrayOutputStream)) {

			Manifest manifest = new Manifest();

			Attributes attributes = manifest.getMainAttributes();

			attributes.putValue(Constants.BUNDLE_MANIFESTVERSION, "2");
			attributes.putValue(
				Constants.BUNDLE_SYMBOLICNAME,
				DirectRequestDispatcherFactoryUtilTest.class.getName());
			attributes.putValue(Constants.BUNDLE_VERSION, "1.0.0");
			attributes.putValue(
				Constants.IMPORT_PACKAGE, "com.liferay.portal.kernel.servlet");
			attributes.putValue("Manifest-Version", "1.0");
			attributes.putValue("Web-ContextPath", _WEB_CONTEXT_PATH);

			jarOutputStream.putNextEntry(new ZipEntry(JarFile.MANIFEST_NAME));

			manifest.write(jarOutputStream);

			jarOutputStream.closeEntry();

			jarOutputStream.putNextEntry(
				new ZipEntry("META-INF/resources/inner.jsp"));

			jarOutputStream.write(
				_getBytes(
					"<%= request.getContextPath() %>,",
					"<%= request.getServletContext().getServletContextName() ",
					"%>,<%= application.getServletContextName() %>,",
					"<%= request.getServletPath() %>,",
					"<%= request.getAttribute(RequestDispatcher.",
					"INCLUDE_SERVLET_PATH) %>,",
					"<%= request.getAttribute(RequestDispatcher.",
					"INCLUDE_PATH_INFO) %>,",
					"<%= session.getServletContext().getServletContextName() ",
					"%>\n"));

			jarOutputStream.closeEntry();

			jarOutputStream.putNextEntry(
				new ZipEntry("META-INF/resources/outer.jsp"));

			jarOutputStream.write(
				_getBytes(
					"<%@ page import=\"com.liferay.portal.kernel.servlet.",
					"DirectRequestDispatcherFactoryUtil\" %><%\n",
					"RequestDispatcher requestDispatcher = ",
					"DirectRequestDispatcherFactoryUtil.getRequestDispatcher(",
					"application, \"/inner.jsp\");\n",
					"requestDispatcher.include(request, response);\n",
					"%><%= request.getAttribute(RequestDispatcher.",
					"INCLUDE_SERVLET_PATH) %>,<%= request.getAttribute(",
					"RequestDispatcher.INCLUDE_PATH_INFO) %>"));

			jarOutputStream.closeEntry();

			jarOutputStream.finish();

			return new UnsyncByteArrayInputStream(
				unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
				unsyncByteArrayOutputStream.size());
		}
	}

	private byte[] _getBytes(String... strings) {
		String string = StringBundler.concat(strings);

		return string.getBytes();
	}

	private static final String _WEB_CONTEXT_PATH =
		"/test-direct-request-dispatcher-factory-util";

	private Bundle _bundle;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.jsp.compiler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Servlet;

import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
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
 * @author Eric Yan
 */
@RunWith(Arquillian.class)
public class JspServletPerformanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			JspServletPerformanceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_bundle = bundleContext.installBundle(
			JspServletPerformanceTest.class.getName(), _createBundle());

		_bundle.start();

		Runtime runtime = Runtime.getRuntime();

		_executorService = Executors.newFixedThreadPool(
			runtime.availableProcessors());

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

	@AfterClass
	public static void tearDownClass() throws Exception {
		_executorService.shutdownNow();

		_bundle.uninstall();
	}

	@Test
	public void testElExpressionWithUndefinedScopedVariablesJsp()
		throws Exception {

		_test(_FILE_NAME_EL_EXPRESSION_UNDEFINED_SCOPED_VARIABLES_JSP, 1);

		try (PerformanceTimer performanceTimer = new PerformanceTimer(15000)) {
			_test(
				_FILE_NAME_EL_EXPRESSION_UNDEFINED_SCOPED_VARIABLES_JSP,
				_NUMBER_OF_REQUESTS);
		}
	}

	@Test
	public void testElExpressionWithUndefinedVariablesJsp() throws Exception {
		_test(_FILE_NAME_EL_EXPRESSION_UNDEFINED_VARIABLES_JSP, 1);

		try (PerformanceTimer performanceTimer = new PerformanceTimer(15000)) {
			_test(
				_FILE_NAME_EL_EXPRESSION_UNDEFINED_VARIABLES_JSP,
				_NUMBER_OF_REQUESTS);
		}
	}

	@Test
	public void testJsp() throws Exception {
		_test(_FILE_NAME_TEST_JSP, 1);

		try (PerformanceTimer performanceTimer = new PerformanceTimer(15000)) {
			_test(_FILE_NAME_TEST_JSP, _NUMBER_OF_REQUESTS);
		}
	}

	private static InputStream _createBundle() throws Exception {
		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream();
			JarOutputStream jarOutputStream = new JarOutputStream(
				unsyncByteArrayOutputStream)) {

			Manifest manifest = new Manifest();

			Attributes attributes = manifest.getMainAttributes();

			attributes.putValue(Constants.BUNDLE_MANIFESTVERSION, "2");
			attributes.putValue(
				Constants.BUNDLE_SYMBOLICNAME,
				JspServletPerformanceTest.class.getName());
			attributes.putValue(Constants.BUNDLE_VERSION, "1.0.0");
			attributes.putValue("Manifest-Version", "1.0");
			attributes.putValue("Web-ContextPath", _WEB_CONTEXT_PATH);

			jarOutputStream.putNextEntry(new ZipEntry(JarFile.MANIFEST_NAME));

			manifest.write(jarOutputStream);

			jarOutputStream.closeEntry();

			String fileName =
				_FILE_NAME_EL_EXPRESSION_UNDEFINED_SCOPED_VARIABLES_JSP;

			jarOutputStream.putNextEntry(
				new ZipEntry("META-INF/resources/" + fileName));

			jarOutputStream.write(
				_getBytes(
					"<html><body>",
					_FILE_NAME_EL_EXPRESSION_UNDEFINED_SCOPED_VARIABLES_JSP,
					"${elExpression0.test}${elExpression1.test}",
					"${elExpression2.test}${elExpression3.test}",
					"${elExpression4.test}${elExpression5.test}",
					"${elExpression6.test}${elExpression7.test}",
					"${elExpression8.test}${elExpression9.test}</body>",
					"</html>"));

			jarOutputStream.closeEntry();

			jarOutputStream.putNextEntry(
				new ZipEntry(
					"META-INF/resources/" +
						_FILE_NAME_EL_EXPRESSION_UNDEFINED_VARIABLES_JSP));

			jarOutputStream.write(
				_getBytes(
					"<html><body>",
					_FILE_NAME_EL_EXPRESSION_UNDEFINED_VARIABLES_JSP,
					"${elExpression0}${elExpression1}${elExpression2}",
					"${elExpression3}${elExpression4}${elExpression5}",
					"${elExpression6}${elExpression7}${elExpression8}",
					"${elExpression9}</body></html>"));

			jarOutputStream.closeEntry();

			jarOutputStream.putNextEntry(
				new ZipEntry("META-INF/resources/" + _FILE_NAME_TEST_JSP));

			jarOutputStream.write(
				_getBytes(
					"<html><body>", _FILE_NAME_TEST_JSP, "</body></html>"));

			jarOutputStream.closeEntry();

			jarOutputStream.finish();

			return new UnsyncByteArrayInputStream(
				unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
				unsyncByteArrayOutputStream.size());
		}
	}

	private static byte[] _getBytes(String... strings) {
		String string = StringBundler.concat(strings);

		return string.getBytes();
	}

	private void _test(String jspFileName, int numberOfRequests)
		throws Exception {

		URL url = new URL(
			StringBundler.concat(
				"http://localhost:8080/o", _WEB_CONTEXT_PATH, "/",
				jspFileName));

		Assert.assertEquals(
			jspFileName, HtmlUtil.stripHtml(URLUtil.toString(url)));

		List<Future<?>> futures = new ArrayList<>();

		for (int i = 0; i < numberOfRequests; i++) {
			futures.add(_executorService.submit(() -> URLUtil.toString(url)));
		}

		for (Future<?> future : futures) {
			future.get();
		}
	}

	private static final String
		_FILE_NAME_EL_EXPRESSION_UNDEFINED_SCOPED_VARIABLES_JSP =
			"el_expression_undefined_scoped_variables.jsp";

	private static final String
		_FILE_NAME_EL_EXPRESSION_UNDEFINED_VARIABLES_JSP =
			"el_expression_undefined_variables.jsp";

	private static final String _FILE_NAME_TEST_JSP = "test.jsp";

	private static final int _NUMBER_OF_REQUESTS = 1000;

	private static final String _WEB_CONTEXT_PATH =
		"/test-jsp-servlet-performance";

	private static Bundle _bundle;
	private static ExecutorService _executorService;

}
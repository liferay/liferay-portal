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

import javax.servlet.Servlet;

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
	public void test01() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test02() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test03() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test04() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test05() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test06() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test07() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test08() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test09() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test10() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test11() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test12() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test13() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test14() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test15() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test16() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test17() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test18() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test19() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test20() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test21() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test22() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test23() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test24() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test25() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test26() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test27() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test28() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test29() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test30() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test31() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test32() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test33() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test34() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test35() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test36() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test37() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test38() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test39() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test40() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test41() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test42() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test43() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test44() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test45() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test46() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test47() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test48() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test49() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test50() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test51() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test52() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test53() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test54() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test55() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test56() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test57() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test58() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test59() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test60() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test61() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test62() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test63() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test64() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test65() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test66() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test67() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test68() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test69() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test70() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test71() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test72() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test73() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test74() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test75() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test76() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test77() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test78() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test79() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test80() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test81() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test82() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test83() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test84() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test85() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test86() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test87() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test88() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test89() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test90() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test91() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test92() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test93() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test94() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test95() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test96() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test97() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test98() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test99() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}
	@Test
	public void test100() throws Exception {
		testElExpressionWithUndefinedScopedVariablesJsp();
	}

	@Test
	public void testElExpressionWithUndefinedScopedVariablesJsp()
		throws Exception {

		_test(_FILE_NAME_EL_EXPRESSION_UNDEFINED_SCOPED_VARIABLES_JSP, 1);

		try (PerformanceTimer performanceTimer = new PerformanceTimer(5000)) {
			_test(
				_FILE_NAME_EL_EXPRESSION_UNDEFINED_SCOPED_VARIABLES_JSP,
				_NUMBER_OF_REQUESTS);
		}
	}

	@Test
	public void testElExpressionWithUndefinedVariablesJsp() throws Exception {
		_test(_FILE_NAME_EL_EXPRESSION_UNDEFINED_VARIABLES_JSP, 1);

		try (PerformanceTimer performanceTimer = new PerformanceTimer(1000)) {
			_test(
				_FILE_NAME_EL_EXPRESSION_UNDEFINED_VARIABLES_JSP,
				_NUMBER_OF_REQUESTS);
		}
	}

	@Test
	public void testJsp() throws Exception {
		_test(_FILE_NAME_TEST_JSP, 1);

		try (PerformanceTimer performanceTimer = new PerformanceTimer(1000)) {
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
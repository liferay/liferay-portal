/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.aggregate;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.ServletContextUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.ServletContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import java.util.List;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.springframework.mock.web.MockServletContext;

/**
 * @author Shuyang Zhou
 */
public class ServletPathsTest {

	@ClassRule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		FileUtil.deltree(_testDir);
	}

	@AfterClass
	public static void tearDownClass() {
		FileUtil.deltree(_testDir);
	}

	@Test
	public void testConstructor() {
		try {
			new ServletPaths(null, null);

			Assert.fail();
		}
		catch (NullPointerException nullPointerException) {
			Assert.assertEquals(
				"Servlet context is null", nullPointerException.getMessage());
		}

		try {
			new ServletPaths(new MockServletContext(), null);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Resource path is null", illegalArgumentException.getMessage());
		}

		ServletContext servletContext = _prepareServletContext(
			new MockServletContext());

		ServletPaths servletPaths = new ServletPaths(
			servletContext, "/test1/test2/");

		Assert.assertEquals("/test1/test2/", servletPaths.getResourcePath());

		servletPaths = new ServletPaths(servletContext, "test1/");

		Assert.assertEquals("test1/", servletPaths.getResourcePath());
	}

	@Test
	public void testDown() {
		ServletContext servletContext = _prepareServletContext(
			new MockServletContext());

		ServletPaths servletPaths1 = new ServletPaths(servletContext, "/test1");

		Assert.assertSame(servletPaths1, servletPaths1.down(null));
		Assert.assertSame(servletPaths1, servletPaths1.down(StringPool.SLASH));

		ServletPaths servletPaths2 = servletPaths1.down("test2/");

		Assert.assertEquals("/test1/test2", servletPaths2.getResourcePath());

		ServletPaths servletPaths3 = servletPaths1.down("/test2");

		Assert.assertEquals("/test1/test2", servletPaths3.getResourcePath());

		ServletPaths servletPaths4 = new ServletPaths(servletContext, "test1/");

		ServletPaths servletPaths5 = servletPaths4.down("test2");

		Assert.assertEquals("test1/test2", servletPaths5.getResourcePath());

		ServletPaths servletPaths6 = servletPaths1.down(
			"/test2?extraparameters");

		Assert.assertEquals("/test1/test2", servletPaths6.getResourcePath());

		ServletPaths servletPaths7 = servletPaths1.down("../test2");

		Assert.assertEquals("/test2", servletPaths7.getResourcePath());

		ServletPaths servletPaths8 = servletPaths1.down("./test2");

		Assert.assertEquals("/test1/test2", servletPaths8.getResourcePath());
	}

	@Test
	public void testGetContent() throws IOException {
		final File file1 = new File(_testDir, "test1");
		final File file2 = new File(_testDir, "test2");

		String testContent = "Test Content";

		FileUtil.write(file2, testContent);

		ServletContext servletContext = new MockServletContext() {

			@Override
			public URL getResource(String path) throws MalformedURLException {
				if (path.contains(file1.getName())) {
					URI uri = file1.toURI();

					return uri.toURL();
				}

				if (path.contains(file2.getName())) {
					URI uri = file2.toURI();

					return uri.toURL();
				}

				return super.getResource(path);
			}

		};

		servletContext = _prepareServletContext(servletContext);

		ServletPaths servletPaths = new ServletPaths(servletContext, "test");

		Assert.assertNull(servletPaths.getContent());

		try (LogCapture logCapture = LoggerTestUtil.configureJDKLogger(
				ServletPaths.class.getName(), Level.SEVERE)) {

			List<LogEntry> logEntries = logCapture.getLogEntries();

			servletPaths = new ServletPaths(servletContext, file1.getName());

			Assert.assertNull(servletPaths.getContent());

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(
				FileNotFoundException.class, throwable.getClass());

			servletPaths = new ServletPaths(servletContext, file2.getName());

			Assert.assertEquals(testContent, servletPaths.getContent());
		}
	}

	@Test
	public void testGetParentPath() {
		try {
			ServletPaths.getParentPath(null);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Resource path is null", illegalArgumentException.getMessage());
		}

		Assert.assertEquals(
			"test1", ServletPaths.getParentPath("test1/test2/"));
		Assert.assertEquals("test1", ServletPaths.getParentPath("test1/test2"));
		Assert.assertEquals("test1", ServletPaths.getParentPath("test1/"));
		Assert.assertEquals("test1", ServletPaths.getParentPath("test1"));
	}

	private ServletContext _prepareServletContext(
		ServletContext servletContext) {

		File webINFFile = new File(_testDir, ServletContextUtil.PATH_WEB_INF);

		servletContext.setAttribute(
			ServletContextUtil.URI_ATTRIBUTE, webINFFile.toURI());

		return servletContext;
	}

	private static final File _testDir = new File(
		SystemProperties.get(SystemProperties.TMP_DIR),
		ServletPathsTest.class.getSimpleName());

}
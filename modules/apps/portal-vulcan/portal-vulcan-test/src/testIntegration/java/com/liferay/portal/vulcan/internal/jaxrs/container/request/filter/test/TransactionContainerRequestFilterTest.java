/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.request.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.internal.test.util.URLConnectionUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import java.io.IOException;

import java.net.HttpURLConnection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class TransactionContainerRequestFilterTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			TransactionContainerRequestFilterTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistrations = Arrays.asList(
			bundleContext.registerService(
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
				).put(
					"osgi.jaxrs.name", "Test.Vulcan"
				).build()),
			bundleContext.registerService(
				ExceptionMapper.class, new TestExceptionMapper(),
				HashMapDictionaryBuilder.<String, Object>put(
					"osgi.jaxrs.application.select",
					"(osgi.jaxrs.name=Test.Vulcan)"
				).put(
					"osgi.jaxrs.extension", "true"
				).put(
					"osgi.jaxrs.name", "TestVulcan.TestExceptionMapper"
				).build()));
	}

	@After
	public void tearDown() {
		_serviceRegistrations.forEach(ServiceRegistration::unregister);
	}

	@Test(expected = NoSuchGroupException.class)
	public void testCommit() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Assert.assertEquals(
			204,
			_getResponseCode(
				"http://localhost:8080/o/test-vulcan/commit/" +
					group.getGroupId()));
		Assert.assertNull(GroupLocalServiceUtil.getGroup(group.getGroupId()));
	}

	@Test
	public void testRollback() throws Exception {
		Group group = GroupTestUtil.addGroup();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_EXCEPTION_MAPPER, LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				500,
				_getResponseCode(
					"http://localhost:8080/o/test-vulcan/rollback/" +
						group.getGroupId() + "?failInExceptionMapper=false"));

			Assert.assertNotNull(
				GroupLocalServiceUtil.getGroup(group.getGroupId()));

			Assert.assertEquals(
				500,
				_getResponseCode(
					"http://localhost:8080/o/test-vulcan/rollback/" +
						group.getGroupId() + "?failInExceptionMapper=true"));

			Assert.assertNotNull(
				GroupLocalServiceUtil.getGroup(group.getGroupId()));
		}
	}

	public static class TestApplication extends Application {

		@Override
		public Set<Object> getSingletons() {
			return Collections.singleton(this);
		}

		@DELETE
		@Path("/commit/{siteId}")
		public void testCommit(@PathParam("siteId") long siteId)
			throws Exception {

			GroupLocalServiceUtil.deleteGroup(siteId);
		}

		@DELETE
		@Path("/rollback/{siteId}")
		public void testRollback(
				@QueryParam("failInExceptionMapper") boolean
					failInExceptionMapper,
				@PathParam("siteId") long siteId)
			throws Exception {

			GroupLocalServiceUtil.deleteGroup(siteId);

			throw new TestException(failInExceptionMapper);
		}

	}

	public static class TestException extends RuntimeException {

		public TestException(boolean failInExceptionMapper) {
			_failInExceptionMapper = failInExceptionMapper;
		}

		public boolean isFailInExceptionMapper() {
			return _failInExceptionMapper;
		}

		private final boolean _failInExceptionMapper;

	}

	public static class TestExceptionMapper
		extends BaseExceptionMapper<TestException> {

		@Override
		protected Problem getProblem(TestException testException) {
			if (testException.isFailInExceptionMapper()) {
				throw testException;
			}

			return new Problem() {
				{
					setStatus(Response.Status.INTERNAL_SERVER_ERROR);
				}
			};
		}

	}

	private int _getResponseCode(String urlString) throws IOException {
		HttpURLConnection httpURLConnection =
			(HttpURLConnection)URLConnectionUtil.createURLConnection(urlString);

		httpURLConnection.setRequestMethod("DELETE");

		return httpURLConnection.getResponseCode();
	}

	private static final String _CLASS_NAME_EXCEPTION_MAPPER =
		"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
			"ExceptionMapper";

	private List<ServiceRegistration<?>> _serviceRegistrations;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.lifecycle.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class SafeReleaseInstanceResourceProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			SafeReleaseInstanceResourceProviderTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_applicationServiceRegistration = bundleContext.registerService(
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
			).put(
				"osgi.jaxrs.name", "Liferay.Headless.Test"
			).build());

		_resourceServiceRegistration = bundleContext.registerService(
			TestResource.class,
			new PrototypeServiceFactory<TestResource>() {

				@Override
				public TestResource getService(
					Bundle bundle,
					ServiceRegistration<TestResource> serviceRegistration) {

					return new TestResource();
				}

				@Override
				public void ungetService(
					Bundle bundle,
					ServiceRegistration<TestResource> serviceRegistration,
					TestResource testResource) {
				}

			},
			HashMapDictionaryBuilder.<String, Object>put(
				"api.version", "v1.0"
			).put(
				"osgi.jaxrs.application.select",
				"(osgi.jaxrs.name=Liferay.Headless.Test)"
			).put(
				"osgi.jaxrs.resource", "true"
			).build());
	}

	@After
	public void tearDown() {
		_applicationServiceRegistration.unregister();
		_resourceServiceRegistration.unregister();
	}

	@Test
	public void testGet() throws Exception {
		System.gc();

		System.runFinalization();

		// Invalid endpoint

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					StringPool.BLANK, "test-vulcan/404", Http.Method.GET));
		}

		// Invalid filter

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				StringPool.BLANK,
				"test-vulcan/test/" + TestPropsValues.getGroupId() +
					"?filter=invalid",
				Http.Method.GET));

		// Invalid site ID

		Assert.assertEquals(
			404,
			HTTPTestUtil.invokeToHttpCode(
				StringPool.BLANK, "test-vulcan/test/0", Http.Method.GET));

		// Valid site ID

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				StringPool.BLANK,
				"test-vulcan/test/" + TestPropsValues.getGroupId(),
				Http.Method.GET));

		System.gc();

		System.runFinalization();

		Assert.assertTrue(_instancesCountDownLatch.await(5, TimeUnit.SECONDS));
	}

	public static class TestApplication extends Application {
	}

	public static class TestResource implements EntityModelResource {

		public TestResource() {
			_instancesCountDownLatch = new CountDownLatch(
				(int)(_instancesCountDownLatch.getCount() + 1));
		}

		@Override
		public EntityModel getEntityModel(MultivaluedMap<?, ?> multivaluedMap) {
			return Collections::emptyMap;
		}

		@Override
		public void setContextCompany(Company contextCompany) {
		}

		@GET
		@Path("/test/{siteId}")
		@Produces("application/json")
		public Response test(
			@PathParam("siteId") Long siteId, @Context Filter filter) {

			return Response.ok(
			).build();
		}

		@GET
		@Path("/test-exception")
		@Produces("application/json")
		public Response testException() {
			throw new NotFoundException();
		}

		@Override
		protected void finalize() throws Throwable {
			super.finalize();

			_instancesCountDownLatch.countDown();
		}

	}

	private static CountDownLatch _instancesCountDownLatch = new CountDownLatch(
		0);

	private ServiceRegistration<Application> _applicationServiceRegistration;
	private ServiceRegistration<TestResource> _resourceServiceRegistration;

}
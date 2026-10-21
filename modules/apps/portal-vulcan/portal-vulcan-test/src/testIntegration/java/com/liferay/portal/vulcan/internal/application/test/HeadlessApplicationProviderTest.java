/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.application.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManager;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.application.HeadlessApplicationProvider;
import com.liferay.portal.vulcan.internal.test.util.FeatureFlagManagerTestUtil;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class HeadlessApplicationProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			HeadlessApplicationProviderTest.class);

		_bundleContext = bundle.getBundleContext();

		_serviceRegistrations = Arrays.asList(
			_bundleContext.registerService(
				Application.class, new TestApplication(),
				HashMapDictionaryBuilder.<String, Object>put(
					"liferay.auth.verifier", true
				).put(
					"liferay.oauth2", false
				).put(
					"osgi.jaxrs.application.base", "/test-vulcan-application"
				).put(
					"osgi.jaxrs.extension.select",
					"(osgi.jaxrs.name=Liferay.Vulcan)"
				).put(
					"osgi.jaxrs.name", "Test.Vulcan"
				).build()),
			_bundleContext.registerService(
				TestResource_v1_0.class, new TestResource_v1_0(),
				HashMapDictionaryBuilder.<String, Object>put(
					"api.version", "v1.0"
				).put(
					"osgi.jaxrs.application.select",
					"(osgi.jaxrs.name=Test.Vulcan)"
				).put(
					"osgi.jaxrs.resource", "true"
				).build()),
			_bundleContext.registerService(
				TestResource_v2_0.class, new TestResource_v2_0(),
				HashMapDictionaryBuilder.<String, Object>put(
					"api.version", "v2.0"
				).put(
					"osgi.jaxrs.application.select",
					"(osgi.jaxrs.name=Test.Vulcan)"
				).put(
					"osgi.jaxrs.resource", "true"
				).build()),
			_bundleContext.registerService(
				Application.class, new TestApplication(),
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId",
					Collections.singletonList(
						String.valueOf(TestPropsValues.getCompanyId()))
				).put(
					"liferay.auth.verifier", true
				).put(
					"liferay.oauth2", false
				).put(
					"osgi.jaxrs.application.base",
					"/test-vulcan-application-company"
				).put(
					"osgi.jaxrs.extension.select",
					"(osgi.jaxrs.name=Liferay.Vulcan)"
				).put(
					"osgi.jaxrs.name", "Test.Vulcan.Company"
				).build()),
			_bundleContext.registerService(
				Application.class, new TestApplication(),
				HashMapDictionaryBuilder.<String, Object>put(
					"liferay.auth.verifier", true
				).put(
					"liferay.oauth2", false
				).put(
					"osgi.jaxrs.application.base",
					"/test-vulcan-application-feature-flag"
				).put(
					"osgi.jaxrs.extension.select",
					"(osgi.jaxrs.name=Liferay.Vulcan)"
				).put(
					"osgi.jaxrs.name", "Test.Vulcan.FeatureFlag"
				).build()),
			_bundleContext.registerService(
				TestFeatureFlagResource_v1_0.class,
				new TestFeatureFlagResource_v1_0(),
				HashMapDictionaryBuilder.<String, Object>put(
					"api.version", "v1.0"
				).put(
					"openapi.resource", "true"
				).put(
					"openapi.resource.path",
					"/test-vulcan-application-feature-flag"
				).put(
					"osgi.jaxrs.application.select",
					"(osgi.jaxrs.name=Test.Vulcan.FeatureFlag)"
				).put(
					"osgi.jaxrs.resource", "true"
				).build()),
			_bundleContext.registerService(
				Application.class, new TestApplication(),
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId",
					Collections.singletonList(
						String.valueOf(TestPropsValues.getCompanyId() + 1))
				).put(
					"liferay.auth.verifier", true
				).put(
					"liferay.oauth2", false
				).put(
					"osgi.jaxrs.application.base",
					"/test-vulcan-application-other-company"
				).put(
					"osgi.jaxrs.extension.select",
					"(osgi.jaxrs.name=Liferay.Vulcan)"
				).put(
					"osgi.jaxrs.name", "Test.Vulcan.OtherCompany"
				).build()),
			_bundleContext.registerService(
				Application.class, new TestApplication(),
				HashMapDictionaryBuilder.<String, Object>put(
					"liferay.auth.verifier", true
				).put(
					"liferay.oauth2", false
				).put(
					"osgi.jaxrs.application.base",
					"/test-vulcan-application-undeclared-feature-flag"
				).put(
					"osgi.jaxrs.extension.select",
					"(osgi.jaxrs.name=Liferay.Vulcan)"
				).put(
					"osgi.jaxrs.name", "Test.Vulcan.UndeclaredFeatureFlag"
				).build()),
			_bundleContext.registerService(
				TestUndeclaredFeatureFlagResource_v1_0.class,
				new TestUndeclaredFeatureFlagResource_v1_0(),
				HashMapDictionaryBuilder.<String, Object>put(
					"api.version", "v1.0"
				).put(
					"openapi.resource", "true"
				).put(
					"openapi.resource.path",
					"/test-vulcan-application-undeclared-feature-flag"
				).put(
					"osgi.jaxrs.application.select",
					"(osgi.jaxrs.name=Test.Vulcan.UndeclaredFeatureFlag)"
				).put(
					"osgi.jaxrs.resource", "true"
				).build()),
			_bundleContext.registerService(
				Application.class, new TestApplication(),
				HashMapDictionaryBuilder.<String, Object>put(
					"liferay.auth.verifier", true
				).put(
					"liferay.oauth2", false
				).put(
					"osgi.jaxrs.application.base",
					"/test-vulcan-application-unversioned"
				).put(
					"osgi.jaxrs.extension.select",
					"(osgi.jaxrs.name=Liferay.Vulcan)"
				).put(
					"osgi.jaxrs.name", "Test.Vulcan.Unversioned"
				).build()),
			_bundleContext.registerService(
				TestResource.class, new TestResource(),
				HashMapDictionaryBuilder.<String, Object>put(
					"osgi.jaxrs.application.select",
					"(osgi.jaxrs.name=Test.Vulcan.Unversioned)"
				).put(
					"osgi.jaxrs.resource", "true"
				).build()));
	}

	@After
	public void tearDown() {
		_serviceRegistrations.forEach(ServiceRegistration::unregister);
	}

	@Test
	public void testGetApplications() throws Exception {
		HeadlessApplicationProvider.Application application = _getApplication(
			"/test-vulcan-application");

		Assert.assertEquals(
			"/test-vulcan-application", application.getBasePath());

		Assert.assertEquals(
			ListUtil.fromArray("v1.0", "v2.0"),
			TransformUtil.transform(
				application.getOpenAPIDocuments(),
				HeadlessApplicationProvider.OpenAPIDocument::getVersion));

		HeadlessApplicationProvider.OpenAPIDocument openAPIDocument =
			_getOpenAPIDocument(application, "v1.0");

		Assert.assertEquals(
			"/test-vulcan-application/v1.0/openapi.json",
			openAPIDocument.getPath(
				HeadlessApplicationProvider.OpenAPIDocument.Type.JSON));
		Assert.assertEquals(
			"/test-vulcan-application/v1.0/openapi.yaml",
			openAPIDocument.getPath(
				HeadlessApplicationProvider.OpenAPIDocument.Type.YAML));

		Assert.assertSame(application, openAPIDocument.getApplication());

		HeadlessApplicationProvider.ResourceMethod resourceMethod =
			_getResourceMethod(
				application, "/test-vulcan-application/v1.0/tests");

		Assert.assertEquals("GET", resourceMethod.getMethod());
		Assert.assertArrayEquals(
			new String[] {MediaType.APPLICATION_JSON},
			resourceMethod.getProducingMimeTypes());

		application = _getApplication("/test-vulcan-application-unversioned");

		List<HeadlessApplicationProvider.OpenAPIDocument> openAPIDocuments =
			application.getOpenAPIDocuments();

		Assert.assertEquals(
			openAPIDocuments.toString(), 1, openAPIDocuments.size());

		openAPIDocument = openAPIDocuments.get(0);

		Assert.assertNull(openAPIDocument.getVersion());
		Assert.assertEquals(
			"/test-vulcan-application-unversioned/openapi.json",
			openAPIDocument.getPath(
				HeadlessApplicationProvider.OpenAPIDocument.Type.JSON));

		Assert.assertNotNull(
			_getApplication("/test-vulcan-application-company"));
		Assert.assertNull(
			_getApplication("/test-vulcan-application-other-company"));

		Assert.assertNull(_getApplication("/test-vulcan-application-added"));

		ServiceRegistration<?> serviceRegistration =
			_bundleContext.registerService(
				Application.class, new TestApplication(),
				HashMapDictionaryBuilder.<String, Object>put(
					"liferay.auth.verifier", true
				).put(
					"liferay.oauth2", false
				).put(
					"osgi.jaxrs.application.base",
					"/test-vulcan-application-added"
				).put(
					"osgi.jaxrs.extension.select",
					"(osgi.jaxrs.name=Liferay.Vulcan)"
				).put(
					"osgi.jaxrs.name", "Test.Vulcan.Added"
				).build());

		try {
			Assert.assertNotNull(
				_getApplication("/test-vulcan-application-added"));
		}
		finally {
			serviceRegistration.unregister();
		}

		Assert.assertNull(_getApplication("/test-vulcan-application-added"));
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY)
	@Test
	public void testGetApplicationsWhenFeatureFlagIsDisabled()
		throws Exception {

		Assert.assertNull(
			_getApplication("/test-vulcan-application-feature-flag"));
	}

	@FeatureFlag(_FEATURE_FLAG_KEY)
	@Test
	public void testGetApplicationsWhenFeatureFlagIsEnabled() throws Exception {
		HeadlessApplicationProvider.Application application = _getApplication(
			"/test-vulcan-application-feature-flag");

		Assert.assertEquals(
			ListUtil.fromArray("v1.0"),
			TransformUtil.transform(
				application.getOpenAPIDocuments(),
				HeadlessApplicationProvider.OpenAPIDocument::getVersion));
	}

	@Test
	public void testGetApplicationsWhenFeatureFlagIsUndeclared()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.application." +
					"HeadlessApplicationProviderImpl",
				LoggerTestUtil.WARN);
			SafeCloseable safeCloseable =
				FeatureFlagManagerTestUtil.swapWithSafeCloseable(
					_featureFlagManager)) {

			Assert.assertNull(
				_getApplication(
					"/test-vulcan-application-undeclared-feature-flag"));

			List<String> messages = TransformUtil.transform(
				logCapture.getLogEntries(), LogEntry::getMessage);

			Assert.assertTrue(
				messages.toString(),
				messages.contains(
					StringBundler.concat(
						"Feature flag ", _FEATURE_FLAG_KEY_UNDECLARED,
						" is not available for company ",
						TestPropsValues.getCompanyId())));
		}
	}

	public static class TestApplication extends Application {

		@Override
		public Set<Object> getSingletons() {
			return Collections.singleton(this);
		}

	}

	@com.liferay.portal.vulcan.feature.flag.FeatureFlag(_FEATURE_FLAG_KEY)
	@Path("/v1.0")
	public static class TestFeatureFlagResource_v1_0 {

		@GET
		@Path("/openapi.{type:json|yaml}")
		@Produces(MediaType.APPLICATION_JSON)
		public String getOpenAPI(@PathParam("type") String type) {
			return type;
		}

	}

	public static class TestResource {

		@GET
		@Path("/openapi.{type:json|yaml}")
		@Produces(MediaType.APPLICATION_JSON)
		public String getOpenAPI(@PathParam("type") String type) {
			return type;
		}

	}

	@Path("/v1.0")
	public static class TestResource_v1_0 {

		@GET
		@Path("/openapi.{type:json|yaml}")
		@Produces(MediaType.APPLICATION_JSON)
		public String getOpenAPI(@PathParam("type") String type) {
			return type;
		}

		@GET
		@Path("/tests")
		@Produces(MediaType.APPLICATION_JSON)
		public String getTestsPage() {
			return "[]";
		}

	}

	@Path("/v2.0")
	public static class TestResource_v2_0 {

		@GET
		@Path("/openapi.{type:json|yaml}")
		@Produces(MediaType.APPLICATION_JSON)
		public String getOpenAPI(@PathParam("type") String type) {
			return type;
		}

		@GET
		@Path("/tests")
		@Produces(MediaType.APPLICATION_JSON)
		public String getTestsPage() {
			return "[]";
		}

	}

	@com.liferay.portal.vulcan.feature.flag.FeatureFlag(
		_FEATURE_FLAG_KEY_UNDECLARED
	)
	@Path("/v1.0")
	public static class TestUndeclaredFeatureFlagResource_v1_0 {

		@GET
		@Path("/openapi.{type:json|yaml}")
		@Produces(MediaType.APPLICATION_JSON)
		public String getOpenAPI(@PathParam("type") String type) {
			return type;
		}

	}

	private HeadlessApplicationProvider.Application _getApplication(
			String basePath)
		throws Exception {

		for (HeadlessApplicationProvider.Application application :
				_headlessApplicationProvider.getApplications(
					TestPropsValues.getCompanyId())) {

			if (basePath.equals(application.getBasePath())) {
				return application;
			}
		}

		return null;
	}

	private HeadlessApplicationProvider.OpenAPIDocument _getOpenAPIDocument(
		HeadlessApplicationProvider.Application application, String version) {

		for (HeadlessApplicationProvider.OpenAPIDocument openAPIDocument :
				application.getOpenAPIDocuments()) {

			if (Objects.equals(version, openAPIDocument.getVersion())) {
				return openAPIDocument;
			}
		}

		return null;
	}

	private HeadlessApplicationProvider.ResourceMethod _getResourceMethod(
		HeadlessApplicationProvider.Application application, String path) {

		for (HeadlessApplicationProvider.ResourceMethod resourceMethod :
				application.getResourceMethods()) {

			if (path.equals(resourceMethod.getPath())) {
				return resourceMethod;
			}
		}

		return null;
	}

	private static final String _FEATURE_FLAG_KEY = "APPLICATION-123";

	private static final String _FEATURE_FLAG_KEY_UNDECLARED = "UNDECLARED-123";

	private BundleContext _bundleContext;

	@Inject
	private FeatureFlagManager _featureFlagManager;

	@Inject
	private HeadlessApplicationProvider _headlessApplicationProvider;

	private List<ServiceRegistration<?>> _serviceRegistrations;

}
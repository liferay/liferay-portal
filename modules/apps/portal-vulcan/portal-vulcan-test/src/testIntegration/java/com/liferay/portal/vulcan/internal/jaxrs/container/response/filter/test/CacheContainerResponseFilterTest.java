/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.response.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.internal.test.util.URLConnectionUtil;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
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
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class CacheContainerResponseFilterTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			CacheContainerResponseFilterTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			Application.class,
			new CacheContainerResponseFilterTest.TestApplication(),
			HashMapDictionaryBuilder.<String, Object>put(
				"auth.verifier.guest.allowed", true
			).put(
				"liferay.access.control.disable", true
			).put(
				"liferay.auth.verifier", true
			).put(
				"liferay.oauth2", false
			).put(
				"osgi.jaxrs.application.base", "/test-vulcan-cache"
			).put(
				"osgi.jaxrs.extension.select",
				"(osgi.jaxrs.name=Liferay.Vulcan)"
			).build());
	}

	@After
	public void tearDown() throws Exception {
		for (String pid : _pids) {
			ConfigurationTestUtil.deleteFactoryConfiguration(pid, _FACTORY_PID);
		}

		_pids.clear();

		_serviceRegistration.unregister();

		if (_company != null) {
			CompanyLocalServiceUtil.deleteCompany(_company.getCompanyId());
		}
	}

	@Test
	public void testCache() throws Exception {
		_assertNotCacheable(_openURLConnection("/test"));
	}

	@Test
	public void testCacheWithAnotherCompany() throws Exception {
		_company = CompanyTestUtil.addCompany();

		_addCacheableEndpoint(
			"/test-vulcan-cache/test", "public", 3600, _company.getCompanyId());

		_assertNotCacheable(_openURLConnection("/test"));
	}

	@Test
	public void testCacheWithCacheableEndpoint() throws Exception {
		_addCacheableEndpoint("/test-vulcan-cache/test", "public", 3600);

		HttpURLConnection httpURLConnection = _openURLConnection("/test");

		Assert.assertEquals(
			"public, max-age=3600", _getCacheControl(httpURLConnection));
		Assert.assertEquals(
			"Accept, Accept-Encoding, Accept-Language, Origin, " +
				"X-Accept-All-Languages, X-Liferay-Accept-All-Languages, " +
					"X-Liferay-Data-Masks",
			httpURLConnection.getHeaderField("Vary"));
	}

	@Test
	public void testCacheWithHeadRequest() throws Exception {
		_addCacheableEndpoint("/test-vulcan-cache/test", "public", 3600);

		HttpURLConnection httpURLConnection = _openURLConnection("/test");

		httpURLConnection.setRequestMethod("HEAD");

		Assert.assertEquals(
			"public, max-age=3600", _getCacheControl(httpURLConnection));
	}

	@Test
	public void testCacheWithHttpSession() throws Exception {
		_addCacheableEndpoint(
			"/test-vulcan-cache/with-http-session", "public", 3600);

		_assertNotCacheable(_openURLConnection("/with-http-session"));
	}

	@Test
	public void testCacheWithOverlappingCacheableEndpoints() throws Exception {
		_addCacheableEndpoint(
			"/test-vulcan-cache/tests/*/nested", "private", 0);
		_addCacheableEndpoint("/test-vulcan-cache/tests/1/nested", "public", 0);

		Assert.assertEquals(
			"public", _getCacheControl(_openURLConnection("/tests/1/nested")));
		Assert.assertEquals(
			"private", _getCacheControl(_openURLConnection("/tests/2/nested")));
	}

	@Test
	public void testCacheWithOverlappingCacheableEndpointsAndEqualWildcardCount()
		throws Exception {

		_addCacheableEndpoint(
			"/test-vulcan-cache/tests/*/nested", "private", 0);
		_addCacheableEndpoint("/test-vulcan-cache/tests/1/*", "public", 0);

		Assert.assertEquals(
			"public", _getCacheControl(_openURLConnection("/tests/1/nested")));
	}

	@Test
	public void testCacheWithOverlappingCacheableEndpointsAndEqualWildcardCountInReverseOrder()
		throws Exception {

		_addCacheableEndpoint("/test-vulcan-cache/tests/1/*", "public", 0);
		_addCacheableEndpoint(
			"/test-vulcan-cache/tests/*/nested", "private", 0);

		Assert.assertEquals(
			"public", _getCacheControl(_openURLConnection("/tests/1/nested")));
	}

	@Test
	public void testCacheWithPostRequest() throws Exception {
		_addCacheableEndpoint("/test-vulcan-cache/test", "public", 3600);

		HttpURLConnection httpURLConnection = _openURLConnection("/test");

		httpURLConnection.setRequestMethod("POST");

		_assertNotCacheable(httpURLConnection);
	}

	@Test
	public void testCacheWithSetCookie() throws Exception {
		_addCacheableEndpoint("/test-vulcan-cache/with-cookie", "public", 3600);

		_assertNotCacheable(_openURLConnection("/with-cookie"));
	}

	@Test
	public void testCacheWithSignedInUser() throws Exception {
		_addCacheableEndpoint("/test-vulcan-cache/test", "public", 3600);

		_assertNotCacheable(_openAuthenticatedURLConnection("/test"));
	}

	@Test
	public void testCacheWithUnsuccessfulResponse() throws Exception {
		_addCacheableEndpoint("/test-vulcan-cache/not-found", "public", 3600);

		_assertNotCacheable(_openURLConnection("/not-found"));
	}

	@Test
	public void testCacheWithoutMaxAge() throws Exception {
		_addCacheableEndpoint("/test-vulcan-cache/test", "public", 0);

		Assert.assertEquals(
			"public", _getCacheControl(_openURLConnection("/test")));
	}

	public static class TestApplication extends Application {

		@Override
		public Set<Object> getSingletons() {
			return Collections.singleton(this);
		}

		@GET
		@Path("/tests/{testId}/nested")
		public void nested() {
		}

		@GET
		@Path("/not-found")
		public void notFound() {
			throw new NotFoundException();
		}

		@GET
		@Path("/test")
		public void test() {
		}

		@Path("/test")
		@POST
		public void testPost() {
		}

		@GET
		@Path("/with-cookie")
		public Response withCookie() {
			return Response.ok(
			).cookie(
				new NewCookie.Builder(
					"TEST_COOKIE"
				).value(
					"1"
				).build()
			).build();
		}

		@GET
		@Path("/with-http-session")
		public void withHttpSession(
			@Context HttpServletRequest httpServletRequest) {

			httpServletRequest.getSession();
		}

	}

	private void _addCacheableEndpoint(
			String path, String cacheControl, int maxAge)
		throws Exception {

		_addCacheableEndpoint(
			path, cacheControl, maxAge, TestPropsValues.getCompanyId());
	}

	private void _addCacheableEndpoint(
			String path, String cacheControl, int maxAge, long companyId)
		throws Exception {

		_pids.add(
			ConfigurationTestUtil.createFactoryConfiguration(
				_FACTORY_PID,
				HashMapDictionaryBuilder.<String, Object>put(
					"cacheControl", cacheControl
				).put(
					"companyId", companyId
				).put(
					"maxAge", maxAge
				).put(
					"path", path
				).build()));
	}

	private void _assertNotCacheable(HttpURLConnection httpURLConnection) {
		Assert.assertEquals(
			"no-cache, no-store", _getCacheControl(httpURLConnection));
		Assert.assertNull(httpURLConnection.getHeaderField("Vary"));
	}

	private String _getCacheControl(HttpURLConnection httpURLConnection) {
		return httpURLConnection.getHeaderField("Cache-Control");
	}

	private String _getURL(String path) {
		return StringBundler.concat(
			"http://localhost:", PortalUtil.getPortalServerPort(false),
			"/o/test-vulcan-cache", path);
	}

	private HttpURLConnection _openAuthenticatedURLConnection(String path)
		throws Exception {

		return (HttpURLConnection)URLConnectionUtil.createURLConnection(
			_getURL(path));
	}

	private HttpURLConnection _openURLConnection(String path) throws Exception {
		URL url = new URL(_getURL(path));

		return (HttpURLConnection)url.openConnection();
	}

	private static final String _FACTORY_PID =
		"com.liferay.portal.vulcan.internal.configuration." +
			"HeadlessAPICacheCompanyConfiguration";

	private Company _company;
	private final List<String> _pids = new ArrayList<>();
	private ServiceRegistration<Application> _serviceRegistration;

}
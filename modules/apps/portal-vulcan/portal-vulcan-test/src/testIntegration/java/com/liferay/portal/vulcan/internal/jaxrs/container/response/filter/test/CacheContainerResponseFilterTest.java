/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.response.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;

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
	}

	@Test
	public void testCache() throws Exception {
		_assertNotCacheable(_openURLConnection("/test"));
	}

	@Test
	public void testCacheWithCacheableEndpoint() throws Exception {
		_addCacheableEndpoint("/test-vulcan-cache/test", "public", 3600);

		HttpURLConnection httpURLConnection = _openURLConnection("/test");

		Assert.assertEquals(
			"public, max-age=3600", _getCacheControl(httpURLConnection));
	}

	@Test
	public void testCacheWithoutMaxAge() throws Exception {
		_addCacheableEndpoint("/test-vulcan-cache/test", "public", 0);

		Assert.assertEquals(
			"public", _getCacheControl(_openURLConnection("/test")));
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
		@Path("/test")
		public void test() {
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
	}

	private String _getCacheControl(HttpURLConnection httpURLConnection) {
		return httpURLConnection.getHeaderField("Cache-Control");
	}

	private String _getURL(String path) {
		return StringBundler.concat(
			"http://localhost:", PortalUtil.getPortalServerPort(false),
			"/o/test-vulcan-cache", path);
	}

	private HttpURLConnection _openURLConnection(String path) throws Exception {
		URL url = new URL(_getURL(path));

		return (HttpURLConnection)url.openConnection();
	}

	private static final String _FACTORY_PID =
		"com.liferay.portal.vulcan.internal.configuration." +
			"HeadlessAPICacheCompanyConfiguration";

	private final List<String> _pids = new ArrayList<>();
	private ServiceRegistration<Application> _serviceRegistration;

}
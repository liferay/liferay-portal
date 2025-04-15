/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.response.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.internal.test.util.URLConnectionUtil;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;

import java.net.HttpURLConnection;

import java.util.Collections;
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
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testCache() throws Exception {
		HttpURLConnection httpURLConnection =
			(HttpURLConnection)URLConnectionUtil.createURLConnection(
				"http://localhost:8080/o/test-vulcan-cache/test");

		Assert.assertEquals(
			"no-cache, no-store",
			httpURLConnection.getHeaderField("Cache-Control"));
	}

	public static class TestApplication extends Application {

		@Override
		public Set<Object> getSingletons() {
			return Collections.singleton(this);
		}

		@GET
		@Path("/test")
		public void test() {
		}

	}

	private ServiceRegistration<Application> _serviceRegistration;

}
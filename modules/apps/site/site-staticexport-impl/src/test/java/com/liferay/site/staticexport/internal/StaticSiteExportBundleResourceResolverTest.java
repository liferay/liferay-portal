/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Shuyang Zhou
 */
public class StaticSiteExportBundleResourceResolverTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetResourcePaths() throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		String moduleName = RandomTestUtil.randomString();

		ServiceRegistration<ServletContextHelper> serviceRegistration1 =
			_registerServletContextHelper(bundleContext, moduleName, "/a.js");

		ServiceRegistration<ServletContextHelper> serviceRegistration2 =
			_registerServletContextHelper(bundleContext, "(*)\\", "/b.js");

		try {
			StaticSiteExportBundleResourceResolver
				staticSiteExportBundleResourceResolver =
					new StaticSiteExportBundleResourceResolver(bundleContext);

			Assert.assertEquals(
				Collections.singleton("/a.js"),
				staticSiteExportBundleResourceResolver.getResourcePaths(
					moduleName, "/"));

			Set<String> resourcePaths =
				staticSiteExportBundleResourceResolver.getResourcePaths(
					"*", "/");

			Assert.assertTrue(
				resourcePaths.toString(), resourcePaths.isEmpty());

			Assert.assertEquals(
				Collections.singleton("/b.js"),
				staticSiteExportBundleResourceResolver.getResourcePaths(
					"(*)\\", "/"));
		}
		finally {
			serviceRegistration1.unregister();
			serviceRegistration2.unregister();
		}
	}

	private ServiceRegistration<ServletContextHelper>
		_registerServletContextHelper(
			BundleContext bundleContext, String moduleName,
			String resourcePath) {

		return bundleContext.registerService(
			ServletContextHelper.class,
			new ServletContextHelper() {

				@Override
				public Set<String> getResourcePaths(String path) {
					if (path.equals("/META-INF/resources/")) {
						return Collections.singleton(
							"/META-INF/resources" + resourcePath);
					}

					return null;
				}

			},
			MapUtil.singletonDictionary(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
				"/" + moduleName));
	}

}
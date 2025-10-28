/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.hashed.files;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.ServletContext;

import java.net.URL;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;

/**
 * @author Iván Zaera Avellón
 */
public class HashedFilesRegistryImplTest {

	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_hashedFilesRegistryImpl = new HashedFilesRegistryImpl();

		BundleContext bundleContext = Mockito.mock(BundleContext.class);

		_hashedFilesRegistryImpl.activate(bundleContext);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testGetResource() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_hashedFilesRegistryImpl, "_portal", _mockPortal(StringPool.BLANK));
		ReflectionTestUtil.setFieldValue(
			_hashedFilesRegistryImpl, "_serviceTrackerMap",
			_mockServiceTrackerMap("/main.css", "/o/frontend-js-web"));

		Assert.assertNotNull(
			_hashedFilesRegistryImpl.getResource(
				"/o/frontend-js-web/main.css"));

		ReflectionTestUtil.setFieldValue(
			_hashedFilesRegistryImpl, "_portal", _mockPortal("liferay"));
		ReflectionTestUtil.setFieldValue(
			_hashedFilesRegistryImpl, "_serviceTrackerMap",
			_mockServiceTrackerMap("/main.css", "/liferay/o/frontend-js-web"));

		Assert.assertNotNull(
			_hashedFilesRegistryImpl.getResource(
				"/liferay/o/frontend-js-web/main.css"));
	}

	private Portal _mockPortal(String pathContext) {
		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getPathContext()
		).thenReturn(
			pathContext
		);

		return portal;
	}

	private ServiceTrackerMap<String, ServletContext> _mockServiceTrackerMap(
			String resourcePath, String servletContextPath)
		throws Exception {

		ServiceTrackerMap<String, ServletContext> serviceTrackerMap =
			Mockito.mock(ServiceTrackerMap.class);

		ServletContext servletContext = Mockito.mock(ServletContext.class);

		Mockito.when(
			servletContext.getResource(resourcePath)
		).thenReturn(
			Mockito.mock(URL.class)
		);

		Mockito.when(
			serviceTrackerMap.getService(servletContextPath)
		).thenReturn(
			servletContext
		);

		return serviceTrackerMap;
	}

	private HashedFilesRegistryImpl _hashedFilesRegistryImpl;

}
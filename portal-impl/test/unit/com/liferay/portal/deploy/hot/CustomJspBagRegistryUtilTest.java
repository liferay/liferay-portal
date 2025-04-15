/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.deploy.hot;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.url.URLContainer;
import com.liferay.portal.kernel.util.CustomJspRegistryUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.spring.context.PortalContextLoaderListener;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.CustomJspRegistryImpl;
import com.liferay.portal.util.FastDateFormatFactoryImpl;
import com.liferay.portal.util.PortalImpl;

import jakarta.servlet.ServletContext;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Leon Chi
 */
public class CustomJspBagRegistryUtilTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());

		CustomJspRegistryUtil customJspRegistryUtil =
			new CustomJspRegistryUtil();

		customJspRegistryUtil.setCustomJspRegistry(new CustomJspRegistryImpl());
	}

	@Before
	public void setUp() throws IOException {
		_tempFolder = FileUtil.createTempFolder();

		ServletContext servletContext = Mockito.mock(ServletContext.class);

		Mockito.when(
			servletContext.getRealPath(Mockito.anyString())
		).thenReturn(
			_tempFolder.getPath()
		);

		ServletContextPool.put(
			PortalContextLoaderListener.getPortalServletContextName(),
			servletContext);
	}

	@After
	public void tearDown() {
		FileUtil.deltree(_tempFolder);
	}

	@Test
	public void testGetCustomJspBags() {
		_testGetCustomJspBags(
			false, "TEST_CUSTOM_JSP_BAG", "Test Custom JSP Bag");
	}

	@Test
	public void testGetGlobalCustomJspBags() {
		_testGetCustomJspBags(
			true, "TEST_GLOBAL_CUSTOM_JSP_BAG", "Test Global Custom JSP Bag");
	}

	private CustomJspBag _getCustomJspBag(String targetContextId) {
		Map<ServiceReference<CustomJspBag>, CustomJspBag> customJspBags =
			CustomJspBagRegistryUtil.getCustomJspBags();

		for (Map.Entry<ServiceReference<CustomJspBag>, CustomJspBag> entry :
				customJspBags.entrySet()) {

			ServiceReference<CustomJspBag> serviceReference = entry.getKey();

			String contextId = GetterUtil.getString(
				serviceReference.getProperty("context.id"));

			if (contextId.equals(targetContextId)) {
				return entry.getValue();
			}
		}

		return null;
	}

	private void _testGetCustomJspBags(
		boolean customJspGlobal, String contextId, String contextName) {

		TestCustomJspBag testCustomJspBag = new TestCustomJspBag(
			customJspGlobal);

		ServiceRegistration<CustomJspBag> serviceRegistration =
			_bundleContext.registerService(
				CustomJspBag.class, testCustomJspBag,
				HashMapDictionaryBuilder.<String, Object>put(
					"context.id", contextId
				).put(
					"context.name", contextName
				).build());

		try {
			Assert.assertSame(testCustomJspBag, _getCustomJspBag(contextId));

			Set<String> servletContextNames =
				CustomJspRegistryUtil.getServletContextNames();

			if (customJspGlobal) {
				Assert.assertFalse(
					contextId + " should not be found in " +
						servletContextNames.toString(),
					servletContextNames.contains(contextId));
			}
			else {
				Assert.assertTrue(
					contextId + " not found in " +
						servletContextNames.toString(),
					servletContextNames.contains(contextId));
			}

			Assert.assertFalse(
				Files.exists(Paths.get(_tempFolder.getPath() + "html")));
			Assert.assertTrue(
				Files.exists(Paths.get(_tempFolder.getPath() + "/html")));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private File _tempFolder;

	private static class TestCustomJspBag implements CustomJspBag {

		@Override
		public String getCustomJspDir() {
			return StringPool.SLASH;
		}

		@Override
		public List<String> getCustomJsps() {
			return _customJsps;
		}

		@Override
		public URLContainer getURLContainer() {
			return new URLContainer() {

				@Override
				public URL getResource(String name) {
					Class<?> clazz = getClass();

					return clazz.getResource("dependencies/bottom-ext.jsp");
				}

				@Override
				public Set<String> getResources(String path) {
					return Collections.singleton(
						"/html/common/themes/bottom-ext.jsp");
				}

			};
		}

		@Override
		public boolean isCustomJspGlobal() {
			return _customJspGlobal;
		}

		private TestCustomJspBag(boolean customJspGlobal) {
			_customJspGlobal = customJspGlobal;
		}

		private final boolean _customJspGlobal;
		private final List<String> _customJsps = new ArrayList<>();

	}

}
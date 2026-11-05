/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.display.page.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class LayoutDisplayPageProviderRegistryImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetLayoutDisplayPageProviderByURLSeparator() {
		ServiceRegistration<LayoutDisplayPageProvider> serviceRegistration =
			null;

		try {
			serviceRegistration = _registerLayoutDisplayPageProvider(null);

			Assert.assertNotNull(
				_layoutDisplayPageProviderRegistry.
					getLayoutDisplayPageProviderByURLSeparator(
						0, "/url-separator/"));
		}
		finally {
			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		}
	}

	@Test
	public void testGetLayoutDisplayPageProviderByURLSeparatorRegisteredByDefaultURLSeparator() {
		ServiceRegistration<?> serviceRegistration = null;

		try {
			serviceRegistration = _registerLayoutDisplayPageProvider(
				"/default-url-separator/");

			Assert.assertNotNull(
				_layoutDisplayPageProviderRegistry.
					getLayoutDisplayPageProviderByURLSeparator(
						0, "/default-url-separator/"));
		}
		finally {
			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		}
	}

	private ServiceRegistration<LayoutDisplayPageProvider>
		_registerLayoutDisplayPageProvider(String defaultURLSeparator) {

		Bundle bundle = FrameworkUtil.getBundle(
			LayoutDisplayPageProviderRegistryImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			LayoutDisplayPageProvider.class,
			new LayoutDisplayPageProviderImpl(
				defaultURLSeparator, "/url-separator/"),
			null);
	}

	@Inject(
		filter = "component.name=com.liferay.layout.display.page.internal.LayoutDisplayPageProviderRegistryImpl"
	)
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	private static class LayoutDisplayPageProviderImpl
		implements LayoutDisplayPageProvider<String> {

		public LayoutDisplayPageProviderImpl(
			String defaultURLSeparator, String urlSeparator) {

			_defaultURLSeparator = defaultURLSeparator;
			_urlSeparator = urlSeparator;
		}

		@Override
		public String getClassName() {
			return String.class.getName();
		}

		@Override
		public String getDefaultURLSeparator() {
			return _defaultURLSeparator;
		}

		@Override
		public LayoutDisplayPageObjectProvider<String>
			getLayoutDisplayPageObjectProvider(
				InfoItemReference infoItemReference) {

			return null;
		}

		@Override
		public LayoutDisplayPageObjectProvider<String>
			getLayoutDisplayPageObjectProvider(long groupId, String urlTitle) {

			return null;
		}

		@Override
		public String getURLSeparator() {
			return _urlSeparator;
		}

		private final String _defaultURLSeparator;
		private final String _urlSeparator;

	}

}
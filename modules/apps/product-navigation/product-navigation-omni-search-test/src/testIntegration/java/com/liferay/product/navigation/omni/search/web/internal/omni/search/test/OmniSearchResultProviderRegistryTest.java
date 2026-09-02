/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.omni.search.web.internal.omni.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.product.navigation.omni.search.OmniSearchResult;
import com.liferay.product.navigation.omni.search.OmniSearchResultProvider;
import com.liferay.product.navigation.omni.search.OmniSearchResultProviderRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class OmniSearchResultProviderRegistryTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			OmniSearchResultProviderRegistryTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@After
	public void tearDown() {
		for (ServiceRegistration<OmniSearchResultProvider> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetOmniSearchResultProviders() {
		OmniSearchResultProvider lastOmniSearchResultProvider =
			_registerOmniSearchResultProvider(2000);

		OmniSearchResultProvider firstOmniSearchResultProvider =
			_registerOmniSearchResultProvider(1000);

		List<OmniSearchResultProvider> omniSearchResultProviders =
			_omniSearchResultProviderRegistry.getOmniSearchResultProviders();

		Assert.assertTrue(
			omniSearchResultProviders.toString(),
			omniSearchResultProviders.indexOf(firstOmniSearchResultProvider) <
				omniSearchResultProviders.indexOf(
					lastOmniSearchResultProvider));
	}

	private OmniSearchResultProvider _registerOmniSearchResultProvider(
		int order) {

		OmniSearchResultProvider omniSearchResultProvider =
			new MockOmniSearchResultProvider();

		_serviceRegistrations.add(
			_bundleContext.registerService(
				OmniSearchResultProvider.class, omniSearchResultProvider,
				HashMapDictionaryBuilder.<String, Object>put(
					"omni.search.result.provider.order", order
				).build()));

		return omniSearchResultProvider;
	}

	private BundleContext _bundleContext;

	@Inject
	private OmniSearchResultProviderRegistry _omniSearchResultProviderRegistry;

	private final List<ServiceRegistration<OmniSearchResultProvider>>
		_serviceRegistrations = new ArrayList<>();

	private static class MockOmniSearchResultProvider
		implements OmniSearchResultProvider {

		@Override
		public List<OmniSearchResult> getOmniSearchResults(
			String keywords, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			ThemeDisplay themeDisplay) {

			return Collections.emptyList();
		}

	}

}
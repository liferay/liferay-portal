/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.application.list;

import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.test.util.BasePanelAppNavigationItemsTestCase;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mario Leandro
 */
public class SamlAdminPanelAppNavigationItemsTest
	extends BasePanelAppNavigationItemsTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ReflectionTestUtil.setFieldValue(
			_samlAdminPanelApp, "_language", language);
		ReflectionTestUtil.setFieldValue(
			_samlAdminPanelApp, "_samlProviderConfigurationHelper",
			_samlProviderConfigurationHelper);
	}

	@Test
	public void testGetPanelAppNavigationItems() throws Exception {
		Mockito.when(
			_samlProviderConfigurationHelper.isRoleIb()
		).thenReturn(
			true
		);

		List<PanelAppNavigationItem> panelAppNavigationItems =
			_samlAdminPanelApp.getPanelAppNavigationItems(httpServletRequest);
		String[] expectedTabs1Names = {
			"general", "identity-provider", "service-provider-connections",
			"service-provider", "identity-provider-connections"
		};

		assertCanonicalNames(panelAppNavigationItems, expectedTabs1Names);

		for (int i = 0; i < expectedTabs1Names.length; i++) {
			assertParameterValue(
				expectedTabs1Names[i], panelAppNavigationItems.get(i), "tabs1");
		}
	}

	@Test
	public void testGetPanelAppNavigationItemsForIdentityProviderRole()
		throws Exception {

		Mockito.when(
			_samlProviderConfigurationHelper.isRoleIdp()
		).thenReturn(
			true
		);

		List<PanelAppNavigationItem> panelAppNavigationItems =
			_samlAdminPanelApp.getPanelAppNavigationItems(httpServletRequest);
		String[] expectedTabs1Names = {
			"general", "identity-provider", "service-provider-connections"
		};

		assertCanonicalNames(panelAppNavigationItems, expectedTabs1Names);

		for (int i = 0; i < expectedTabs1Names.length; i++) {
			assertParameterValue(
				expectedTabs1Names[i], panelAppNavigationItems.get(i), "tabs1");
		}
	}

	@Test
	public void testGetPanelAppNavigationItemsForServiceProviderRole()
		throws Exception {

		Mockito.when(
			_samlProviderConfigurationHelper.isRoleSp()
		).thenReturn(
			true
		);

		List<PanelAppNavigationItem> panelAppNavigationItems =
			_samlAdminPanelApp.getPanelAppNavigationItems(httpServletRequest);
		String[] expectedTabs1Names = {
			"general", "service-provider", "identity-provider-connections"
		};

		assertCanonicalNames(panelAppNavigationItems, expectedTabs1Names);

		for (int i = 0; i < expectedTabs1Names.length; i++) {
			assertParameterValue(
				expectedTabs1Names[i], panelAppNavigationItems.get(i), "tabs1");
		}
	}

	@Test
	public void testGetPanelAppNavigationItemsSkipsTheLoneGeneralTab()
		throws Exception {

		List<PanelAppNavigationItem> panelAppNavigationItems =
			_samlAdminPanelApp.getPanelAppNavigationItems(httpServletRequest);

		Assert.assertTrue(
			panelAppNavigationItems.toString(),
			panelAppNavigationItems.isEmpty());
	}

	private final SamlAdminPanelApp _samlAdminPanelApp =
		new SamlAdminPanelApp() {

			@Override
			public PortletURL getPortletURL(
				HttpServletRequest httpServletRequest) {

				return _portletURL;
			}

			private final PortletURL _portletURL = new MockLiferayPortletURL();

		};

	private final SamlProviderConfigurationHelper
		_samlProviderConfigurationHelper = Mockito.mock(
			SamlProviderConfigurationHelper.class);

}
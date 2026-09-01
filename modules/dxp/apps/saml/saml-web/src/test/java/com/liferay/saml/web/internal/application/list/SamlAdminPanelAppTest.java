/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.application.list;

import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
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
public class SamlAdminPanelAppTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_samlAdminPanelApp, "_language", _language);
		ReflectionTestUtil.setFieldValue(
			_samlAdminPanelApp, "_samlProviderConfigurationHelper",
			_samlProviderConfigurationHelper);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_language.get(Mockito.eq(LocaleUtil.ENGLISH), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		Mockito.when(
			_language.get(Mockito.eq(LocaleUtil.SPAIN), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1) + "-es"
		);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.SPAIN
		);
	}

	@Test
	public void testGetPanelAppNavigationItems() throws Exception {
		Mockito.when(
			_samlProviderConfigurationHelper.isRoleIb()
		).thenReturn(
			true
		);

		List<PanelAppNavigationItem> panelAppNavigationItems =
			_samlAdminPanelApp.getPanelAppNavigationItems(_httpServletRequest);

		String[] expectedCanonicalNames = {
			"general", "identity-provider", "service-provider-connections",
			"service-provider", "identity-provider-connections"
		};

		Assert.assertEquals(
			panelAppNavigationItems.toString(), expectedCanonicalNames.length,
			panelAppNavigationItems.size());

		for (int i = 0; i < expectedCanonicalNames.length; i++) {
			PanelAppNavigationItem panelAppNavigationItem =
				panelAppNavigationItems.get(i);

			Assert.assertEquals(
				expectedCanonicalNames[i],
				panelAppNavigationItem.getCanonicalName());
			Assert.assertEquals(
				expectedCanonicalNames[i] + "-es",
				panelAppNavigationItem.getLabel());

			String href = panelAppNavigationItem.getHref();

			Assert.assertEquals(
				href, expectedCanonicalNames[i],
				_getParameterValue(href, "tabs1"));
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
			_samlAdminPanelApp.getPanelAppNavigationItems(_httpServletRequest);

		String[] expectedCanonicalNames = {
			"general", "identity-provider", "service-provider-connections"
		};

		Assert.assertEquals(
			panelAppNavigationItems.toString(), expectedCanonicalNames.length,
			panelAppNavigationItems.size());

		for (int i = 0; i < expectedCanonicalNames.length; i++) {
			PanelAppNavigationItem panelAppNavigationItem =
				panelAppNavigationItems.get(i);

			Assert.assertEquals(
				expectedCanonicalNames[i],
				panelAppNavigationItem.getCanonicalName());
			Assert.assertEquals(
				expectedCanonicalNames[i] + "-es",
				panelAppNavigationItem.getLabel());

			String href = panelAppNavigationItem.getHref();

			Assert.assertEquals(
				href, expectedCanonicalNames[i],
				_getParameterValue(href, "tabs1"));
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
			_samlAdminPanelApp.getPanelAppNavigationItems(_httpServletRequest);

		String[] expectedCanonicalNames = {
			"general", "service-provider", "identity-provider-connections"
		};

		Assert.assertEquals(
			panelAppNavigationItems.toString(), expectedCanonicalNames.length,
			panelAppNavigationItems.size());

		for (int i = 0; i < expectedCanonicalNames.length; i++) {
			PanelAppNavigationItem panelAppNavigationItem =
				panelAppNavigationItems.get(i);

			Assert.assertEquals(
				expectedCanonicalNames[i],
				panelAppNavigationItem.getCanonicalName());
			Assert.assertEquals(
				expectedCanonicalNames[i] + "-es",
				panelAppNavigationItem.getLabel());

			String href = panelAppNavigationItem.getHref();

			Assert.assertEquals(
				href, expectedCanonicalNames[i],
				_getParameterValue(href, "tabs1"));
		}
	}

	@Test
	public void testGetPanelAppNavigationItemsSkipsTheLoneGeneralTab()
		throws Exception {

		List<PanelAppNavigationItem> panelAppNavigationItems =
			_samlAdminPanelApp.getPanelAppNavigationItems(_httpServletRequest);

		Assert.assertTrue(
			panelAppNavigationItems.toString(),
			panelAppNavigationItems.isEmpty());
	}

	private String _getParameterValue(String href, String name) {
		for (String parameter : StringUtil.split(href, CharPool.SEMICOLON)) {
			int index = parameter.indexOf(CharPool.EQUAL);

			if (index == -1) {
				continue;
			}

			String parameterName = parameter.substring(0, index);

			if (parameterName.endsWith(StringPool.UNDERLINE + name)) {
				return parameter.substring(index + 1);
			}
		}

		return null;
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Language _language = Mockito.mock(Language.class);

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
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
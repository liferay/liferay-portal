/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.application.list;

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
public class OAuthClientAdminPanelAppTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_oAuthClientAdminPanelApp, "_language", _language);

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
		List<PanelAppNavigationItem> panelAppNavigationItems =
			_oAuthClientAdminPanelApp.getPanelAppNavigationItems(
				_httpServletRequest);

		String[] expectedCanonicalNames = {
			"oauth-clients", "oauth-client-as-local-metadata",
			"oauth-client-pr-local-metadata"
		};

		Assert.assertEquals(
			panelAppNavigationItems.toString(), expectedCanonicalNames.length,
			panelAppNavigationItems.size());

		String[] expectedMVCRenderCommandNames = {
			"/oauth_client_admin/view_oauth_client_entries",
			"/oauth_client_admin/view_oauth_client_as_local_metadata",
			"/oauth_client_admin/view_oauth_client_pr_local_metadata"
		};

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
				href, expectedMVCRenderCommandNames[i],
				_getParameterValue(href, "mvcRenderCommandName"));
			Assert.assertEquals(
				href, expectedCanonicalNames[i],
				_getParameterValue(href, "navigation"));
		}
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

	private final OAuthClientAdminPanelApp _oAuthClientAdminPanelApp =
		new OAuthClientAdminPanelApp() {

			@Override
			public PortletURL getPortletURL(
				HttpServletRequest httpServletRequest) {

				return _portletURL;
			}

			private final PortletURL _portletURL = new MockLiferayPortletURL();

		};

	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
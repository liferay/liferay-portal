/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.application.list;

import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.change.tracking.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
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
public class PublicationsPanelAppTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_publicationsPanelApp, "_ctSettingsConfigurationHelper",
			_ctSettingsConfigurationHelper);
		ReflectionTestUtil.setFieldValue(
			_publicationsPanelApp, "_language", _language);

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
			_ctSettingsConfigurationHelper.isEnabled(Mockito.anyLong())
		).thenReturn(
			true
		);

		List<PanelAppNavigationItem> panelAppNavigationItems =
			_publicationsPanelApp.getPanelAppNavigationItems(
				_httpServletRequest);

		Assert.assertFalse(
			panelAppNavigationItems.toString(),
			panelAppNavigationItems.isEmpty());

		PanelAppNavigationItem panelAppNavigationItem =
			panelAppNavigationItems.get(0);

		Assert.assertEquals(
			"ongoing", panelAppNavigationItem.getCanonicalName());
		Assert.assertEquals("ongoing-es", panelAppNavigationItem.getLabel());

		String href = panelAppNavigationItem.getHref();

		Assert.assertTrue(
			href,
			href.contains(
				"mvcRenderCommandName=/change_tracking/view_publications"));

		panelAppNavigationItem = panelAppNavigationItems.get(
			panelAppNavigationItems.size() - 1);

		Assert.assertEquals(
			"history", panelAppNavigationItem.getCanonicalName());
		Assert.assertEquals("history-es", panelAppNavigationItem.getLabel());

		href = panelAppNavigationItem.getHref();

		Assert.assertTrue(
			href,
			href.contains(
				"mvcRenderCommandName=/change_tracking/view_history"));
	}

	@Test
	public void testGetPanelAppNavigationItemsIsEmptyWithoutChangeTracking()
		throws Exception {

		Mockito.when(
			_ctSettingsConfigurationHelper.isEnabled(Mockito.anyLong())
		).thenReturn(
			false
		);

		Assert.assertTrue(
			_publicationsPanelApp.getPanelAppNavigationItems(
				_httpServletRequest
			).isEmpty());
	}

	private final CTSettingsConfigurationHelper _ctSettingsConfigurationHelper =
		Mockito.mock(CTSettingsConfigurationHelper.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Language _language = Mockito.mock(Language.class);

	private final PublicationsPanelApp _publicationsPanelApp =
		new PublicationsPanelApp() {

			@Override
			public PortletURL getPortletURL(
				HttpServletRequest httpServletRequest) {

				return _portletURL;
			}

			private final PortletURL _portletURL = new MockLiferayPortletURL();

		};

	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
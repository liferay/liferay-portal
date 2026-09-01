/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.application.list;

import com.liferay.application.list.PanelAppNavigationItem;
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
public class DispatchPanelAppTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_dispatchPanelApp, "_language", _language);

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
			_dispatchPanelApp.getPanelAppNavigationItems(_httpServletRequest);

		Assert.assertEquals(
			panelAppNavigationItems.toString(), 2,
			panelAppNavigationItems.size());

		PanelAppNavigationItem panelAppNavigationItem =
			panelAppNavigationItems.get(0);

		Assert.assertEquals(
			"dispatch-triggers", panelAppNavigationItem.getCanonicalName());
		Assert.assertEquals(
			"dispatch-triggers-es", panelAppNavigationItem.getLabel());

		String href = panelAppNavigationItem.getHref();

		Assert.assertTrue(
			href,
			href.contains(
				"mvcRenderCommandName=/dispatch/view_dispatch_trigger"));
		Assert.assertTrue(href, href.contains("tabs1=dispatch-trigger"));

		panelAppNavigationItem = panelAppNavigationItems.get(1);

		Assert.assertEquals(
			"scheduled-jobs", panelAppNavigationItem.getCanonicalName());
		Assert.assertEquals(
			"scheduled-jobs-es", panelAppNavigationItem.getLabel());

		href = panelAppNavigationItem.getHref();

		Assert.assertTrue(
			href,
			href.contains(
				"mvcRenderCommandName=/dispatch/edit_scheduler_response"));
		Assert.assertTrue(href, href.contains("tabs1=scheduler-response"));
	}

	private final DispatchPanelApp _dispatchPanelApp = new DispatchPanelApp() {

		@Override
		public PortletURL getPortletURL(HttpServletRequest httpServletRequest) {
			return _portletURL;
		}

		private final PortletURL _portletURL = new MockLiferayPortletURL();

	};

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Language _language = Mockito.mock(Language.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
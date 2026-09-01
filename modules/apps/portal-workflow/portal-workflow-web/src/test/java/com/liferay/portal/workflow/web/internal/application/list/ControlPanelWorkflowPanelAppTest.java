/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.application.list;

import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;

import jakarta.portlet.PortletRequest;
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
 * @author Jhosseph Gonzalez
 */
public class ControlPanelWorkflowPanelAppTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_controlPanelWorkflowPanelApp, "_language", _language);

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
			_controlPanelWorkflowPanelApp.getPanelAppNavigationItems(
				_httpServletRequest);

		Assert.assertEquals(
			panelAppNavigationItems.toString(), 2,
			panelAppNavigationItems.size());

		PanelAppNavigationItem panelAppNavigationItem =
			panelAppNavigationItems.get(0);

		Assert.assertEquals(
			"workflows", panelAppNavigationItem.getCanonicalName());
		Assert.assertEquals("workflows-es", panelAppNavigationItem.getLabel());

		String href = panelAppNavigationItem.getHref();

		Assert.assertTrue(href, href.contains("tab=workflows"));

		panelAppNavigationItem = panelAppNavigationItems.get(1);

		Assert.assertEquals(
			"configuration", panelAppNavigationItem.getCanonicalName());
		Assert.assertEquals(
			"configuration-es", panelAppNavigationItem.getLabel());

		href = panelAppNavigationItem.getHref();

		Assert.assertTrue(href, href.contains("tab=configuration"));
	}

	@Test
	public void testGetPortletURL() {
		Group group = Mockito.mock(Group.class);
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);
		Portal portal = Mockito.mock(Portal.class);
		PortletURL portletURL = Mockito.mock(PortletURL.class);

		Mockito.when(
			portal.getControlPanelPortletURL(
				Mockito.same(httpServletRequest), Mockito.same(group),
				Mockito.eq(WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW),
				Mockito.eq(0L), Mockito.eq(0L),
				Mockito.eq(PortletRequest.RENDER_PHASE))
		).thenReturn(
			portletURL
		);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			themeDisplay.getControlPanelGroup()
		).thenReturn(
			group
		);

		ControlPanelWorkflowPanelApp controlPanelWorkflowPanelApp =
			new ControlPanelWorkflowPanelApp();

		ReflectionTestUtil.setFieldValue(
			controlPanelWorkflowPanelApp, "_portal", portal);

		Assert.assertSame(
			portletURL,
			controlPanelWorkflowPanelApp.getPortletURL(httpServletRequest));

		Mockito.verify(
			portal
		).getControlPanelPortletURL(
			Mockito.same(httpServletRequest), Mockito.same(group),
			Mockito.eq(WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW),
			Mockito.eq(0L), Mockito.eq(0L),
			Mockito.eq(PortletRequest.RENDER_PHASE)
		);
	}

	private final ControlPanelWorkflowPanelApp _controlPanelWorkflowPanelApp =
		new ControlPanelWorkflowPanelApp() {

			@Override
			public PortletURL getPortletURL(
				HttpServletRequest httpServletRequest) {

				return _portletURL;
			}

			private final PortletURL _portletURL = new MockLiferayPortletURL();

		};

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Language _language = Mockito.mock(Language.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
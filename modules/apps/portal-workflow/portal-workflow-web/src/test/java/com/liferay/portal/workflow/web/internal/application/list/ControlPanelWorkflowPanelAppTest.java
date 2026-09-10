/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.application.list;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
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

}
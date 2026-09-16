/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.layout.page.template.admin.web.internal.constants.LayoutPageTemplateAdminWebKeys;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Georgel Pop
 */
public class LayoutPageTemplateDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-104842")
	public void testIsShowCollectionsPanel() {
		_testIsShowCollectionsPanel(true, null);
		_testIsShowCollectionsPanel(false, Boolean.FALSE);
	}

	private void _testIsShowCollectionsPanel(
		boolean expectedShowCollectionsPanel, Boolean showCollectionsPanel) {

		Mockito.when(
			_httpServletRequest.getAttribute(
				LayoutPageTemplateAdminWebKeys.SHOW_COLLECTIONS_PANEL)
		).thenReturn(
			showCollectionsPanel
		);

		LayoutPageTemplateDisplayContext layoutPageTemplateDisplayContext =
			new LayoutPageTemplateDisplayContext(
				_httpServletRequest, _renderRequest, _renderResponse);

		Assert.assertEquals(
			expectedShowCollectionsPanel,
			layoutPageTemplateDisplayContext.isShowCollectionsPanel());
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);

}
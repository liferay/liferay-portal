/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.display.context;

import com.liferay.audiences.criteria.AudiencesCriteriaProvider;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Víctor Galán
 */
public class EditAudiencesEntryDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-102269")
	public void testGetBackURL() {
		EditAudiencesEntryDisplayContext editAudiencesEntryDisplayContext =
			_createEditAudiencesEntryDisplayContext(_URL, null);

		Assert.assertEquals(
			_URL, editAudiencesEntryDisplayContext.getBackURL());

		editAudiencesEntryDisplayContext =
			_createEditAudiencesEntryDisplayContext(_UNSAFE_URL, null);

		Assert.assertEquals(
			_RENDER_URL, editAudiencesEntryDisplayContext.getBackURL());
	}

	@Test
	@TestInfo("LPD-102269")
	public void testGetRedirect() {
		EditAudiencesEntryDisplayContext editAudiencesEntryDisplayContext =
			_createEditAudiencesEntryDisplayContext(null, _URL);

		Assert.assertEquals(
			_URL, editAudiencesEntryDisplayContext.getRedirect());

		editAudiencesEntryDisplayContext =
			_createEditAudiencesEntryDisplayContext(null, _UNSAFE_URL);

		Assert.assertEquals(
			_RENDER_URL, editAudiencesEntryDisplayContext.getRedirect());
	}

	private EditAudiencesEntryDisplayContext
		_createEditAudiencesEntryDisplayContext(
			String backURL, String redirect) {

		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.escapeRedirect(_UNSAFE_URL)
		).thenReturn(
			null
		);

		Mockito.when(
			portal.escapeRedirect(_URL)
		).thenReturn(
			_URL
		);

		portalUtil.setPortal(portal);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getParameter("backURL")
		).thenReturn(
			backURL
		);

		Mockito.when(
			httpServletRequest.getParameter("redirect")
		).thenReturn(
			redirect
		);

		PortletURL portletURL = Mockito.mock(PortletURL.class);

		Mockito.doReturn(
			_RENDER_URL
		).when(
			portletURL
		).toString();

		RenderResponse renderResponse = Mockito.mock(RenderResponse.class);

		Mockito.when(
			renderResponse.createRenderURL()
		).thenReturn(
			portletURL
		);

		return new EditAudiencesEntryDisplayContext(
			Mockito.mock(AudiencesCriteriaProvider.class), httpServletRequest,
			renderResponse);
	}

	private static final String _RENDER_URL = "/render-url";

	private static final String _UNSAFE_URL =
		"javascript:document.title='CAP-REFLECTED-XSS';void(0)";

	private static final String _URL = "/group/control_panel/manage";

}
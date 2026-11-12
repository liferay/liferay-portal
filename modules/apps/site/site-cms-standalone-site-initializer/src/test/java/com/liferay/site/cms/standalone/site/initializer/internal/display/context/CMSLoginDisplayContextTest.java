/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.standalone.site.initializer.internal.display.context;

import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class CMSLoginDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetQueryString() {
		Assert.assertEquals(
			StringPool.BLANK, _getQueryString(RandomTestUtil.randomString()));
		Assert.assertEquals(StringPool.BLANK, _getQueryString(null));
		Assert.assertEquals(
			"mvcRenderCommandName=/login/login",
			_getQueryString(LoginPortletKeys.LOGIN));
	}

	private String _getQueryString(String portletId) {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		HttpServletRequest originalHttpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			originalHttpServletRequest.getParameter("p_p_id")
		).thenReturn(
			portletId
		);

		Mockito.when(
			portal.getOriginalServletRequest(httpServletRequest)
		).thenReturn(
			originalHttpServletRequest
		);

		portalUtil.setPortal(portal);

		CMSLoginDisplayContext cmsLoginDisplayContext =
			new CMSLoginDisplayContext(httpServletRequest);

		return cmsLoginDisplayContext.getQueryString();
	}

}
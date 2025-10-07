/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mariano Álvaro Sáiz
 */
public class GoogleGadgetServletTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		new PortalUtil(
		).setPortal(
			Mockito.mock(Portal.class)
		);

		PortletLocalServiceUtil.setService(_portletLocalService);
	}

	@Test
	public void testGetContent() throws Exception {
		String injection =
			"x\"/><x:script xmlns:x=\"http:&#x2f;&#x2f;www.w3.org/1999" +
				"/xhtml\">alert(document.domain)</x:script>";

		Mockito.when(
			_httpServletRequest.getPathInfo()
		).thenReturn(
			Portal.FRIENDLY_URL_SEPARATOR + injection
		);

		Mockito.when(
			_httpServletRequest.getRequestURL()
		).thenReturn(
			new StringBuffer(
				"www.test.com/google_gadget" + Portal.FRIENDLY_URL_SEPARATOR +
					injection)
		);

		Mockito.when(
			_portlet.getDisplayName()
		).thenReturn(
			injection
		);

		Mockito.when(
			_portletLocalService.getPortletById(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			_portlet
		);

		GoogleGadgetServlet googleGadgetServlet = new GoogleGadgetServlet();

		String content = googleGadgetServlet.getContent(_httpServletRequest);

		Assert.assertFalse(content.contains(injection));
		Assert.assertTrue(
			content.contains(HtmlUtil.escapeAttribute(injection)));
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Portlet _portlet = Mockito.mock(Portlet.class);
	private final PortletLocalService _portletLocalService = Mockito.mock(
		PortletLocalService.class);

}
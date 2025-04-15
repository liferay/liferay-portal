/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.web.internal.portlet;

import com.liferay.client.extension.type.IFrameCET;
import com.liferay.portal.kernel.test.portlet.MockPortletPreferences;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Iván Zaera Avellón
 */
public class IFrameCETPortletTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_portal = new PortalImpl();

		new PortalUtil(
		).setPortal(
			_portal
		);
	}

	@Test
	public void testRenderWithQuotedProperties() throws IOException {
		IFrameCET iFrameCET = Mockito.mock(IFrameCET.class);

		Properties properties = new Properties();

		properties.put("\"height", "399\"");

		Mockito.when(
			iFrameCET.getProperties()
		).thenReturn(
			properties
		);

		Mockito.when(
			iFrameCET.getURL()
		).thenReturn(
			"https://example.com"
		);

		IFrameCETPortlet iFrameCETPortlet = new IFrameCETPortlet(
			iFrameCET, "portletId", _portal);

		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		Mockito.when(
			renderRequest.getPreferences()
		).thenReturn(
			new MockPortletPreferences()
		);

		RenderResponse renderResponse = Mockito.mock(RenderResponse.class);

		CharArrayWriter charArrayWriter = new CharArrayWriter();

		PrintWriter printWriter = new PrintWriter(charArrayWriter);

		Mockito.when(
			renderResponse.getWriter()
		).thenReturn(
			printWriter
		);

		iFrameCETPortlet.render(renderRequest, renderResponse);

		Assert.assertEquals(
			"<iframe src=\"https://example.com?%22height=399%22\"></iframe>",
			charArrayWriter.toString());
	}

	private Portal _portal;

}
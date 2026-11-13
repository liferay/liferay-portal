/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockRenderResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jürgen Kappler
 */
public class ImportTranslationResultsMVCRenderCommandTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_importTranslationResultsMVCRenderCommand, "_portal", _portal);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Mockito.when(
			_portal.getHttpServletRequest(Mockito.any(PortletRequest.class))
		).thenReturn(
			mockHttpServletRequest
		);

		Mockito.when(
			_portal.getOriginalServletRequest(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			mockHttpServletRequest
		);
	}

	@Test
	@TestInfo("LPD-115188")
	public void testRender() throws Exception {
		String redirect = RandomTestUtil.randomString();

		MockHttpServletResponse mockHttpServletResponse = _render(redirect);

		Assert.assertNull(mockHttpServletResponse.getRedirectedUrl());

		String escapedRedirect = RandomTestUtil.randomString();

		Mockito.when(
			_portal.escapeRedirect(redirect)
		).thenReturn(
			escapedRedirect
		);

		mockHttpServletResponse = _render(redirect);

		Assert.assertEquals(
			escapedRedirect, mockHttpServletResponse.getRedirectedUrl());
	}

	private MockHttpServletResponse _render(String redirect) throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		Mockito.when(
			_portal.getHttpServletResponse(Mockito.any(PortletResponse.class))
		).thenReturn(
			mockHttpServletResponse
		);

		MockRenderRequest mockRenderRequest = new MockRenderRequest();

		mockRenderRequest.addParameter("redirect", redirect);

		Assert.assertEquals(
			MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH,
			_importTranslationResultsMVCRenderCommand.render(
				mockRenderRequest, new MockRenderResponse()));

		return mockHttpServletResponse;
	}

	private final ImportTranslationResultsMVCRenderCommand
		_importTranslationResultsMVCRenderCommand =
			new ImportTranslationResultsMVCRenderCommand();
	private final Portal _portal = Mockito.mock(Portal.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.configuration.DLSizeLimitConfigurationProvider;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockRenderResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
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
 * @author Mikel Lorza
 */
public class CopyDLObjectsMVCRenderCommandTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(_language);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);

		ReflectionTestUtil.setFieldValue(
			_copyDLObjectsMVCRenderCommand, "_dlFileEntryLocalService",
			_dlFileEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_copyDLObjectsMVCRenderCommand, "_dlSizeLimitConfigurationProvider",
			_dlSizeLimitConfigurationProvider);
		ReflectionTestUtil.setFieldValue(
			_copyDLObjectsMVCRenderCommand, "_language", _language);
		ReflectionTestUtil.setFieldValue(
			_copyDLObjectsMVCRenderCommand, "_portal", _portal);

		DLFileEntry dlFileEntry = Mockito.mock(DLFileEntry.class);

		Mockito.when(
			dlFileEntry.getSize()
		).thenReturn(
			2L
		);

		Mockito.when(
			_dlFileEntryLocalService.fetchDLFileEntry(_DL_OBJECT_ID)
		).thenReturn(
			dlFileEntry
		);

		Mockito.when(
			_dlSizeLimitConfigurationProvider.getSystemMaxSizeToCopy()
		).thenReturn(
			1L
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"dlObjectIds", String.valueOf(_DL_OBJECT_ID));

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
	@TestInfo("LPD-106193")
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
		mockRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, Mockito.mock(ThemeDisplay.class));

		Assert.assertEquals(
			MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH,
			_copyDLObjectsMVCRenderCommand.render(
				mockRenderRequest, new MockRenderResponse()));

		return mockHttpServletResponse;
	}

	private static final long _DL_OBJECT_ID = RandomTestUtil.randomLong();

	private final CopyDLObjectsMVCRenderCommand _copyDLObjectsMVCRenderCommand =
		new CopyDLObjectsMVCRenderCommand();
	private final DLFileEntryLocalService _dlFileEntryLocalService =
		Mockito.mock(DLFileEntryLocalService.class);
	private final DLSizeLimitConfigurationProvider
		_dlSizeLimitConfigurationProvider = Mockito.mock(
			DLSizeLimitConfigurationProvider.class);
	private final Language _language = Mockito.mock(Language.class);
	private final Portal _portal = Mockito.mock(Portal.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.spa.web.internal.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Iván Zaera Avellón
 */
@RunWith(Arquillian.class)
public class SPATopHeadJSPDynamicIncludeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-104833")
	public void testInclude() throws Exception {

		// The clear cache request attribute is set

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE, Boolean.TRUE);

		String json = _include(mockHttpServletRequest);

		Assert.assertNull(mockHttpServletRequest.getSession(false));

		Assert.assertTrue(json, json.contains("\"clearScreensCache\":true"));

		// The clear cache request attribute is not set

		mockHttpServletRequest = new MockHttpServletRequest();

		json = _include(mockHttpServletRequest);

		Assert.assertNull(mockHttpServletRequest.getSession(false));

		Assert.assertTrue(json, json.contains("\"clearScreensCache\":false"));
	}

	private String _include(MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		mockHttpServletRequest.setRequestURI("/web/guest/home");

		long companyId = _portal.getDefaultCompanyId();

		mockHttpServletRequest.setAttribute(WebKeys.COMPANY_ID, companyId);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_companyLocalService.getCompany(companyId));
		themeDisplay.setLocale(LocaleUtil.getDefault());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_dynamicInclude.include(
			mockHttpServletRequest, mockHttpServletResponse, null);

		return mockHttpServletResponse.getContentAsString();
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.frontend.js.spa.web.internal.servlet.taglib.SPATopHeadJSPDynamicInclude"
	)
	private DynamicInclude _dynamicInclude;

	@Inject
	private Portal _portal;

}
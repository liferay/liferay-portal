/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.test.util;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.WindowState;

import org.junit.Assert;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Rafael Praxedes
 */
public class CaptchaTestUtil {

	public static boolean isCaptchaRendered(String expectedText)
		throws Exception {

		URL url = new URL(
			PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					_getMockHttpServletRequest(), PortletKeys.LOGIN,
					LayoutLocalServiceUtil.fetchLayout(
						TestPropsValues.getPlid()),
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/login/create_account"
			).setParameter(
				"saveLastPath", false
			).setPortletMode(
				PortletMode.VIEW
			).setWindowState(
				WindowState.MAXIMIZED
			).buildString());

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		Assert.assertEquals(
			HttpURLConnection.HTTP_OK, httpURLConnection.getResponseCode());

		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(httpURLConnection.getInputStream()))) {

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains(expectedText)) {
					return true;
				}
			}
		}

		return false;
	}

	private static MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Company company = CompanyLocalServiceUtil.getCompany(
			TestPropsValues.getCompanyId());

		mockHttpServletRequest.setServerName(company.getVirtualHostname());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(
			LayoutLocalServiceUtil.fetchLayout(TestPropsValues.getPlid()));
		themeDisplay.setPlid(TestPropsValues.getPlid());
		themeDisplay.setPortalURL(
			"http://" + company.getVirtualHostname() + ":8080");
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

}
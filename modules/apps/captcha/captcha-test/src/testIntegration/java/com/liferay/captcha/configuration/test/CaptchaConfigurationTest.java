/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.configuration.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowState;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Istvan Sajtos
 */
@RunWith(Arquillian.class)
public class CaptchaConfigurationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		_test(false, false, true);
		_test(true, true, false);
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(
			_layoutLocalService.fetchLayout(TestPropsValues.getPlid()));
		themeDisplay.setPlid(TestPropsValues.getPlid());
		themeDisplay.setPortalURL("http://localhost:8080");
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private boolean _isCaptchaRendered() throws Exception {
		URL url = new URL(
			PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					_getMockHttpServletRequest(), PortletKeys.LOGIN,
					_layoutLocalService.fetchLayout(TestPropsValues.getPlid()),
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
				if (line.contains("CAPTCHA")) {
					return true;
				}
			}
		}

		return false;
	}

	private void _test(
			boolean expectedCaptchaRendered,
			boolean instanceSettingsCreateAccountCaptchaEnabled,
			boolean systemSettingsCreateAccountCaptchaEnabled)
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CaptchaConfiguration.class.getName(),
						new HashMapDictionaryBuilder(
						).<String, Object>put(
							"createAccountCaptchaEnabled",
							instanceSettingsCreateAccountCaptchaEnabled
						).build());
			ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					CaptchaConfiguration.class.getName(),
					new HashMapDictionaryBuilder(
					).<String, Object>put(
						"createAccountCaptchaEnabled",
						systemSettingsCreateAccountCaptchaEnabled
					).build())) {

			Assert.assertEquals(expectedCaptchaRendered, _isCaptchaRendered());
		}
	}

	@Inject
	private LayoutLocalService _layoutLocalService;

}
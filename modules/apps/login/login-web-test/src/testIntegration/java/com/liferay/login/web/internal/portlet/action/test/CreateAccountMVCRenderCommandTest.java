/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.persistence.CompanyUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PrefsPropsTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eva Budai
 */
@RunWith(Arquillian.class)
public class CreateAccountMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		CompanyUtil.clearCache();
	}

	@After
	public void tearDown() {
		CompanyUtil.clearCache();
	}

	@Test
	public void testRedirectToCreateAccountWhenCompanyStrangersTrue()
		throws Exception {

		try (SafeCloseable safeCloseable =
				PrefsPropsTestUtil.swapWithSafeCloseable(
					TestPropsValues.getCompanyId(),
					PropsKeys.COMPANY_SECURITY_STRANGERS,
					Boolean.TRUE.toString())) {

			Assert.assertEquals(
				"/create_account.jsp",
				_mvcRenderCommand.render(
					_getMockLiferayPortletRenderRequest(),
					new MockLiferayPortletRenderResponse()));
		}
	}

	@Test
	public void testRedirectToLoginWhenCompanyStrangersFalse()
		throws Exception {

		try (SafeCloseable safeCloseable =
				PrefsPropsTestUtil.swapWithSafeCloseable(
					TestPropsValues.getCompanyId(),
					PropsKeys.COMPANY_SECURITY_STRANGERS,
					Boolean.FALSE.toString())) {

			Assert.assertEquals(
				"/login.jsp",
				_mvcRenderCommand.render(
					_getMockLiferayPortletRenderRequest(),
					new MockLiferayPortletRenderResponse()));
		}
	}

	private MockLiferayPortletRenderRequest
			_getMockLiferayPortletRenderRequest()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest() {

				@Override
				public Portlet getPortlet() {
					return _portletLocalService.getPortletById(
						LoginPortletKeys.CREATE_ACCOUNT);
				}

				@Override
				public String getPortletName() {
					return LoginPortletKeys.CREATE_ACCOUNT;
				}

				{
					Portlet portlet = getPortlet();

					PortletApp portletApp = portlet.getPortletApp();

					portletApp.setSpecMajorVersion(2);
				}
			};

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.fetchCompany(
				TestPropsValues.getCompanyId()));

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletRenderRequest;
	}

	@Inject(
		filter = "mvc.command.name=/login/create_account",
		type = MVCRenderCommand.class
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private PortletLocalService _portletLocalService;

}
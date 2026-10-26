/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.CompanyVirtualHostException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class EditCompanyMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			TestPropsValues.getCompanyId());

		portletPreferences.setValue(_LEGACY_KEY, _LEGACY_VALUE);

		portletPreferences.store();
	}

	@After
	public void tearDown() throws Exception {
		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			TestPropsValues.getCompanyId());

		portletPreferences.reset(_LEGACY_KEY);

		portletPreferences.store();
	}

	@Test
	public void testProcessAction() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"virtualHostname", "-" + RandomTestUtil.randomString());

		Assert.assertFalse(
			_editCompanyMVCActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse()));

		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletActionRequest,
				CompanyVirtualHostException.class.getName()));

		Assert.assertEquals(
			_LEGACY_VALUE,
			PrefsPropsUtil.getString(
				TestPropsValues.getCompanyId(), _LEGACY_KEY));

		Assert.assertTrue(
			_editCompanyMVCActionCommand.processAction(
				_getMockLiferayPortletActionRequest(),
				new MockLiferayPortletActionResponse()));

		Assert.assertNull(
			PrefsPropsUtil.getString(
				TestPropsValues.getCompanyId(), _LEGACY_KEY));
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest() {

				@Override
				public String[] getParameterValues(String name) {
					Map<String, String[]> parameterMap = getParameterMap();

					return parameterMap.get(name);
				}

			};

		mockLiferayPortletActionRequest.addParameter(
			"discardLegacyKey", "adminEmailUserAddedBody");
		mockLiferayPortletActionRequest.addParameter(
			Constants.CMD, Constants.UPDATE);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(
			_layoutLocalService.getLayout(
				_portal.getControlPanelPlid(TestPropsValues.getCompanyId())));

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		return mockLiferayPortletActionRequest;
	}

	private static final String _LEGACY_KEY = "adminEmailUserAddedBody_en_US";

	private static final String _LEGACY_VALUE = RandomTestUtil.randomString();

	@Inject(filter = "mvc.command.name=/portal_settings/edit_company")
	private MVCActionCommand _editCompanyMVCActionCommand;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

}
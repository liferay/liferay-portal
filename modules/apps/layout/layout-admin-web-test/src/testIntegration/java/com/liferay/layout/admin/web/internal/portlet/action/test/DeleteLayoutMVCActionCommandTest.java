/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.BulkLayoutConverter;
import com.liferay.layout.util.template.LayoutConversionResult;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Bárbara Cabrera
 */
@RunWith(Arquillian.class)
@Sync
public class DeleteLayoutMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-117394")
	public void testDeleteLayout() throws Exception {
		String redirect = RandomTestUtil.randomString();

		Assert.assertEquals(
			redirect,
			_testDeleteLayout(
				LayoutTestUtil.addTypePortletLayout(_group.getGroupId()),
				redirect));

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId());

		LayoutConversionResult layoutConversionResult =
			_bulkLayoutConverter.generatePreviewLayout(
				layout.getPlid(), LocaleUtil.getDefault());

		Assert.assertNull(
			redirect,
			_testDeleteLayout(
				layoutConversionResult.getDraftLayout(), redirect));

		Assert.assertEquals(
			redirect,
			_testDeleteLayout(
				LayoutTestUtil.addLaunchDraftLayout(
					RandomTestUtil.randomLong(), layout),
				redirect));
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			long plid, String redirect)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockLiferayPortletActionRequest.addParameter("redirect", redirect);
		mockLiferayPortletActionRequest.addParameter(
			"rowIds", new String[] {String.valueOf(plid)});

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private String _testDeleteLayout(Layout layout, String redirect)
		throws Exception {

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(layout.getPlid(), redirect),
			mockLiferayPortletActionResponse);

		Assert.assertNull(_layoutLocalService.fetchLayout(layout.getPlid()));

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		String content = mockHttpServletResponse.getContentAsString();

		if (Validator.isNull(content)) {
			return null;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(content);

		return jsonObject.getString("redirectURL");
	}

	@Inject
	private BulkLayoutConverter _bulkLayoutConverter;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(filter = "mvc.command.name=/layout_admin/delete_layout")
	private MVCActionCommand _mvcActionCommand;

}
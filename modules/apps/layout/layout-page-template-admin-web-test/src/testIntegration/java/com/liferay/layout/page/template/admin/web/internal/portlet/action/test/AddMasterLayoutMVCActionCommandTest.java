/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Georgel Pop
 */
@FeatureFlag("LPD-57283")
@RunWith(Arquillian.class)
public class AddMasterLayoutMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-108813")
	public void testGetRedirectURL() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		Group group = depotEntry.getGroup();

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(group);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_DRAFT);

		String expectedBackURL = DesignLibraryUtil.getDesignLibraryResourcesURL(
			group,
			_portal.getHttpServletRequest(mockLiferayPortletActionRequest));
		String expectedBackURLTitle = group.getDescriptiveName(
			LocaleUtil.getSiteDefault());

		String redirectURL = ReflectionTestUtil.invoke(
			_mvcActionCommand, "getRedirectURL",
			new Class<?>[] {ActionRequest.class, LayoutPageTemplateEntry.class},
			mockLiferayPortletActionRequest, layoutPageTemplateEntry);

		Assert.assertEquals(
			expectedBackURL, _getDecodedParameter(redirectURL, "p_l_back_url"));
		Assert.assertEquals(
			expectedBackURLTitle,
			_getDecodedParameter(redirectURL, "p_l_back_url_title"));
		Assert.assertEquals(
			Constants.EDIT, _getDecodedParameter(redirectURL, "p_l_mode"));
	}

	private String _getDecodedParameter(String url, String name) {
		return HttpComponentsUtil.decodeURL(
			HttpComponentsUtil.getParameter(url, name, false));
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			Group group)
		throws Exception {

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				company,
				_groupLocalService.getGroup(TestPropsValues.getGroupId()),
				_layoutLocalService.getLayout(
					_portal.getControlPanelPlid(company.getCompanyId())));

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockLiferayPortletActionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setScopeGroupId(group.getGroupId());

		return mockLiferayPortletActionRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_page_template_admin/add_master_layout"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

}
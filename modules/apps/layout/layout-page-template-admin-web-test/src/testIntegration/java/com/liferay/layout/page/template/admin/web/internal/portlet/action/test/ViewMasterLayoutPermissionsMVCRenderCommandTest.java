/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Georgel Pop
 */
@FeatureFlag("LPD-57283")
@RunWith(Arquillian.class)
public class ViewMasterLayoutPermissionsMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		_controlPanelLayout = _layoutLocalService.getLayout(
			_portal.getControlPanelPlid(_company.getCompanyId()));

		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			_company.getCompanyId(), true, "LPD-57283");

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_depotGroup = depotEntry.getGroup();
	}

	@Test
	@TestInfo("LPD-81022")
	public void testRender() throws Exception {
		_testRenderRedirectsToPermissionsModal();
		_testRenderWithUnknownExternalReferenceCode();
		_testRenderWithoutExternalReferenceCode();
	}

	private String _getRedirect(
		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse) {

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletRenderResponse.getHttpServletResponse();

		return mockHttpServletResponse.getRedirectedUrl();
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_company, _group, _controlPanelLayout);

		themeDisplay.setScopeGroupId(_depotGroup.getGroupId());

		return themeDisplay;
	}

	private String _render(
			String externalReferenceCode,
			MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		ThemeDisplay themeDisplay = _getThemeDisplay();

		themeDisplay.setRequest(
			_portal.getHttpServletRequest(mockLiferayPortletRenderRequest));

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletRenderRequest.setParameter(
			"masterPageExternalReferenceCode", externalReferenceCode);

		return _mvcRenderCommand.render(
			mockLiferayPortletRenderRequest, mockLiferayPortletRenderResponse);
	}

	private void _testRenderRedirectsToPermissionsModal() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_depotGroup.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_DRAFT);
		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		Assert.assertEquals(
			MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH,
			_render(
				layoutPageTemplateEntry.getExternalReferenceCode(),
				mockLiferayPortletRenderResponse));

		String redirect = _getRedirect(mockLiferayPortletRenderResponse);

		String namespace = _portal.getPortletNamespace(
			"com_liferay_portlet_configuration_web_portlet_" +
				"PortletConfigurationPortlet");

		Assert.assertEquals(
			redirect, LayoutPageTemplateEntry.class.getName(),
			HttpComponentsUtil.getParameter(
				redirect, namespace + "modelResource", false));
		Assert.assertEquals(
			redirect,
			String.valueOf(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()),
			HttpComponentsUtil.getParameter(
				redirect, namespace + "resourcePrimKey", false));
	}

	private void _testRenderWithUnknownExternalReferenceCode()
		throws Exception {

		Assert.assertEquals(
			"/view.jsp",
			_render(
				RandomTestUtil.randomString(),
				new MockLiferayPortletRenderResponse()));
	}

	private void _testRenderWithoutExternalReferenceCode() throws Exception {
		Assert.assertEquals(
			"/view.jsp",
			_render(StringPool.BLANK, new MockLiferayPortletRenderResponse()));
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Layout _controlPanelLayout;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _depotGroup;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_page_template_admin/view_master_layout_permissions"
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private Portal _portal;

}
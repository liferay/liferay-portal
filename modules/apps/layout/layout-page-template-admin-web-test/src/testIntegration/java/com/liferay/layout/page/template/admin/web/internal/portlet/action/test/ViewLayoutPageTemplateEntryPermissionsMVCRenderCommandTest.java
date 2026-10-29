/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
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
 * @author Javier Moral
 */
@FeatureFlag("LPD-57283")
@RunWith(Arquillian.class)
public class ViewLayoutPageTemplateEntryPermissionsMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		_controlPanelLayout = _layoutLocalService.getLayout(
			_portal.getControlPanelPlid(_company.getCompanyId()));

		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			_company.getCompanyId(), true, "LPD-57283");
	}

	@Test
	@TestInfo("LPD-106071")
	public void testRender() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_testRenderRedirectsToPermissionsModal(
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				depotEntry.getGroupId()));
		_testRenderWithUnknownExternalReferenceCode(depotEntry.getGroupId());
		_testRenderWithoutExternalReferenceCode(depotEntry.getGroupId());
	}

	private String _getRedirect(
		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse) {

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletRenderResponse.getHttpServletResponse();

		return mockHttpServletResponse.getRedirectedUrl();
	}

	private ThemeDisplay _getThemeDisplay(long groupId) throws Exception {
		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_company, _group, _controlPanelLayout);

		themeDisplay.setScopeGroupId(groupId);

		return themeDisplay;
	}

	private String _render(
			String externalReferenceCode, long groupId,
			MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		ThemeDisplay themeDisplay = _getThemeDisplay(groupId);

		themeDisplay.setRequest(
			_portal.getHttpServletRequest(mockLiferayPortletRenderRequest));

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletRenderRequest.setParameter(
			"layoutPageTemplateEntryExternalReferenceCode",
			externalReferenceCode);

		return _mvcRenderCommand.render(
			mockLiferayPortletRenderRequest, mockLiferayPortletRenderResponse);
	}

	private void _testRenderRedirectsToPermissionsModal(
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		Assert.assertEquals(
			MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH,
			_render(
				layoutPageTemplateEntry.getExternalReferenceCode(),
				layoutPageTemplateEntry.getGroupId(),
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

	private void _testRenderWithUnknownExternalReferenceCode(long groupId)
		throws Exception {

		Assert.assertEquals(
			"/view.jsp",
			_render(
				RandomTestUtil.randomString(), groupId,
				new MockLiferayPortletRenderResponse()));
	}

	private void _testRenderWithoutExternalReferenceCode(long groupId)
		throws Exception {

		Assert.assertEquals(
			"/view.jsp",
			_render(
				StringPool.BLANK, groupId,
				new MockLiferayPortletRenderResponse()));
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Layout _controlPanelLayout;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_page_template_admin/view_layout_page_template_entry_permissions"
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private Portal _portal;

}
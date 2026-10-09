/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class AddLayoutUtilityPageEntryMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testAddLayoutUtilityPageEntry() throws Exception {
		String name = RandomTestUtil.randomString();

		_addLayoutUtilityPageEntry(
			_getMockLiferayPortletActionRequest(
				name, TestPropsValues.getUser(),
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND));

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.fetchLayoutUtilityPageEntry(
				TestPropsValues.getGroupId(), name,
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND);

		Assert.assertNotNull(layoutUtilityPageEntry);

		User user = UserTestUtil.addGroupUser(
			_groupLocalService.fetchGroup(TestPropsValues.getGroupId()),
			RoleConstants.POWER_USER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			Assert.assertThrows(
				PrincipalException.class,
				() -> _addLayoutUtilityPageEntry(
					_getMockLiferayPortletActionRequest(
						RandomTestUtil.randomString(), user,
						LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND)));
		}
	}

	@FeatureFlag("LPD-57283")
	@Test
	@TestInfo("LPD-81022")
	public void testAddLayoutUtilityPageEntryWithDesignLibraryMasterLayout()
		throws Exception {

		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		Group designLibraryGroup = _addConnectedDesignLibraryGroup();

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				designLibraryGroup.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				RandomTestUtil.randomString(), TestPropsValues.getUser(),
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND);

		mockLiferayPortletActionRequest.addParameter(
			"masterLayoutPlid",
			String.valueOf(masterLayoutPageTemplateEntry.getPlid()));

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_addLayoutUtilityPageEntry(mockLiferayPortletActionRequest);

		_assertMasterLayout(
			designLibraryGroup,
			_layoutLocalService.getLayout(layoutUtilityPageEntry.getPlid()),
			masterLayoutPageTemplateEntry);
	}

	private Group _addConnectedDesignLibraryGroup() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), TestPropsValues.getGroupId());

		return depotEntry.getGroup();
	}

	private LayoutUtilityPageEntry _addLayoutUtilityPageEntry(
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest) {

		return ReflectionTestUtil.invoke(
			_addLayoutUtilityPageEntryMVCActionCommand,
			"_addLayoutUtilityPageEntry", new Class<?>[] {ActionRequest.class},
			mockLiferayPortletActionRequest);
	}

	private void _assertMasterLayout(
		Group designLibraryGroup, Layout layout,
		LayoutPageTemplateEntry masterLayoutPageTemplateEntry) {

		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getExternalReferenceCode(),
			layout.getMasterLayoutPageTemplateEntryERC());
		Assert.assertEquals(
			designLibraryGroup.getExternalReferenceCode(),
			layout.getMasterLayoutPageTemplateEntryScopeERC());
		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getPlid(),
			layout.getMasterLayoutPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertEquals(
			designLibraryGroup.getExternalReferenceCode(),
			draftLayout.getMasterLayoutPageTemplateEntryScopeERC());
		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getPlid(),
			draftLayout.getMasterLayoutPlid());
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String name, User user, String type)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRequest(
			mockLiferayPortletActionRequest.getHttpServletRequest());
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(user);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.addParameter("name", name);
		mockLiferayPortletActionRequest.addParameter("type", type);

		return mockLiferayPortletActionRequest;
	}

	@Inject(
		filter = "mvc.command.name=/layout_admin/add_layout_utility_page_entry"
	)
	private MVCActionCommand _addLayoutUtilityPageEntryMVCActionCommand;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

}
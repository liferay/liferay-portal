/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.utility.internal.layout.type.controller.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.LayoutTypeControllerTracker;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class UtilityLayoutTypeControllerTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				false, RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND, 0,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		_layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				LayoutConstants.TYPE_UTILITY);
	}

	@Test
	public void testIncludeLayoutContent() throws Exception {
		try {
			_includeLayoutContent(ActionKeys.PREVIEW_DRAFT, Constants.PREVIEW);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		Assert.assertFalse(
			_includeLayoutContent(ActionKeys.UPDATE, Constants.PREVIEW));

		try {
			_includeLayoutContent(ActionKeys.VIEW, Constants.PREVIEW);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}
	}

	@Test
	@TestInfo("LPD-65271")
	public void testUtilityLayoutTypeControllerMasterLayoutWrapperDiv()
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		Layout draftLayout = _layout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			null, fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getExternalReferenceCode(),
			fragmentEntry.getScopeERC(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), _layout.fetchDraftLayout(),
			fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(), null,
			0,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, _layout);

		_layout = _layoutLocalService.getLayout(_layout.getPlid());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			ContentLayoutTestUtil.getThemeDisplay(
				_companyLocalService.fetchCompany(
					TestPropsValues.getCompanyId()),
				_group, _layout));
		mockHttpServletRequest.setMethod(HttpMethods.GET);

		_layoutTypeController.includeLayoutContent(
			mockHttpServletRequest, new MockHttpServletResponse(), _layout);

		String content = String.valueOf(
			mockHttpServletRequest.getAttribute(WebKeys.LAYOUT_CONTENT));

		Assert.assertFalse(content.contains("id=\"master-layout-wrapper\""));

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, StringUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_DRAFT,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layout = _layoutLocalService.updateMasterLayoutPlid(
			_layout.getGroupId(), _layout.isPrivateLayout(),
			_layout.getLayoutId(), layoutPageTemplateEntry.getPlid());

		mockHttpServletRequest = new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			ContentLayoutTestUtil.getThemeDisplay(
				_companyLocalService.fetchCompany(
					TestPropsValues.getCompanyId()),
				_group, _layout));
		mockHttpServletRequest.setMethod(HttpMethods.GET);

		_layoutTypeController.includeLayoutContent(
			mockHttpServletRequest, new MockHttpServletResponse(), _layout);

		content = String.valueOf(
			mockHttpServletRequest.getAttribute(WebKeys.LAYOUT_CONTENT));

		Assert.assertTrue(content.contains("id=\"master-layout-wrapper\""));
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			String layoutMode, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, "http://www.liferay.com");

		UserTestUtil.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(user, mockHttpServletRequest));

		if (Validator.isNotNull(layoutMode)) {
			mockHttpServletRequest.setParameter("p_l_mode", layoutMode);
		}

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay(
			User user, HttpServletRequest mockHttpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		themeDisplay.setCompany(company);

		themeDisplay.setLanguageId(_group.getDefaultLanguageId());
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(
			_layoutSetLocalService.getLayoutSet(_group.getGroupId(), false));
		themeDisplay.setLocale(
			LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setPortalDomain(company.getVirtualHostname());
		themeDisplay.setPortalURL(company.getPortalURL(_group.getGroupId()));
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setServerPort(8080);
		themeDisplay.setSignedIn(true);
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private User _getUser(String actionId) throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			role, Layout.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_group.getCompanyId()), actionId);

		User user = UserTestUtil.addUser();

		_roleLocalService.clearUserRoles(user.getUserId());

		_roleLocalService.addUserRole(user.getUserId(), role);

		return user;
	}

	private boolean _includeLayoutContent(String actionId, String layoutMode)
		throws Exception {

		return _layoutTypeController.includeLayoutContent(
			_getMockHttpServletRequest(layoutMode, _getUser(actionId)),
			new MockHttpServletResponse(), _layout.fetchDraftLayout());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UtilityLayoutTypeControllerTest.class);

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	private LayoutTypeController _layoutTypeController;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.content.internal.layout.type.controller.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
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
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
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

import org.junit.After;
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
public class ContentLayoutTypeControllerTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				LayoutConstants.TYPE_CONTENT);

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPS-125516")
	public void testContentLayoutTypeControllerDraftEditWithViewPermission()
		throws Exception {

		ContentLayoutTestUtil.publishLayout(
			_layout.fetchDraftLayout(), _layout);

		Assert.assertFalse(
			_layoutTypeController.includeLayoutContent(
				_getMockHttpServletRequest(
					Constants.EDIT, _getUser(ActionKeys.VIEW)),
				new MockHttpServletResponse(), _layout));
	}

	@Test
	public void testContentLayoutTypeControllerDraftPreviewPermission()
		throws Exception {

		try {
			_includeLayoutContent(
				ActionKeys.PREVIEW_DRAFT, _layout, Constants.EDIT);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		Assert.assertFalse(
			_includeLayoutContent(
				ActionKeys.PREVIEW_DRAFT, _layout, Constants.PREVIEW));
		Assert.assertFalse(
			_includeLayoutContent(
				ActionKeys.UPDATE, _layout, Constants.PREVIEW));

		try {
			_includeLayoutContent(ActionKeys.VIEW, _layout, Constants.PREVIEW);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}
	}

	@Test
	public void testContentLayoutTypeControllerLayout() throws Exception {
		Assert.assertFalse(
			_layoutTypeController.includeLayoutContent(
				_getMockHttpServletRequest(null, TestPropsValues.getUser()),
				new MockHttpServletResponse(), _layout));

		User guestUser = _userLocalService.getGuestUser(_group.getCompanyId());

		try {
			_layoutTypeController.includeLayoutContent(
				_getMockHttpServletRequest(null, guestUser),
				new MockHttpServletResponse(),
				LayoutTestUtil.addTypeContentLayout(_group));

			Assert.fail();
		}
		catch (NoSuchLayoutException noSuchLayoutException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchLayoutException);
			}
		}

		Layout draftLayout = _layout.fetchDraftLayout();

		_layoutLocalService.updateStatus(
			TestPropsValues.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextThreadLocal.getServiceContext());

		Assert.assertFalse(
			_layoutTypeController.includeLayoutContent(
				_getMockHttpServletRequest(null, TestPropsValues.getUser()),
				new MockHttpServletResponse(), _layout));
		Assert.assertFalse(
			_layoutTypeController.includeLayoutContent(
				_getMockHttpServletRequest(null, guestUser),
				new MockHttpServletResponse(), _layout));
	}

	@Test
	public void testContentLayoutTypeControllerMainContentDiv()
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		Layout draftLayout = _layout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			null, fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), _layout.fetchDraftLayout(),
			fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(), null,
			0,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, _layout);

		_layout = _layoutLocalService.getLayout(_layout.getPlid());

		String html = ContentLayoutTestUtil.getRenderLayoutHTML(
			_layout, _layoutServiceContextHelper, _layoutStructureProvider,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid()));

		Assert.assertFalse(html.contains("main-content"));

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

		html = ContentLayoutTestUtil.getRenderLayoutHTML(
			_layout, _layoutServiceContextHelper, _layoutStructureProvider,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid()));

		Assert.assertTrue(
			html.startsWith(
				"<div class=\"layout-content portlet-layout\"" +
					"id=\"main-content\" role=\"main\">"));

		Layout masterLayout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftMasterLayout = masterLayout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			null, fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), masterLayout.fetchDraftLayout(),
			fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(), null,
			0,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftMasterLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftMasterLayout, masterLayout);

		html = ContentLayoutTestUtil.getRenderLayoutHTML(
			_layout, _layoutServiceContextHelper, _layoutStructureProvider,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid()));

		Assert.assertTrue(html.contains("main-content"));
		Assert.assertFalse(
			html.startsWith(
				"<div class=\"layout-content portlet-layout\"" +
					"id=\"main-content\" role=\"main\">"));
	}

	@Test
	public void testContentLayoutTypeControllerPageTemplateDraftPreviewPermission()
		throws Exception {

		Layout layout = _addTypePageTemplateEntryLayout();

		try {
			_includeLayoutContent(
				ActionKeys.PREVIEW_DRAFT, layout, Constants.EDIT);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		try {
			_includeLayoutContent(
				ActionKeys.PREVIEW_DRAFT, layout, Constants.PREVIEW);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		Assert.assertFalse(
			_includeLayoutContent(
				ActionKeys.UPDATE, layout, Constants.PREVIEW));

		try {
			_includeLayoutContent(ActionKeys.VIEW, layout, Constants.PREVIEW);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}
	}

	@Test
	public void testContentLayoutTypeControllerWithLockedLayout()
		throws Exception {

		Layout draftLayout = _layout.fetchDraftLayout();

		draftLayout.setStatus(WorkflowConstants.STATUS_DRAFT);

		draftLayout = _layoutLocalService.updateLayout(draftLayout);

		_lockLayout(draftLayout, TestPropsValues.getUser());

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				Constants.EDIT, UserTestUtil.addGroupAdminUser(_group));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_layoutTypeController.includeLayoutContent(
			mockHttpServletRequest, mockHttpServletResponse, draftLayout);

		Assert.assertEquals(
			_layoutLockManager.getLockedLayoutURL(mockHttpServletRequest),
			mockHttpServletResponse.getRedirectedUrl());
	}

	@Test
	@TestInfo("LPD-46099")
	public void testContentLayoutTypeControllerWithSegmentsExperienceId()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(null, TestPropsValues.getUser());

		_testIncludeLayoutContent(
			"http://www.liferay.com", mockHttpServletRequest,
			RandomTestUtil.randomString());
		_testIncludeLayoutContent(
			"http://www.liferay.com", mockHttpServletRequest,
			String.valueOf(RandomTestUtil.randomLong()));

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_testIncludeLayoutContent(
			"http://www.liferay.com", mockHttpServletRequest,
			String.valueOf(
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(layout.getPlid())));

		_testIncludeLayoutContent(
			null, mockHttpServletRequest,
			String.valueOf(
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid())));
		_testIncludeLayoutContent(null, mockHttpServletRequest, null);
	}

	private Layout _addTypePageTemplateEntryLayout() throws Exception {
		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				addLayoutPageTemplateCollection(
					null, _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					ServiceContextThreadLocal.getServiceContext());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, _group.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.BASIC, 0,
				WorkflowConstants.STATUS_DRAFT,
				ServiceContextThreadLocal.getServiceContext());

		return _layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid());
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

	private boolean _includeLayoutContent(
			String actionId, Layout layout, String layoutMode)
		throws Exception {

		return _layoutTypeController.includeLayoutContent(
			_getMockHttpServletRequest(layoutMode, _getUser(actionId)),
			new MockHttpServletResponse(), layout.fetchDraftLayout());
	}

	private void _lockLayout(Layout layout, User user) throws Exception {
		MockActionRequest mockActionRequest = new MockActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(layout);
		themeDisplay.setUser(user);

		mockActionRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		_layoutLockManager.getLock(mockActionRequest);
	}

	private void _testIncludeLayoutContent(
			String expectedRedirectURL, Layout layout,
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_layoutTypeController.includeLayoutContent(
			mockHttpServletRequest, mockHttpServletResponse, layout);

		Assert.assertEquals(
			expectedRedirectURL, mockHttpServletResponse.getRedirectedUrl());
	}

	private void _testIncludeLayoutContent(
			String expectedRedirectURL,
			MockHttpServletRequest mockHttpServletRequest,
			String segmentsExperienceId)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (Validator.isNotNull(segmentsExperienceId)) {
			mockHttpServletRequest.setParameter(
				"segmentsExperienceId", segmentsExperienceId);
			themeDisplay.setURLCurrent(
				"http://www.liferay.com?segmentsExperienceId=" +
					segmentsExperienceId);
		}
		else {
			mockHttpServletRequest.removeParameter("segmentsExperienceId");
			themeDisplay.setURLCurrent("http://www.liferay.com");
		}

		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, themeDisplay.getURLCurrent());

		_testIncludeLayoutContent(
			expectedRedirectURL, _layout, mockHttpServletRequest);
		_testIncludeLayoutContent(
			expectedRedirectURL, _layout.fetchDraftLayout(),
			mockHttpServletRequest);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentLayoutTypeControllerTest.class);

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
	private LayoutLockManager _layoutLockManager;

	@Inject
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	private LayoutTypeController _layoutTypeController;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private UserLocalService _userLocalService;

}
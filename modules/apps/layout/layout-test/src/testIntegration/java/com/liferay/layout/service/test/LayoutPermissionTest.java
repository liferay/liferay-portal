/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManagerUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class LayoutPermissionTest {

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
	public void testContainsGuestWithoutPreviewDraftPermission()
		throws Exception {

		Assert.assertFalse(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				PermissionCheckerFactoryUtil.create(
					_userLocalService.getGuestUser(_group.getCompanyId())),
				LayoutTestUtil.addTypeContentLayout(_group)));
	}

	@Test
	public void testContainsGuestWithPreviewDraftPermission() throws Exception {
		Role role = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.GUEST);

		try {
			Assert.assertTrue(
				_layoutPermission.containsLayoutPreviewDraftPermission(
					_getPermissionChecker(ActionKeys.PREVIEW_DRAFT, role),
					LayoutTestUtil.addTypeContentLayout(_group)));
		}
		finally {
			_resourcePermissionLocalService.removeResourcePermission(
				_group.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_group.getCompanyId()), role.getRoleId(),
				ActionKeys.PREVIEW_DRAFT);
		}
	}

	@Test
	public void testContainsPortalContentReviewerWithoutPreviewDraftPermission()
		throws Exception {

		Assert.assertFalse(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(
					_roleLocalService.getRole(
						TestPropsValues.getCompanyId(),
						RoleConstants.PORTAL_CONTENT_REVIEWER),
					UserTestUtil.addUser()),
				LayoutTestUtil.addTypeContentLayout(_group)));
	}

	@Test
	public void testContainsPortalContentReviewerWithPreviewDraftPermission()
		throws Exception {

		Role role = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.PORTAL_CONTENT_REVIEWER);

		try {
			Assert.assertTrue(
				_layoutPermission.containsLayoutPreviewDraftPermission(
					_getPermissionChecker(ActionKeys.PREVIEW_DRAFT, role),
					LayoutTestUtil.addTypeContentLayout(_group)));
		}
		finally {
			_resourcePermissionLocalService.removeResourcePermission(
				_group.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_group.getCompanyId()), role.getRoleId(),
				ActionKeys.PREVIEW_DRAFT);
		}
	}

	@Test
	public void testContainsPreviewDraftPermissionOnAssetDisplayLayoutWithPreviewDraftPermission()
		throws Exception {

		Assert.assertTrue(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.PREVIEW_DRAFT),
				_addDisplayPageTemplateLayout()));
	}

	@Test
	public void testContainsPreviewDraftPermissionOnAssetDisplayLayoutWithRestrictedUpdatePermission()
		throws Exception {

		Layout layout = _addDisplayPageTemplateLayout();

		Assert.assertTrue(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.UPDATE_LAYOUT_BASIC), layout));
		Assert.assertTrue(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.UPDATE_LAYOUT_CONTENT),
				layout));
		Assert.assertTrue(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.UPDATE_LAYOUT_LIMITED),
				layout));
	}

	@Test
	public void testContainsPreviewDraftPermissionOnAssetDisplayLayoutWithUpdatePermission()
		throws Exception {

		Assert.assertTrue(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.UPDATE),
				_addDisplayPageTemplateLayout()));
	}

	@Test
	public void testContainsPreviewDraftPermissionOnAssetDisplayLayoutWithViewPermission()
		throws Exception {

		Assert.assertFalse(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.VIEW),
				_addDisplayPageTemplateLayout()));
	}

	@Test
	public void testContainsPreviewDraftPermissionOnPortletTypeLayoutWithPreviewDraftPermission()
		throws Exception {

		Assert.assertFalse(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.PREVIEW_DRAFT),
				LayoutTestUtil.addTypePortletLayout(_group)));
	}

	@Test
	public void testContainsPreviewDraftPermissionOnPortletTypeLayoutWithRestrictedUpdatePermission()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		Assert.assertFalse(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.UPDATE_LAYOUT_BASIC), layout));
		Assert.assertFalse(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.UPDATE_LAYOUT_CONTENT),
				layout));
		Assert.assertFalse(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.UPDATE_LAYOUT_LIMITED),
				layout));
	}

	@Test
	public void testContainsPreviewDraftPermissionOnPortletTypeLayoutWithUpdatePermission()
		throws Exception {

		Assert.assertFalse(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.UPDATE),
				LayoutTestUtil.addTypePortletLayout(_group)));
	}

	@Test
	public void testContainsPreviewDraftPermissionWithPreviewDraftPermission()
		throws Exception {

		Assert.assertTrue(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.PREVIEW_DRAFT),
				LayoutTestUtil.addTypeContentLayout(_group)));
	}

	@Test
	public void testContainsPreviewDraftPermissionWithRestrictedUpdatePermission()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Assert.assertTrue(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.UPDATE_LAYOUT_BASIC), layout));
		Assert.assertTrue(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.UPDATE_LAYOUT_CONTENT),
				layout));
		Assert.assertTrue(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.UPDATE_LAYOUT_LIMITED),
				layout));
	}

	@Test
	public void testContainsPreviewDraftPermissionWithUpdatePermission()
		throws Exception {

		Assert.assertTrue(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.UPDATE),
				LayoutTestUtil.addTypeContentLayout(_group)));
	}

	@Test
	public void testContainsPreviewDraftPermissionWithViewPermission()
		throws Exception {

		Assert.assertFalse(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				_getPermissionChecker(ActionKeys.VIEW),
				LayoutTestUtil.addTypeContentLayout(_group)));
	}

	@Test
	public void testContainsWithoutViewPermissionOnApprovedLayout()
		throws Exception {

		Layout layout = _addTypeContentLayout(true);

		_removeGuestViewPermission(layout);

		Assert.assertFalse(
			_layoutPermission.contains(
				_getGuestPermissionChecker(), layout, ActionKeys.VIEW));
	}

	@Test
	public void testContainsWithoutViewPermissionOnPendingLayout()
		throws Exception {

		try {
			Layout layout = _addTypeContentLayout(true);

			_removeGuestViewPermission(layout);

			_setUpLayoutWorkflow();

			layout = _updateLayout(layout);

			Assert.assertEquals(
				WorkflowConstants.STATUS_PENDING, layout.getStatus());
			Assert.assertFalse(
				_layoutPermission.contains(
					_getGuestPermissionChecker(), layout, ActionKeys.VIEW));
		}
		finally {
			_tearDownLayoutWorkflow();
		}
	}

	@Test
	@TestInfo("LPD-52364")
	public void testContainsWithParentLayoutWithUpdateLayoutBasicPermission()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_MEMBER);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(layout.getPlid()), role.getRoleId(),
			new String[] {ActionKeys.UPDATE});

		PermissionChecker permissionChecker = _getPermissionChecker(
			role, UserTestUtil.addUser());

		Assert.assertTrue(
			_layoutPermission.contains(
				permissionChecker, layout, ActionKeys.UPDATE));
		Assert.assertTrue(
			_layoutPermission.containsLayoutRestrictedUpdatePermission(
				permissionChecker, layout));
		Assert.assertTrue(
			_layoutPermission.containsLayoutUpdatePermission(
				permissionChecker, layout));

		Layout childLayout = LayoutTestUtil.addTypeContentLayout(
			_group, layout.getPlid());

		Assert.assertEquals(layout.getPlid(), childLayout.getParentPlid());
		Assert.assertFalse(
			_layoutPermission.contains(
				permissionChecker, childLayout, ActionKeys.UPDATE));
		Assert.assertFalse(
			_layoutPermission.containsLayoutRestrictedUpdatePermission(
				permissionChecker, childLayout));
		Assert.assertFalse(
			_layoutPermission.containsLayoutUpdatePermission(
				permissionChecker, childLayout));
	}

	@Test
	@TestInfo("LPS-140136")
	public void testContainsWithPreviewDraftPermissionWithLocalStaging()
		throws Exception {

		PermissionChecker permissionChecker = _getPermissionChecker(
			ActionKeys.PREVIEW_DRAFT);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), _group, false, false,
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		Assert.assertFalse(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				permissionChecker, layout));

		Group stagingGroup = _group.getStagingGroup();

		Layout stagingLayout = _layoutLocalService.fetchLayout(
			layout.getUuid(), stagingGroup.getGroupId(),
			layout.isPrivateLayout());

		Assert.assertTrue(
			_layoutPermission.containsLayoutPreviewDraftPermission(
				permissionChecker, stagingLayout));
	}

	@Test
	public void testContainsWithUpdateLayoutAdvancedOptionsPermission()
		throws Exception {

		PermissionChecker permissionChecker = _getPermissionChecker(
			ActionKeys.UPDATE_LAYOUT_ADVANCED_OPTIONS);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Assert.assertTrue(
			_layoutPermission.contains(
				permissionChecker, layout,
				ActionKeys.UPDATE_LAYOUT_ADVANCED_OPTIONS));
		Assert.assertFalse(
			_layoutPermission.containsLayoutRestrictedUpdatePermission(
				permissionChecker, layout));
		Assert.assertFalse(
			_layoutPermission.containsLayoutUpdatePermission(
				permissionChecker, layout));
	}

	@Test
	public void testContainsWithUpdateLayoutBasicPermission() throws Exception {
		PermissionChecker permissionChecker = _getPermissionChecker(
			ActionKeys.UPDATE_LAYOUT_BASIC);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Assert.assertTrue(
			_layoutPermission.contains(
				permissionChecker, layout, ActionKeys.UPDATE_LAYOUT_BASIC));
		Assert.assertTrue(
			_layoutPermission.containsLayoutRestrictedUpdatePermission(
				permissionChecker, layout));
		Assert.assertTrue(
			_layoutPermission.containsLayoutUpdatePermission(
				permissionChecker, layout));
	}

	@Test
	public void testContainsWithUpdateLayoutContentPermission()
		throws Exception {

		PermissionChecker permissionChecker = _getPermissionChecker(
			ActionKeys.UPDATE_LAYOUT_CONTENT);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Assert.assertTrue(
			_layoutPermission.contains(
				permissionChecker, layout, ActionKeys.UPDATE_LAYOUT_CONTENT));

		Assert.assertFalse(
			_layoutPermission.containsLayoutRestrictedUpdatePermission(
				permissionChecker, layout));
		Assert.assertTrue(
			_layoutPermission.containsLayoutUpdatePermission(
				permissionChecker, layout));
	}

	@Test
	public void testContainsWithUpdateLayoutLimitedPermission()
		throws Exception {

		PermissionChecker permissionChecker = _getPermissionChecker(
			ActionKeys.UPDATE_LAYOUT_LIMITED);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Assert.assertTrue(
			_layoutPermission.contains(
				permissionChecker, layout, ActionKeys.UPDATE_LAYOUT_LIMITED));
		Assert.assertTrue(
			_layoutPermission.containsLayoutRestrictedUpdatePermission(
				permissionChecker, layout));
		Assert.assertTrue(
			_layoutPermission.containsLayoutUpdatePermission(
				permissionChecker, layout));
	}

	@Test
	public void testContainsWithUpdatePermission() throws Exception {
		PermissionChecker permissionChecker = _getPermissionChecker(
			ActionKeys.UPDATE);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Assert.assertTrue(
			_layoutPermission.contains(
				permissionChecker, layout, ActionKeys.UPDATE));
		Assert.assertTrue(
			_layoutPermission.containsLayoutRestrictedUpdatePermission(
				permissionChecker, layout));
		Assert.assertTrue(
			_layoutPermission.containsLayoutUpdatePermission(
				permissionChecker, layout));
	}

	@Test
	@TestInfo("LPS-140136")
	public void testContainsWithUpdatePermissionWithLocalStaging()
		throws Exception {

		PermissionChecker permissionChecker = _getPermissionChecker(
			ActionKeys.UPDATE);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), _group, false, false,
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		Assert.assertFalse(
			_layoutPermission.contains(
				permissionChecker, layout, ActionKeys.UPDATE));
		Assert.assertFalse(
			_layoutPermission.containsLayoutRestrictedUpdatePermission(
				permissionChecker, layout));
		Assert.assertFalse(
			_layoutPermission.containsLayoutUpdatePermission(
				permissionChecker, layout));

		Group stagingGroup = _group.getStagingGroup();

		Layout stagingLayout = _layoutLocalService.fetchLayout(
			layout.getUuid(), stagingGroup.getGroupId(),
			layout.isPrivateLayout());

		Assert.assertTrue(
			_layoutPermission.contains(
				permissionChecker, stagingLayout, ActionKeys.UPDATE));
		Assert.assertTrue(
			_layoutPermission.containsLayoutRestrictedUpdatePermission(
				permissionChecker, stagingLayout));
		Assert.assertTrue(
			_layoutPermission.containsLayoutUpdatePermission(
				permissionChecker, stagingLayout));
	}

	@Test
	public void testContainsWithViewPermissionOnApprovedLayout()
		throws Exception {

		Assert.assertTrue(
			_layoutPermission.contains(
				_getGuestPermissionChecker(), _addTypeContentLayout(true),
				ActionKeys.VIEW));
	}

	@Test
	public void testContainsWithViewPermissionOnPendingLayout()
		throws Exception {

		try {
			Layout layout = _addTypeContentLayout(true);

			_setUpLayoutWorkflow();

			layout = _updateLayout(layout);

			Assert.assertEquals(
				WorkflowConstants.STATUS_PENDING, layout.getStatus());

			Assert.assertTrue(
				_layoutPermission.contains(
					_getGuestPermissionChecker(), layout, ActionKeys.VIEW));
		}
		finally {
			_tearDownLayoutWorkflow();
		}
	}

	@Test
	public void testContainsWithViewPermissionOnUnpublishedPendingLayout()
		throws Exception {

		try {
			Layout layout = _addTypeContentLayout(false);

			_setUpLayoutWorkflow();

			layout = _updateLayout(layout);

			Assert.assertEquals(
				WorkflowConstants.STATUS_PENDING, layout.getStatus());

			Assert.assertFalse(
				_layoutPermission.contains(
					_getGuestPermissionChecker(), layout, ActionKeys.VIEW));
		}
		finally {
			_tearDownLayoutWorkflow();
		}
	}

	private Layout _addDisplayPageTemplateLayout() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, _group.getGroupId(), 0, null,
				_portal.getClassNameId(AssetCategory.class.getName()), 0,
				RandomTestUtil.randomString(), 0,
				WorkflowConstants.STATUS_DRAFT, serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		return _layoutLocalService.getLayout(layout.getPlid());
	}

	private Layout _addTypeContentLayout(boolean publish) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		Layout layout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			LayoutConstants.TYPE_CONTENT, false, StringPool.BLANK,
			serviceContext);

		if (publish) {
			Layout draftLayout = layout.fetchDraftLayout();

			Assert.assertNotNull(draftLayout);

			ContentLayoutTestUtil.publishLayout(draftLayout, layout);

			layout = _layoutLocalService.getLayout(layout.getPlid());

			Assert.assertTrue(layout.isPublished());
		}
		else {
			Assert.assertFalse(layout.isPublished());
		}

		return layout;
	}

	private PermissionChecker _getGuestPermissionChecker() throws Exception {
		return PermissionCheckerFactoryUtil.create(
			_userLocalService.getGuestUser(TestPropsValues.getCompanyId()));
	}

	private HttpServletRequest _getHttpServletRequest() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			company, _group, layout);

		themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private PermissionChecker _getPermissionChecker(Role role, User user)
		throws Exception {

		_roleLocalService.clearUserRoles(user.getUserId());

		_roleLocalService.addUserRole(user.getUserId(), role);

		return PermissionCheckerFactoryUtil.create(user);
	}

	private PermissionChecker _getPermissionChecker(String actionId)
		throws Exception {

		return _getPermissionChecker(
			actionId, RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR));
	}

	private PermissionChecker _getPermissionChecker(String actionId, Role role)
		throws Exception {

		RoleTestUtil.addResourcePermission(
			role, Layout.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_group.getCompanyId()), actionId);

		return _getPermissionChecker(role, UserTestUtil.addUser());
	}

	private void _removeGuestViewPermission(Layout layout) throws Exception {
		Role guestRole = _roleLocalService.getRole(
			layout.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.removeResourcePermission(
			layout.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(layout.getPlid()), guestRole.getRoleId(),
			ActionKeys.VIEW);
	}

	private void _setUpLayoutWorkflow() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		serviceContext.setRequest(_getHttpServletRequest());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			_group.getGroupId(), Layout.class.getName(), 0, 0,
			"Single Approver@1");
	}

	private void _tearDownLayoutWorkflow() throws Exception {
		List<WorkflowInstance> workflowInstances =
			WorkflowInstanceManagerUtil.getWorkflowInstances(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				new String[] {Layout.class.getName()}, false, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		for (WorkflowInstance workflowInstance : workflowInstances) {
			WorkflowInstanceManagerUtil.deleteWorkflowInstance(
				TestPropsValues.getCompanyId(),
				workflowInstance.getWorkflowInstanceId());
		}

		ServiceContextThreadLocal.popServiceContext();
	}

	private Layout _updateLayout(Layout layout) throws Exception {
		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		ContentLayoutTestUtil.addPortletToLayout(
			layout, AssetPublisherPortletKeys.ASSET_PUBLISHER);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		return _layoutLocalService.getLayout(layout.getPlid());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Inject
	private LayoutPermission _layoutPermission;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private StagingLocalService _stagingLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}
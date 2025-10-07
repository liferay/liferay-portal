/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.navigation.constants.SiteNavigationConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
@Sync
public class CopyLayoutMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentPublishedLayout(
			_group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		_draftLayout = _layout.fetchDraftLayout();

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid());

		_serviceContext = _getServiceContext(_group);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testCopyLayout() throws Exception {
		_addFragmentEntryLinkToLayout(null);

		_addModelResources(RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR));

		_testCopyLayout(false, Collections.emptyMap());
	}

	@Test
	@TestInfo("LPD-60259")
	public void testCopyLayoutWithLayoutClassedModelUsage() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		ContentLayoutTestUtil.addItemToLayout(
			JSONUtil.put(
				"styles",
				JSONUtil.put(
					"backgroundImage",
					JSONUtil.put(
						"className", JournalArticle.class.getName()
					).put(
						"classNameId",
						_portal.getClassNameId(JournalArticle.class)
					).put(
						"classPK", journalArticle.getResourcePrimKey()
					).put(
						"fieldId", "JournalArticle_authorProfileImage"
					))
			).toString(),
			LayoutDataItemTypeConstants.TYPE_CONTAINER, _draftLayout,
			_layoutStructureProvider, _segmentsExperienceId);

		_addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"element-text",
					JSONUtil.put(
						"className", JournalArticle.class.getName()
					).put(
						"classNameId",
						_portal.getClassNameId(JournalArticle.class)
					).put(
						"classPK", journalArticle.getResourcePrimKey()
					).put(
						"fieldId", "JournalArticle_title"
					))
			).toString());

		_assertLayoutClassedModelUsages(
			2, journalArticle.getResourcePrimKey(), _draftLayout.getPlid());
		_assertLayoutClassedModelUsages(
			2, journalArticle.getResourcePrimKey(), _layout.getPlid());

		_addModelResources(RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR));

		Layout layout = _testCopyLayout(false, Collections.emptyMap());

		_assertLayoutClassedModelUsages(
			0, journalArticle.getResourcePrimKey(), layout.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		_assertLayoutClassedModelUsages(
			2, journalArticle.getResourcePrimKey(), draftLayout.getPlid());

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_assertLayoutClassedModelUsages(
			2, journalArticle.getResourcePrimKey(), draftLayout.getPlid());

		_assertLayoutClassedModelUsages(
			2, journalArticle.getResourcePrimKey(), layout.getPlid());
	}

	@Test
	@TestInfo("LPS-131982")
	public void testCopyLayoutWithMasterLayout() throws Exception {
		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layout = _layoutLocalService.updateMasterLayoutPlid(
			_group.getGroupId(), _layout.isPrivateLayout(),
			_layout.getLayoutId(), masterLayoutPageTemplateEntry.getPlid());

		_processAction(false, Collections.emptyMap());

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			_layout.getGroupId(), _layout.isPrivateLayout(), "/" + _NAME);

		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getPlid(),
			layout.getMasterLayoutPlid());
	}

	@Test
	public void testCopyLayoutWithNavigationMenu() throws Exception {
		_addFragmentEntryLinkToLayout(null);

		_addModelResources(RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR));

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), _group.getGroupId(), "Menu",
				SiteNavigationConstants.TYPE_DEFAULT, true, _serviceContext);

		_testCopyLayout(
			false,
			HashMapBuilder.put(
				"TypeSettingsProperties--siteNavigationMenuId--",
				String.valueOf(siteNavigationMenu.getSiteNavigationMenuId())
			).build());

		long navigationItemCount =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItemsCount(
				siteNavigationMenu.getSiteNavigationMenuId());

		Assert.assertEquals(1, navigationItemCount);
	}

	@Test
	@TestInfo({"LPS-175090", "LPS-192724"})
	public void testCopyLayoutWithPermissions() throws Exception {
		_addFragmentEntryLinkToLayout(null);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_addModelResources(role);

		Role guestRole = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		_removeViewResourcePermission(guestRole.getRoleId());

		Layout layout = _testCopyLayout(true, Collections.emptyMap());

		_assertViewResourcePermission(
			layout.getPlid(), guestRole.getRoleId(), false);
		_assertViewResourcePermission(layout.getPlid(), role.getRoleId(), true);
	}

	@Test
	@TestInfo("LPD-42253")
	public void testCopyLayoutWithSegmentsExperience() throws Exception {
		_addSegmentsExperience();

		int count = _segmentsExperienceLocalService.getSegmentsExperiencesCount(
			_layout.getGroupId(), _layout.getPlid());

		_processAction(false, Collections.emptyMap());

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			_group.getGroupId(), _layout.isPrivateLayout(), "/" + _NAME);

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertEquals(
			count,
			_segmentsExperienceLocalService.getSegmentsExperiencesCount(
				_group.getGroupId(), draftLayout.getPlid()));
	}

	private void _addFragmentEntryLinkToLayout(String editableValues)
		throws Exception {

		FragmentEntry fragmentEntry = _getFragmentEntry();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			editableValues, fragmentEntry.getCss(),
			fragmentEntry.getConfiguration(),
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), _draftLayout,
			fragmentEntry.getFragmentEntryKey(), _segmentsExperienceId,
			fragmentEntry.getType());

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);
	}

	private void _addModelResources(Role role) throws Exception {
		ModelPermissions modelPermissions = ModelPermissionsFactory.create(
			Layout.class.getName());

		modelPermissions.addRolePermissions(role.getName(), ActionKeys.VIEW);

		_resourceLocalService.addModelResources(
			_layout.getCompanyId(), _layout.getGroupId(), _layout.getUserId(),
			Layout.class.getName(), _layout.getPlid(), modelPermissions);
	}

	private void _addSegmentsExperience() throws Exception {
		int count = _segmentsExperienceLocalService.getSegmentsExperiencesCount(
			_layout.getGroupId(), _layout.getPlid());

		Layout draftLayout = _layout.fetchDraftLayout();

		MVCActionCommand addSegmentsExperienceMVCActionCommand =
			ContentLayoutTestUtil.getMVCActionCommand(
				"/layout_content_page_editor/add_segments_experience");

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_companyLocalService.getCompany(_group.getCompanyId()), _group,
				draftLayout);

		mockLiferayPortletActionRequest.addParameter(
			"groupId", String.valueOf(draftLayout.getGroupId()));
		mockLiferayPortletActionRequest.addParameter(
			"name", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			"plid", String.valueOf(draftLayout.getPlid()));

		HttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockHttpServletRequest.setAttribute(
			WebKeys.USER_ID, TestPropsValues.getUserId());

		mockLiferayPortletActionRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);

		ReflectionTestUtil.invoke(
			addSegmentsExperienceMVCActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		ContentLayoutTestUtil.publishLayout(draftLayout, _layout);

		Assert.assertEquals(
			count + 1,
			_segmentsExperienceLocalService.getSegmentsExperiencesCount(
				_group.getGroupId(), _layout.getPlid()));
	}

	private void _assertLayoutClassedModelUsages(
			int count, long classPK, long plid)
		throws Exception {

		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesByPlid(plid);

		Assert.assertEquals(
			layoutClassedModelUsages.toString(), count,
			layoutClassedModelUsages.size());

		for (int i = 0; i < count; i++) {
			LayoutClassedModelUsage layoutClassedModelUsage =
				layoutClassedModelUsages.get(i);

			Assert.assertEquals(classPK, layoutClassedModelUsage.getClassPK());

			if (_portal.getClassNameId(FragmentEntryLink.class) ==
					layoutClassedModelUsage.getContainerType()) {

				FragmentEntryLink fragmentEntryLink =
					_fragmentEntryLinkLocalService.getFragmentEntryLink(
						GetterUtil.getLong(
							layoutClassedModelUsage.getContainerKey()));

				Assert.assertEquals(plid, fragmentEntryLink.getPlid());

				continue;
			}

			Assert.assertEquals(
				_portal.getClassNameId(LayoutPageTemplateStructure.class),
				layoutClassedModelUsage.getContainerType());

			LayoutPageTemplateStructure layoutPageTemplateStructure =
				_layoutPageTemplateStructureLocalService.
					getLayoutPageTemplateStructure(
						GetterUtil.getLong(
							layoutClassedModelUsage.getContainerKey()));

			Assert.assertEquals(plid, layoutPageTemplateStructure.getPlid());
		}
	}

	private void _assertViewResourcePermission(
			long plid, long roleId, boolean permission)
		throws Exception {

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				TestPropsValues.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(plid),
				roleId);

		if (permission) {
			Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));
		}
		else {
			Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.VIEW));
		}
	}

	private FragmentEntry _getFragmentEntry() throws Exception {
		if (_fragmentEntry != null) {
			return _fragmentEntry;
		}

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				_serviceContext);

		_fragmentEntry = _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), "fragment-entry-key",
			RandomTestUtil.randomString(), StringPool.BLANK,
			"<h1 data-lfr-editable-id=\"element-text\" " +
				"data-lfr-editable-type=\"text\">Heading Example</h1>",
			StringPool.BLANK, false, StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);

		return _fragmentEntry;
	}

	private ServiceContext _getServiceContext(Group group) throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			group, TestPropsValues.getUserId());
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(_group.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _processAction(
			boolean copyPermissions, Map<String, String> map)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"copyPermissions", String.valueOf(copyPermissions));
		mockLiferayPortletActionRequest.addParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletActionRequest.addParameter(
			"privateLayout", String.valueOf(_layout.isPrivateLayout()));
		mockLiferayPortletActionRequest.addParameter("name", _NAME);
		mockLiferayPortletActionRequest.addParameter(
			"sourcePlid", String.valueOf(_layout.getPlid()));

		for (Map.Entry<String, String> entry : map.entrySet()) {
			mockLiferayPortletActionRequest.addParameter(
				entry.getKey(), entry.getValue());
		}

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());
	}

	private void _removeViewResourcePermission(long roleId) throws Exception {
		_resourcePermissionLocalService.removeResourcePermission(
			TestPropsValues.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_layout.getPlid()), roleId, ActionKeys.VIEW);

		_assertViewResourcePermission(_layout.getPlid(), roleId, false);
	}

	private Layout _testCopyLayout(
			boolean copyPermissions, Map<String, String> map)
		throws Exception {

		_processAction(copyPermissions, map);

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			_layout.getGroupId(), _layout.isPrivateLayout(), "/" + _NAME);

		Assert.assertNotNull(layout);

		List<FragmentEntryLink>
			expectedLayoutSegmentsExperienceLayoutFragmentEntryLinks =
				_fragmentEntryLinkLocalService.
					getFragmentEntryLinksBySegmentsExperienceId(
						_group.getGroupId(),
						_segmentsExperienceLocalService.
							fetchDefaultSegmentsExperienceId(_layout.getPlid()),
						_layout.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		List<FragmentEntryLink>
			actualLayoutSegmentsExperienceLayoutFragmentEntryLinks =
				_fragmentEntryLinkLocalService.
					getFragmentEntryLinksBySegmentsExperienceId(
						_group.getGroupId(),
						_segmentsExperienceLocalService.
							fetchDefaultSegmentsExperienceId(
								draftLayout.getPlid()),
						draftLayout.getPlid());

		Assert.assertEquals(
			actualLayoutSegmentsExperienceLayoutFragmentEntryLinks.toString(),
			expectedLayoutSegmentsExperienceLayoutFragmentEntryLinks.size(),
			actualLayoutSegmentsExperienceLayoutFragmentEntryLinks.size());

		FragmentEntryLink expectedLayoutFragmentEntryLink =
			expectedLayoutSegmentsExperienceLayoutFragmentEntryLinks.get(0);

		FragmentEntryLink actualLayoutFragmentEntryLink =
			actualLayoutSegmentsExperienceLayoutFragmentEntryLinks.get(0);

		Assert.assertEquals(
			expectedLayoutFragmentEntryLink.getConfiguration(),
			actualLayoutFragmentEntryLink.getConfiguration());
		Assert.assertEquals(
			expectedLayoutFragmentEntryLink.getCss(),
			actualLayoutFragmentEntryLink.getCss());
		Assert.assertEquals(
			expectedLayoutFragmentEntryLink.getEditableValues(),
			actualLayoutFragmentEntryLink.getEditableValues());
		Assert.assertEquals(
			expectedLayoutFragmentEntryLink.getHtml(),
			actualLayoutFragmentEntryLink.getHtml());
		Assert.assertEquals(
			expectedLayoutFragmentEntryLink.getJs(),
			actualLayoutFragmentEntryLink.getJs());
		Assert.assertEquals(
			expectedLayoutFragmentEntryLink.getRendererKey(),
			actualLayoutFragmentEntryLink.getRendererKey());
		Assert.assertEquals(
			expectedLayoutFragmentEntryLink.getEditableValues(),
			actualLayoutFragmentEntryLink.getEditableValues());
		Assert.assertEquals(
			expectedLayoutFragmentEntryLink.getPosition(),
			actualLayoutFragmentEntryLink.getPosition());

		List<ResourcePermission> expectedResourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				_layout.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(_layout.getPlid()));

		List<ResourcePermission> actualResourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				_layout.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout.getPlid()));

		if (copyPermissions) {
			Assert.assertEquals(
				expectedResourcePermissions.toString(),
				expectedResourcePermissions.size(),
				actualResourcePermissions.size());
		}
		else {
			Assert.assertNotEquals(
				expectedResourcePermissions.toString(),
				expectedResourcePermissions.size(),
				actualResourcePermissions.size());
		}

		return layout;
	}

	private static final String _NAME = StringUtil.toLowerCase(
		RandomTestUtil.randomString());

	@Inject
	private CompanyLocalService _companyLocalService;

	private Layout _draftLayout;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	private FragmentEntry _fragmentEntry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject(filter = "mvc.command.name=/layout_admin/copy_layout")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private ResourceLocalService _resourceLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private long _segmentsExperienceId;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Inject
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

}
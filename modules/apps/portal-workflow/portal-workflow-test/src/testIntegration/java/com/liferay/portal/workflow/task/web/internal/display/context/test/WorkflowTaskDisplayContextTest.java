/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.task.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.kernel.workflow.WorkflowTaskManagerUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class WorkflowTaskDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_cmsGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetTaglibEditURL() throws Exception {
		String taglibEditURL = _getTaglibEditURL(_cmsGroup);

		Assert.assertTrue(taglibEditURL.contains("cms/edit_content_item"));

		taglibEditURL = _getTaglibEditURL(_group);

		Assert.assertTrue(taglibEditURL.contains("control_panel/manage"));
	}

	@Test
	public void testGetWorkflowAssetEntry() throws Exception {
		User user = UserTestUtil.addUser(_group.getGroupId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());

		Folder folder = DLAppTestUtil.addFolder(_group.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAssetTagNames(RandomTestUtil.randomStrings(1));

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			RandomTestUtil.randomString(), user.getUserId(),
			folder.getRepositoryId(), folder.getFolderId(), "test",
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomBytes(), new Date(),
			null, null, serviceContext);

		_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			null, TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			_group.getGroupId(), DLFolder.class.getName(), 0, -1,
			"Single Approver", 1);

		serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		String newTag = RandomTestUtil.randomString();

		serviceContext.setAssetTagNames(new String[] {newTag});

		_dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), fileEntry.getFileName(),
			fileEntry.getMimeType(), RandomTestUtil.randomString(),
			StringUtil.randomString(), fileEntry.getDescription(),
			RandomTestUtil.randomString(), DLVersionNumberIncrease.MAJOR,
			fileEntry.getContentStream(), fileEntry.getSize(),
			fileEntry.getDisplayDate(), fileEntry.getExpirationDate(),
			fileEntry.getReviewDate(), serviceContext);

		List<WorkflowTask> workflowTasks =
			WorkflowTaskManagerUtil.getWorkflowTasksBySubmittingUser(
				_group.getCompanyId(), user.getUserId(), false, -1, -1, null);

		WorkflowTask workflowTask = workflowTasks.get(0);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest(_group);

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		Object workflowTaskDisplayContext =
			mockLiferayPortletRenderRequest.getAttribute(
				"PORTLET_DISPLAY_CONTEXT");

		AssetRenderer<DLFileEntry> assetRenderer = ReflectionTestUtil.invoke(
			workflowTaskDisplayContext, "getAssetRenderer",
			new Class<?>[] {WorkflowTask.class}, workflowTask);

		WorkflowHandler<DLFileEntry> workflowHandler =
			ReflectionTestUtil.invoke(
				workflowTaskDisplayContext, "getWorkflowHandler",
				new Class<?>[] {WorkflowTask.class}, workflowTask);

		Long classPK = ReflectionTestUtil.invoke(
			workflowTaskDisplayContext, "getWorkflowContextEntryClassPK",
			new Class<?>[] {WorkflowHandler.class, WorkflowTask.class},
			workflowHandler, workflowTask);

		AssetEntry assetEntry = ReflectionTestUtil.invoke(
			workflowTaskDisplayContext, "getWorkflowAssetEntry",
			new Class<?>[] {String.class, long.class, long.class},
			workflowHandler.getClassName(), classPK,
			assetRenderer.getClassPK());

		List<AssetTag> assetTags =
			AssetTagLocalServiceUtil.getAssetEntryAssetTags(
				assetEntry.getEntryId());

		Assert.assertEquals(assetTags.toString(), 1, assetTags.size());

		AssetTag assetTag = assetTags.get(0);

		Assert.assertEquals(assetTags.toString(), newTag, assetTag.getName());
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
			Group group)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		String path = "/view.jsp";

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(group.getCompanyId()));

		Layout layout = LayoutTestUtil.addTypePortletLayout(group.getGroupId());

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		themeDisplay.setLocale(LocaleUtil.getSiteDefault());

		User user = UserTestUtil.getAdminUser(group.getCompanyId());

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		themeDisplay.setPortalURL(
			"http://localhost:" + PortalUtil.getPortalServerPort(false));
		themeDisplay.setRealUser(user);
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setServerName("localhost");
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setUser(user);

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletRenderRequest.setParameter("mvcPath", path);

		return mockLiferayPortletRenderRequest;
	}

	private String _getTaglibEditURL(Group group) throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest(group);

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		Object workflowTaskDisplayContext =
			mockLiferayPortletRenderRequest.getAttribute(
				"PORTLET_DISPLAY_CONTEXT");

		_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			null, TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			group.getGroupId(), BlogsEntry.class.getName(), 0, 0,
			"Single Approver", 1);

		_blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), StringUtil.randomString(),
			StringUtil.randomString(),
			new Date(System.currentTimeMillis() - Time.SECOND),
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		List<WorkflowTask> workflowTasks = new ArrayList<>(
			_workflowTaskManager.getWorkflowTasksByUserRoles(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));

		WorkflowTask workflowTask = workflowTasks.get(0);

		return ReflectionTestUtil.invoke(
			workflowTaskDisplayContext, "getTaglibEditURL",
			new Class<?>[] {WorkflowTask.class}, workflowTask);
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	private Group _cmsGroup;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLAppService _dlAppService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(
		filter = "component.name=com.liferay.portal.workflow.task.web.internal.portlet.MyWorkflowTaskPortlet"
	)
	private Portlet _portlet;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.workflow.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class DLFileEntryWorkflowHandlerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		_folder = _getSingleApproverFolder(_serviceContext);

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		_user = TestPropsValues.getUser();
	}

	@Test
	public void testContributeWorkflowContext() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		_serviceContext.setRequest(httpServletRequest);

		_serviceContext.setAttribute(DDMFormValues.class.getName(), null);

		_addLayoutPageTemplateEntry();

		FileEntry fileEntry = _addFileEntry(null, null);

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, dlFileEntry.getStatus());

		WorkflowInstance workflowInstance = _approveWorkflowTask(dlFileEntry);

		dlFileEntry = _dlFileEntryLocalService.getFileEntry(
			dlFileEntry.getFileEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, dlFileEntry.getStatus());

		Map<String, Serializable> approvedWorkflowContext =
			workflowInstance.getWorkflowContext();

		ServiceContext serviceContext =
			(ServiceContext)approvedWorkflowContext.get(
				WorkflowConstants.CONTEXT_SERVICE_CONTEXT);

		Map<String, Serializable> attributes = serviceContext.getAttributes();

		Assert.assertFalse(
			attributes.containsKey(DDMFormValues.class.getName()));

		Assert.assertNotEquals(
			StringPool.BLANK,
			approvedWorkflowContext.get(WorkflowConstants.CONTEXT_URL));
	}

	@Test
	public void testGetAssetRendererWhenFileEntryIsCheckedOut()
		throws Exception {

		FileEntry fileEntry = _addFileEntry(null, null);

		FileVersion fileVersion = fileEntry.getFileVersion();

		_dlAppService.checkOutFileEntry(
			fileEntry.getFileEntryId(), _serviceContext);

		_dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), fileEntry.getFileName(),
			fileEntry.getMimeType(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), DLVersionNumberIncrease.NONE,
			StringPool.SPACE.getBytes(), null, null, null, _serviceContext);

		WorkflowHandler<DLFileEntry> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				DLFileEntry.class.getName());

		AssetRenderer<DLFileEntry> assetRenderer =
			workflowHandler.getAssetRenderer(fileVersion.getFileVersionId());

		Assert.assertEquals(
			fileVersion.getTitle(),
			assetRenderer.getTitle(LocaleUtil.getDefault()));
	}

	@Test
	public void testWorkflowApprovesFileEntry() throws PortalException {
		FileEntry fileEntry = _addFileEntry(null, null);

		FileVersion fileVersion = fileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, fileVersion.getStatus());

		_updateStatus(fileVersion, WorkflowConstants.STATUS_APPROVED);

		fileEntry = _dlAppService.getFileEntry(fileEntry.getFileEntryId());

		fileVersion = fileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion.getStatus());
	}

	@Test
	public void testWorkflowApprovesFileEntryExpireFutureDate()
		throws PortalException {

		_serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		Date expirationDate = new Date(System.currentTimeMillis() + Time.DAY);

		FileEntry fileEntry = _addFileEntry(null, expirationDate);

		FileVersion fileVersion = fileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, fileVersion.getStatus());

		_updateStatus(fileVersion, WorkflowConstants.STATUS_APPROVED);

		fileEntry = _dlAppService.getFileEntry(fileEntry.getFileEntryId());

		fileVersion = fileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion.getStatus());
		Assert.assertEquals(expirationDate, fileVersion.getExpirationDate());
	}

	@Test
	public void testWorkflowApprovesFileEntryExpirePastDate()
		throws PortalException {

		FileEntry fileEntry = _addFileEntry(null, null);

		FileVersion fileVersion = fileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, fileVersion.getStatus());

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.getDLFileEntry(
			fileEntry.getFileEntryId());

		dlFileEntry.setExpirationDate(
			new Date(System.currentTimeMillis() - Time.DAY));

		_dlFileEntryLocalService.updateDLFileEntry(dlFileEntry);

		_updateStatus(fileVersion, WorkflowConstants.STATUS_APPROVED);

		fileEntry = _dlAppService.getFileEntry(fileEntry.getFileEntryId());

		fileVersion = fileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion.getStatus());
		Assert.assertNull(fileVersion.getExpirationDate());
	}

	@Test
	public void testWorkflowApprovesFileEntryScheduledFutureDate()
		throws PortalException {

		FileEntry fileEntry = _addFileEntry(
			new Date(System.currentTimeMillis() + Time.DAY), null);

		FileVersion fileVersion = fileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, fileVersion.getStatus());

		_updateStatus(fileVersion, WorkflowConstants.STATUS_APPROVED);

		fileEntry = _dlAppService.getFileEntry(fileEntry.getFileEntryId());

		fileVersion = fileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, fileVersion.getStatus());
	}

	@Test
	public void testWorkflowApprovesFileEntryScheduledPastDate()
		throws PortalException {

		FileEntry fileEntry = _addFileEntry(null, null);

		FileVersion fileVersion = fileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, fileVersion.getStatus());

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.getDLFileEntry(
			fileEntry.getFileEntryId());

		dlFileEntry.setDisplayDate(
			new Date(System.currentTimeMillis() - Time.DAY));

		_dlFileEntryLocalService.updateDLFileEntry(dlFileEntry);

		_updateStatus(fileVersion, WorkflowConstants.STATUS_APPROVED);

		fileEntry = _dlAppService.getFileEntry(fileEntry.getFileEntryId());

		fileVersion = fileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, fileVersion.getStatus());
	}

	@Test
	public void testWorkflowRejectsFileEntry() throws PortalException {
		FileEntry fileEntry = _addFileEntry(null, null);

		FileVersion fileVersion = fileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, fileVersion.getStatus());

		_updateStatus(fileVersion, WorkflowConstants.STATUS_DENIED);

		fileEntry = _dlAppService.getFileEntry(fileEntry.getFileEntryId());

		fileVersion = fileEntry.getFileVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_DENIED, fileVersion.getStatus());
	}

	private FileEntry _addFileEntry(Date displayDate, Date expirationDate)
		throws PortalException {

		return _dlAppService.addFileEntry(
			null, _group.getGroupId(), _folder.getFolderId(),
			RandomTestUtil.randomString(), "text",
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), StringPool.BLANK,
			StringPool.SPACE.getBytes(), displayDate, expirationDate, null,
			_serviceContext);
	}

	private void _addLayoutPageTemplateEntry() throws Exception {
		String name = StringUtil.randomString();
		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.fetchDLFileEntryType(
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				name.toLowerCase(LocaleUtil.ROOT),
				PortalUtil.getClassNameId(FileEntry.class.getName()),
				dlFileEntryType.getFileEntryTypeKey(), name,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0L, 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), true);
	}

	private WorkflowInstance _approveWorkflowTask(DLFileEntry dlFileEntry)
		throws Exception {

		DLFileVersion latestFileVersion = dlFileEntry.getLatestFileVersion(
			true);

		List<WorkflowInstance> workflowInstances =
			_workflowInstanceManager.getWorkflowInstances(
				_group.getCompanyId(), TestPropsValues.getUserId(),
				DLFileEntry.class.getName(),
				latestFileVersion.getFileVersionId(), false, -1, -1, null);

		WorkflowInstance workflowInstance = workflowInstances.get(0);

		List<WorkflowTask> workflowTasks =
			_workflowTaskManager.getWorkflowTasksByWorkflowInstance(
				_group.getCompanyId(), null,
				workflowInstance.getWorkflowInstanceId(), false, 0, 1, null);

		if (ListUtil.isNotEmpty(workflowTasks)) {
			WorkflowTask workflowTask = workflowTasks.get(0);

			_workflowTaskManager.assignWorkflowTaskToUser(
				_group.getCompanyId(), TestPropsValues.getUserId(),
				workflowTask.getWorkflowTaskId(), TestPropsValues.getUserId(),
				null, null, workflowInstance.getWorkflowContext());

			_workflowTaskManager.completeWorkflowTask(
				_group.getCompanyId(), TestPropsValues.getUserId(),
				workflowTask.getWorkflowTaskId(), "approve", null,
				workflowInstance.getWorkflowContext());
		}

		return workflowInstance;
	}

	private Folder _getSingleApproverFolder(ServiceContext serviceContext)
		throws Exception {

		Folder folder = _dlAppService.addFolder(
			null, _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		serviceContext.setAttribute(
			"restrictionType", DLFolderConstants.RESTRICTION_TYPE_WORKFLOW);
		serviceContext.setAttribute(
			"workflowDefinition" +
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL,
			"Single Approver@1");

		return _dlAppService.updateFolder(
			folder.getFolderId(), folder.getName(), folder.getDescription(),
			serviceContext);
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLanguageId(_user.getLanguageId());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRequest(new MockHttpServletRequest());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setServerName("localhost");
		themeDisplay.setServerPort(PortalUtil.getPortalServerPort(false));
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _updateStatus(FileVersion fileVersion, int status)
		throws PortalException {

		WorkflowHandler<DLFileEntry> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				DLFileEntry.class.getName());

		workflowHandler.updateStatus(
			status,
			HashMapBuilder.<String, Serializable>put(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK,
				String.valueOf(fileVersion.getFileVersionId())
			).put(
				WorkflowConstants.CONTEXT_USER_ID,
				String.valueOf(TestPropsValues.getUserId())
			).put(
				"serviceContext", _serviceContext
			).build());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	private Folder _folder;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	private ServiceContext _serviceContext;
	private User _user;

	@Inject
	private WorkflowInstanceManager _workflowInstanceManager;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.display.context.DLDisplayContextProvider;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownGroupItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.item.selector.ItemSelector;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Gergely Szalay
 */
@RunWith(Arquillian.class)
@Sync
public class DefaultDLViewFileVersionDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		_fileEntry = _addDLFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	@Test
	public void testCreateViewUsageDropdownItemWithUsage() throws Exception {
		_addLayoutClassedModelUsage(_fileEntry);

		DropdownItem dropdownItem = _getDropdownItem(_fileEntry, "View Usages");

		Assert.assertFalse((Boolean)dropdownItem.get("disabled"));
		Assert.assertEquals("list-ul", dropdownItem.get("icon"));
		Assert.assertEquals("View Usages", dropdownItem.get("label"));
	}

	@Test
	public void testCreateViewUsageDropdownItemWithoutUsage() throws Exception {
		DropdownItem dropdownItem = _getDropdownItem(_fileEntry, "View Usages");

		Assert.assertTrue((Boolean)dropdownItem.get("disabled"));
		Assert.assertEquals("list-ul", dropdownItem.get("icon"));
		Assert.assertEquals("View Usages", dropdownItem.get("label"));
	}

	@Test
	public void testGetActionDropdownItems() throws Exception {
		DropdownItem dropdownItem = _getDropdownItem(_fileEntry, "Checkout");

		Assert.assertEquals("lock", dropdownItem.get("icon"));
		Assert.assertEquals("Checkout", dropdownItem.get("label"));
	}

	@Test
	public void testGetActionDropdownItemsWithWorkflowEnabled()
		throws Exception {

		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_serviceContext);

		_dlFolderService.updateFolder(
			folder.getFolderId(), folder.getName(), folder.getDescription(), 0,
			ListUtil.fromArray(0L), DLFolderConstants.RESTRICTION_TYPE_WORKFLOW,
			_serviceContext);

		_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			null, TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			_group.getGroupId(), DLFolder.class.getName(), folder.getFolderId(),
			-1, "Single Approver", 1);

		FileEntry fileEntry = _addDLFileEntry(
			folder.getFolderId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		DropdownItem dropdownItem = _getDropdownItem(fileEntry, "Checkout");

		Assert.assertEquals("lock", dropdownItem.get("icon"));
		Assert.assertEquals("Checkout", dropdownItem.get("label"));

		List<WorkflowTask> workflowTasks =
			_workflowTaskManager.getWorkflowTasksBySubmittingUser(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				false, 0, 1, null);

		WorkflowTask workflowTask = workflowTasks.get(0);

		_workflowTaskManager.assignWorkflowTaskToUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			workflowTask.getWorkflowTaskId(), TestPropsValues.getUserId(),
			StringPool.BLANK, null, null);

		_workflowTaskManager.completeWorkflowTask(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			workflowTask.getWorkflowTaskId(), Constants.APPROVE,
			StringPool.BLANK, null);

		dropdownItem = _getDropdownItem(fileEntry, "Checkout");

		Assert.assertEquals("lock", dropdownItem.get("icon"));
		Assert.assertEquals("Checkout", dropdownItem.get("label"));
	}

	private FileEntry _addDLFileEntry(
			long folderId, String fileName, String content)
		throws Exception {

		return DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(), folderId,
			fileName, ContentTypes.TEXT_PLAIN, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			content.getBytes(), null, null, null, _serviceContext);
	}

	private LayoutClassedModelUsage _addLayoutClassedModelUsage(
			FileEntry fileEntry)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		return _layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			_group.getGroupId(), StringPool.BLANK,
			PortalUtil.getClassNameId(FileEntry.class),
			fileEntry.getFileEntryId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLong(), layout.getPlid(), _serviceContext);
	}

	private List<DropdownItem> _getDropdownGroupItems(
		List<DropdownItem> dropdownItems, int groupIndex) {

		DropdownGroupItem dropdownGroupItem =
			(DropdownGroupItem)dropdownItems.get(groupIndex);

		return (List<DropdownItem>)dropdownGroupItem.get("items");
	}

	private DropdownItem _getDropdownItem(FileEntry fileEntry, String label)
		throws Exception {

		List<DropdownItem> dropdownGroupItems = _getDropdownGroupItems(
			_getDropdownItems(fileEntry), 1);

		for (DropdownItem dropdownItem : dropdownGroupItems) {
			if (Objects.equals(dropdownItem.get("label"), label)) {
				return dropdownItem;
			}
		}

		return null;
	}

	private List<DropdownItem> _getDropdownItems(FileEntry fileEntry)
		throws Exception {

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			ItemSelector.class.getName(), _itemSelector);

		String portletName = RandomTestUtil.randomString();

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest() {

				@Override
				public String getPortletName() {
					return portletName;
				}

			};

		mockLiferayPortletRenderRequest.setAttribute(
			StringBundler.concat(
				portletName, StringPool.DASH, WebKeys.CURRENT_PORTLET_URL),
			new MockLiferayPortletURL());

		httpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			mockLiferayPortletRenderRequest);

		httpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setPortletName(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN);

		themeDisplay.setCompany(_company);
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		DLViewFileVersionDisplayContext dLViewFileVersionDisplayContext =
			_dLDisplayContextProvider.getDLViewFileVersionDisplayContext(
				httpServletRequest, new MockHttpServletResponse(),
				fileEntry.getFileVersion());

		return dLViewFileVersionDisplayContext.getActionDropdownItems();
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.document.library.web.internal.display.context.DLDisplayContextProviderImpl",
		type = Inject.NoType.class
	)
	private DLDisplayContextProvider _dLDisplayContextProvider;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFolderService _dlFolderService;

	private FileEntry _fileEntry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ItemSelector _itemSelector;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}
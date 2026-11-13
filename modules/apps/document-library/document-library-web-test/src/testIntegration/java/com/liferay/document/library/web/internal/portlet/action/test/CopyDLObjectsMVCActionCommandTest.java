/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
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

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class CopyDLObjectsMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-115331")
	public void testProcessAction() throws Exception {
		_testProcessActionWithCompanyGroupFileEntryType();
		_testProcessActionWithDepotGroupFileEntryType();
		_testProcessActionWithSiteGroupFileEntryType();
	}

	private FileEntry _addFileEntry(
			long fileEntryTypeId, long folderId, long groupId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		serviceContext.setAttribute("fileEntryTypeId", fileEntryTypeId);

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), groupId, folderId,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, RandomTestUtil.randomBytes(), null, null, null,
			serviceContext);
	}

	private DLFileEntryType _addFileEntryType(long groupId) throws Exception {
		Group group = _groupLocalService.getGroup(groupId);

		DDMStructure ddmStructure = _ddmStructureLocalService.addStructure(
			null, TestPropsValues.getUserId(), groupId,
			DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
			PortalUtil.getClassNameId(DLFileEntryMetadata.class),
			StringPool.BLANK,
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.emptyMap(), StringPool.BLANK,
			StorageType.DEFAULT.toString(),
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		return _dlFileEntryTypeLocalService.addFileEntryType(
			null, TestPropsValues.getUserId(), groupId,
			ddmStructure.getStructureId(), null,
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.emptyMap(),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private void _assertProcessAction(
			long destinationGroupId, long expectedFileEntryTypeId,
			FileEntry fileEntry)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(
				_portletLocalService.getPortletById(
					DLPortletKeys.DOCUMENT_LIBRARY_ADMIN),
				null));
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(destinationGroupId));
		mockLiferayPortletActionRequest.setParameter(
			"destinationParentFolderId",
			String.valueOf(DLFolderConstants.DEFAULT_PARENT_FOLDER_ID));
		mockLiferayPortletActionRequest.setParameter(
			"destinationRepositoryId", String.valueOf(destinationGroupId));
		mockLiferayPortletActionRequest.setParameter(
			"dlObjectIds", String.valueOf(fileEntry.getFileEntryId()));
		mockLiferayPortletActionRequest.setParameter(
			"sourceRepositoryId", String.valueOf(fileEntry.getGroupId()));
		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		FileEntry copiedFileEntry = _dlAppLocalService.getFileEntry(
			destinationGroupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			fileEntry.getTitle());

		DLFileEntry dlFileEntry = (DLFileEntry)copiedFileEntry.getModel();

		Assert.assertEquals(
			expectedFileEntryTypeId, dlFileEntry.getFileEntryTypeId());
	}

	private void _deleteRepositories() throws Exception {
		_dlAppLocalService.deleteAllRepositories(_group1.getGroupId());
		_dlAppLocalService.deleteAllRepositories(_group2.getGroupId());
	}

	private ThemeDisplay _getThemeDisplay(long groupId) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(groupId);
		themeDisplay.setSiteGroupId(groupId);
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _testProcessActionWithCompanyGroupFileEntryType()
		throws Exception {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		DLFileEntryType dlFileEntryType = _addFileEntryType(
			companyGroup.getGroupId());

		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), _group1.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_group1.getGroupId()));

		_assertProcessAction(
			_group2.getGroupId(), dlFileEntryType.getFileEntryTypeId(),
			_addFileEntry(
				dlFileEntryType.getFileEntryTypeId(), folder.getFolderId(),
				_group1.getGroupId()));

		_deleteRepositories();

		_dlFileEntryTypeLocalService.deleteFileEntryType(dlFileEntryType);
	}

	private void _testProcessActionWithDepotGroupFileEntryType()
		throws Exception {

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.emptyMap(), DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			false, _depotEntry.getDepotEntryId(), _group2.getGroupId(), true);
		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			true, _depotEntry.getDepotEntryId(), _group1.getGroupId(), true);

		DLFileEntryType dlFileEntryType = _addFileEntryType(
			_depotEntry.getGroupId());

		FileEntry fileEntry = _addFileEntry(
			dlFileEntryType.getFileEntryTypeId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			_depotEntry.getGroupId());

		_assertProcessAction(
			_group1.getGroupId(), dlFileEntryType.getFileEntryTypeId(),
			fileEntry);
		_assertProcessAction(
			_group2.getGroupId(),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
			fileEntry);

		_deleteRepositories();
	}

	private void _testProcessActionWithSiteGroupFileEntryType()
		throws Exception {

		DLFileEntryType dlFileEntryType = _addFileEntryType(
			_group1.getGroupId());

		_assertProcessAction(
			_group2.getGroupId(),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
			_addFileEntry(
				dlFileEntryType.getFileEntryTypeId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				_group1.getGroupId()));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(filter = "mvc.command.name=/document_library/copy_dl_objects")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private PortletLocalService _portletLocalService;

}
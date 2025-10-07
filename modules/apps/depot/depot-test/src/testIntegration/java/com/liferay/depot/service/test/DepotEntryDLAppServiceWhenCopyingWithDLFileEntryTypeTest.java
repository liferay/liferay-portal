/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.exception.InvalidFileEntryTypeException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.HashMap;

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
public class DepotEntryDLAppServiceWhenCopyingWithDLFileEntryTypeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "name"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "description"
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_depotGroup = _groupLocalService.getGroup(_depotEntry.getGroupId());

		DDMStructure depotDDMStructure = _ddmStructureLocalService.addStructure(
			null, _depotGroup.getCreatorUserId(), _depotGroup.getGroupId(),
			DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
			PortalUtil.getClassNameId(DLFileEntryMetadata.class),
			StringPool.BLANK,
			HashMapBuilder.put(
				LocaleUtil.getDefault(),
				DLFileEntryMetadata.class.getSimpleName()
			).build(),
			new HashMap<>(), StringPool.BLANK, StorageType.DEFAULT.toString(),
			ServiceContextTestUtil.getServiceContext(_depotGroup.getGroupId()));

		_depotDLFileEntryType = _dlFileEntryTypeLocalService.addFileEntryType(
			null, _depotGroup.getCreatorUserId(), _depotGroup.getGroupId(),
			depotDDMStructure.getStructureId(), null,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			new HashMap<>(),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(_depotGroup.getGroupId()));

		_group = GroupTestUtil.addGroup();

		DDMStructure ddmStructure = _ddmStructureLocalService.addStructure(
			null, _group.getCreatorUserId(), _group.getGroupId(),
			DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
			PortalUtil.getClassNameId(DLFileEntryMetadata.class),
			StringPool.BLANK,
			HashMapBuilder.put(
				LocaleUtil.getDefault(),
				DLFileEntryMetadata.class.getSimpleName()
			).build(),
			new HashMap<>(), StringPool.BLANK, StorageType.DEFAULT.toString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_dlFileEntryType = _dlFileEntryTypeLocalService.addFileEntryType(
			null, _group.getCreatorUserId(), _group.getGroupId(),
			ddmStructure.getStructureId(), null,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			new HashMap<>(),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = InvalidFileEntryTypeException.class)
	public void testCopyFileEntryFailsUnlessDLFileEntryTypeAvailable()
		throws Exception {

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			true, _depotEntry.getDepotEntryId(), _group.getGroupId(), false);

		FileEntry fileEntry = _addFileEntry(
			_group.getGroupId(), _dlFileEntryType.getFileEntryTypeId());

		_dlAppService.copyFileEntry(
			fileEntry.getFileEntryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			_depotGroup.getGroupId(), _dlFileEntryType.getFileEntryTypeId(),
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(
					_depotGroup.getGroupId()),
			ServiceContextTestUtil.getServiceContext(_depotGroup.getGroupId()));
	}

	@Test
	public void testCopyFileEntryShouldCopyDLFileEntryTypeWhenDLFileEntryTypeAvailable()
		throws Exception {

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			true, _depotEntry.getDepotEntryId(), _group.getGroupId(), false);

		FileEntry fileEntry1 = _addFileEntry(
			_depotGroup.getGroupId(),
			_depotDLFileEntryType.getFileEntryTypeId());

		FileEntry fileEntry2 = _copyFileEntry(
			_group.getGroupId(), fileEntry1.getFileEntryId(),
			_depotDLFileEntryType.getFileEntryTypeId());

		DLFileEntry dlFileEntry1 = (DLFileEntry)fileEntry1.getModel();

		DLFileEntry dlFileEntry2 = (DLFileEntry)fileEntry2.getModel();

		Assert.assertEquals(
			dlFileEntry1.getFileEntryTypeId(),
			dlFileEntry2.getFileEntryTypeId());
	}

	@Test
	public void testCopyFileEntryShouldNotCopyDLFileEntryTypeFromRelatedGroupUnlessDLFileEntryTypeAvailable()
		throws Exception {

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			_depotEntry.getDepotEntryId(), _group.getGroupId());

		FileEntry fileEntry1 = _addFileEntry(
			_depotGroup.getGroupId(),
			_depotDLFileEntryType.getFileEntryTypeId());

		DLFileEntry dlFileEntry1 = (DLFileEntry)fileEntry1.getModel();

		Assert.assertEquals(
			_depotDLFileEntryType.getFileEntryTypeId(),
			dlFileEntry1.getFileEntryTypeId());

		Assert.assertNotEquals(
			DLFileEntryTypeConstants.COMPANY_ID_BASIC_DOCUMENT,
			dlFileEntry1.getFileEntryTypeId());

		FileEntry fileEntry2 = _copyFileEntry(
			_group.getGroupId(), fileEntry1.getFileEntryId());

		DLFileEntry dlFileEntry2 = (DLFileEntry)fileEntry2.getModel();

		Assert.assertEquals(
			DLFileEntryTypeConstants.COMPANY_ID_BASIC_DOCUMENT,
			dlFileEntry2.getFileEntryTypeId());
	}

	@Test
	public void testCopyFileEntryShouldNotCopyDLFileEntryTypeFromUnrelatedGroup()
		throws Exception {

		FileEntry fileEntry1 = _addFileEntry(
			_group.getGroupId(), _dlFileEntryType.getFileEntryTypeId());

		DLFileEntry dlFileEntry1 = (DLFileEntry)fileEntry1.getModel();

		Assert.assertEquals(
			_dlFileEntryType.getFileEntryTypeId(),
			dlFileEntry1.getFileEntryTypeId());

		Assert.assertNotEquals(
			DLFileEntryTypeConstants.COMPANY_ID_BASIC_DOCUMENT,
			dlFileEntry1.getFileEntryTypeId());

		FileEntry fileEntry2 = _copyFileEntry(
			_depotGroup.getGroupId(), fileEntry1.getFileEntryId());

		DLFileEntry dlFileEntry2 = (DLFileEntry)fileEntry2.getModel();

		Assert.assertEquals(
			DLFileEntryTypeConstants.COMPANY_ID_BASIC_DOCUMENT,
			dlFileEntry2.getFileEntryTypeId());
	}

	@Test
	public void testCopyFileEntryShouldNotCopyDLFileEntryTypeToUnrelatedGroup()
		throws Exception {

		FileEntry fileEntry1 = _addFileEntry(
			_depotGroup.getGroupId(),
			_depotDLFileEntryType.getFileEntryTypeId());

		DLFileEntry dlFileEntry1 = (DLFileEntry)fileEntry1.getModel();

		Assert.assertEquals(
			_depotDLFileEntryType.getFileEntryTypeId(),
			dlFileEntry1.getFileEntryTypeId());

		Assert.assertNotEquals(
			DLFileEntryTypeConstants.COMPANY_ID_BASIC_DOCUMENT,
			dlFileEntry1.getFileEntryTypeId());

		FileEntry fileEntry2 = _copyFileEntry(
			_group.getGroupId(), fileEntry1.getFileEntryId());

		DLFileEntry dlFileEntry2 = (DLFileEntry)fileEntry2.getModel();

		Assert.assertEquals(
			DLFileEntryTypeConstants.COMPANY_ID_BASIC_DOCUMENT,
			dlFileEntry2.getFileEntryTypeId());
	}

	@Test
	public void testCopyFileEntryShouldNotCopyDLFileEntryTypeUnlessDLFileEntryTypeAvailable()
		throws Exception {

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			_depotEntry.getDepotEntryId(), _group.getGroupId());

		FileEntry fileEntry1 = _addFileEntry(
			_group.getGroupId(), _dlFileEntryType.getFileEntryTypeId());

		DLFileEntry dlFileEntry1 = (DLFileEntry)fileEntry1.getModel();

		Assert.assertEquals(
			_dlFileEntryType.getFileEntryTypeId(),
			dlFileEntry1.getFileEntryTypeId());

		Assert.assertNotEquals(
			DLFileEntryTypeConstants.COMPANY_ID_BASIC_DOCUMENT,
			dlFileEntry1.getFileEntryTypeId());

		FileEntry fileEntry2 = _copyFileEntry(
			_depotGroup.getGroupId(), fileEntry1.getFileEntryId());

		DLFileEntry dlFileEntry2 = (DLFileEntry)fileEntry2.getModel();

		Assert.assertEquals(
			DLFileEntryTypeConstants.COMPANY_ID_BASIC_DOCUMENT,
			dlFileEntry2.getFileEntryTypeId());
	}

	private FileEntry _addFileEntry(long groupId, long fileEntryTypeId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		serviceContext.setAttribute("fileEntryTypeId", fileEntryTypeId);

		return _dlAppService.addFileEntry(
			RandomTestUtil.randomString(), groupId,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, _FILE_NAME,
			ContentTypes.TEXT_PLAIN, _FILE_NAME, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, new byte[0], null, null, null,
			serviceContext);
	}

	private FileEntry _copyFileEntry(long groupId, long fileEntryId)
		throws Exception {

		return _copyFileEntry(
			groupId, fileEntryId,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);
	}

	private FileEntry _copyFileEntry(
			long groupId, long fileEntryId, long fileEntryTypeId)
		throws Exception {

		return _dlAppService.copyFileEntry(
			fileEntryId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, groupId,
			fileEntryTypeId,
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(groupId),
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private static final String _FILE_NAME = "Title.txt";

	@Inject
	private static DLAppService _dlAppService;

	@Inject
	private static DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private DLFileEntryType _depotDLFileEntryType;

	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _depotGroup;

	private DLFileEntryType _dlFileEntryType;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
public class DLFileEntryCTTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, JournalArticleCTTest.class.getName(), null);
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddFileEntry() throws Exception {
		String fileName = RandomTestUtil.randomString();
		String title = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_addFileEntry(fileName, title);
		}

		_addFileEntry(fileName, title);

		Map<Long, List<ConflictInfo>> conflictInfosMap =
			_ctCollectionLocalService.checkConflicts(_ctCollection);

		List<ConflictInfo> conflictInfos = conflictInfosMap.get(
			_classNameLocalService.getClassNameId(
				FriendlyURLEntryLocalization.class));

		for (ConflictInfo conflictInfo : conflictInfos) {
			Assert.assertTrue(conflictInfo.isResolved());
		}
	}

	@Test
	public void testCheckOutFileEntry() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(fileEntry.getGroupId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_dlAppService.checkOutFileEntry(
				fileEntry.getFileEntryId(), serviceContext);
		}

		List<CTEntry> ctEntries = _ctEntryLocalService.getCTCollectionCTEntries(
			_ctCollection.getCtCollectionId());

		Assert.assertEquals(ctEntries.toString(), 0, ctEntries.size());

		Assert.assertTrue(fileEntry.isCheckedOut());

		_dlAppService.cancelCheckOut(fileEntry.getFileEntryId());

		_dlAppService.checkOutFileEntry(
			fileEntry.getFileEntryId(), serviceContext);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_dlAppService.cancelCheckOut(fileEntry.getFileEntryId());
		}

		ctEntries = _ctEntryLocalService.getCTCollectionCTEntries(
			_ctCollection.getCtCollectionId());

		Assert.assertEquals(ctEntries.toString(), 0, ctEntries.size());

		Assert.assertFalse(fileEntry.isCheckedOut());
	}

	@Test
	public void testUpdateFileEntry() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(fileEntry.getGroupId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_dlAppService.updateFileEntry(
				fileEntry.getFileEntryId(), fileEntry.getFileName(),
				fileEntry.getMimeType(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), fileEntry.getDescription(),
				RandomTestUtil.randomString(), DLVersionNumberIncrease.MINOR,
				null, fileEntry.getSize(), fileEntry.getDisplayDate(),
				fileEntry.getExpirationDate(), fileEntry.getReviewDate(),
				serviceContext);
		}

		fileEntry = _dlAppService.getFileEntry(fileEntry.getFileEntryId());

		Assert.assertFalse(fileEntry.isCheckedOut());
	}

	private FileEntry _addFileEntry() throws Exception {
		return _addFileEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	private FileEntry _addFileEntry(String fileName, String title)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		return _dlAppLocalService.addFileEntry(
			null, serviceContext.getUserId(), folder.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.TEXT_PLAIN, title, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, "liferay".getBytes(), null, null, null,
			serviceContext);
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTEntryLocalService _ctEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLAppService _dlAppService;

	@DeleteAfterTestRun
	private Group _group;

}